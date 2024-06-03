package com.edu.zjut.mlsprojectbackend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库中的点赞文件信息
 */
@Data
@TableName("mlsLF")
@NoArgsConstructor
@AllArgsConstructor
public class LikeFile {
    @TableId(value = "lfId", type = IdType.AUTO)
    Integer lfId;
    @TableField(value = "username")
    String userName;
    @TableField(value = "fileId")
    Integer fileId;
}
