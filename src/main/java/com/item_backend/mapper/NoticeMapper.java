package com.item_backend.mapper;

import com.item_backend.model.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NoticeMapper {

     List<Notice> findManagerNotice(Integer schoolId);
     List<Notice> findSuperManagerNotice(Integer schoolId);

     int saveNotice(Notice notice);
     int deleteNoticeById(Integer id);
     int getSchoolIdForNotice(Notice notice);
     int getSchoolIdFromNid(Integer n_id);
}
