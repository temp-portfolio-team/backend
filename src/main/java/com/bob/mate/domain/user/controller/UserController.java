package com.bob.mate.domain.user.controller;


import com.bob.mate.domain.post.dto.MyCommentResponse;
import com.bob.mate.domain.post.dto.MyPostResponse;
import com.bob.mate.domain.user.dto.UserProfileQueryDto;
import com.bob.mate.domain.user.dto.UserProfileRequest;
import com.bob.mate.domain.user.dto.UserProfileResponse;
import com.bob.mate.domain.user.dto.UserResponse;
import com.bob.mate.domain.user.service.UserService;
import com.bob.mate.global.dto.CustomResponse;
import com.bob.mate.global.exception.CustomException;
import com.bob.mate.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@RestController
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 단건 조회 API", description = "헤더에서 엑세스 토큰을 받아와서 유저 정보를 반환하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 정보가 정상 리턴된 경우"),
            @ApiResponse(responseCode = "404", description = "입력받은 엑세스 토큰으로 회원 ID를 찾을 수 없는 경우")
    })
    @GetMapping("/me")
    public UserResponse getUser() {
        return userService.findValidateProfileSaveUser();
    }


    @Operation(summary = "유저 정보 삭제 API", description = "헤더에서 엑세스 토큰을 받아와서 삭제하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴가 정상적으로 성공된 경우"),
            @ApiResponse(responseCode = "404", description = "입력받은 엑세스 토큰으로 회원 ID를 찾지 못한경우")
    })
    @DeleteMapping("/me")
    public CustomResponse deleteUser() {
        return userService.deleteUser();
    }


    @Operation(summary = "유저 프로필 생성 및 수정 API", description = "헤더에서 엑세스 토큰을 받아와서 프로필을 생성 및 수정 하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 프로필이 정상적으로 저장된 경우"),
            @ApiResponse(responseCode = "400", description = "Request Body 입력값이 잘못된 경우"),
            @ApiResponse(responseCode = "404", description = "받아온 토큰으로 유저를 찾지 못한 경우")
    })
    @PostMapping(value = "/profile/me", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public UserProfileResponse updateProfile(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                             @Validated @RequestPart(value = "json") UserProfileRequest userProfile,
                                             BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PROFILE);
        }
        return userService.updateProfile(multipartFile, userProfile);
    }


    @Operation(summary = "유저 프로필 조회 API", description = "헤더에서 엑세스 토큰을 받아와서 프로필을 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 프로필 조회가 정상적으로 리턴된 경우"),
            @ApiResponse(responseCode = "404", description = "받아온 토큰으로 유저를 찾지 못한 경우")
    })
    @GetMapping("/profile/me")
    public UserProfileQueryDto getUserProfile() {
        return userService.findUserProfileById();
    }

    @Operation(summary = "현재 유저가 작성한 모든 글 조회", description = "로그인된 사용자에 한해서 자신이 쓴 글 모두 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 유저가 작성한 모든 글들을 page 로 리턴함"),
            @ApiResponse(responseCode = "404", description = "로그인된 유저가 DB에 없을때")
    })
    @GetMapping("/me/posts")
    public Page<MyPostResponse> getAllMyPosts(Pageable pageable) {
        return userService.getAllMyPosts(pageable);
    }

    @Operation(summary = "현재 유저가 작성한 모든 댓글 조회", description = "로그인된 사용자에 한해서 자신이 쓴 댓글 모두 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 유저가 작성한 모든 댓글 들을 page 로 리턴함"),
            @ApiResponse(responseCode = "404", description = "로그인된 유저가 DB에 없을때")
    })
    @GetMapping("/me/comments")
    public Page<MyCommentResponse> getAllMyComments(Pageable pageable) {
        return userService.getAllMyComments(pageable);
    }
}