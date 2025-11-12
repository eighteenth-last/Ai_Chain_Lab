package com.gpt.server.Service.Impl;

import com.gpt.server.Config.properties.MinioProperties;
import com.gpt.server.Service.FileUploadService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: FileUpload服务实现类
 * @Version: 1.0
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String uploadFile(String folder, MultipartFile file)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 连接minio客户端
        //判断通是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        //不存在创建桶，并且设置访问权限
        if (!bucketExists){
            String policy= """
                    {
                    "Statement":[{
                        "Action":"s3:GetObject",
                        "Effect":"Allow",
                        "Principal":"*",
                        "Resource":"arn:aws:s3:::%s/*"
                    }],
                    "Version":"2012-10-17"
                    }
                    """.formatted(minioProperties.getBucketName());
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .region(minioProperties.getRegion())
                    .build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .config(policy)
                    .build());
        }
        //上传文件
        // 希望在minio的服务器中，创建一个文件夹，存储对象
        // 为防止文件名重复，采用UUID去重
        String fileName = folder + "/" + new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "/" + UUID.randomUUID().toString().replace("-", "") + "-" + file.getOriginalFilename();
        // 按照日期创建文件夹
        minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .contentType(file.getContentType())
                    .object(fileName)
                    .region(minioProperties.getRegion())
                    .stream(file.getInputStream(),file.getSize(),-1)
                .build()
        );
        //返回文件访问路径  url=端点+ /+ 桶名+ /+对象名
        String fileUrl = String.join("/", minioProperties.getEndPoint(), minioProperties.getBucketName(), fileName);
        log.info("文件上传成功，地址为：{}", fileUrl);
        return fileUrl;
    }
}
