package com.item_backend.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface NoticeDao {

     Map findNotice(int pageNum, int total);
}
