package com.edu.zjut.mlsprojectbackend.service;

import com.edu.zjut.mlsprojectbackend.entity.dto.FavoriteFile;
import com.github.yulichang.base.MPJBaseService;

import java.util.List;

public interface FavoriteFileService extends MPJBaseService<FavoriteFile> {
    //新建内容，收藏文件
   String favoriteFiles(String username,Integer fileId);
   String undoFavorite(Integer ffId);
   List<FavoriteFile> getFFile(String username);
}
