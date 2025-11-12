package com.gpt.server.Service.Impl;

import com.gpt.server.Entity.Banner;
import com.gpt.server.Mapper.BannerMapper;
import com.gpt.server.Service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Service.FileUploadService;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Banner服务实现类
 * @Version: 1.0
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public String uploadBannerImage(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (file.isEmpty()){
            throw new RuntimeException("上传的轮播图文件为空");
        }
        String contentType = file.getContentType();

        if (ObjectUtils.isEmpty(contentType)||!contentType.startsWith("image/")){
            throw new RuntimeException("上传的轮播图文件格式错误");
        }

        String imageUrl = fileUploadService.uploadFile("banners", file);
        return imageUrl;
    }
}