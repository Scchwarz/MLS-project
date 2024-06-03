package com.edu.zjut.mlsprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.zjut.mlsprojectbackend.entity.dto.LikeFile;
import com.edu.zjut.mlsprojectbackend.mapper.LikeFileMapper;
import com.edu.zjut.mlsprojectbackend.service.LikeFileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点赞文件服务实现类。
 */
@Service
public class LikeFileServiceImpl extends ServiceImpl<LikeFileMapper, LikeFile> implements LikeFileService {
    @Resource
    UserFileServiceImpl userFileService;
    /**
     * 点赞文件操作。
     *
     * @param username 用户名
     * @param fileId   文件ID
     * @return 如果文件点赞失败，则返回错误信息；否则返回null。
     */
    @Override
    public String likeFile(String username, Integer fileId) {
        LikeFile likeFile=new LikeFile(null, username,fileId);
        if(!this.save(likeFile)){
            return "文件点赞失败！";
        }
        return userFileService.updateLikeCount(fileId, 1);
    }
    /**
     * 根据点赞文件ID查找点赞文件。
     *
     * @param id 点赞文件ID
     * @return 点赞文件实体
     */
    public LikeFile findLFileById(Integer id) {
        return this.query()
                .eq("lfId", id)
                .one();
    }
    /**
     * 取消点赞操作。
     *
     * @param lfId 点赞文件ID
     * @return 如果取消点赞失败，则返回错误信息；否则返回null。
     */
    @Override
    public String undoLike(Integer lfId) {
        LikeFile likeFile = this.findLFileById(lfId);
        if(!this.removeById(lfId)) {
            return "取消收藏失败";
        }
        return userFileService.updateLikeCount(likeFile.getFileId(), -1);
    }
    /**
     * 根据用户名获取点赞文件列表。
     *
     * @param username 用户名
     * @return 点赞文件列表
     */
    @Override
    public List<LikeFile> getLFile(String username) {
        QueryWrapper<LikeFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.baseMapper.selectList(queryWrapper);
    }
}
