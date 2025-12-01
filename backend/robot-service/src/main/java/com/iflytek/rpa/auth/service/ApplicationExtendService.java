package com.iflytek.rpa.auth.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.casbin.casdoor.config.Config;
import org.casbin.casdoor.entity.Application;
import org.casbin.casdoor.service.ApplicationService;
import org.casbin.casdoor.util.Map;
import org.casbin.casdoor.util.http.CasdoorResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

/**
 * @desc: TODO
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/12/1 10:57
 */
@Service
public class ApplicationExtendService extends ApplicationService {

    public ApplicationExtendService(Config config) {
        super(config);
    }

    public Application getApplicationWithKey(String name) throws IOException {
        java.util.Map<String, String> params = new HashMap<>();
        params.put("id", "admin/" + name);
        params.put("withKey", "1");

        CasdoorResponse<Application, Object> response =
                doGet("get-application", params, new TypeReference<CasdoorResponse<Application, Object>>() {});
        return response.getData();
    }

}
