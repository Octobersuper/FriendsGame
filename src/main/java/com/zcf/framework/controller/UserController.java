package com.zcf.framework.controller;


import com.zcf.framework.bean.User;
import com.zcf.framework.common.json.Body;
import com.zcf.framework.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zq123
 * @since 2019-06-11
 */
@CrossOrigin
@Controller
@ResponseBody
@RequestMapping("/user/")
public class UserController {

    @Autowired
    UserServiceImpl us;


    /*
     *@Author:ZhaoQi
     *@methodName:获取所有用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/6/12
     */
    @GetMapping("/getUsers")
    public Body getUsers(){
        List<User> list = us.selectList(null);
        if (list == null) {
            return Body.BODY_451;
        }
        return Body.newInstance(list);
    }

}

