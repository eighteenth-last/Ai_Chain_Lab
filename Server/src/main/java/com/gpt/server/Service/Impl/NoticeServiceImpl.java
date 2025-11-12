package com.gpt.server.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gpt.server.Common.Result;
import com.gpt.server.Entity.Notice;
import com.gpt.server.Mapper.NoticeMapper;
import com.gpt.server.Service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service.Impl
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Notice服务实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public Result<List<Notice>> getActiveNotices() {
        try {
            LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Notice::getIsActive, true)
                   .orderByDesc(Notice::getPriority)
                   .orderByDesc(Notice::getCreateTime);
            List<Notice> notices = this.list(wrapper);
            log.info("获取启用公告成功，数量：{}", notices.size());
            return Result.success(notices);
        } catch (Exception e) {
            log.error("获取公告失败", e);
            return Result.error("获取公告失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<Notice>> getLatestNotices(int limit) {
        try {
            LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Notice::getIsActive, true)
                   .orderByDesc(Notice::getPriority)
                   .orderByDesc(Notice::getCreateTime)
                   .last("LIMIT " + limit);
            List<Notice> notices = this.list(wrapper);
            log.info("获取最新{}条公告成功", limit);
            return Result.success(notices);
        } catch (Exception e) {
            log.error("获取最新公告失败", e);
            return Result.error("获取最新公告失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<Notice>> getAllNotices() {
        try {
            LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(Notice::getPriority)
                   .orderByDesc(Notice::getCreateTime);
            List<Notice> notices = this.list(wrapper);
            log.info("获取所有公告成功，数量：{}", notices.size());
            return Result.success(notices);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error("获取公告列表失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> addNotice(Notice notice) {
        try {
            // 参数校验
            if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
                return Result.error("公告标题不能为空");
            }
            if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
                return Result.error("公告内容不能为空");
            }

            // 设置默认值
            notice.setCreateTime(new Date());
            notice.setUpdateTime(new Date());
            if (notice.getIsActive() == null) {
                notice.setIsActive(true);
            }
            if (notice.getPriority() == null) {
                notice.setPriority(0);
            }
            if (notice.getType() == null) {
                notice.setType("NOTICE");
            }

            boolean success = this.save(notice);
            if (success) {
                log.info("公告添加成功，标题：{}", notice.getTitle());
                return Result.success("公告添加成功");
            } else {
                log.warn("公告添加失败");
                return Result.error("公告添加失败");
            }
        } catch (Exception e) {
            log.error("公告添加失败", e);
            return Result.error("公告添加失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> updateNotice(Notice notice) {
        try {
            // 参数校验
            if (notice.getId() == null) {
                return Result.error("公告ID不能为空");
            }
            
            // 检查公告是否存在
            Notice existNotice = this.getById(notice.getId());
            if (existNotice == null) {
                return Result.error("公告不存在");
            }

            notice.setUpdateTime(new Date());
            boolean success = this.updateById(notice);
            if (success) {
                log.info("公告更新成功，ID：{}", notice.getId());
                return Result.success("公告更新成功");
            } else {
                log.warn("公告更新失败，ID：{}", notice.getId());
                return Result.error("公告更新失败");
            }
        } catch (Exception e) {
            log.error("公告更新失败", e);
            return Result.error("公告更新失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> deleteNotice(Long id) {
        try {
            // 参数校验
            if (id == null) {
                return Result.error("公告ID不能为空");
            }

            // 检查公告是否存在
            Notice notice = this.getById(id);
            if (notice == null) {
                return Result.error("公告不存在");
            }

            boolean success = this.removeById(id);
            if (success) {
                log.info("公告删除成功，ID：{}，标题：{}", id, notice.getTitle());
                return Result.success("公告删除成功");
            } else {
                log.warn("公告删除失败，ID：{}", id);
                return Result.error("公告删除失败");
            }
        } catch (Exception e) {
            log.error("公告删除失败，ID：{}", id, e);
            return Result.error("公告删除失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> toggleNoticeStatus(Long id, Boolean isActive) {
        try {
            // 参数校验
            if (id == null) {
                return Result.error("公告ID不能为空");
            }
            if (isActive == null) {
                return Result.error("状态参数不能为空");
            }

            // 检查公告是否存在
            Notice existNotice = this.getById(id);
            if (existNotice == null) {
                return Result.error("公告不存在");
            }

            Notice notice = new Notice();
            notice.setId(id);
            notice.setIsActive(isActive);
            notice.setUpdateTime(new Date());

            boolean success = this.updateById(notice);
            if (success) {
                String status = isActive ? "启用" : "禁用";
                log.info("公告{}成功，ID：{}，标题：{}", status, id, existNotice.getTitle());
                return Result.success("公告" + status + "成功");
            } else {
                log.warn("公告状态更新失败，ID：{}", id);
                return Result.error("公告状态更新失败");
            }
        } catch (Exception e) {
            log.error("公告状态更新失败，ID：{}", id, e);
            return Result.error("公告状态更新失败：" + e.getMessage());
        }
    }
} 