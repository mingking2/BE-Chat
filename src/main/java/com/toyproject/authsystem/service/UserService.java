package com.toyproject.authsystem.service;

import com.toyproject.authsystem.IncorrectPasswordException;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // newUser 객체에 있는 이메일의 중복 확인 후 db에 저장하고 해당 객체 반환
    public User register(User newUser) {
        // 이메일 중복 확인
        log.info(newUser.getEmail());
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if(existingUser != null) {
            return null;
        }

        newUser = userRepository.save(newUser);
        return newUser;
    }

    // 이메일과 비밀번호가 db와 일치하는지 확인해서 일치하면 객체 반환
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public User updateUserStatus(User user, String newStatus) {
        // 이미 같은 상태 메시지를 가진 유저가 있는 경우 예외를 발생시킵니다.
        if(newStatus.equals(user.getStatus())) {
            throw new StatusAlreadyExistsException(String.format("이미 사용중인 상태메시지 입니다: %s", newStatus));
        }

        user.setStatus(newStatus);
        return userRepository.save(user);
    }

    public User changePassword(User user, String currentPassword, String newPassword) {
        if(!user.getPassword().equals(currentPassword)) {
            throw new IncorrectPasswordException();
        }

        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    public User addFriend(String userEmail, String friendNickname) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByNickname(friendNickname);

        if (friend == null) {
            throw new RuntimeException("그런 놈 없다 임마");
        } else if (user.equals(friend)) {
            throw new RuntimeException("자기 자신을 친구로 추가할 수 없습니다.");
        } else if (user.getFriends().contains(friend)) {
            throw new RuntimeException("이미 친구다 임마");
        } else {
            user.getFriends().add(friend);
            friend.getFriends().add(user);
            userRepository.save(user);
            userRepository.save(friend);

            return friend;
        }
    }

    public List<User> getAllFriends(String nickname) {
        User user = userRepository.findByNickname(nickname);

        if (user == null) {
            throw new IllegalArgumentException("User not found: " + nickname);
        }

        return user.getFriends();
    }

    // test를 위한 db초기화기능 넣음
    public void resetData() {
        userRepository.deleteAll();
        userRepository.resetIdSequence();
    }
}

