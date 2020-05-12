package com.item_backend.controller;

import com.item_backend.dao.NoticeDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    NoticeDao noticeDao;

    @ApiOperation("获取通知消息")
    @PostMapping("/getNoticeList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pagenum", value = "页号", defaultValue = "1"),
            @ApiImplicitParam(name = "pagesize", value = "每页大小", defaultValue = "5")
    })
   public String getNoticeList(Integer pagenum,Integer pagesize){


       return null;

    }
}
