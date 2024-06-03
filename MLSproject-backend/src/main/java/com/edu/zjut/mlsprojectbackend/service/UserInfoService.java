package com.edu.zjut.mlsprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserInfoService extends IService<Account> {
    String resetUserInfo(UserInfoVO vo);
    UserInfoVO getUserInfo(Integer id);
    String uploadAvatar(MultipartFile file, Integer id) throws IOException;
}
