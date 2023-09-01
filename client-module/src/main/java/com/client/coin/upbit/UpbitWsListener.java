package com.client.coin.upbit;

import com.client.coin.upbit.util.enums.SiseType;
import com.client.coin.upbit.util.json.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Component
public class UpbitWsListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private String json;
    private SiseType siseType;

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        System.out.printf("Socket Closed : %s / %s\r\n", code, reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        System.out.printf("Socket Closing : %s / %s\n", code, reason);
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        webSocket.cancel();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        System.out.println("Socket Error : " + t.getMessage());
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        JsonNode jsonNode = JsonUtil.fromJson(text, JsonNode.class);
        System.out.println(jsonNode.toPrettyString());
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        HashMap<String, Object> result = JsonUtil.fromJson(bytes.string(StandardCharsets.UTF_8), HashMap.class);
        System.out.println(result);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        webSocket.send(getParameter());
//        webSocket.close(NORMAL_CLOSURE_STATUS, null); // 없을 경우 끊임없이 서버와 통신함
    }

    public void setParameter(SiseType siseType, List<String> codes) {
        this.siseType = siseType;
        this.json = JsonUtil.toJson(List.of(Ticket.of(UUID.randomUUID().toString()), Type.of(siseType, codes)));
    }

    private String getParameter() {
        return this.json;
    }

    @Data(staticConstructor = "of")
    private static class Ticket {
        private final String ticket;
    }

    @Data(staticConstructor = "of")
    private static class Type {
        private final SiseType type;
        private final List<String> codes; // market
        private Boolean isOnlySnapshot = false;
        private Boolean isOnlyRealtime = true;
    }
}
