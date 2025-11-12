package com.gpt.server.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Entity.Banner;
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
 * @Description: Banner服务接口
 * @Version: 1.0
 */
public interface BannerService extends IService<Banner> {

    // 自定义方法，轮播图文件上传
    String uploadBannerImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}