package org.example.helloworld.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.helloworld.service.FileService;
import org.example.helloworld.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 * 支持阿里云 OSS 存储
 */
@Tag(name = "文件管理", description = "文件上传下载接口")
@RestController
@RequestMapping("/file")
public class FileController {

  @Autowired
  private FileService fileService;

  /**
   * 上传文件到阿里云 OSS
   * 
   * @param file 上传的文件
   * @return 文件访问 URL
   */
  @Operation(summary = "上传文件", description = "上传文件到阿里云OSS，返回文件访问URL")
  @PostMapping("/upload")
  public Result upload(@RequestParam("file") MultipartFile file) {
    // 基础参数校验
    if (file == null || file.isEmpty()) {
      return Result.error().message("请选择要上传的文件");
    }

    try {
      // 业务逻辑（包括文件类型验证）交给 Service 层处理
      String url = fileService.upload(file);
      return Result.ok()
          .message("上传成功")
          .data("url", url)
          .data("fileName", file.getOriginalFilename())
          .data("size", file.getSize());
    } catch (IllegalArgumentException e) {
      // 业务异常（如文件类型不支持）
      return Result.error().message(e.getMessage());
    } catch (Exception e) {
      // 系统异常
      e.printStackTrace();
      return Result.error().message("上传失败：" + e.getMessage());
    }
  }

  /**
   * 批量上传文件
   * 
   * @param files 上传的文件数组
   * @return 文件访问 URL 列表
   */
  @Operation(summary = "批量上传文件", description = "批量上传文件到阿里云OSS")
  @PostMapping("/batch-upload")
  public Result batchUpload(@RequestParam("files") MultipartFile[] files) {
    // 基础参数校验
    if (files == null || files.length == 0) {
      return Result.error().message("请选择要上传的文件");
    }

    try {
      java.util.List<String> urls = fileService.batchUpload(files);
      return Result.ok()
          .message("批量上传成功")
          .data("urls", urls)
          .data("count", files.length);
    } catch (IllegalArgumentException e) {
      // 业务异常
      return Result.error().message(e.getMessage());
    } catch (Exception e) {
      // 系统异常
      e.printStackTrace();
      return Result.error().message("批量上传失败：" + e.getMessage());
    }
  }

  /**
   * 删除文件
   * 
   * @param fileUrl 文件URL
   * @return 删除结果
   */
  @Operation(summary = "删除文件", description = "从阿里云OSS删除文件")
  @DeleteMapping("/delete")
  public Result delete(@RequestParam("fileUrl") String fileUrl) {
    // 基础参数校验
    if (fileUrl == null || fileUrl.trim().isEmpty()) {
      return Result.error().message("文件URL不能为空");
    }

    try {
      fileService.delete(fileUrl);
      return Result.ok().message("删除成功");
    } catch (Exception e) {
      e.printStackTrace();
      return Result.error().message("删除失败：" + e.getMessage());
    }
  }
}
