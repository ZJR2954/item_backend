package com.item_backend.service;

import com.item_backend.model.entity.Notice;
import com.item_backend.model.entity.PageQueryInfo;

import java.util.List;
import java.util.Map;

public interface NoticeService {
    List<Notice> getNoticeList(PageQueryInfo pageQueryInfo, Integer SchoolId);
    Map saveNoticeService(Notice notice);
    Map deleteNoticeByIdService(Integer id);
    int getSchoolIdForNotice(Notice notice);
    int getSchoolIdFromNid(Integer u_id);
}
