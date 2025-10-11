package com.iflytek.rpa.auth.constants.enums;

/**
 * @desc: TODO
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/10/11 9:56
 */
public enum ScopeType {
    READ("read", "读取权限"),
    PROFILE("profile", "个人信息权限");

    private final String scopeType;
    private final String description;

    ScopeType(String scopeType, String description) {
        this.scopeType = scopeType;
        this.description = description;
    }

    public String getScopeType() {
        return scopeType;
    }

    public String getDescription() {
        return description;
    }

    //根据字符串code找枚举
    public static ScopeType fromScopeType(String scopeType) {
        for (ScopeType type : values()) {
            if (type.scopeType.equals(scopeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的scope类型: " + scopeType);
    }
}
