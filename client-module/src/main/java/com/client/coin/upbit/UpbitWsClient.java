package com.client.coin.upbit;


import com.client.coin.upbit.util.enums.SiseType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UpbitWsClient {

    private final OkHttpClient client;
    private WebSocket webSocket;  // Added to hold the WebSocket instance

    public UpbitWsClient() {
        this.client = createHttpClientWithConnectionPool();
    }

    public void connect() {
        if (webSocket == null) {  // Connect only if WebSocket is not already established
            Request request = new Request.Builder()
                    .url("wss://api.upbit.com/websocket/v1")
                    .build();

            UpbitWsListener webSocketListener = new UpbitWsListener();
            webSocketListener.setParameter(SiseType.TICKER, List.of("KRW-BTC"));
            webSocket = client.newWebSocket(request, webSocketListener);

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            // Schedule a task to process received data every 5 seconds
            executor.scheduleAtFixedRate(() -> {
                // You can implement your data processing logic here
                System.out.println("소켓 스케줄 실행되었습니다.");
            }, 0, 5, TimeUnit.SECONDS);

            // Add shutdown hook to gracefully shutdown the executor
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                executor.shutdown();
                try {
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }
    }

    private OkHttpClient createHttpClientWithConnectionPool() {
        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);
        return new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .build();
    }
}

