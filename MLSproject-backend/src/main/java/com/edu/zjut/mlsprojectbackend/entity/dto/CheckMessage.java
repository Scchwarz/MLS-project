package com.edu.zjut.mlsprojectbackend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库中的审核消息信息
 */
@Data
@TableName("mlscm")
@NoArgsConstructor
@AllArgsConstructor
public class CheckMessage {
    @TableId(value = "cm_id", type = IdType.AUTO)
    Integer id;
    @TableField(value = "m_id")
    Integer Mid;
    @TableField(value = "u_name")
    String Uname;
    @TableField(value = "f_id")
    Integer FId;
    @TableField(value = "message")
    String message;
    @TableField(value = "checked")
    String checked;
}
