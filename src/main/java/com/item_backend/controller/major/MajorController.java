package com.item_backend.controller.major;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.dto.MajorDto;
import com.item_backend.model.entity.Major;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.MajorServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/17
 * @Description TODO
 **/
@Api(tags = "Major_api", description = "Major_api", basePath = "/major")
@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    MajorServiceImpl majorService;

    @Autowired
    FormatUtil formatUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    HttpServletRequest request;

    /**
     * 根据院系id查询专业列表
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "根据院系id查询专业列表", notes = "Result：状态码+msg+(data)", httpMethod = "GET")
    @GetMapping("/major_list/{faculty_id}")
    public Result majorList(@PathVariable("faculty_id") Integer faculty_id) throws JsonProcessingException {
        if (!formatUtil.checkObjectNull(faculty_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        List<MajorDto> majorDtoList = majorService.searchMajorListByFacultyId(faculty_id);
        return Result.create(StatusCode.OK, "查询成功", majorDtoList);
    }

    /**
     * 添加专业
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "添加专业", notes = "Result：状态码+msg+(data)", httpMethod = "POST")
    @PostMapping("/add_major/{faculty_id}")
    public Result addMajor(HttpServletRequest request, @PathVariable("faculty_id") Integer faculty_id, @RequestBody Major major) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "院级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if (!formatUtil.checkObjectNull(faculty_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!majorService.addMajor(faculty_id, major)) {
            return Result.create(StatusCode.ERROR, "添加失败");
        }
        return Result.create(StatusCode.OK, "添加成功");
    }

    /**
     * 根据专业id删除专业
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "删除专业", notes = "Result：状态码+msg+(data)", httpMethod = "DELETE")
    @DeleteMapping("/delete_major/{faculty_id}/{major_id}")
    public Result deleteMajor(HttpServletRequest request, @PathVariable("faculty_id") Integer faculty_id, @PathVariable("major_id") Integer major_id) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request, "院级管理员")) {
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if (!formatUtil.checkObjectNull(major_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if (!majorService.deleteMajorByMajorId(faculty_id, major_id)) {
            return Result.create(StatusCode.ERROR, "删除失败");
        }
        return Result.create(StatusCode.OK, "删除成功");
    }
}
