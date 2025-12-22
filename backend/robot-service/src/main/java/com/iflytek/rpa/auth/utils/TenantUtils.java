package com.iflytek.rpa.auth.utils;

import com.iflytek.rpa.auth.feign.RpaAuthFeign;
import com.iflytek.rpa.starter.utils.response.AppResponse;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desc: 租户工具类
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/9/11 14:10
 */
@Component
@Slf4j
public class TenantUtils {

    @Autowired
    private RpaAuthFeign rpaAuthFeign;

    // 静态变量，用于在静态方法中访问
    private static RpaAuthFeign staticRpaAuthFeign;

    @PostConstruct
    public void init() {
        staticRpaAuthFeign = this.rpaAuthFeign;
    }

    /**
     * 获取当前登录租户ID
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        if (Objects.isNull(staticRpaAuthFeign)) {
            log.warn("Feign客户端未初始化");
            return null;
        }

        try {
            AppResponse<String> response = staticRpaAuthFeign.getCurrentTenantId();
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("获取当前登录租户ID失败, 响应: {}", response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("获取当前登录租户ID失败", e);
            return null;
        }
    }
}
