package com.gpt.server.Controller;

import com.gpt.server.Common.Result;
import com.gpt.server.Config.properties.MinioProperties;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Controller
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: File控制器
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {

    @Value("${file.upload.path:./uploads/}")  // 本地文件存储路径
    private String localUploadPath;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    /**
     * 设置MinIO bucket的公共访问策略（临时方法，用于修复访问权限）
     */
    @PostMapping("/fix-minio-policy")
    public Result<String> fixMinioPolicy() {
        try {
            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [{
                            "Effect": "Allow",
                            "Principal": {"AWS": ["*"]},
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }]
                    }
                    """.formatted(minioProperties.getBucketName());
            
            minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .config(policy)
                    .build()
            );
            
            log.info("MinIO bucket策略设置成功");
            return Result.success("MinIO访问策略已设置，现在可以公开访问bucket中的文件了");
        } catch (Exception e) {
            log.error("设置MinIO bucket策略失败", e);
            return Result.error("设置失败: " + e.getMessage());
        }
    }

    /**
     * 访问上传的文件
     * @param filePath 文件路径（如：banners/2024/06/25/abc123.jpg）
     * @param response HTTP响应
     */
    @GetMapping("/**")
    public void getFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取文件路径  // 提取请求的文件路径
            String requestURI = request.getRequestURI();
            String filePath = requestURI.substring("/files/".length());
            
            // 构建完整文件路径  // 构建本地文件路径
            Path fullPath = Paths.get(localUploadPath, filePath);
            File file = fullPath.toFile();
            
            // 检查文件是否存在  // 验证文件存在
            if (!file.exists() || !file.isFile()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 检查文件是否在允许的目录内（安全检查）  // 防止路径穿越攻击
            String canonicalFilePath = file.getCanonicalPath();
            String canonicalUploadPath = new File(localUploadPath).getCanonicalPath();
            if (!canonicalFilePath.startsWith(canonicalUploadPath)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            // 设置响应头  // 配置HTTP响应
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = "application/octet-stream";  // 默认类型
            }
            response.setContentType(contentType);
            response.setContentLength((int) file.length());
            
            // 设置缓存头  // 优化缓存策略
            response.setHeader("Cache-Control", "public, max-age=86400");  // 缓存1天
            
            // 输出文件内容  // 返回文件数据
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
            
            log.debug("文件访问成功: {}", filePath);
            
        } catch (IOException e) {
            log.error("文件访问失败: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
} 