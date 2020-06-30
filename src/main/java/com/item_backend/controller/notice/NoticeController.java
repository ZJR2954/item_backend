package com.item_backend.controller.notice;

import com.item_backend.model.entity.Notice;
import com.item_backend.model.entity.PageQueryInfo;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.NoticeServiceImp;
import com.item_backend.utils.FormatUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @Autowired
    FormatUtil formatUtil;

    @ApiOperation(value = "获取通知消息")
    @GetMapping("/get_notice_list/{school_id}/{pageNum}/{pageSize}")//+ shcollid 地址栏上不用驼峰
    @Transactional
    public Result getNoticeList(@PathVariable("school_id") Integer school_id, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        if (!formatUtil.checkObjectNull(school_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        PageQueryInfo pageQueryInfo = new PageQueryInfo();
        pageQueryInfo.setPageNum(pageNum);
        pageQueryInfo.setPageSize(pageSize);
        PageResult<Notice> pageResult = noticeServiceImp.getNoticeService(pageQueryInfo, school_id);
        return new Result(StatusCode.OK, "查询成功", pageResult);
    }

    @ApiOperation(value = "添加消息")
    @PostMapping("/save_notice")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_title", required = true, value = "消息标题", defaultValue = "title"),
            @ApiImplicitParam(name = "n_content", required = true, value = "消息内容", defaultValue = "good good stydy"),
            @ApiImplicitParam(name = "u_name", required = true, value = "提交消息的用户", defaultValue = "hahaha"),
            @ApiImplicitParam(name = "u_id", required = true, value = "用户id", defaultValue = "4")
    })
    @Transactional
    public Result saveNotice(@RequestBody Notice notice) {
        Integer changeRow = noticeServiceImp.saveNoticeService(notice);
        String msg = "消息保存成功";
        if (changeRow > 0)
            return new Result(StatusCode.OK, msg);
        else {
            msg = "消息保存失败，可能的原因是该用户不属于任何学校";
            return new Result(StatusCode.ERROR, msg);
        }
    }


    @ApiOperation(value = "通过公告id删除公告")
    @DeleteMapping("/delete_notice/{id}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_id", required = true, value = "消息id", defaultValue = "42"),
    })
    @Transactional
    public Result deleteNoticeById(@PathVariable("id") int n_id) {
        Integer changeRow = noticeServiceImp.deleteNoticeService(n_id);
        String msg = "消息删除成功";
        if (changeRow > 0)
            return new Result(StatusCode.OK, msg);
        else {
            msg = "消息删除失败，可能的原因是该用户不属于任何学校";
            return new Result(StatusCode.ERROR, msg);
        }
    }
}