package com.iflytek.rpa.monitor.entity.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;

@Data
public class RobotExecuteTodayDto {
    /**
     * 执行次数
     */
    private Long executeTotal = 0L;
    /**
     * 执行时长（单位秒）
     */
    private Long executeTime = 0L;
    /**
     * 执行中次数
     */
    private Long executeRunning = 0L;

    private BigDecimal executeRunningRate = new BigDecimal(0);
    /**
     * 执行成功次数
     */
    private Long executeSuccess = 0L;

    private BigDecimal executeSuccessRate = new BigDecimal(0);

    /**
     * 执行失败次数
     */
    private Long executeFail = 0L;

    private BigDecimal executeFailRate = new BigDecimal(0);

    /**
     * 执行中止次数
     */
    private Long executeAbort = 0L;

    private BigDecimal executeAbortRate = new BigDecimal(0);

    /**
     * 获取执行时长（小时）
     */
    public BigDecimal getExecuteTimeHourData() {
        return executeTime == null
                ? BigDecimal.ZERO
                : new BigDecimal(executeTime).divide(new BigDecimal(3600), 2, RoundingMode.HALF_UP);
    }
}
