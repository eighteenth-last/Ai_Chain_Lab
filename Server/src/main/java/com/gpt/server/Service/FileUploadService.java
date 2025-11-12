package com.gpt.server.Service;


import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: FileUpload服务接口
 * @Version: 1.0
 */
public interface FileUploadService {
    /**
     * 上传文件
     * @param folder 文件夹
     * @param file 文件
     * @return 文件访问路径
     */
    String uploadFile(String folder, MultipartFile  file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

} 