package com.gpt.server.Service;

import com.gpt.server.Entity.Paper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpt.server.Vo.AiPaperVo;
import com.gpt.server.Vo.PaperVo;

/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: Paper服务接口
 * @Version: 1.0
 */
public interface PaperService extends IService<Paper> {
    // 手动创建试卷
    Paper createPaper(PaperVo paperVo);

    // 使用AI创建试卷
    Paper createPaperWithAI(AiPaperVo aiPaperVo);

    // 跟新试卷
    Paper updataPaper(Integer id, PaperVo paperVo);

    // 删除试卷
    void removePaper(Integer id);

    // 根据id查询试卷详情
    Paper getPaperById(Integer paperId);
}