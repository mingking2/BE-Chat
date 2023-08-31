package com.toyproject.authsystem.controller;


import com.toyproject.authsystem.IncorrectPasswordException;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.StatusAlreadyExistsException;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User registeredUser = userService.register(user);

        if (registeredUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이메일이 중복되었습니다.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.login(loginUser.getEmail(), loginUser.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }

        createSession(user, request, response);

        return ResponseEntity.ok().body(String.format("Login successful! Email: %s", user.getEmail()));
    }

    private void createSession(User user, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        String sessionIdCookie = "JSESSIONID=" + session.getId() + "; Path=/; Secure; HttpOnly; SameSite=None";

        response.setHeader("Set-Cookie", ""); // Clear existing Set-Cookie header
        response.addHeader("Set-Cookie", sessionIdCookie); // Add new Set-Cookie header
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        log.info(String.valueOf(session));

        if (session != null) {
            log.info(session.getId());
            User loginUser = (User) session.getAttribute("user");
            String email = loginUser.getEmail();
            session.invalidate();
            return ResponseEntity.ok("로그아웃 성공! 이메일: " + email);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 상태가 아닙니다.");

    }

    @PostMapping("/profile/image")
    public ResponseEntity<?> getImage(@SessionAttribute(name = "user", required = false) User user) {
        log.info("image 들어왓용ㅁ");

        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }
        else {
            log.info(user.getEmail());
            return ResponseEntity.ok().body(String.format("로그인 유저의 이메일: %s, 이미지 URL: %s", user.getEmail(), user.getImageUrl()));
        }
    }

    @PostMapping("/profile/nickname")
    public ResponseEntity<?> getNickname(@SessionAttribute(name = "user", required = false) User user) {
        log.info("nickname 들어왓용ㅁ");

        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }
        else {
            log.info(user.getEmail());
            return ResponseEntity.ok().body(String.format("로그인 유저의 이메일: %s, 닉네임: %s", user.getEmail(), user.getNickname()));
        }
    }

    @PostMapping("/profile/status")
    public ResponseEntity<?> getStatusOrUpdate(@SessionAttribute(name = "user", required = false) User user,
                                               @RequestBody(required = false) Map<String, Object> payload) {
        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }

        // 클라이언트가 상태 메시지를 보냈는지 확인합니다.
        if(payload != null && payload.containsKey("status")) {
            try {
                String newStatus = (String) payload.get("status");

                // 사용자의 상태 메시지를 변경합니다.
                userService.updateUserStatus(user, newStatus);

                return ResponseEntity.ok().body(String.format("상태메세지가 성공적으로 업데이트 되었습니다: %s", user.getStatus()));
            } catch (StatusAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
            }
        } else {
            // 클라이언트가 상태 메시지를 보내지 않았다면 현재의 상태 메시지를 반환합니다.
            return ResponseEntity.ok().body(String.format("현재 유저의 이메일: %s, 상태메세지: %s", user.getEmail(), user.getStatus()));
        }
    }

    @PostMapping("/profile/pwChange")
    public ResponseEntity<?> changePassword(@SessionAttribute(name = "user", required = false) User user,
                                            @RequestBody Map<String, String> payload) {
        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }

        if(!payload.containsKey("currentPassword") || !payload.containsKey("newPassword")) {
            return ResponseEntity.badRequest().body("현재 비밀번호와 새로운 비밀번호를 모두 제공해야 합니다");
        }

        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        try {
            userService.changePassword(user, currentPassword, newPassword);
        } catch (IncorrectPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        return ResponseEntity.ok().body(String.format("%s 유저의 패스워드가 성공적으로 변경되었습니다.", user.getEmail()));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetDB() {
        //userService.resetData();
        userService.resetData2();
        return ResponseEntity.ok("데이터베이스가 초기화되었다.");
    }

}
