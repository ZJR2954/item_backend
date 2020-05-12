package com.item_backend.mapper;

import com.item_backend.model.entity.UserType;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeMapper {

    // 根据类型id查询UserType
    UserType searchUserTypeByUType(Integer u_type);
}
