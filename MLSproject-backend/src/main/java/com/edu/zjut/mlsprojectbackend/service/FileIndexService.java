package com.edu.zjut.mlsprojectbackend.service;

import com.edu.zjut.mlsprojectbackend.entity.dto.FileIndex;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;

import java.util.List;

public interface FileIndexService {
	void initIndex();
	List<FileIndex> matchingQuery(String keywords);
	void saveByUserFile(UserFile file) throws Exception;
	void deleteByUsrFileId(Integer id);
	void updateFavCount(Integer id, Integer fav);
	void updateLikeCount(Integer id, Integer like);
	void addDownloadCount(Integer id, Integer download);
	void setCheck(Integer id, String checked);
	void setState(Integer id, String state);
}
