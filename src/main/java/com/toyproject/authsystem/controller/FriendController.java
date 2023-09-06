package com.toyproject.authsystem.controller;

import com.toyproject.authsystem.domain.dto.FriendRequestDto;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.service.ChatService;
import com.toyproject.authsystem.service.FileStorageService;
import com.toyproject.authsystem.service.MessageService;
import com.toyproject.authsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/friends")
@Slf4j
public class FriendController {

    private final UserService userService;
    private final ChatService chatService;
    private final MessageService messageService;
    private final FileStorageService fileStorageService;


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
    @GetMapping
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


            // Create a list of FriendInfo objects
            List<Map<String, String>> friendInfos = new ArrayList<>();
            log.info("for문 직전이야: ");

            for (User friend : friends) {
                String id = String.valueOf(friend.getId());
                String nickname = friend.getNickname();
                String status = friend.getStatus();

                Map<String, String> infoMap = new HashMap<>();
                infoMap.put("id", id);
                infoMap.put("nickname", nickname);
                infoMap.put("status", status);

                friendInfos.add(infoMap);
            }

            return ResponseEntity.ok(friendInfos);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
