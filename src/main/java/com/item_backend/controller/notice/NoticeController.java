package com.item_backend.controller.notice;

import com.github.pagehelper.PageHelper;
import com.item_backend.config.RedisConfig;

import com.item_backend.model.entity.Notice;
import com.item_backend.model.entity.PageQueryInfo;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.NoticeServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
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
   @Cacheable(cacheNames = RedisConfig.REDIS_NOTICE,key ="#SchoolId+''+#pageQueryInfo.pageNum+''+#pageQueryInfo.pageSize" )
    public Result getNoticeList(PageQueryInfo pageQueryInfo, @PathVariable("school_id") Integer SchoolId) {
        if (SchoolId!=null) {
            if (SchoolId > 0) {
                PageHelper.startPage(pageQueryInfo.getPageNum(), pageQueryInfo.getPageSize());
                List<Notice> managerNoticeList = noticeServiceImp.getManagerNoticeService(SchoolId);
                System.out.println("managerNoticeList---size-->"+managerNoticeList.size());
                //超级管理与只保留前三条信息
                PageHelper.startPage(1, 3);
                List<Notice> superManagerNoticeList = noticeServiceImp.getSuperManagerNoticeService(0);
                System.out.println("managerNoticeList---size--->"+superManagerNoticeList.size());
                Map map=new HashMap();
                map.put("managerNoticeList",managerNoticeList);
                map.put("superManagerNoticeList",superManagerNoticeList);
                return new Result(StatusCode.OK,"获取消息成功",map);
            }
        }
            return null;
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
       Integer school_id= noticeServiceImp.getSchoolIdForNoticeUidService(notice);
       Integer changerrow;
       String msg="保存了0条消息";
       if (school_id!=null){
           if (school_id>0){
             changerrow=  noticeServiceImp.saveManagerNoticeService(notice);
           }else {
             changerrow=  noticeServiceImp.saveSuperManagerNoticeService(notice);
           }
           if (changerrow>0){
               msg="保存了"+changerrow+"行消息";
           }
           /*保存数据时需要清空 notice 对应的缓存区*/
           myCacheManager.getCache(RedisConfig.REDIS_NOTICE).clear();
           return new Result(StatusCode.OK,msg,null);
       }
        return new Result(StatusCode.ERROR,"消息不属于任何学校",null);
    }


    @ApiOperation("通过公告id删除公告")
    @DeleteMapping("/deleteNotice/{id}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_id", value = "消息id", defaultValue = "42"),
    })
    @Transactional
    public Result deleteNoticeById(@PathVariable("id") int n_id) {
            Integer school_id=noticeServiceImp.getSchoolIdFromNidService(n_id);
             Integer changerrow;
            String msg="删除了0条消息";
            if (school_id!=null){
                if (school_id>0){
                    changerrow=  noticeServiceImp.deleteManagerNoticeByNoticeIdService(n_id);
                }else {
                    changerrow=  noticeServiceImp.deleteSuperManagerNoticeByNoticeIdService(n_id);
                }
                if (changerrow>0){
                    msg="删除了"+changerrow+"行消息";
                }
                /*删除数据时需要清空 notice 对应的缓存区*/
                myCacheManager.getCache(RedisConfig.REDIS_NOTICE).clear();
                return new Result(StatusCode.OK,msg,null);
            }

        return new Result(StatusCode.ERROR,"删除失败--消息不属于学校",null);
    }
}
