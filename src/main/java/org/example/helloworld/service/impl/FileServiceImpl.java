package org.example.helloworld.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.example.helloworld.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 * 使用阿里云 OSS 存储
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${aliyun.oss.endpoint:}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName:}")
    private String bucketName;

    @Value("${aliyun.oss.urlPrefix:}")
    private String urlPrefix;

    /**
     * 上传文件到阿里云 OSS
     * 
     * @param file 上传的文件
     * @return 文件访问 URL
     * @throws Exception 上传异常
     */
    @Override
    public String upload(MultipartFile file) throws Exception {
        // 文件类型验证（业务规则）
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            throw new IllegalArgumentException("不支持的文件类型：" + contentType);
        }

        // 检查配置
        if (endpoint == null || endpoint.trim().isEmpty()) {
            // 如果未配置 OSS，使用本地存储（开发环境）
            return uploadToLocal(file);
        }

        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unnamed";
            }

            // 生成唯一文件名：UUID + 原始文件扩展名
            String extension = "";
            int lastDot = originalFilename.lastIndexOf('.');
            if (lastDot > 0) {
                extension = originalFilename.substring(lastDot);
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

            // 按日期分文件夹：uploads/2025/11/03/xxx.jpg
            String datePath = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date());
            String objectName = "uploads/" + datePath + "/" + fileName;

            // 上传文件
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            ossClient.putObject(putObjectRequest);

            // 返回文件访问 URL
            String url;
            if (urlPrefix != null && !urlPrefix.trim().isEmpty()) {
                // 使用自定义域名
                url = urlPrefix + "/" + objectName;
            } else {
                // 使用默认域名
                url = "https://" + bucketName + "." + endpoint + "/" + objectName;
            }

            return url;

        } finally {
            // 关闭 OSS 客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 批量上传文件
     * 
     * @param files 文件数组
     * @return 文件访问 URL 列表
     * @throws Exception 上传异常
     */
    @Override
    public List<String> batchUpload(MultipartFile[] files) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                // upload 方法内部已包含文件类型验证
                String url = upload(file);
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * 检查文件类型是否允许（业务规则）
     * 
     * @param contentType 文件类型
     * @return 是否允许
     */
    private boolean isAllowedType(String contentType) {
        return contentType.startsWith("image/") ||
                contentType.startsWith("video/") ||
                contentType.startsWith("audio/") ||
                contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                ||
                contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    /**
     * 删除文件
     * 
     * @param fileUrl 文件 URL
     * @throws Exception 删除异常
     */
    @Override
    public void delete(String fileUrl) throws Exception {
        // 检查配置
        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new RuntimeException("未配置阿里云OSS");
        }

        // 从 URL 中提取 objectName
        String objectName = extractObjectName(fileUrl);
        if (objectName == null) {
            throw new RuntimeException("无效的文件URL");
        }

        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除文件
            ossClient.deleteObject(bucketName, objectName);
        } finally {
            // 关闭 OSS 客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从 URL 中提取 objectName
     * 
     * @param fileUrl 文件 URL
     * @return objectName
     */
    private String extractObjectName(String fileUrl) {
        try {
            // 示例 URL:
            // https://bucket.oss-cn-hangzhou.aliyuncs.com/uploads/2025/11/03/xxx.jpg
            // 提取: uploads/2025/11/03/xxx.jpg
            int index = fileUrl.indexOf(".com/");
            if (index > 0) {
                return fileUrl.substring(index + 5);
            }
            // 自定义域名: https://cdn.example.com/uploads/2025/11/03/xxx.jpg
            index = fileUrl.indexOf("/uploads/");
            if (index > 0) {
                return fileUrl.substring(index + 1);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 上传到本地（开发环境使用）
     * 
     * @param file 上传的文件
     * @return 文件访问 URL
     * @throws Exception 上传异常
     */
    private String uploadToLocal(MultipartFile file) throws Exception {
        // 这里返回一个模拟的 URL
        // 实际项目中可以实现本地文件存储逻辑
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;
        return "http://localhost:8080/uploads/" + fileName;
    }
}
