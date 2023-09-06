package com.toyproject.authsystem.controller;


import com.toyproject.authsystem.IncorrectPasswordException;
import com.toyproject.authsystem.StatusAlreadyExistsException;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.FileStorageService;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

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

    @GetMapping("/profile/image")
    public ResponseEntity<?> getImage(@SessionAttribute(name = "user", required = false) User user) throws IOException {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        } else {
            String fileName = userService.getUserImageFileName(user.getId());
            Resource image = fileStorageService.loadFileAsResource(fileName);
            String contentType = URLConnection.guessContentTypeFromName(image.getFilename());

            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                    .body(image);

        }
    }


    @PostMapping("/profile/imageUpdate")
    public ResponseEntity<?> updateImage(@SessionAttribute(name = "user", required = false) User user,
                                         @RequestParam("image") MultipartFile imageFile) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        } else {
            try {
                User updatedUser = userService.updateProfileImage(user.getId(), imageFile);

                return new ResponseEntity<>(updatedUser, HttpStatus.OK);


            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생");
            }
        }
    }


    @GetMapping("/profile/nickname")
    public ResponseEntity<?> getNickname(@SessionAttribute(name = "user", required = false) User user) {
        log.info("nickname 들어왓용ㅁ");

        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }
        else {
            Map<String, String> response = new HashMap<>();
            response.put("nickname", user.getNickname());
            return ResponseEntity.ok().body(response);
//            return ResponseEntity.ok().body(String.format("로그인 유저의 이메일: %s, 닉네임: %s", user.getEmail(), user.getNickname()));
        }
    }

    @GetMapping("/profile/status")
    public ResponseEntity<?> getStatus(@SessionAttribute(name = "user", required = false) User user) {
        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", user.getStatus());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/profile/statusUpdate")
    public ResponseEntity<?> updateStatus(@SessionAttribute(name = "user", required = false) User user,
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

                //return ResponseEntity.ok().body(String.format("상태메세지가 성공적으로 업데이트 되었습니다: %s", user.getStatus()));
            } catch (StatusAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", user.getStatus());

        return ResponseEntity.ok().body(response);
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

    @GetMapping("/reset")
    public ResponseEntity<?> resetDB() {
        //userService.resetData();
        userService.resetData2();
        return ResponseEntity.ok("데이터베이스가 초기화되었다.");
    }

}
