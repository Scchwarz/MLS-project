package com.edu.zjut.mlsprojectbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import com.edu.zjut.mlsprojectbackend.entity.vo.request.UserInfoVO;
import com.edu.zjut.mlsprojectbackend.mapper.AccountMapper;
import com.edu.zjut.mlsprojectbackend.service.AccountService;
import com.edu.zjut.mlsprojectbackend.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 用户信息服务实现类。
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<AccountMapper, Account> implements UserInfoService {
    @Resource
    AccountService service;
    /**
     * 重置用户信息。
     *
     * @param vo 用户信息视图对象
     * @return 如果修改失败，返回错误信息；否则返回null。
     */
    @Override
    public String resetUserInfo(UserInfoVO vo){
        Integer id = vo.getId();
//        if(service.existsByUsername(vo.getUsername())){
//            return "该用户名已存在";
//        }
//        if(service.existsByEmail(vo.getEmail())){
//            return "该邮箱已被注册";
//        }
        String userName = service.findAccountByName(vo.getUsername()).getUsername();
        if(service.existsByUsername(vo.getUsername()) && !Objects.equals(userName, vo.getUsername()))
        {
            return "该用户名已存在";
        }
        boolean update = this.update()
                .eq("id", id)
                .set("username", vo.getUsername())
                .set("email", vo.getEmail())
                .set("phone", vo.getPhone())
                .update();
        if(update){
            return null;
        } else {
            return "修改失败";
        }
    }
    /**
     * 获取用户信息。
     *
     * @param id 用户ID
     * @return 用户信息视图对象
     */
    @Override
    public UserInfoVO getUserInfo(Integer id) {
        Account account = this.getById(id);
        if(account == null) return null;
        UserInfoVO vo =new UserInfoVO();
        BeanUtils.copyProperties(account, vo);
        return vo;
    }
    /**
     * 上传用户头像。
     *
     * @param file 头像文件
     * @param id   用户ID
     * @return 如果头像上传失败，返回错误信息；否则返回null。
     * @throws IOException IO异常
     */
    @Override
    public String uploadAvatar(MultipartFile file, Integer id) throws IOException {
        String originalFilename = "AvatarOf" + id + ".jpg";
        Account account = this.getById(id);

        String filePath = "E:\\软件工程\\课设\\mls-project\\MLSproject-backend\\mlsproject-frontend\\src\\assets\\avatar\\" + originalFilename;
        if(account.getAvatarUrl() != null) {
            File f = new File(filePath);
            if(!f.delete()) return "原头像删除失败";
        }
        //文件上传路径
        String fileName = "http://localhost:8080/api/avatar/" + originalFilename;
        file.transferTo(new File(filePath));
        boolean update = this.update()
                .eq("id", id)
                .set("avatarUrl", fileName)
                .update();
        if (!update) return "头像上传失败";
        return null;
    }
}
