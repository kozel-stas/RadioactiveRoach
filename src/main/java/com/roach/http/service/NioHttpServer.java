package com.roach.http.service;

import com.google.common.base.Preconditions;
import com.roach.config.ConfigConstants;
import com.roach.http.model.HttpConnection;
import com.roach.http.model.HttpMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static com.roach.config.ConfigConstants.SERVER_BUFFER_SIZE;
import static com.roach.config.ConfigConstants.SERVER_SOCKET_ADDRESS;
import static com.roach.config.ConfigConstants.SERVER_SOCKET_PORT;

public class NioHttpServer {

    private static final Logger LOG = LogManager.getLogger(NioHttpServer.class);

    private HttpConnectionManager httpConnectionManager;
    private HttpMultiProcessor httpMultiProcessor;
    private ServerSocketChannel serverSocketChannel;
    private SelectableChannel serverSelectableChannel;
    private Selector serverSelector;
    private volatile boolean running;

    public NioHttpServer() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSelectableChannel = serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_SOCKET_ADDRESS, SERVER_SOCKET_PORT));

        serverSelector = Selector.open();

        serverSelectableChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
        running = true;
        LOG.info("HTTP server was started!!!");
    }

    private void pollConnections() {
        while (running) {
            try {
                int num = serverSelector.select();
                if (num <= 0) {
                    continue;
                }

                handleSelectorActivation(serverSelector.selectedKeys());
            } catch (Exception e) {
                LOG.error("Error was thrown during selecting", e);
            }
        }
    }

    public void startProcessing() throws IOException {
        httpMultiProcessor.startProcessing();
        pollConnections();
    }

    public void shutdown() throws IOException {
        httpConnectionManager.shutdown();
        httpMultiProcessor.shutdown();
        running = false;
        serverSocketChannel.close();
        serverSelectableChannel.close();
        serverSelector.close();
    }

    private void handleSelectorActivation(Set<SelectionKey> keys) {
        Iterator<SelectionKey> keySetIterator = keys.iterator();
        while (keySetIterator.hasNext()) {
            SelectionKey key = keySetIterator.next();
            keySetIterator.remove();
            try {
                switch (key.readyOps()) {
                    case SelectionKey.OP_ACCEPT:
                        onAccept(key);
                        break;
                    case SelectionKey.OP_READ:
                        onRead(key);
                        break;
                }
            } catch (Exception ex) {
                internalClose(key);
                LOG.error("Error during handling data from selector.", ex);
            }
        }
    }

    private void internalClose(SelectionKey key) {
        httpConnectionManager.removeConnection((String) key.attachment());
    }

    private void onAccept(SelectionKey selectionKey) throws IOException {
        Socket socket = serverSocketChannel.socket().accept();
        SocketChannel channel = socket.getChannel();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(serverSelector, SelectionKey.OP_READ);
        HttpConnection httpConnection = new HttpConnection(channel, System.currentTimeMillis(), ConfigConstants.INITIAL_TIME_OUT);
        httpConnectionManager.addConnection(httpConnection);
        key.attach(httpConnection.getId());
        LOG.info("New client was accepted. {}", channel);
    }

    private void onRead(SocketChannel socketChannel, SelectionKey key) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(SERVER_BUFFER_SIZE);
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int bytesCount = socketChannel.read(byteBuffer);
            if (bytesCount > 0) {
                stringBuilder.append(new String(byteBuffer.array()));
                byteBuffer.flip();
            } else {
                if (bytesCount == -1) {
                    LOG.debug("Close connection {}.", key);
                    internalClose(key);
                    return;
                }
                break;
            }
        }

        HttpMessage message = new HttpMessage(
                stringBuilder.toString(),
                Preconditions.checkNotNull(httpConnectionManager.touchConnection((String) key.attachment())),
                System.currentTimeMillis()
        );
        message.onStartProcessing();
        httpMultiProcessor.offerData(message);
    }

    private void onRead(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        onRead(socketChannel, selectionKey);
    }

    public void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    public void setHttpMultiProcessor(HttpMultiProcessor httpMultiProcessor) {
        this.httpMultiProcessor = httpMultiProcessor;
    }

}
