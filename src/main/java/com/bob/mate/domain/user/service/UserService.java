package com.bob.mate.domain.user.service;

import com.bob.mate.domain.user.dto.UserProfileRequest;
import com.bob.mate.domain.user.dto.UserRequest;
import com.bob.mate.domain.user.entity.User;
import com.bob.mate.domain.user.repository.UserRepository;
import com.bob.mate.global.config.redis.RedisUtil;
import com.bob.mate.global.dto.CustomResponse;
import com.bob.mate.global.exception.CustomException;
import com.bob.mate.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }


    /**
     * 회원 프로필 단건 조회
     */
    public User findById(Long id){
        return getFindById(id);
    }


    /**
     * 회원 탈퇴
     */
    @Transactional
    public CustomResponse deleteUser(Long userId) {

        userRepository.deleteById(userId);

        redisUtil.deleteData(String.valueOf(userId));

        return new CustomResponse("회원 탈퇴가 완료 되었습니다.");

    }

    /**
     * 회원 닉네임 생성 및 변경
     */
    @Transactional
    public CustomResponse createNickName(Long userId, UserRequest userRequest) {

        User user = getFindById(userId);

        if (!user.getUserProfile().getNickName().equals(userRequest.getNickname())) {
            user.createNickName(userRequest.getNickname());
        }
        return new CustomResponse("닉네임이 저장 되었습니다.");
    }


    /**
     * 회원 프로필 생성 및 변경
     */
    @Transactional
    public CustomResponse updateProfile(Long userId, MultipartFile multipartFile, UserProfileRequest userProfileRequest) {
        User findUser = getFindById(userId);

        findUser.createProfile(userProfileRequest.getAddress(),
                userProfileRequest.getPhoneNumber(),
                userProfileRequest.getEmail(),
                userProfileRequest.getGender(),
                userProfileRequest.getImgUrl());

        return new CustomResponse("회원 프로필이 저장 되었습니다.");
    }



    /**
     * 중복 로직 findById
     */
    private User getFindById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }
}


