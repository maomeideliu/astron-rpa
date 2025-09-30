package com.iflytek.rpa.market.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UseDeleteDto {
    @NotBlank
    String id;
}
