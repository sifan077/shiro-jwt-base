package com.sifan.basis.service;

import com.sifan.basis.domain.Utoken;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 思凡
* @description 针对表【utoken】的数据库操作Service
* @createDate 2022-10-14 17:10:33
*/
public interface UtokenService extends IService<Utoken> {
    String getToken(String username);
    void clearToken(String username);
}
