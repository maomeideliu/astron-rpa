package com.iflytek.rpa.market.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 卓越中心租户级部署DTO
 */
@Data
public class ExcellenceDeployDto {

    @NotBlank(message = "机器人ID不能为空")
    private String robotId;

//    @NotBlank(message = "机器人版本不能为空")
//    private String robotVersion;

    /**
     * 目标人员ID列表
     */
    @NotEmpty(message = "目标人员列表不能为空")
    private List<String> userIdList;

} 