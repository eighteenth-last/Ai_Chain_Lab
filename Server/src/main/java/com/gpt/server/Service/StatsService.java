package com.gpt.server.Service;


import com.gpt.server.Vo.StatsVo;

/**
 * 统计数据服务接口
 */
public interface StatsService {
    
    /**
     * 获取系统统计数据
     * @return 统计数据DTO
     */
    StatsVo getSystemStats();
} 