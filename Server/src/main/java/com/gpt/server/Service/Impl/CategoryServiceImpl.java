package com.gpt.server.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.Category;
import com.gpt.server.Mapper.CategoryMapper;
import com.gpt.server.Mapper.QuestionMapper;
import com.gpt.server.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Category服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<Category> findCategoryList() {
        // 查所有分类集合
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);  // 正序排序查询
        List<Category> categoryList = list(queryWrapper);

        // 查询每个分类下的题目数量
        List<Map<String, Long>> mapList = questionMapper.selectQuestionCountCategory();

        Map<Long, Long> countMap = mapList.stream().collect(Collectors.toMap(k -> k.get("category_id"), v -> v.get("count")));
        // 查找
        for (Category category : categoryList) {
            Long id=category.getId();
            category.setCount(countMap.getOrDefault(id, 0L));
        }
        return categoryList;
    }
}