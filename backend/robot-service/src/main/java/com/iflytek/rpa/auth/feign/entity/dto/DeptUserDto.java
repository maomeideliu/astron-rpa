package com.iflytek.rpa.auth.feign.entity.dto;

import com.iflytek.rpa.auth.feign.entity.User;
import lombok.Data;

/**
 * @author mjren
 * @date 2025-03-06 19:08
 * @copyright Copyright (c) 2025 mjren
 */
@Data
public class DeptUserDto extends User {

    private String roleId;

    private String roleName;

}
