package com.item_backend.service;

import com.item_backend.model.entity.Notice;
import com.item_backend.model.entity.PageQueryInfo;

import java.util.List;
import java.util.Map;

public interface NoticeService {


    Integer  saveManagerNoticeService(Notice notice);
    Integer  saveSuperManagerNoticeService(Notice notice);

   Integer deleteManagerNoticeByNoticeIdService(Integer id);
   Integer deleteSuperManagerNoticeByNoticeIdService(Integer id);

    List<Notice> getSuperManagerNoticeService(Integer school_id);
    List<Notice> getManagerNoticeService(Integer school_id);

    Integer getSchoolIdForNoticeUidService(Notice notice);
    Integer getSchoolIdFromNidService(Integer n_id);
}
