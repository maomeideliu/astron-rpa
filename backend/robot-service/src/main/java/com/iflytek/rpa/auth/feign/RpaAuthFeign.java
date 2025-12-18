package com.iflytek.rpa.auth.feign;

import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @desc: TODO
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/12/18 17:23
 */
@FeignClient( name="rpa-auth",
//        url = "http://rpaauth-service:10251"
        url = "http://localhost:10251"
        , configuration = FeignAutoConfiguration.class)
public interface RpaAuthFeign {

}
