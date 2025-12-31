package com.iflytek.rpa.auth.feign;

import com.iflytek.rpa.auth.feign.entity.Permission;
import com.iflytek.rpa.auth.feign.entity.Role;
import com.iflytek.rpa.auth.feign.entity.User;
import com.iflytek.rpa.auth.utils.TenantUtils;
import com.iflytek.rpa.auth.utils.UserUtils;
import com.iflytek.rpa.starter.exception.NoLoginException;
import com.iflytek.rpa.starter.utils.response.AppResponse;
import com.iflytek.rpa.starter.utils.response.ErrorCodeEnum;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @desc: Feign测试控制器 - 用于测试TenantUtils和UserUtils中的方法
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/01/XX
 */
@RestController
@RequestMapping("/feign-test")
@Slf4j
public class FeignTestController {

    // ==================== TenantUtils 测试接口 ====================

    /**
     * 测试获取当前登录租户ID
     *
     * @return 租户ID
     */
    @GetMapping("/tenant/getTenantId")
    public AppResponse<String> testGetTenantId() {
        log.info("========== 开始测试: TenantUtils.getTenantId() ==========");
        try {
            String tenantId = TenantUtils.getTenantId();
            log.info("测试结果 - 租户ID: {}", tenantId);
            if (tenantId != null) {
                log.info("========== 测试成功: TenantUtils.getTenantId() ==========");
                return AppResponse.success(tenantId);
            } else {
                log.warn("========== 测试失败: TenantUtils.getTenantId() 返回null ==========");
                return AppResponse.<String>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取租户ID失败，返回null");
            }
        } catch (Exception e) {
            log.error("========== 测试异常: TenantUtils.getTenantId() ==========", e);
            return AppResponse.<String>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    // ==================== UserUtils 测试接口 ====================

    /**
     * 测试获取当前登录用户ID
     *
     * @return 用户ID
     */
    @GetMapping("/user/nowUserId")
    public AppResponse<String> testNowUserId() {
        log.info("========== 开始测试: UserUtils.nowUserId() ==========");
        try {
            String userId = UserUtils.nowUserId();
            log.info("测试结果 - 用户ID: {}", userId);
            log.info("========== 测试成功: UserUtils.nowUserId() ==========");
            return AppResponse.success(userId);
        } catch (NoLoginException e) {
            log.warn("========== 测试失败: UserUtils.nowUserId() - 未登录 ==========");
            return AppResponse.<String>error(ErrorCodeEnum.E_NOT_LOGIN, "未登录: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.nowUserId() ==========", e);
            return AppResponse.<String>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/user/nowLoginUser")
    public AppResponse<User> testNowLoginUser() {
        log.info("========== 开始测试: UserUtils.nowLoginUser() ==========");
        try {
            User user = UserUtils.nowLoginUser();
            log.info("测试结果 - 用户信息: {}", user);
            log.info("========== 测试成功: UserUtils.nowLoginUser() ==========");
            return AppResponse.success(user);
        } catch (NoLoginException e) {
            log.warn("========== 测试失败: UserUtils.nowLoginUser() - 未登录 ==========");
            return AppResponse.<User>error(ErrorCodeEnum.E_NOT_LOGIN, "未登录: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.nowLoginUser() ==========", e);
            return AppResponse.<User>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/getUserInfoById")
    public AppResponse<User> testGetUserInfoById(@RequestParam("id") String id) {
        log.info("========== 开始测试: UserUtils.getUserInfoById() ==========");
        log.info("测试参数 - 用户ID: {}", id);
        try {
            User user = UserUtils.getUserInfoById(id);
            log.info("测试结果 - 用户信息: {}", user);
            if (user != null) {
                log.info("========== 测试成功: UserUtils.getUserInfoById() ==========");
                return AppResponse.success(user);
            } else {
                log.warn("========== 测试失败: UserUtils.getUserInfoById() 返回null ==========");
                return AppResponse.<User>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取用户信息失败，返回null");
            }
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getUserInfoById() ==========", e);
            return AppResponse.<User>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据用户ID获取用户真实姓名
     *
     * @param id 用户ID
     * @return 用户真实姓名
     */
    @GetMapping("/user/getRealNameById")
    public AppResponse<String> testGetRealNameById(@RequestParam("id") String id) {
        log.info("========== 开始测试: UserUtils.getRealNameById() ==========");
        log.info("测试参数 - 用户ID: {}", id);
        try {
            String realName = UserUtils.getRealNameById(id);
            log.info("测试结果 - 用户真实姓名: {}", realName);
            if (realName != null) {
                log.info("========== 测试成功: UserUtils.getRealNameById() ==========");
                return AppResponse.success(realName);
            } else {
                log.warn("========== 测试失败: UserUtils.getRealNameById() 返回null ==========");
                return AppResponse.<String>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取用户真实姓名失败，返回null");
            }
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getRealNameById() ==========", e);
            return AppResponse.<String>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据用户ID查询登录名
     *
     * @param id 用户ID
     * @return 登录名
     */
    @GetMapping("/user/getLoginNameById")
    public AppResponse<String> testGetLoginNameById(@RequestParam("id") String id) {
        log.info("========== 开始测试: UserUtils.getLoginNameById() ==========");
        log.info("测试参数 - 用户ID: {}", id);
        try {
            String loginName = UserUtils.getLoginNameById(id);
            log.info("测试结果 - 登录名: {}", loginName);
            if (loginName != null) {
                log.info("========== 测试成功: UserUtils.getLoginNameById() ==========");
                return AppResponse.success(loginName);
            } else {
                log.warn("========== 测试失败: UserUtils.getLoginNameById() 返回null ==========");
                return AppResponse.<String>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取登录名失败，返回null");
            }
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getLoginNameById() ==========", e);
            return AppResponse.<String>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据用户ID列表查询用户信息列表
     *
     * @param userIdList 用户ID列表
     * @return 用户信息列表
     */
    @PostMapping("/user/queryUserPageList")
    public AppResponse<List<User>> testQueryUserPageList(@RequestBody List<String> userIdList) {
        log.info("========== 开始测试: UserUtils.queryUserPageList() ==========");
        log.info("测试参数 - 用户ID列表: {}", userIdList);
        try {
            List<User> userList = UserUtils.queryUserPageList(userIdList);
            log.info("测试结果 - 用户列表大小: {}", userList != null ? userList.size() : 0);
            log.info("测试结果 - 用户列表: {}", userList);
            log.info("========== 测试成功: UserUtils.queryUserPageList() ==========");
            return AppResponse.success(userList);
        } catch (IOException e) {
            log.error("========== 测试异常: UserUtils.queryUserPageList() - IO异常 ==========", e);
            return AppResponse.<List<User>>error(ErrorCodeEnum.E_EXCEPTION, "IO异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.queryUserPageList() ==========", e);
            return AppResponse.<List<User>>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试检查当前用户是否已登录
     *
     * @return 登录状态
     */
    @GetMapping("/user/isCurrentUserLogin")
    public AppResponse<Boolean> testIsCurrentUserLogin() {
        log.info("========== 开始测试: UserUtils.isCurrentUserLogin() ==========");
        try {
            boolean isLogin = UserUtils.isCurrentUserLogin();
            log.info("测试结果 - 登录状态: {}", isLogin);
            log.info("========== 测试成功: UserUtils.isCurrentUserLogin() ==========");
            return AppResponse.success(isLogin);
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.isCurrentUserLogin() ==========", e);
            return AppResponse.<Boolean>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试检查登录状态并返回响应
     *
     * @return 登录状态响应
     */
    @GetMapping("/user/nowLoginUserResponse")
    public AppResponse<?> testNowLoginUserResponse() {
        log.info("========== 开始测试: UserUtils.nowLoginUserResponse() ==========");
        try {
            AppResponse<?> response = UserUtils.nowLoginUserResponse();
            log.info("测试结果 - 响应: {}", response);
            log.info("========== 测试成功: UserUtils.nowLoginUserResponse() ==========");
            return response;
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.nowLoginUserResponse() ==========", e);
            return AppResponse.error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试转换成 id : User 的格式
     *
     * @param userList 用户列表
     * @return 用户Map
     */
    @PostMapping("/user/getUserMap")
    public AppResponse<Map<String, User>> testGetUserMap(@RequestBody List<User> userList) {
        log.info("========== 开始测试: UserUtils.getUserMap() ==========");
        log.info("测试参数 - 用户列表大小: {}", userList != null ? userList.size() : 0);
        try {
            Map<String, User> userMap = UserUtils.getUserMap(userList);
            log.info("测试结果 - 用户Map大小: {}", userMap != null ? userMap.size() : 0);
            log.info("测试结果 - 用户Map: {}", userMap);
            log.info("========== 测试成功: UserUtils.getUserMap() ==========");
            return AppResponse.success(userMap);
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getUserMap() ==========", e);
            return AppResponse.<Map<String, User>>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试转换成 id : 用户名 的格式
     *
     * @param userList 用户列表
     * @return 用户名Map
     */
    @PostMapping("/user/getUserNameMap")
    public AppResponse<Map<String, String>> testGetUserNameMap(@RequestBody List<User> userList) {
        log.info("========== 开始测试: UserUtils.getUserNameMap() ==========");
        log.info("测试参数 - 用户列表大小: {}", userList != null ? userList.size() : 0);
        try {
            Map<String, String> userNameMap = UserUtils.getUserNameMap(userList);
            log.info("测试结果 - 用户名Map大小: {}", userNameMap != null ? userNameMap.size() : 0);
            log.info("测试结果 - 用户名Map: {}", userNameMap);
            log.info("========== 测试成功: UserUtils.getUserNameMap() ==========");
            return AppResponse.success(userNameMap);
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getUserNameMap() ==========", e);
            return AppResponse.<Map<String, String>>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据角色名称查询角色详情
     *
     * @param roleName 角色名称
     * @return 角色详情
     */
    @GetMapping("/user/queryRoleDetail")
    public AppResponse<Role> testQueryRoleDetail(@RequestParam("roleName") String roleName) {
        log.info("========== 开始测试: UserUtils.queryRoleDetail() ==========");
        log.info("测试参数 - 角色名称: {}", roleName);
        try {
            Role role = UserUtils.queryRoleDetail(roleName);
            log.info("测试结果 - 角色详情: {}", role);
            if (role != null) {
                log.info("========== 测试成功: UserUtils.queryRoleDetail() ==========");
                return AppResponse.success(role);
            } else {
                log.warn("========== 测试失败: UserUtils.queryRoleDetail() 返回null ==========");
                return AppResponse.<Role>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取角色详情失败，返回null");
            }
        } catch (IOException e) {
            log.error("========== 测试异常: UserUtils.queryRoleDetail() - IO异常 ==========", e);
            return AppResponse.<Role>error(ErrorCodeEnum.E_EXCEPTION, "IO异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.queryRoleDetail() ==========", e);
            return AppResponse.<Role>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试获取当前用户权限列表
     *
     * @return 权限列表
     */
    @GetMapping("/user/getCurrentUserPermissionList")
    public AppResponse<List<Permission>> testGetCurrentUserPermissionList() {
        log.info("========== 开始测试: UserUtils.getCurrentUserPermissionList() ==========");
        try {
            List<Permission> permissionList = UserUtils.getCurrentUserPermissionList();
            log.info("测试结果 - 权限列表大小: {}", permissionList != null ? permissionList.size() : 0);
            log.info("测试结果 - 权限列表: {}", permissionList);
            log.info("========== 测试成功: UserUtils.getCurrentUserPermissionList() ==========");
            return AppResponse.success(permissionList);
        } catch (NoLoginException e) {
            log.warn("========== 测试失败: UserUtils.getCurrentUserPermissionList() - 未登录 ==========");
            return AppResponse.<List<Permission>>error(ErrorCodeEnum.E_NOT_LOGIN, "未登录: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getCurrentUserPermissionList() ==========", e);
            return AppResponse.<List<Permission>>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试获取用户角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/user/getCurrentUserRoleList")
    public AppResponse<List<Role>> testGetCurrentUserRoleList() {
        log.info("========== 开始测试: UserUtils.getCurrentUserRoleList() ==========");
        try {
            List<Role> roleList = UserUtils.getCurrentUserRoleList();
            log.info("测试结果 - 角色列表大小: {}", roleList != null ? roleList.size() : 0);
            log.info("测试结果 - 角色列表: {}", roleList);
            log.info("========== 测试成功: UserUtils.getCurrentUserRoleList() ==========");
            return AppResponse.success(roleList);
        } catch (NoLoginException e) {
            log.warn("========== 测试失败: UserUtils.getCurrentUserRoleList() - 未登录 ==========");
            return AppResponse.<List<Role>>error(ErrorCodeEnum.E_NOT_LOGIN, "未登录: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getCurrentUserRoleList() ==========", e);
            return AppResponse.<List<Role>>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据电话获取用户信息
     *
     * @param phoneNumber 电话号码
     * @return 用户信息
     */
    @GetMapping("/user/getUserInfoByPhone")
    public AppResponse<User> testGetUserInfoByPhone(@RequestParam("phoneNumber") String phoneNumber) {
        log.info("========== 开始测试: UserUtils.getUserInfoByPhone() ==========");
        log.info("测试参数 - 电话号码: {}", phoneNumber);
        try {
            User user = UserUtils.getUserInfoByPhone(phoneNumber);
            log.info("测试结果 - 用户信息: {}", user);
            if (user != null) {
                log.info("========== 测试成功: UserUtils.getUserInfoByPhone() ==========");
                return AppResponse.success(user);
            } else {
                log.warn("========== 测试失败: UserUtils.getUserInfoByPhone() 返回null ==========");
                return AppResponse.<User>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取用户信息失败，返回null");
            }
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getUserInfoByPhone() ==========", e);
            return AppResponse.<User>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试根据电话获取用户姓名
     *
     * @param phoneNumber 电话号码
     * @return 用户姓名
     */
    @GetMapping("/user/getRealNameByPhone")
    public AppResponse<String> testGetRealNameByPhone(@RequestParam("phoneNumber") String phoneNumber) {
        log.info("========== 开始测试: UserUtils.getRealNameByPhone() ==========");
        log.info("测试参数 - 电话号码: {}", phoneNumber);
        try {
            String realName = UserUtils.getRealNameByPhone(phoneNumber);
            log.info("测试结果 - 用户姓名: {}", realName);
            if (realName != null) {
                log.info("========== 测试成功: UserUtils.getRealNameByPhone() ==========");
                return AppResponse.success(realName);
            } else {
                log.warn("========== 测试失败: UserUtils.getRealNameByPhone() 返回null ==========");
                return AppResponse.<String>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取用户姓名失败，返回null");
            }
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getRealNameByPhone() ==========", e);
            return AppResponse.<String>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }

    /**
     * 测试通过用户名获取用户
     *
     * @param name 用户名
     * @return 用户信息
     */
    @GetMapping("/user/getUserByName")
    public AppResponse<User> testGetUserByName(@RequestParam("name") String name) {
        log.info("========== 开始测试: UserUtils.getUserByName() ==========");
        log.info("测试参数 - 用户名: {}", name);
        try {
            User user = UserUtils.getUserByName(name);
            log.info("测试结果 - 用户信息: {}", user);
            if (user != null) {
                log.info("========== 测试成功: UserUtils.getUserByName() ==========");
                return AppResponse.success(user);
            } else {
                log.warn("========== 测试失败: UserUtils.getUserByName() 返回null ==========");
                return AppResponse.<User>error(ErrorCodeEnum.E_SERVICE_INFO_LOSE, "获取用户信息失败，返回null");
            }
        } catch (IOException e) {
            log.error("========== 测试异常: UserUtils.getUserByName() - IO异常 ==========", e);
            return AppResponse.<User>error(ErrorCodeEnum.E_EXCEPTION, "IO异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("========== 测试异常: UserUtils.getUserByName() ==========", e);
            return AppResponse.<User>error(ErrorCodeEnum.E_EXCEPTION, "测试异常: " + e.getMessage());
        }
    }
}

