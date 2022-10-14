package com.sifan.basis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifan.basis.domain.Utoken;
import com.sifan.basis.mapper.UtokenMapper;
import com.sifan.basis.service.UtokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 思凡
 * @description 针对表【utoken】的数据库操作Service实现
 * @createDate 2022-10-14 17:10:33
 */
@Service
public class UtokenServiceImpl extends ServiceImpl<UtokenMapper, Utoken>
        implements UtokenService {

    @Autowired
    private UtokenMapper utokenMapper;

    @Override
    public String getToken(String username) {
        LambdaQueryWrapper<Utoken> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Utoken::getUsername, username);
        Utoken utoken = utokenMapper.selectOne(lqw);
        if (utoken != null) return utoken.getToken();
        return null;
    }

    @Override
    public void clearToken(String username) {
        LambdaQueryWrapper<Utoken> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Utoken::getUsername, username);
        utokenMapper.delete(lqw);
    }
}




