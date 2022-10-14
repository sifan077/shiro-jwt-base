package com.sifan.basis.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifan.basis.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 思凡
 * @description 针对表【user(用户表)】的数据库操作Mapper
 * @createDate 2022-10-12 16:04:33
 * @Entity com.sifan.study.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT NAME FROM role WHERE id IN (SELECT rid FROM role_user" +
            " WHERE uid=(SELECT id FROM USER WHERE NAME =#{principal}))")
    List<String> getUserRoleInfoMapper(@Param("principal") String
                                               principal);

    List<String> getUserPermissionInfoMapper(@Param("roles") List<String> roles);
}




