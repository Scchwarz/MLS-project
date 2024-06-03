package com.edu.zjut.mlsprojectbackend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库中的收藏文件信息
 */
@Data
@TableName("mlsFF")
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteFile {
    @TableId(value = "ffId", type = IdType.AUTO)
    Integer ffId;
    @TableField(value = "username")
    String userName;
    @TableField(value = "fileId")
    Integer fileId;
}
