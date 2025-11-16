package com.gpt.server.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Entity.Category;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Category服务接口
 * @Version: 1.0
 */
public interface CategoryService extends IService<Category> {

    // 查询分类列表及所有列表
    List<Category> findCategoryList();

    // 查询分类树列表
    List<Category> findCategoryTreeList();

    // 保存分类
    void saveCategory(Category category);

    // 修改分类
    void updateCategory(Category category);

    // 删除分类
    void removeCategory(Long id);
}