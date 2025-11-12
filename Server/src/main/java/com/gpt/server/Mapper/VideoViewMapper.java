package com.gpt.server.Mapper;

import com.gpt.server.Entity.VideoView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: VideoView数据访问层接口
 * @Version: 1.0
 */
@Mapper
public interface VideoViewMapper extends BaseMapper<VideoView> {
    
    /**
     * 获取视频的观看总数
     * @param videoId 视频ID
     * @return 观看总数
     */
    @Select("SELECT COUNT(*) FROM video_views WHERE video_id = #{videoId}")
    Long getViewCountByVideoId(@Param("videoId") Long videoId);
    
    /**
     * 获取视频的平均观看时长
     * @param videoId 视频ID
     * @return 平均观看时长（秒）
     */
    @Select("SELECT AVG(view_duration) FROM video_views WHERE video_id = #{videoId} AND view_duration > 0")
    Double getAverageViewDuration(@Param("videoId") Long videoId);
    
    /**
     * 获取观看统计数据（按日期分组）
     * @param videoId 视频ID
     * @param days 统计天数
     * @return 观看统计列表
     */
    @Select("SELECT DATE(created_at) as view_date, COUNT(*) as view_count " +
            "FROM video_views " +
            "WHERE video_id = #{videoId} AND created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY view_date DESC")
    List<Map<String, Object>> getViewStatsByDate(@Param("videoId") Long videoId, @Param("days") Integer days);
} 