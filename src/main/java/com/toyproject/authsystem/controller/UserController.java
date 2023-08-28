package com.toyproject.authsystem.controller;


import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 로그인 후 홈화면이라 가정
     * @param user // @SessionAttribute를 사용하여 이름이 "user"인 객체를 value 값으로 가진 sessionId가 있으면 객체를 반환
     * @param session // sessionId 값을 보여주기 위함
     * @return
     */
    @GetMapping("/success")
    public ResponseEntity<?> home(@SessionAttribute(name = "user", required = false) User user, HttpSession session) {
        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 하지 않음");
        }
        else {
            String sessionId = session.getId();
            return ResponseEntity.ok().body(String.format("로그인 유저의 이메일: %s, 세션 ID: %s", user.getEmail(), sessionId));
        }
    }

    /**
     * 회원가입하는 로직
     * @param user // 회원가입을 위한 user 객체 불러옴
     * @return registeredUser // ResponseEntity로 상태를 반환하고 body부분에 회원가입한 계정의 Json타입으로 출력
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User registeredUser = userService.register(user);

        if (registeredUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이메일이 중복되었습니다.");
        }
    }

    /**
     * 로그인을 위한 로직
     * @param email // 쿼리스트링으로 아이디에 해당하는 email값
     * @param password // 비밀번호
     * @param session // 해당 파리미터를 퉁해 선언이 되면서 sessionId를 생성하고
     *                      session.setAttribute를 이용하여 해당 아이디에 value값으로 user 객체를 넣어준다.
     * @return
     */
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

    /**
     * 로그아웃을 위한 로직
     * @param  // 로그아웃을 위해 기존 세션id를 불러온다.
     * @return
     */
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

    @PostMapping("/reset")
    public ResponseEntity<?> resetDB() {
        userService.resetData();
        return ResponseEntity.ok("데이터베이스가 초기화되었다.");
    }

}
