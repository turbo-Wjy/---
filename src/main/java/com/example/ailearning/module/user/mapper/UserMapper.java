package com.example.ailearning.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ailearning.module.user.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    List<String> selectMenuCodesByUserId(@Param("userId") Long userId);
}
