package com.client;

import com.client.coin.upbit.UpbitWsClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.client", "com.core"})
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);

        // upbit socket connect
        UpbitWsClient ws = new UpbitWsClient();
        ws.connect();
    }
}
