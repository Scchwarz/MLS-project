package com.edu.zjut.mlsprojectbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.edu.zjut.mlsprojectbackend.entity.RestBean;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.UserInfoVO;
import com.edu.zjut.mlsprojectbackend.service.UserInfoService;
import com.edu.zjut.mlsprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * 用于用户信息操作
 */
@Validated
@RestController
@RequestMapping("/api/auth/user")
public class UserInfoController {
    @Resource
    UserInfoService service;
    @Resource
    JwtUtils utils;

    /**
     * 用户更新自身信息
     * @param vo 用户信息
     * @return 更新结果
     */
    @PostMapping("/update")
    public RestBean<Void> updateUserInfo(@RequestBody @Valid UserInfoVO vo){
        return messageHandle(() ->
                service.resetUserInfo(vo));
    }
    /**
     * 用户获取自身信息
     * @return 自身信息
     */
    @GetMapping("/thisUser")
    @ResponseBody
    public RestBean<UserInfoVO> getThisUserInfo(HttpServletRequest request) {
        DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
        Integer id = utils.toId(jwt);
        UserInfoVO vo = service.getUserInfo(id);
        return RestBean.success(vo);
    }
    /**
     * 用户获取自身信息
     * @param file 头像文件信息
     * @return 头像上传结果
     */
    @PostMapping(value = "/uploadAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<Void> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
        Integer id = utils.toId(jwt);
        return this.messageHandle(() ->
        {
            try {
                return service.uploadAvatar(file, id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private RestBean<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}
