package com.item_backend.service.impl;
import com.item_backend.mapper.NoticeMapper;
import com.item_backend.model.entity.Notice;

import com.item_backend.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;


@Service
public class NoticeServiceImp implements NoticeService {

    @Resource
    NoticeMapper noticeMapper;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Override
    public Integer saveManagerNoticeService(Notice notice) {
        return  noticeMapper.saveManagerNotice(notice);
    }

    @Override
    public Integer saveSuperManagerNoticeService(Notice notice) {
         return  noticeMapper.saveSuperManagerNotice(notice);
    }

    @Override
    public Integer deleteManagerNoticeByNoticeIdService(Integer n_id) {
        return noticeMapper.deleteManagerNoticeByNoticeId(n_id);
    }

    @Override
    public Integer deleteSuperManagerNoticeByNoticeIdService(Integer n_id) {
        return noticeMapper.deleteSuperManagerNoticeByNoticeId(n_id);
    }


    @Override
    public List<Notice> getSuperManagerNoticeService(Integer school_id) {
        System.out.println("-------->SuperManagerNoticeService");
       return noticeMapper.getSuperManagerNotice(school_id);
    }

    @Override
    public List<Notice> getManagerNoticeService(Integer school_id) {
        System.out.println("-------->ManagerNoticeService");
        return noticeMapper.getManagerNotice(school_id);
    }

    @Override
    public Integer getSchoolIdForNoticeUidService(Notice notice) {
        return noticeMapper.getSchoolIdForNoticeUid(notice);
    }

    @Override
    public Integer getSchoolIdFromNidService(Integer n_id) {
        return noticeMapper.getSchoolIdFromNid(n_id);
    }

}
