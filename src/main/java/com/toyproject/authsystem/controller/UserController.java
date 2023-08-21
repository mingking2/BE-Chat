package com.toyproject.authsystem.controller;


import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserService userService;

//    @PostMapping("/register")
//    public ResponseEntity<User> register(@RequestBody User user) {
//        return userService.register(user);
//    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpSession session) {
        User registeredUser = userService.register(user);

        if (registeredUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이메일이 중복되었습니다.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        // 로그인 상태 확인
        User loggedinUser = (User) session.getAttribute("user");
        if(loggedinUser != null) {
             return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 로그인된 상태이다.");
        }

        User user = userService.login(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            // 로그인 성공 시, 사용자의 email 및 세션 ID 출력
            return ResponseEntity.ok().body(
                    String.format("로그인 성공! 이메일: %s, 세션 ID: %s", user.getEmail(), session.getId()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 잘못되었습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        User loginUser = (User) session.getAttribute("user");

        if(loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 로그아웃된 상태이다.");
        }

        String email = loginUser.getEmail();
        session.removeAttribute("user");
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공! 이메일: " + email);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetDB() {
        userService.resetData();
        return ResponseEntity.ok("데이터베이스가 초기화되었다.");
    }

}
