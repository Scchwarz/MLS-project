package com.edu.zjut.mlsprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.zjut.mlsprojectbackend.entity.dto.LikeFile;

import java.util.List;

public interface LikeFileService extends IService<LikeFile> {
    String likeFile(String username,Integer fileId);
    String undoLike(Integer lfId);
    List<LikeFile> getLFile(String username);
}
