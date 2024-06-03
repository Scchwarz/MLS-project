package com.edu.zjut.mlsprojectbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import com.edu.zjut.mlsprojectbackend.mapper.UserFileMapper;
import com.edu.zjut.mlsprojectbackend.service.CheckMessageService;
import com.edu.zjut.mlsprojectbackend.service.UserFileService;
import jakarta.annotation.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * 用户文件服务实现类。
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {
	@Resource
	private UserFileMapper mapper;
	@Resource
	private CheckMessageService service;
	@Resource
	private FileIndexServiceImpl fileIndexService;
	/**
	 * 上传用户文件。
	 *
	 * @param fileList  文件列表
	 * @param userName  用户名
	 * @return 如果上传失败，返回错误信息；否则返回null。
	 * @throws Exception 异常
	 */
	public String uploadUserFile(MultipartFile[] fileList, String userName) throws Exception {
		for (MultipartFile file : fileList) {
			// 判断文件类型是否为pdf
			if (!"application/pdf".equals(file.getContentType())) {
				return "文件类型错误，请上传pdf文件";
			}
		}
		for (MultipartFile file : fileList) {
			String originalFilename = file.getOriginalFilename();
			long timestamp = System.currentTimeMillis();
			// 将时间戳转换为 java.util.Date 对象
			Date date = new Date(timestamp);
			assert originalFilename != null;
			String filename = originalFilename.replace("-", "_");
			//文件上传路径
			String filePath = "E:\\软件工程\\课设\\mls-project\\MLSproject-backend\\mlsproject-frontend\\src\\assets\\pdfFiles\\" + filename;
			file.transferTo(new File(filePath));
			UserFile userFile = new UserFile(null, filename, filePath, file.getSize(), 0, date, userName,"公共", "未审核",0, 0);
			if (!this.save(userFile)) return "上传失败";
			fileIndexService.saveByUserFile(userFile);
		}
		return null;
	}

	/**
	 * 删除用户文件。
	 *
	 * @param id 文件ID
	 * @return 如果删除失败，返回错误信息；否则返回null。
	 */
	@Override
	public String deleteUserFile(Integer id) {
		UserFile file = this.findFileById(id);
		String filePath = file.getPath();
		File f = new File(filePath);
		if (f.delete()) {
			this.removeById(id);
			fileIndexService.deleteByUsrFileId(id);
			return null;
		} else {
			return "删除失败！";
		}
	}

	/**
	 * 根据文件ID查找文件。
	 *
	 * @param id 文件ID
	 * @return 用户文件实体
	 */
	public UserFile findFileById(Integer id) {
		return this.query()
				.eq("fileId", id)
				.one();
	}

	@Transactional
	@Override
	public List<UserFile> getFilesByName(String filename) {
		QueryWrapper<UserFile> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("fileName", filename)
				.eq("checked", "已审核")
				.eq("state", "公共");
		return this.baseMapper.selectList(queryWrapper);
	}



	@Transactional
	@Override
	public List<UserFile> getFilesByUserName(String userName) {
		QueryWrapper<UserFile> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userName", userName);
		return this.baseMapper.selectList(queryWrapper);
	}

	/**
	 * 下载用户文件。
	 *
	 * @param fileId 文件ID
	 * @return 文件资源
	 * @throws MalformedURLException 异常
	 */
	@Override
	public org.springframework.core.io.Resource downloadUserFile(Integer fileId) throws MalformedURLException {
		UserFile file = this.findFileById(fileId);
		int downloadCounts = file.getDownloadCounts() + 1;
		boolean update = this.update().eq("fileId", fileId).set("downloadCounts", downloadCounts).update();
		if (update) {
			Path downloadFile = Paths.get(file.getPath());
			fileIndexService.addDownloadCount(fileId, downloadCounts);
			return new UrlResource(downloadFile.toUri());
		} else {
			return null;
		}
	}
	/**
	 * 审核文件操作。
	 *
	 * @param fileId   文件ID
	 * @param check    审核状态
	 * @param MId      消息ID
	 * @param Uname    用户名
	 * @param message  审核消息
	 * @return 如果审核状态修改失败，返回错误信息；否则返回null。
	 */
	@Override
	public String checkFile(Integer fileId, String check,Integer MId, String Uname, String message) {
		boolean update = this.update()
				.eq("fileId", fileId)
				.set("checked", check)
				.update();
		service.createMessage(MId, Uname, fileId, check, message);
		if(!update) return "审核状态修改失败";
		else {
			fileIndexService.setCheck(fileId, check);
			return null;
		}
	}
	/**
	 * 设置文件状态。
	 *
	 * @param fileId 文件ID
	 * @param state  文件状态
	 * @return 如果修改失败，返回错误信息；否则返回null。
	 */
	@Override
	public String setFileState(Integer fileId, String state){ //将修改的state以String的形式修改
		boolean update = this.update()
				.eq("fileId", fileId)
				.set("state", state)
				.update();
		if(!update) return "修改失败";
		else {
			fileIndexService.setState(fileId, state);
			return null;
		}
	}
	/**
	 * 获取所有文件通过文件名。
	 *
	 * @param filename 文件名
	 * @return 文件列表
	 */
	@Override
	public List<UserFile> getAllFilesByName(String filename) {
		QueryWrapper<UserFile> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("fileName", filename);
		return this.baseMapper.selectList(queryWrapper);
	}
	/**
	 * 获取所有未审核文件通过文件名。
	 *
	 * @param filename 文件名
	 * @return 未审核文件列表
	 */
	@Override
	public List<UserFile> getAllUncheckedFiles(String filename) {
		QueryWrapper<UserFile> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("fileName", filename)
				.eq("checked", "未审核");
		return this.baseMapper.selectList(queryWrapper);
	}
	/**
	 * 通过用户名获取用户收藏的文件列表。
	 *
	 * @param username 用户名
	 * @return 用户收藏的文件列表
	 */
	@Override
	public List<UserFile> selectUserFFilesByUsername(String username) {
		return mapper.selectUserFFilesByUsername(username);
	}
	/**
	 * 通过用户名获取用户点赞的文件列表。
	 *
	 * @param username 用户名
	 * @return 用户点赞的文件列表
	 */
	@Override
	public List<UserFile> selectUserLFilesByUsername(String username) {
		return mapper.selectUserLFilesByUsername(username);
	}
	/**
	 * 更新文件的收藏数量。
	 *
	 * @param fileId       文件ID
	 * @param updateCount  更新数量
	 * @return 如果修改失败，返回错误信息；否则返回null。
	 */
	@Override
	public String updateFavCount(Integer fileId,Integer updateCount) {
		UserFile userFile = this.findFileById(fileId);
		Integer favCount = userFile.getFavCounts() + updateCount;
		boolean update = this.update()
				.eq("fileId", fileId)
				.set("fav_count", favCount)
				.update();
		if(!update) return "修改失败";
		else {
			fileIndexService.updateFavCount(fileId, favCount);
			return null;
		}
	}
	/**
	 * 更新文件的点赞数量。
	 *
	 * @param fileId       文件ID
	 * @param updateCount  更新数量
	 * @return 如果修改失败，返回错误信息；否则返回null。
	 */
	@Override
	public String updateLikeCount(Integer fileId,Integer updateCount) {
		UserFile userFile = this.findFileById(fileId);
		Integer likeCount = userFile.getLikeCounts() + updateCount;
		boolean update = this.update()
				.eq("fileId", fileId)
				.set("like_count", likeCount)
				.update();
		if(!update) return "修改失败";
		else {
			fileIndexService.updateLikeCount(fileId, likeCount);
			return null;
		}
	}
}
