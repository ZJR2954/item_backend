package com.item_backend.controller;

import com.item_backend.model.dto.UEditorFile;
import com.item_backend.utils.UEditorUploadUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @Author xiao
 * @Time 2020/5/12
 * @Describe 与前端UEditor交互的Controller，主要用于返回后端UEditor配置给前端
 **/
@Controller
public class UEditorController {
    @Autowired
    private UEditorUploadUtil uEditorUploadUtil;

    @Value("classpath:ueditor/config.json")
    private Resource config;

    //用于给前端UEditor返回后端相应配置文件
    @ResponseBody
    @RequestMapping("/ueditor")
    public UEditorFile uEditorConfig(HttpServletRequest request, HttpServletResponse response, String action, MultipartFile upfile) throws Exception {
        if (action.equals("config")) {
            request.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "text/html");
            PrintWriter filterWriter = response.getWriter();
            String areaData = IOUtils.toString(config.getInputStream(), "UTF-8");
            filterWriter.write(areaData);
            filterWriter.flush();
            filterWriter.close();
        } else if (action.equals("uploadimage")) {
            return uEditorUploadUtil.uploadImage(upfile);
        }
        return null;
    }
}
