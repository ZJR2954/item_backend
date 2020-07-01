package com.item_backend.exam;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface GaMapper {
    List<Questions> getOneTypeQuestions(Map map);
}
