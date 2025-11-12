package com.gpt.server.Mapper;


import com.gpt.server.Entity.VideoLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: VideoLike数据访问层接口
 * @Version: 1.0
 */
@Mapper
public interface VideoLikeMapper extends BaseMapper<VideoLike> {
    
    /**
     * 检查用户是否已点赞该视频（基于IP）
     * @param videoId 视频ID
     * @param userIp 用户IP
     * @return 是否已点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM video_likes WHERE video_id = #{videoId} AND user_ip = #{userIp}")
    boolean isLikedByIp(@Param("videoId") Long videoId, @Param("userIp") String userIp);
    
    /**
     * 获取视频的点赞总数
     * @param videoId 视频ID
     * @return 点赞总数
     */
    @Select("SELECT COUNT(*) FROM video_likes WHERE video_id = #{videoId}")
    Long getLikeCountByVideoId(@Param("videoId") Long videoId);
} 