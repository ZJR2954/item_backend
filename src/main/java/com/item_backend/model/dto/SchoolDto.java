package com.item_backend.model.dto;

import com.item_backend.model.entity.School;
import com.item_backend.model.entity.User;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author xiao
 * @Time 2020/5/30
 * @Description TODO
 **/
@Data
@ToString
public class SchoolDto implements Serializable {

    private static final long serialVersionUID = 6081788972024663987L;

    private School school;

    private User user;
}
