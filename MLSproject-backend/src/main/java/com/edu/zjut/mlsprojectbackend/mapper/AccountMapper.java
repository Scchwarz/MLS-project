package com.edu.zjut.mlsprojectbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.zjut.mlsprojectbackend.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
