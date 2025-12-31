package com.gpt.server.Mapper;


import com.gpt.server.Entity.ExamRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpt.server.Vo.ExamRankingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Mapper
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: ExamRecord数据访问层接口
 * @Version: 1.0
 */
@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    List<ExamRankingVo> queryRankList(@Param("paperId")  Integer paperId, @Param("limit") Integer limit);
}