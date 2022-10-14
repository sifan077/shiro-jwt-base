package com.sifan.basis.controller;

import com.sifan.basis.common.Result;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/delete")
    @RequiresRoles(value = {"admin"})
    public Object deleteUser(ModelMap model) {
        return Result.success("用户删除成功！");
    }

    @GetMapping("/edit")
    @RequiresPermissions(value = {"user:edit"})
    public Object readUser(ModelMap model) {
        return Result.success("用户修改！");
    }

}