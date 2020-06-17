package com.item_backend.controller.school;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.config.JwtConfig;
import com.item_backend.model.dto.SchoolDto;
import com.item_backend.model.entity.School;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.SchoolServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author xiao
 * @Time 2020/5/30
 * @Description TODO
 **/
@Api(tags = "School_api", description = "School_api", basePath = "/school")
@RestController
@RequestMapping("/school")
public class SchoolController {
    @Autowired
    SchoolServiceImpl schoolService;

    @Autowired
    FormatUtil formatUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    HttpServletRequest request;

    /**
     * 查询学校列表
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "查询学校列表", notes = "Result：状态码+msg+(data)", httpMethod = "GET")
    @GetMapping({"/school_list/{page}/{showCount}", "/school_list"})
    public Result majorList(@PathVariable(value = "page", required = false) Integer page, @PathVariable(value = "showCount", required = false) Integer showCount) throws JsonProcessingException {
        PageResult<SchoolDto> pageResult = schoolService.searchSchoolList(page, showCount);
        return Result.create(StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 添加学校
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "添加学校", notes = "Result：状态码+msg+(data)", httpMethod = "POST")
    @PostMapping("/add_school")
    public Result addMajor(@RequestBody School school) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "超级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if (!formatUtil.checkObjectNull(school)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!schoolService.addSchool(request.getHeader(jwtConfig.getHeader()), school)) {
            return Result.create(StatusCode.ERROR, "添加失败");
        }
        return Result.create(StatusCode.OK, "添加成功");
    }

    /**
     * 根据学校id删除学校
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "删除学校", notes = "Result：状态码+msg+(data)", httpMethod = "DELETE")
    @DeleteMapping("/delete_school/{school_id}")
    public Result deleteMajor(@PathVariable("school_id") Integer school_id) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "超级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if (!formatUtil.checkObjectNull(school_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!schoolService.deleteSchoolBySchoolId(school_id)) {
            return Result.create(StatusCode.ERROR, "删除失败");
        }
        return Result.create(StatusCode.OK, "删除成功");
    }

    /**
     * 修改学校信息
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "修改学校信息", notes = "Result：状态码+msg+(data)", httpMethod = "PUT")
    @PutMapping("/update_school")
    public Result updateSchool(@RequestBody School school) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "超级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if (!formatUtil.checkObjectNull(school)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!schoolService.updateSchool(school)) {
            return Result.create(StatusCode.ERROR, "修改失败");
        }
        return Result.create(StatusCode.OK, "修改成功");
    }

}
