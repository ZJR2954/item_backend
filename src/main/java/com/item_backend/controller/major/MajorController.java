package com.item_backend.controller.major;

import com.item_backend.model.entity.Major;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.MajorServiceImpl;
import com.item_backend.utils.FormatUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    MajorServiceImpl majorServiceImpl;

    @Autowired
    FormatUtil formatUtil;

    /**
     * 添加专业
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "添加专业", notes = "Result：状态码+msg+(data)", httpMethod = "POST")
    @PostMapping("/save")
    public Result saveMajor(HttpServletRequest request, @RequestBody Major major) {
        String token = request.getHeader("token");
        if (!formatUtil.checkStringNull(major.getMajor_name())) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map map = majorServiceImpl.saveMajor(token, major);
            if (map.containsKey("msg")) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "添加专业成功", map);
        } catch (Exception e) {
            return Result.create(StatusCode.ERROR, e.getMessage());
        }
    }

    /**
     * 根据院系id查询专业列表
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "根据院系id查询专业列表", notes = "Result：状态码+msg+(data)", httpMethod = "GET")
    @GetMapping("/majors/{faculty_id}")
    public Result majorList(@PathVariable("faculty_id") String faculty_id) {
        if (!formatUtil.checkStringNull(faculty_id)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map map = majorServiceImpl.searchMajorByFacultyId(Integer.parseInt(faculty_id));
            if (map.containsKey("msg")) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "查询专业列表成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    /**
     * 根据专业id删除专业
     *
     * @param
     * @return Result：状态码+msg+(data)
     * @Author xiao
     */
    @ApiOperation(value = "根据专业id删除专业", notes = "Result：状态码+msg+(data)", httpMethod = "DELETE")
    @DeleteMapping("/delete")
    public Result deleteMajor(HttpServletRequest request, @RequestBody Major major) {
        String token = request.getHeader("token");
        if (!formatUtil.checkStringNull(major.getMajor_id().toString())) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map map = majorServiceImpl.deleteMajorByMajorId(token, major.getMajor_id());
            if (map.containsKey("msg")) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "删除专业成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }
}
