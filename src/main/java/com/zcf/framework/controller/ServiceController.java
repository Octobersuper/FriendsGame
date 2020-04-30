package com.zcf.framework.controller;

import com.zcf.framework.gameCenter.mahjong.service.FileService;
import com.zcf.framework.gameCenter.mahjong.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2019/8/17
 * Time:16:17
 */
@CrossOrigin
@Controller
@RequestMapping("service")
@ResponseBody
public class ServiceController {

    @Autowired
    ServiceService as;
    @Autowired
    FileService iFileService;

}
