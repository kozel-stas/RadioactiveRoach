package com.roach.http.service;

import com.roach.http.model.HttpConnection;
import com.roach.http.model.HttpMessage;
import com.roach.utils.MockSocketChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@RunWith(MockitoJUnitRunner.class)
public class HttpMultiProcessorImplTest {

    private HttpMultiProcessorImpl httpMultiProcessor;

    @Mock
    private ScheduledThreadPoolExecutor executor;

    private List<Thread> threads = new ArrayList<>();
    private HttpConnection httpConnection;

    @Before
    public void beforeUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doAnswer((args) -> {
            Thread thread = new Thread((Runnable) args.getArguments()[0]);
            threads.add(thread);
            thread.start();
            return null;
        }).when(executor).execute(Mockito.any());

        httpConnection = new HttpConnection(new MockSocketChannel().recorder(ByteBuffer.allocate(100_000_000)), System.currentTimeMillis(), 5000);
        httpMultiProcessor = new HttpMultiProcessorImpl(executor);
        httpMultiProcessor.startProcessing();
    }

    @After
    public void tearDown() throws InterruptedException {
        httpMultiProcessor.shutdown();
        for (Thread thread : threads) {
            thread.interrupt();
            thread.join();
        }
        threads.clear();
    }

    @Test
    public void testHttpMessage() throws Exception {
        HttpMessage httpMessage = new HttpMessage("GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: ru-RU,ru;q=0.9,en;q=0.8\r\n" +
                "\r\n", httpConnection, System.currentTimeMillis());
        httpMessage.onStartProcessing();

        httpMultiProcessor.offerData(httpMessage);
    }

}
