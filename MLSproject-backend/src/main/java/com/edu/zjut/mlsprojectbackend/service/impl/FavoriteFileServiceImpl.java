package com.edu.zjut.mlsprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.zjut.mlsprojectbackend.entity.dto.FavoriteFile;
import com.edu.zjut.mlsprojectbackend.mapper.FavoriteFileMapper;
import com.edu.zjut.mlsprojectbackend.service.FavoriteFileService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 收藏文件服务实现类。
 */
@Service
public class FavoriteFileServiceImpl extends MPJBaseServiceImpl<FavoriteFileMapper, FavoriteFile> implements FavoriteFileService {
    //新建内容，收藏文件
    @Resource
    UserFileServiceImpl userFileService;
    /**
     * 收藏文件操作。
     *
     * @param username 用户名
     * @param fileId   文件ID
     * @return 如果文件收藏失败，则返回错误信息；否则返回null。
     */
    @Override
    public String favoriteFiles(String username, Integer fileId){
        FavoriteFile favoriteFile=new FavoriteFile(null, username,fileId);
        if(!this.save(favoriteFile)){
            return "文件收藏失败！";
        }
        return userFileService.updateFavCount(fileId, 1);
    }
    /**
     * 取消收藏文件操作。
     *
     * @param ffId 收藏文件ID
     * @return 如果取消收藏失败，则返回错误信息；否则返回null。
     */
    @Override
    public String undoFavorite(Integer ffId) {
        FavoriteFile file = this.findFFileById(ffId);
        if(!this.removeById(ffId)) {
            return "取消收藏失败";
        }
        return userFileService.updateFavCount(file.getFileId(), 1);
    }
    /**
     * 根据用户名获取收藏文件列表。
     *
     * @param username 用户名
     * @return 收藏文件列表
     */
    @Override
    public List<FavoriteFile> getFFile(String username) {
        QueryWrapper<FavoriteFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.baseMapper.selectList(queryWrapper);
    }
    public FavoriteFile findFFileById(Integer id) {
        return this.query()
                .eq("ffId", id)
                .one();
    }
}
