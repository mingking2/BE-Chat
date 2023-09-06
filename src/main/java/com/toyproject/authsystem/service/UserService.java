package com.toyproject.authsystem.service;

import com.toyproject.authsystem.IncorrectPasswordException;
import com.toyproject.authsystem.StatusAlreadyExistsException;
import com.toyproject.authsystem.domain.entity.User;
import com.toyproject.authsystem.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@Slf4j
@Getter @Setter
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // 아래 두 레포지토리는 테스트 초기화용입ㄴ다.ㅇ
    //private final MessageRepository messageRepository;
    //private final ChatRoomRepository chatRoomRepository;


    @Autowired
    public UserService(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    // newUser 객체에 있는 이메일의 중복 확인 후 db에 저장하고 해당 객체 반환
    public User register(User newUser) {
        // 이메일 중복 확인
        log.info(newUser.getEmail());
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser != null) {
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

    public User updateProfileImage(Long userId, MultipartFile imageFile) throws IOException {
        // Load user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // Delete the old image file if it exists
        String oldImageFileName = user.getImageUrl();
        if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
            fileStorageService.deleteFile(oldImageFileName);
        }


        // Store the image and get its URL
        String newImageFileName  = fileStorageService.storeFile(imageFile);

        // Update the user's profile image URL
        user.setImageUrl(newImageFileName);

        // Save and return the updated user
        return userRepository.save(user);
    }

    public String getUserImageFileName(Long userId) {
        // Load user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        // Return the image file name
        return user.getImageUrl();
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
        List<User> friends = user.getFriends();

        return friends;
    }



    // test를 위한 db초기화기능 넣음
    public void resetData() {
        userRepository.deleteAll();
        userRepository.resetIdSequence();
    }

    public void resetData2() {
        //chatRoomRepository.deleteAll();
       // chatRoomRepository.resetIdSequence();
        userRepository.deleteAllFromChatroomUserTable();
        userRepository.resetChatroomUserIdSequence();
        userRepository.deleteAllFromChatRoomTable();
        userRepository.resetChatRoomIdSequence();
        //messageRepository.deleteAll();
        //messageRepository.resetIdSequence();
    }
}


