package org.example.helloworld.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    // MultipartFile 可以获取表单enctype(编码类型)为"multipart/form-data"的文件
    public String up(String nickname, MultipartFile photo, HttpServletRequest request) throws IOException {
        System.out.println(nickname);
        // 获取文件原始名称
        String fileName = photo.getOriginalFilename();
        System.out.println(fileName);

        // 获取图片的后缀
        // String ext = fileName.substring(fileName.lastIndexOf("."));

        // 获取文件类型
        String type = photo.getContentType();
        System.out.println(type);

        // 获取tomcat分配的动态存储路径（云端也适用）
        String path = request.getServletContext().getRealPath("/upload/");
        System.out.println(path);

        saveFile(photo, path);
        return "上传成功";
    }

    public void saveFile(MultipartFile photo, String path) throws IOException {
        // 判断存储的目录是否存在，如果不存在则创建
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println(path);
        // 获取文件完整路径，并通过MultipartFile.transferTo保存网络文件
        File file = new File(path + photo.getOriginalFilename());
        photo.transferTo(file);
    }
}
