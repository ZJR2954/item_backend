package com.item_backend.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-28 23:47
 */
@Data
@ToString
public class TeacherDto {

    private Integer u_id; // 用户唯一标识

    private Integer u_type; // 用户类型

    private Integer operate_subject; // 操作学科id

    private Integer school_id; // 所属学校id

    private Integer faculty_id; // 所属院系id

    private Boolean u_state; // 用户状态（教师仅两种）

    private String u_type_name; // 用户类型名

    private String subject_name; // 操作学科名称

    private String u_school; // 所属学校名

    private String u_faculty; // 所属院系名

    private String job_number; // 工号

    private String name; // 姓名

    private String id_number; // 身份证号

    private String email; // 邮箱

    private String telephone; // 手机号
}
