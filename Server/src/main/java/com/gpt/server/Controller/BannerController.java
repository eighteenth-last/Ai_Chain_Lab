package com.gpt.server.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gpt.server.Common.Result;
import com.gpt.server.Entity.Banner;
import com.gpt.server.Service.BannerService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Controller
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Banner控制器
 * @Version: 1.0
 */
@Slf4j
@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/banners")  // 轮播图API路径前缀
@CrossOrigin  // 允许跨域访问
@Tag(name = "轮播图管理", description = "轮播图相关操作，包括图片上传、轮播图增删改查、状态管理等功能")  // Swagger API分组
public class BannerController {

    @Autowired
    private BannerService bannerService;

    
    /**
     * 上传轮播图图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/upload-image")  // 处理POST请求
    @Operation(summary = "上传轮播图图片", description = "将图片文件上传到MinIO服务器，返回可访问的图片URL")  // API描述
    public Result<String> uploadBannerImage(
            @Parameter(description = "要上传的图片文件，支持jpg、png、gif等格式，大小限制5MB") 
            @RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        String imageUrl=bannerService.uploadBannerImage(file);
        log.info("图片上传成功，图片访问URL为：{}",imageUrl);
        return Result.success(imageUrl, "图片上传成功");
    }
    
    /**
     * 获取启用的轮播图（前台首页使用）
     * @return 轮播图列表
     */
    @GetMapping("/active")  // 处理GET请求
    @Operation(summary = "获取启用的轮播图", description = "获取状态为启用的轮播图列表，供前台首页展示使用")  // API描述
    public Result<List<Banner>> getActiveBanners() {

        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Banner::getIsActive, true);
        queryWrapper.orderByAsc(Banner::getSortOrder);

        List<Banner> bannerList = bannerService.list(queryWrapper);
        Result<List<Banner>> result = Result.success(bannerList);
        log.info("查询启用的轮播图成功，查询数量为：{},具体数据为{}",bannerList.size(),bannerList);
        return result;
    }
    
    /**
     * 获取所有轮播图（管理后台使用）
     * @return 轮播图列表
     */
    @GetMapping("/list")  // 处理GET请求
    @Operation(summary = "获取所有轮播图", description = "获取所有轮播图列表，包括启用和禁用的，供管理后台使用")  // API描述
    public Result<List<Banner>> getAllBanners() {

        // 排序
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Banner::getSortOrder);
        List<Banner> bannerList = bannerService.list(queryWrapper);

        Result<List<Banner>> result = Result.success(bannerList);
        log.info("查询所有轮播图成功，查询数量为：{},具体数据为{}",bannerList.size(),bannerList);
        return result;
    }
    
    /**
     * 根据ID获取轮播图
     * @param id 轮播图ID
     * @return 轮播图详情
     */
    @GetMapping("/{id}")  // 处理GET请求
    @Operation(summary = "根据ID获取轮播图", description = "根据轮播图ID获取单个轮播图的详细信息")  // API描述  
    public Result<Banner> getBannerById(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        // 根据id查询轮播图
        Banner banner = bannerService.getById(id);
        // 日志输出
        log.info("查询id为{}获取轮播图成功,结果为{}", id,banner);
        // 对象装到result
        return Result.success(banner);
    }
    
    /**
     * 添加轮播图
     * @param banner 轮播图对象
     * @return 操作结果
     */
    @PostMapping("/add")  // 处理POST请求
    @Operation(summary = "添加轮播图", description = "创建新的轮播图，需要提供图片URL、标题、跳转链接等信息")  // API描述
    public Result<String> addBanner(@RequestBody Banner banner) {
        bannerService.save(banner);
        log.info("保存轮播图成功，id为{}", banner.getId());
        return Result.success("保存成功");
    }
    
    /**
     * 更新轮播图
     * @param banner 轮播图对象
     * @return 操作结果
     */
    @PutMapping("/update")  // 处理PUT请求
    @Operation(summary = "更新轮播图", description = "更新轮播图的信息，包括图片、标题、跳转链接、排序等")  // API描述
    public Result<String> updateBanner(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        log.info("更新轮播图成功，id为{}",banner.getId());
        return Result.success("更新轮播图成功");
    }
    
    /**
     * 删除轮播图
     * @param id 轮播图ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")  // 处理DELETE请求
    @Operation(summary = "删除轮播图", description = "根据ID删除指定的轮播图")  // API描述
    public Result<String> deleteBanner(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        // 防御性删除
        bannerService.removeById(id);
        log.info("删除id为{}的轮播图成功！！！",id);

        return Result.success("轮播图删除成功");
    }
    
    /**
     * 启用/禁用轮播图
     * @param id 轮播图ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    @PutMapping("/toggle/{id}")  // 处理PUT请求
    @Operation(summary = "切换轮播图状态", description = "启用或禁用指定的轮播图，禁用后不会在前台显示")  // API描述
    public Result<String> toggleBannerStatus(
            @Parameter(description = "轮播图ID") @PathVariable Long id, 
            @Parameter(description = "是否启用，true为启用，false为禁用") @RequestParam Boolean isActive) {

        LambdaUpdateWrapper<Banner> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Banner::getId, id);
        updateWrapper.set(Banner::getIsActive, isActive);
        bannerService.update(updateWrapper);
        log.info("切换轮播图状态成功，轮播图ID为：{}，状态为：{}",id,isActive);
        return Result.success("轮播图状态切换成功");
    }
} 