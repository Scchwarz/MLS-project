package com.edu.zjut.mlsprojectbackend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 数据库中的用户文件信息
 */
@Data
@TableName("mlsfile")
@NoArgsConstructor
@AllArgsConstructor
public class UserFile {
	@TableId(value = "fileId", type = IdType.AUTO)
	Integer fileId;
	@TableField("fileName")
	String fileName;
	@TableField("path")
	String path;
	@TableField("size")
	long size;
	@TableField("downloadCounts")
	Integer downloadCounts;
	@TableField("uploadTime")
	Date uploadTime;
	@TableField("userName")
	String userName;
    //文件权限
	@TableField("state")
	String state;
	//文件是否审核
	@TableField("checked")
	String checked;
	@TableField("fav_count")
	Integer favCounts;
	@TableField("like_count")
	Integer likeCounts;
}
