package com.roach.http.service;
import com.roach.http.model.*;
import com.roach.http.model.impl.SystemFilter;
import com.roach.http.model.parser.HttpMessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

public class HttpMultiProcessorImpl implements HttpMultiProcessor, HttpMessageSender {

    private final static Object STUB = new Object();
    private final static int MAX_SIZE = 10_000;
    private final static int THRESHOLD_FOR_THREAD = 5;

    private final static Logger LOG = LogManager.getLogger(HttpMultiProcessorImpl.class);

    private final BlockingQueue<HttpMessage> messageQueue = new ArrayBlockingQueue<>(MAX_SIZE);
    private final ConcurrentHashMap<HttpMessageProcessor, Object> processors = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Filter> filters = new CopyOnWriteArrayList<>();
    //TODO: prefix tree.
    private final CopyOnWriteArrayList<MappingHandler> handlers = new CopyOnWriteArrayList<>();
    private final ScheduledThreadPoolExecutor executor;
    private final HttpMessageSender httpMessageSender;

    public HttpMultiProcessorImpl(ScheduledThreadPoolExecutor executor) {
        this.executor = executor;
        httpMessageSender = this;
        filters.add(new SystemFilter());
    }

    @Override
    public boolean offerData(HttpMessage httpMessage) {
        LOG.debug("HttpMessage " + httpMessage);
        boolean added = messageQueue.add(httpMessage);
        if (added) {
            startNewProcessorIfNeed();
        } else {
            LOG.warn("Maximum size of messages for processing. Skip message {} ", httpMessage);
        }
        return added;
    }

    @Override
    public void startProcessing() {
        if (processors.size() == 0) {
            createNewMessageProcessor();
        } else {
            LOG.warn("Already started.");
        }
    }

    @Override
    public void shutdown() {
        messageQueue.clear();
        for (HttpMessageProcessor processor : processors.keySet()) {
            try {
                processor.shutdown();
            } catch (Exception ex) {
                LOG.error("Exception during shutdown processor " + processor, ex);
            }
        }
        processors.clear();
    }
// разобраться
    private synchronized void startNewProcessorIfNeed() {
        if (messageQueue.size() > THRESHOLD_FOR_THREAD) {
            createNewMessageProcessor();
        }
    }

    private void createNewMessageProcessor() {
        HttpMessageProcessor processor = new HttpMessageProcessor();
        processors.put(processor, STUB);
        executor.execute(processor);
    }

    @Override
    public boolean sendResponse(HttpRequest httpRequest, HttpResponse response) {
        try {
            HttpMessage httpMessage = HttpMessageParser.INSTANCE.createHttpMessageFromResponse(httpRequest, response);
            ByteBuffer buffer = ByteBuffer.wrap(httpMessage.getMessage().getBytes());
            httpMessage.getHttpConnection().getChannel().write(buffer);
            LOG.debug("Response {}", httpMessage);
            return true;
        } catch (Exception ex) {
            LOG.error("Exception during sending", ex);
            return false;
        }
    }

    private MappingHandler findHandlerForRequest(HttpRequest httpRequest) {
        for (MappingHandler mappingHandler : handlers) {
            if (mappingHandler.getUri().matcher(httpRequest.getUri()).find() && mappingHandler.getSupportedMethods().contains(httpRequest.getMethod())) {
                return mappingHandler;
            }
        }
        return null;
    }

    private final class HttpMessageProcessor implements Runnable {

        private volatile boolean processingAllowed = true;
        private Thread thread;

        @Override
        public void run() {
            thread = Thread.currentThread();

            HttpRequest httpRequest = null;
            HttpResponse httpResponse = null;
            while (processingAllowed) {
                HttpMessage httpMessage = null;
                try {
                    httpMessage = messageQueue.take();
                    httpMessage.onStartProcessing();

                    httpRequest = HttpMessageParser.INSTANCE.createHttpRequestFromMessage(httpMessage);
                    httpResponse = new HttpResponse(httpMessage.getHttpConnection());

                    for (Filter filter : filters) {
                        filter.processData(httpRequest, httpResponse);
                    }

                    MappingHandler mappingHandler = findHandlerForRequest(httpRequest);
                    if (mappingHandler != null) {
                        if (mappingHandler.isAcceptable(httpRequest, httpResponse)) {
                            mappingHandler.handleRequest(httpRequest, httpResponse);
                        }
                    } else {
                        httpResponse.setResponseCode(ResponseCode.NOT_FOUND);
                    }
                } catch (Exception e) {
                    if (httpResponse != null && httpResponse.getResponseCode() == null) {
                        httpResponse.setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR);
                    }
                    LOG.error("Exception was thrown at MessageProcessor", e);
                } finally {
                    if (httpRequest != null && httpResponse != null) {
                        httpMessageSender.sendResponse(httpRequest, httpResponse);
                    }
                    if (httpMessage != null) {
                        httpMessage.onFinishProcessing();
                    }
                    httpMessage = null;
                }
            }
        }

        public void shutdown() throws InterruptedException {
            processingAllowed = false;
            if (thread != null) {
                thread.interrupt();
                thread.join();
            }
        }

    }

}
