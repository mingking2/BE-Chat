package com.toyproject.authsystem.controller;

import com.toyproject.authsystem.domain.dto.FriendRequestDto;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/friends")
@Slf4j
public class FriendController {

    private final UserService userService;


    // 현재 로그인된 사용자의 ID와 새로운 친구의 ID를 파라미터로 받아 친구를 추가합니다.
    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend(@RequestBody FriendRequestDto friendRequest,
                                       HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
            }

            User friend = userService.addFriend(user.getEmail(), friendRequest.getFriendName());

//            Map<String, Object> response = new HashMap<>();
//            response.put("name", friend.getNickname());
//            response.put("statusMessage", friend.getStatus());

            return ResponseEntity.ok("추가 성공했다 임마");

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    // 친구 목록 조회
    @PostMapping
    public ResponseEntity<?> getAllFriends(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션없다");
            }


            User user = (User) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
            }

            List<User> friends = userService.getAllFriends(user.getNickname());
            log.info(friends.toString());
            return ResponseEntity.ok(friends);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
