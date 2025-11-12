package com.gpt.server.Mapper;

import com.gpt.server.Entity.Notice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Notice数据访问层接口
 * @Version: 1.0
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 获取启用的公告，按优先级降序，创建时间降序排列
     * @return 公告列表
     */
    @Select("SELECT * FROM notices WHERE is_active = true ORDER BY priority DESC, create_time DESC")
    List<Notice> selectActiveNotices();

    /**
     * 获取最新的几条公告
     * @param limit 限制数量
     * @return 公告列表
     */
    @Select("SELECT * FROM notices WHERE is_active = true ORDER BY priority DESC, create_time DESC LIMIT #{limit}")
    List<Notice> selectLatestNotices(int limit);
    
} 