package com.imooc.controller;

import com.imooc.pojo.User;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;



@RestController
@RequestMapping("fdfs")
public class CenterUserController extends BaseController {

    @Autowired
    private FdfsService fdfsService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private CenterUserService centerUserService;

    @PostMapping("uploadFace")
    public IMOOCJSONResult uploadFace(
            String userId,
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // .sh .php

        // 定义头像保存的地址
//        String fileSpace = IMAGE_USER_FACE_LOCATION;
        // 在路径上为每一个用户增加一个userid，用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;
        // 开始文件上传
        String path = null;
        if (file != null) {
            // 获得文件上传的文件名称
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                // 文件重命名  imooc-face.png -> ["imooc-face", "png"]
                String fileNameArr[] = fileName.split("\\.");
                // 获取文件的后缀名
                String suffix = fileNameArr[fileNameArr.length - 1];

                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg") ) {
                    return IMOOCJSONResult.errorMsg("图片格式不正确！");
                }
                //fastdfs 实现
//                path = fdfsService.upload(file, suffix);
                //oss实现
                path = fdfsService.uploadOSS(file, userId, suffix);
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空！");
        }
        if (StringUtils.isNoneBlank(path)) {
//            String finalUserFaceUrl = fileResource.getHost() + path;
            String finalUserFaceUrl = fileResource.getOssHost() + path;
            // 更新用户头像到数据库
            User userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);
            //增加令牌token，会整合进redis，分布式会话
            UsersVO usersVO = convertUsersVO(userResult);
            CookieUtils.setCookie(request, response, "user",
                    JsonUtils.objectToJson(usersVO), true);
        } else  {
            return IMOOCJSONResult.errorMsg("上传头像失败！");
        }
        return IMOOCJSONResult.ok();
    }
}
