package com.item_backend.model.dto;

import com.item_backend.model.entity.Faculty;
import com.item_backend.model.entity.Major;
import com.item_backend.model.entity.Subject;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/21
 * @Description TODO
 **/
@Data
@ToString
public class MajorDto implements Serializable {

    private static final long serialVersionUID = -5945034353009631625L;

    private Major major;

    private Faculty faculty;

    private List<Subject> subjectList;
}
