package com.gpt.server.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpt.server.Entity.Category;
import com.gpt.server.Entity.Question;
import com.gpt.server.Mapper.CategoryMapper;
import com.gpt.server.Mapper.QuestionMapper;
import com.gpt.server.Service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
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

    @Override
    public List<Category> findCategoryTreeList() {
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

        // 将分类信息进行分组
        Map<Long, List<Category>> longListMap = categoryList.stream().collect(Collectors.groupingBy(Category::getParentId));  // 父分类ID为键，子分类列表为值
        // 筛选分类信息
        List<Category> parentCategoryList = categoryList.stream().filter(c -> c.getParentId() == 0).toList();

        for (Category parentCategory : parentCategoryList) {
            List<Category> sonCategoryList = longListMap.getOrDefault(parentCategory.getId(), new ArrayList<>());
            parentCategory.setChildren(sonCategoryList);

            Long sonCount = sonCategoryList.stream().mapToLong(Category::getCount).sum();
            parentCategory.setCount(parentCategory.getCount() + sonCount);
        }
        return parentCategoryList;
    }

    @Override
    public void saveCategory(Category category) {
        // 校验当前父分类下不能存在同名子分类
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId,category.getParentId());
        queryWrapper.eq(Category::getName,category.getName());
        long count = count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("%s 父分类下，已存在名为：%s 的子分类信息！本次添加失败！".formatted(category.getParentId(), category.getName()));
        }
        // 保存分类
        save(category);
    }

    @Override
    public void updateCategory(Category category) {
        // 校验当前父分类下不能存在同名子分类，可以和自己原名相同
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId,category.getParentId());
        queryWrapper.eq(Category::getName,category.getName());
        queryWrapper.ne(Category::getId,category.getId());  // 检测子分类
        long count = count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("%s 父分类下，已存在名为：%s 的子分类信息！本次修改失败！".formatted(category.getParentId(), category.getName()));
        }
        updateById(category);
    }

    @Override
    public void removeCategory(Long id) {
        // 判断是不是一级分类
        Category category = getById(id);
        if(category == null){
            log.debug("在删除之前已经被删除！！");
            return;
        }
        if (category.getParentId() == 0){
            // 一级分类
            throw new RuntimeException("id= %s 的分类为一级分类，不被删除！".formatted(id));
        }
        // 判断有没有关联的题目
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper=new LambdaQueryWrapper<>();
        questionLambdaQueryWrapper.eq(Question::getCategoryId,id);
        Long count = questionMapper.selectCount(questionLambdaQueryWrapper);
        if (count() > 0){
            throw new RuntimeException("id= %s 的分类下关联有题目，题目数量为 %s，不能删除！".formatted(id,count));
        }
        // 进行删除分类
        removeById(id);
    }
}