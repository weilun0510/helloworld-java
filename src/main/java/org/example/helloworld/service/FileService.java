package org.example.helloworld.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件到阿里云 OSS
     * 
     * @param file 上传的文件
     * @return 文件访问 URL
     * @throws Exception 上传异常
     */
    String upload(MultipartFile file) throws Exception;
    
    /**
     * 批量上传文件
     * 
     * @param files 文件数组
     * @return 文件访问 URL 列表
     * @throws Exception 上传异常
     */
    List<String> batchUpload(MultipartFile[] files) throws Exception;
    
    /**
     * 删除文件
     * 
     * @param fileUrl 文件 URL
     * @throws Exception 删除异常
     */
    void delete(String fileUrl) throws Exception;
}

