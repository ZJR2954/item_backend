package com.item_backend.model.dto;

import com.item_backend.model.entity.Faculty;
import com.item_backend.model.entity.User;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author xiao
 * @Time 2020/5/29
 * @Description TODO
 **/
@Data
@ToString
public class FacultyDto implements Serializable {

    private static final long serialVersionUID = -4799753335901221144L;

    private Faculty faculty;

    private User user;
}
