package com.toyproject.authsystem;

import com.toyproject.authsystem.domain.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest servletRequest) {
            HttpSession session = servletRequest.getServletRequest().getSession(false);

            if (session != null) {
                User user = (User) session.getAttribute("user");
                log.info(user.getEmail());
                if (user == null) {
                    log.info("로그인이 필요합니다.");
                    return false; // "user" 속성이 없으면 핸드셰이크 거부
                }
                attributes.put("user", user);  // WebSocket 세션에 'user' 속성 추가
                return true;
            }
        }
        log.info("다 튕김 ㅋㅋ");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
