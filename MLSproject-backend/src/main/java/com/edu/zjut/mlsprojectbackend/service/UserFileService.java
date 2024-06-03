package com.edu.zjut.mlsprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

public interface UserFileService extends IService<UserFile> {
	String uploadUserFile(MultipartFile[] fileList, String userName) throws Exception;

	String deleteUserFile(Integer id);

	List<UserFile> getFilesByName(String filename);

	List<UserFile> getFilesByUserName(String userName);

	org.springframework.core.io.Resource downloadUserFile(Integer fileId) throws MalformedURLException;
	String checkFile(Integer fileId, String check,Integer MId,String Uname, String message);
    String setFileState(Integer fileId,String state);
	List<UserFile> getAllFilesByName(String filename);//管理员查询到所有文件（无论是否被检查与状态）
	List<UserFile> getAllUncheckedFiles(String filename);//管理员查询所有未被审核的文件
	List<UserFile> selectUserFFilesByUsername(String username);
	List<UserFile> selectUserLFilesByUsername(String username);
	String updateFavCount(Integer fileId,Integer updateCount);
	String updateLikeCount(Integer fileId,Integer updateCount);
}
