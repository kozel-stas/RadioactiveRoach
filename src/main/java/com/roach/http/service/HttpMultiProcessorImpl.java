package com.roach.http.service;

import com.roach.http.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public class HttpMultiProcessorImpl implements HttpMultiProcessor {

    private final static Object STUB = new Object();
    private final static int MAX_SIZE = 10_000;
    private final static int THRESHOLD_FOR_THREAD = 5;

    private final static Logger LOG = LogManager.getLogger(HttpMultiProcessorImpl.class);

    private final BlockingQueue<HttpMessage> messageQueue = new ArrayBlockingQueue<>(MAX_SIZE);
    private final ConcurrentHashMap<HttpMessageProcessor, Object> processors = new ConcurrentHashMap<>();
    private final ScheduledThreadPoolExecutor executor;

    public HttpMultiProcessorImpl(ScheduledThreadPoolExecutor executor) {
        this.executor = executor;
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

    private final class HttpMessageProcessor implements Runnable {

        private volatile boolean processingAllowed = true;
        private Thread thread;

        @Override
        public void run() {
            thread = Thread.currentThread();

            while (processingAllowed) {
                HttpMessage httpMessage = null;
                try {
                    httpMessage = messageQueue.take();
                    httpMessage.onStartProcessing();


                } catch (Exception e) {

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
