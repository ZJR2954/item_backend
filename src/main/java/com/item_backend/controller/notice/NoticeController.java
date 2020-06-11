package com.item_backend.controller.notice;

import com.item_backend.model.entity.Notice;
import com.item_backend.model.entity.PageQueryInfo;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.NoticeServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.*;

/*
* 在数据库中好区别，设定超级管理员的学校id 为0
* */

@Api("NoticeController")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    NoticeServiceImp noticeServiceImp;

    @Qualifier("myCacheManager")
    @Autowired
    CacheManager myCacheManager;

    @ApiOperation("获取通知消息")
    @GetMapping("/getNoticeList/{school_id}")//+ shcollid 地址栏上不用驼峰
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询关键字", defaultValue = ""),
            @ApiImplicitParam(name = "pageNum", value = "页号", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "8")
    })

    /*自动封装，如果时字符串没有找到，就是空串， 如果是其他数据类型，没有就是null*/
    @Transactional
    public Result getNoticeList(PageQueryInfo pageQueryInfo, @PathVariable("school_id") Integer SchoolId) {
        System.out.println("schoolid----->"+SchoolId);
      Map map =  noticeServiceImp.getNoticeService(pageQueryInfo,SchoolId);
      PageResult<Notice> managerNoticePageResult= (PageResult<Notice>) map.get("managerNoticeList");
        String msg="获取消息成功";
        if (managerNoticePageResult==null){
            msg="获取用户消息失败,可能的原因是学校不存在";
            return new Result(StatusCode.ERROR,msg);
        }
        /*查到学校，但是该学校一条消息都没有的话，就返回一个空的[] managerNoticeList*/
        return new Result(StatusCode.OK,msg,map);
    }

    @ApiOperation("添加消息")
    @PostMapping("/saveNotice")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_title", value = "消息标题", defaultValue = "title"),
            @ApiImplicitParam(name = "n_content", value = "消息内容", defaultValue = "good good stydy"),
            @ApiImplicitParam(name = "u_name", value = "提交消息的用户", defaultValue = "hahaha"),
            @ApiImplicitParam(name = "u_id", value = "用户id", defaultValue = "4")
    })
    @Transactional
    public Result saveNotice(@RequestBody Notice notice) {
     Integer changeRow= noticeServiceImp.saveNoticeService(notice);
     String msg="消息保存成功";
     if (changeRow>0)
        return new Result(StatusCode.OK,msg);
     else {
         msg="消息保存失败，可能的原因是该用户不属于任何学校";
         return new Result(StatusCode.ERROR,msg);
     }
    }


    @ApiOperation("通过公告id删除公告")
    @DeleteMapping("/deleteNotice/{id}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_id", value = "消息id", defaultValue = "42"),
    })
<<<<<<< HEAD

    public Map deleteNoticeById(@PathVariable("id") int n_id) {
        System.out.println("n_id====>"+n_id);
        //要在删除消息之前获取到学校的id
        Integer schoolId = noticeServiceImp.getSchoolIdFromNid(n_id);

        Map data = noticeServiceImp.deleteNoticeByIdService(n_id);
        //更新超级管理员的redis
         List<Notice> superManageNotice= noticeServiceImp.getSuperManagerNotice();
        try {
          String superManageNoticeStr=  objectMapper.writeValueAsString(superManageNotice);
            redisTemplate.opsForValue().set(RedisConfig.REDIS_NOTICE+"superManagernoticeList",superManageNoticeStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

       List<Notice> ManagerNotices= noticeMapper.findManagerNotice(schoolId);

        try {
            String ManagerNoticesStr=  objectMapper.writeValueAsString(ManagerNotices);
            redisTemplate.opsForValue().set(RedisConfig.REDIS_NOTICE + schoolId,ManagerNoticesStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
=======
    @Transactional
    public Result deleteNoticeById(@PathVariable("id") int n_id) {
       Integer changeRow =  noticeServiceImp.deleteNoticeService(n_id);
       String msg  = "消息删除成功";
        if (changeRow>0)
            return new Result(StatusCode.OK,msg);
        else {
            msg="消息删除失败，可能的原因是该用户不属于任何学校";
            return new Result(StatusCode.ERROR,msg);
>>>>>>> e24a25c206b887b2ac8f2a39ec548e5157ee4c85
        }
    }
}
