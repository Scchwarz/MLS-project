package com.edu.zjut.mlsprojectbackend.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * 用户信息重置实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {
    Integer id;
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    String username;
    @Email
    String email;
    String role;
    String phone;
    String avatarUrl;
}
