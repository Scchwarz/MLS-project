package com.edu.zjut.mlsprojectbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.zjut.mlsprojectbackend.entity.dto.UserFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface UserFileMapper extends BaseMapper<UserFile> {
    @Select("SELECT f.*, u.username " +
            "FROM mlsfile f " +
            "JOIN mlsff ff ON f.fileId = ff.fileId " +
            "JOIN mlsaccount u ON ff.username = u.username " +
            "WHERE u.username = #{username}")
    List<UserFile> selectUserFFilesByUsername(@Param("username") String username);

    @Select("SELECT f.*, u.username " +
            "FROM mlsfile f " +
            "JOIN mlslf lf ON f.fileId = lf.fileId " +
            "JOIN mlsaccount u ON lf.username = u.username " +
            "WHERE u.username = #{username}")
    List<UserFile> selectUserLFilesByUsername(@Param("username") String username);
}
