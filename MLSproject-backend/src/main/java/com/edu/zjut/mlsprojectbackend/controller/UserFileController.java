package com.edu.zjut.mlsprojectbackend.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.edu.zjut.mlsprojectbackend.entity.RestBean;
import com.edu.zjut.mlsprojectbackend.entity.dto.FavoriteFile;
import com.edu.zjut.mlsprojectbackend.entity.dto.FileIndex;
import com.edu.zjut.mlsprojectbackend.entity.dto.LikeFile;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import com.edu.zjut.mlsprojectbackend.service.FavoriteFileService;
import com.edu.zjut.mlsprojectbackend.service.FileIndexService;
import com.edu.zjut.mlsprojectbackend.service.LikeFileService;
import com.edu.zjut.mlsprojectbackend.service.UserFileService;
import com.edu.zjut.mlsprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 用于处理文件相关操作
 */
@RestController
@RequestMapping("/api/auth")
public class UserFileController {

	@Resource
	UserFileService userFileService;
	@Resource
	FavoriteFileService favoriteFileService;
	@Resource
	LikeFileService likeFileService;
	@Resource
	FileIndexService fileIndexService;
	@Resource
	JwtUtils utils;

	/**
	 * 用户上传文件
	 * @param fileList 文件列表
	 * @return 上传结果
	 */
	@PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public RestBean<Void> upload(@RequestParam("file") MultipartFile[] fileList, HttpServletRequest request) {
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		return this.messageHandle(() ->
		{
			try {
				return userFileService.uploadUserFile(fileList, userName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	/**
	 * 用户删除
	 * @param requestBody 前端数据
	 * @return 删除结果
	 */
	@PostMapping("/deleteFile")
	public RestBean<Void> delete(@RequestBody Map<String, Integer> requestBody) {
		Integer id = requestBody.get("id");
		return this.messageHandle(() ->
				userFileService.deleteUserFile(id));
	}
	/**
	 * 用户根据文件名查询文件
	 * @param filename 文件名
	 * @return 对应文件列表
	 */
	@GetMapping("/files")
	@ResponseBody
	public RestBean<List<UserFile>> getFiles(@RequestParam String filename) {
		List<UserFile> list = userFileService.getFilesByName(filename);
		return RestBean.success(list);
	}
	/**
	 * 用户根据用户名查询文件
	 * @param username 用户名
	 * @return 对应文件列表
	 */
	@GetMapping("/files/{username}")
	@ResponseBody
	public RestBean<List<UserFile>> getUserFiles(@PathVariable String username) {
		List<UserFile> list = userFileService.getFilesByUserName(username);
		return RestBean.success(list);
	}
	/**
	 * 根据登录信息获取本人上传文件
	 * @return 对应文件列表
	 */
	@GetMapping("/files/thisUser")
	@ResponseBody
	public RestBean<List<UserFile>> getThisUserFiles(HttpServletRequest request) {
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		List<UserFile> list = userFileService.getFilesByUserName(userName);
		return RestBean.success(list);
	}
	/**
	 * 根据文件id获得对应文件
	 * @param fileId 文件编号
	 * @return 对应文件
	 */
	@GetMapping("/file")
	@ResponseBody
	public RestBean<UserFile> getThisFileById(@RequestParam Integer fileId) {
		UserFile file = userFileService.getById(fileId.toString());
		return RestBean.success(file);
	}
	/**
	 * 根据文件关键字检索对应文件
	 * @param keywords 关键字
	 * @return 对应文件列表
	 */
	@GetMapping("/fileMatchingQuery/{keywords}")
	@ResponseBody
	public RestBean<List<FileIndex>> getMatchingFile(@PathVariable String keywords) {
		List<FileIndex> fileIndexList=fileIndexService.matchingQuery(keywords);
		return RestBean.success(fileIndexList);
	}
	/**
	 * 根据文件id获得下载文件
	 * @param fileId 文件id
	 * @return 下载结果
	 */
	@GetMapping("/downloadFile")
	public ResponseEntity<org.springframework.core.io.Resource> downloadFile(Integer fileId) throws IOException {
		org.springframework.core.io.Resource resource = userFileService.downloadUserFile(fileId);
		// 设置Content-Disposition头部信息，指定文件名
		String originalFilename = Objects.requireNonNull(resource.getFilename());
		String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFilename);
		headers.add(HttpHeaders.CACHE_CONTROL, "no-store, no-cache"); // 添加缓存控制选项
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}
	/**
	 * 更改文件权限
	 * @return 更改结果
	 */
    @PostMapping("/setFileState")
	public RestBean<Void> setFileState(@RequestBody Map<String, Object> requestBody){
		String state = (String) requestBody.get("state");
		Integer id = (Integer) requestBody.get("id");
		return this.messageHandle(() ->
				userFileService.setFileState(id, state));
	}
	/**
	 * 收藏文件
	 * @return 收藏结果
	 */
	@PostMapping("/favoriteFile")
	public  RestBean<Void> favorite(HttpServletRequest request,
									@RequestBody Map<String, Integer> requestBody){
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		Integer id = requestBody.get("id");
		return this.messageHandle(() ->
				favoriteFileService.favoriteFiles(userName,id));
	}
	/**
	 * 取消收藏文件
	 * @return 取消收藏结果
	 */
	@PostMapping("/undoFavorite")
	public  RestBean<Void> undoFavorite(@RequestBody Map<String, Integer> requestBody){
		Integer id = requestBody.get("id");
		return this.messageHandle(() ->
				favoriteFileService.undoFavorite(id));
	}
	/**
	 * 获取该用户所有收藏文件
	 * @return 收藏文件列表
	 */
	@GetMapping("/getAllFavoriteFile")
	@ResponseBody
	public  RestBean<List<UserFile>> getAllFavorite(HttpServletRequest request){
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		List<UserFile> list = userFileService.selectUserFFilesByUsername(userName);
		return RestBean.success(list);
	}
	/**
	 * 获取该用户所有收藏文件
	 * @return 收藏文件列表，以FavouriteFile格式
	 */
	@GetMapping("/getFFile")
	@ResponseBody
	public  RestBean<List<FavoriteFile>> getFFile(HttpServletRequest request){
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		List<FavoriteFile> list = favoriteFileService.getFFile(userName);
		return RestBean.success(list);
	}
	/**
	 * 点赞文件
	 * @return 点赞结果
	 */
	@PostMapping("/likeFile")
	public  RestBean<Void> like(HttpServletRequest request,
									@RequestBody Map<String, Integer> requestBody){
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		Integer id = requestBody.get("id");
		return this.messageHandle(() ->
				likeFileService.likeFile(userName,id));
	}
	/**
	 * 取消点赞文件
	 * @return 取消点赞结果
	 */
	@PostMapping("/undoLike")
	public  RestBean<Void> undoLike(@RequestBody Map<String, Integer> requestBody){
		Integer id = requestBody.get("id");
		return this.messageHandle(() ->
				likeFileService.undoLike(id));
	}
	/**
	 * 获取该用户所有点赞文件
	 * @return 点赞文件列表
	 */
	@GetMapping("/getAllLikes")
	@ResponseBody
	public  RestBean<List<UserFile>> getAllLikes(HttpServletRequest request){
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		List<UserFile> list = userFileService.selectUserLFilesByUsername(userName);
		return RestBean.success(list);
	}
	/**
	 * 获取该用户所有点赞文件
	 * @return 点赞文件列表，以LikeFile格式
	 */
	@GetMapping("/getLFile")
	@ResponseBody
	public  RestBean<List<LikeFile>> getLFile(HttpServletRequest request){
		DecodedJWT jwt = utils.resolveJwt(request.getHeader("Authorization"));
		String userName = utils.toUser(jwt).getUsername();
		List<LikeFile> list = likeFileService.getLFile(userName);
		return RestBean.success(list);
	}
	private RestBean<Void> messageHandle(Supplier<String> action) {
		String message = action.get();
		return message == null ? RestBean.success() : RestBean.failure(400, message);
	}
}
