package com.roach;

import com.roach.config.Configurator;
import com.roach.content.scanner.DirectoryScanner;
import com.roach.http.service.HttpConnectionManagerImpl;
import com.roach.http.service.HttpMultiProcessor;
import com.roach.http.service.HttpMultiProcessorImpl;
import com.roach.http.service.NioHttpServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
// разобраться как работает Http
public class Main {

    public static void main(String[] args) throws IOException {
        new Configurator().onStartup();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Socket socket = new Socket("127.0.0.1", 8080);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                out.write("GET / HTTP/1.1\r\n" +
                        "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Cache-Control: max-age=0\r\n" +
                        "Upgrade-Insecure-Requests: 1\r\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36\r\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\r\n" +
                        "Accept-Encoding: gzip, deflate, br\r\n" +
                        "Accept-Language: ru-RU,ru;q=0.9,en;q=0.8\r\n" +
                        "\r\n"
                //тело запроса
                );
                out.flush();
                Thread.sleep(2000);
//                out.write("122");
//                out.flush();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        DirectoryScanner directoryScanner = new DirectoryScanner("C:\\Users\\vlads\\Pictures");
        directoryScanner.show();
        NioHttpServer nioHttpServer = new NioHttpServer();
        ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(80);
        nioHttpServer.setHttpConnectionManager(new HttpConnectionManagerImpl(scheduledExecutorService));
        HttpMultiProcessor httpMultiProcessor = new HttpMultiProcessorImpl(scheduledExecutorService);
        httpMultiProcessor.startProcessing();
        nioHttpServer.setHttpMultiProcessor(httpMultiProcessor);
        nioHttpServer.pollConnection();

    }

}
