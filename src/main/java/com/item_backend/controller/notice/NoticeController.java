package com.item_backend.controller.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item_backend.config.RedisConfig;
import com.item_backend.mapper.NoticeMapper;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
* 在数据库中好区别，设定超级管理员的学校id 为0
* */

@Api("NoticeController")
@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    NoticeMapper noticeMapper;

    @Resource
    NoticeServiceImp noticeServiceImp;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @ApiOperation("获取通知消息")
    @GetMapping("/getNoticeList/{school_id}")//+ shcollid 地址栏上不用驼峰
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "查询关键字", defaultValue = ""),
            @ApiImplicitParam(name = "pageNum", value = "页号", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "8")
    })
    /*自动封装，如果时字符串没有找到，就是空串， 如果是其他数据类型，没有就是null*/
    public Result getNoticeList(PageQueryInfo pageQueryInfo, @PathVariable("school_id") int SchoolId) {
        // 先查询缓存中是否存在
        if(redisTemplate.hasKey(RedisConfig.REDIS_NOTICE + SchoolId)&&redisTemplate.hasKey(RedisConfig.REDIS_NOTICE+"superManagernoticeList")){
            // 缓存中不存在，先查询所有的学科信息放入redis中
            String result= redisTemplate.opsForValue().get(RedisConfig.REDIS_NOTICE + SchoolId);
            try {

                int pagenum=pageQueryInfo.getPageNum();
                int pagesize=pageQueryInfo.getPageSize();


                List managerNoticeList=  objectMapper.readValue(result,List.class);
                List partManagerNoticeList=null;
                /*如果超过界限 则返回最后的数据*/
                if (pagenum*pagesize>managerNoticeList.size()+1){
                     partManagerNoticeList =managerNoticeList.subList(managerNoticeList.size()-pagesize,managerNoticeList.size());
                }else {
                    partManagerNoticeList =managerNoticeList.subList((pagenum-1)*pagesize,pagenum*pagesize);
                }
                //在redis中分页


                String res=redisTemplate.opsForValue().get(RedisConfig.REDIS_NOTICE + "superManagernoticeList");
                List superManagernoticeList= objectMapper.readValue(res,List.class);
                //在超级管理员中分页
                List superManagerNoticeListPart;
                if (superManagernoticeList.size() > 3) {
                     superManagerNoticeListPart=superManagernoticeList.subList(0,3);
                }
                else {
                    superManagerNoticeListPart=superManagernoticeList.subList(0,superManagernoticeList.size());
                }


              Map map=new HashMap();
              map.put("managerNoticeList",partManagerNoticeList);
              map.put("superManagernoticeList",superManagerNoticeListPart);
               return new Result(StatusCode.OK,"获取消息成功",map);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        List<Notice> ManagerNotice = noticeServiceImp.getNoticeList(pageQueryInfo,SchoolId);
        List<Notice> superManagerNotice=noticeServiceImp.getSuperManagerNotice();

        Map map=new HashMap();
        map.put("managerNoticeList",ManagerNotice);
        map.put("superManagernoticeList",superManagerNotice);

        try {
            String strManger= objectMapper.writeValueAsString(ManagerNotice);
            String strSuperManager=objectMapper.writeValueAsString(superManagerNotice);

            redisTemplate.opsForValue().set(RedisConfig.REDIS_NOTICE + SchoolId,strManger);
            redisTemplate.opsForValue().set(RedisConfig.REDIS_NOTICE+"superManagernoticeList",strSuperManager);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new Result(StatusCode.OK,"获取成功",map);
    }


    @ApiOperation("添加消息")
    @PostMapping("/saveNotice")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_title", value = "消息标题", defaultValue = "title"),
            @ApiImplicitParam(name = "n_content", value = "消息内容", defaultValue = "good good stydy"),
            @ApiImplicitParam(name = "u_name", value = "提交消息的用户", defaultValue = "hahaha"),
            @ApiImplicitParam(name = "u_id", value = "用户id", defaultValue = "4")
    })

    //@requestBody
    public Map saveNotice(@RequestBody Notice notice) {
        System.out.println(notice);

        Map data = noticeServiceImp.saveNoticeService(notice);
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        notice.setPublish_time(dateFormat.format(new Date()));

      int school_id = noticeServiceImp.getSchoolIdForNotice(notice);
      if (school_id > 0 && redisTemplate.hasKey(RedisConfig.REDIS_NOTICE + school_id)){
          try {
              List<Notice> managerNotice=objectMapper.readValue(redisTemplate.opsForValue().get(RedisConfig.REDIS_NOTICE + school_id),List.class);
              managerNotice.add(notice);

              Collections.sort(managerNotice, new Comparator<Notice>() {
                  @Override
                  public int compare(Notice o1, Notice o2) {
                      //返回正值是代表左侧日期大于参数日期
                      int flag = o1.getPublish_time().compareTo(o2.getPublish_time());
                      if (flag > 0)
                          return -1;
                      return 1;
                  }
              });

              String managerNoticestr=  objectMapper.writeValueAsString(managerNotice);
              redisTemplate.opsForValue().set(RedisConfig.REDIS_NOTICE + school_id,managerNoticestr);

          } catch (JsonProcessingException e) {
              e.printStackTrace();
          }
      }else {
          try {
              if (redisTemplate.hasKey(RedisConfig.REDIS_NOTICE+"superManagernoticeList")){
                  List<Notice> SupermanagerNotice=objectMapper.readValue(redisTemplate.opsForValue().get(RedisConfig.REDIS_NOTICE+"superManagernoticeList"),List.class);
                  SupermanagerNotice.add(notice);

                  Collections.sort(SupermanagerNotice, new Comparator<Notice>() {
                      @Override
                      public int compare(Notice o1, Notice o2) {
                          //返回正值是代表左侧日期大于参数日期
                          int flag = o1.getPublish_time().compareTo(o2.getPublish_time());
                          if (flag > 0)
                              return -1;
                          return 1;
                      }
                  });

                  String SupermanagerNoticeStr=  objectMapper.writeValueAsString(SupermanagerNotice);
                  redisTemplate.opsForValue().set(RedisConfig.REDIS_NOTICE+"superManagernoticeList",SupermanagerNoticeStr);
              }
          } catch (JsonProcessingException e) {
              e.printStackTrace();
          }
      }
        return data;
    }


    @ApiOperation("通过公告id删除公告")
    @DeleteMapping("/deleteNotice/{id}")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "n_id", value = "消息id", defaultValue = "42"),
    })

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
        }
        return data;
    }
}
