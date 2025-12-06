package com.gpt.server.Vo;
/**
 * @BelongsProject: Server
 * @BelongsPackage: com.gpt.server.Service
 * @Author: 程序员Eighteen
 * @CreateTime: 2025-11-12  22:42
 * @Description: 判卷结果内部类
 * @Version: 1.0
 */

public  class GradingResult {

    private Integer score;
    private String feedback;
    private String reason;

    public GradingResult(Integer score, String feedback, String reason) {
        this.score = score;
        this.feedback = feedback;
        this.reason = reason;
    }

    // Getters
    public Integer getScore() { return score; }
    public String getFeedback() { return feedback; }
    public String getReason() { return reason; }
}