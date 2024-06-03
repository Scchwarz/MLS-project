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
 * 数据库中的用户信息
 */
@Data
@TableName("mlsaccount")
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	@TableId(type = IdType.AUTO)
	Integer id;
	String username;
	String password;
	String email;
	String role;
	@TableField("registerTime")
	Date registerTime;
	@TableField("phone")
	String phone;
	@TableField("avatarUrl")
	String avatarUrl;
}
