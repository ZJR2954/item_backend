package com.item_backend.controller.faculty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.config.JwtConfig;
import com.item_backend.model.dto.FacultyDto;
import com.item_backend.model.entity.Faculty;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.FacultyServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author xiao
 * @Time 2020/5/27
 * @Dscription TODO
 **/
@Api(tags = "Faculty_api", description = "Faculty_api", basePath = "/faculty")
@RestController
@RequestMapping("/faculty")
public class FacultyController {
    @Autowired
    FacultyServiceImpl facultyService;

    @Autowired
    FormatUtil formatUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    HttpServletRequest request;

    /**
     * 根据学校名查询院系列表
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "根据学校名查询院系列表", notes = "Result：状态码+msg+(data)", httpMethod = "GET")
    @GetMapping("/faculty_list/{school}/{page}/{showCount}")
    public Result majorList(@PathVariable("school") String school, @PathVariable("page") Integer page, @PathVariable("showCount") Integer showCount) throws JsonProcessingException {
        if (!formatUtil.checkStringNull(school)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        PageResult<FacultyDto> pageResult = facultyService.searchFacultyListBySchoolName(school, page, showCount);
        return Result.create(StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 添加院系
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "添加院系", notes = "Result：状态码+msg+(data)", httpMethod = "POST")
    @PostMapping("/add_faculty")
    public Result addMajor(@RequestBody Faculty faculty) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "校级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if (!formatUtil.checkObjectNull(faculty)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!facultyService.addFaculty(request.getHeader(jwtConfig.getHeader()), faculty)) {
            return Result.create(StatusCode.ERROR, "添加失败");
        }
        return Result.create(StatusCode.OK, "添加成功");
    }

    /**
     * 根据院系id删除院系
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "删除院系", notes = "Result：状态码+msg+(data)", httpMethod = "DELETE")
    @DeleteMapping("/delete_faculty/{school}/{faculty_id}")
    public Result deleteMajor(@PathVariable("school") String school, @PathVariable("faculty_id") Integer faculty_id) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "校级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if (!formatUtil.checkObjectNull(faculty_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!facultyService.deleteFacultyByFacultyId(school, faculty_id)) {
            return Result.create(StatusCode.ERROR, "删除失败");
        }
        return Result.create(StatusCode.OK, "删除成功");
    }

    /**
     * 修改院系信息
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "修改院系信息", notes = "Result：状态码+msg+(data)", httpMethod = "PUT")
    @PutMapping("/update_faculty")
    public Result updateFaculty(@RequestBody Faculty faculty) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "校级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if (!formatUtil.checkObjectNull(faculty)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!facultyService.updateFaculty(faculty)) {
            return Result.create(StatusCode.ERROR, "修改失败");
        }
        return Result.create(StatusCode.OK, "修改成功");
    }
}
