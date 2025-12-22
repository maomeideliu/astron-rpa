package com.iflytek.rpa.auth.utils;

import com.iflytek.rpa.auth.feign.RpaAuthFeign;
import com.iflytek.rpa.auth.feign.entity.Permission;
import com.iflytek.rpa.auth.feign.entity.Role;
import com.iflytek.rpa.auth.feign.entity.User;
import com.iflytek.rpa.auth.feign.entity.dto.GetRoleDto;
import com.iflytek.rpa.starter.exception.NoLoginException;
import com.iflytek.rpa.starter.utils.response.AppResponse;
import com.iflytek.rpa.starter.utils.response.ErrorCodeEnum;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @desc: 用户工具类
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/9/11 14:08
 */
@Component
@Slf4j
public class UserUtils {

    @Autowired
    private RpaAuthFeign rpaAuthFeign;

    // 静态变量，用于在静态方法中访问
    private static RpaAuthFeign staticRpaAuthFeign;

    @PostConstruct
    public void init() {
        staticRpaAuthFeign = this.rpaAuthFeign;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     * @throws NoLoginException 未登录异常
     */
    public static String nowUserId() throws NoLoginException {
        if (Objects.isNull(staticRpaAuthFeign)) {
            throw new NoLoginException("Feign客户端未初始化");
        }

        try {
            AppResponse<String> response = staticRpaAuthFeign.getCurrentUserId();
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                throw new NoLoginException(response != null ? response.getMessage() : "获取用户ID失败");
            }
        } catch (Exception e) {
            log.error("获取当前登录用户ID失败", e);
            throw new NoLoginException("获取用户ID失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     * @throws NoLoginException 未登录异常
     */
    public static User nowLoginUser() throws NoLoginException {
        if (Objects.isNull(staticRpaAuthFeign)) {
            throw new NoLoginException("Feign客户端未初始化");
        }

        try {
            AppResponse<User> response = staticRpaAuthFeign.getCurrentLoginUser();
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                throw new NoLoginException(response != null ? response.getMessage() : "获取用户信息失败");
            }
        } catch (Exception e) {
            log.error("获取当前登录用户信息失败", e);
            throw new NoLoginException("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public static User getUserInfoById(String id) {
        if (Objects.isNull(staticRpaAuthFeign) || Objects.isNull(id)) {
            return null;
        }

        try {
            AppResponse<User> response = staticRpaAuthFeign.getUserInfoById(id);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据用户ID获取用户信息失败: {}, 响应: {}", id, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据用户ID获取用户信息失败: {}", id, e);
            return null;
        }
    }

    /**
     * 根据用户ID获取用户真实姓名
     *
     * @param id 用户ID
     * @return 用户真实姓名
     */
    public static String getRealNameById(String id) {
        if (Objects.isNull(staticRpaAuthFeign) || Objects.isNull(id)) {
            return null;
        }

        try {
            AppResponse<String> response = staticRpaAuthFeign.getRealNameById(id);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据用户ID获取用户真实姓名失败: {}, 响应: {}", id, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据用户ID获取用户真实姓名失败: {}", id, e);
            return null;
        }
    }

    /**
     * 根据用户id查询用户名
     *
     * @param id
     * @return
     */
    public static String getLoginNameById(String id) {
        if (Objects.isNull(staticRpaAuthFeign) || Objects.isNull(id)) {
            return null;
        }

        try {
            AppResponse<String> response = staticRpaAuthFeign.getLoginNameById(id);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据用户ID查询登录名失败: {}, 响应: {}", id, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据用户ID查询登录名失败: {}", id, e);
            return null;
        }
    }

    /**
     * 根据userIdList查用户基本信息列表,最多支持100个id
     *
     * @param userIdList
     * @return
     */
    public static List<User> queryUserPageList(List<String> userIdList) throws IOException {
        if (Objects.isNull(staticRpaAuthFeign) || CollectionUtils.isEmpty(userIdList)) {
            return Collections.emptyList();
        }

        try {
            // 限制最多100个ID，去重
            List<String> limitedUserIds =
                    userIdList.stream().distinct().limit(100).collect(Collectors.toList());

            AppResponse<List<User>> response = staticRpaAuthFeign.queryUserListByIds(limitedUserIds);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据用户ID列表查询用户信息失败, 响应: {}", response != null ? response.getMessage() : "null");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("根据用户ID列表查询用户信息失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return true如果已登录，false如果未登录
     */
    public static boolean isCurrentUserLogin() {
        if (Objects.isNull(staticRpaAuthFeign)) {
            return false;
        }

        try {
            AppResponse<User> response = staticRpaAuthFeign.getCurrentLoginUser();
            return response != null && response.ok() && response.getData() != null;
        } catch (Exception e) {
            log.warn("检查用户登录状态失败", e);
            return false;
        }
    }

    /**
     * 检查登录状态并返回响应
     *
     * @return 登录状态响应
     */
    public static AppResponse<?> nowLoginUserResponse() {
        if (isCurrentUserLogin()) {
            return AppResponse.success("用户已登录");
        } else {
            return AppResponse.error(ErrorCodeEnum.E_API, "用户未登录");
        }
    }

    /**
     * 转换成 id ：User的格式
     *
     * @param userList
     * @return
     */
    public static Map<String, User> getUserMap(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) return MapUtils.EMPTY_SORTED_MAP;

        Map<String, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }

        return userMap;
    }

    /**
     * 转换成 id ：User的格式
     *
     * @param userList
     * @return
     */
    public static Map<String, String> getUserNameMap(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) return MapUtils.EMPTY_SORTED_MAP;

        Map<String, String> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user.getLoginName());
        }

        return userMap;
    }

    /**
     * 根据roleid 查role详情(roleId用"name"代替)
     *
     * @param roleName
     * @return 角色
     */
    public static Role queryRoleDetail(String roleName) throws IOException {
        if (Objects.isNull(staticRpaAuthFeign) || StringUtils.isEmpty(roleName)) {
            return null;
        }

        try {
            GetRoleDto dto = new GetRoleDto();
            dto.setId(roleName);
            AppResponse<Role> response = staticRpaAuthFeign.queryRoleDetail(dto);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据角色名称查询角色详情失败: {}, 响应: {}", roleName, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据角色名称查询角色详情失败: {}", roleName, e);
            return null;
        }
    }

    /**
     * 获取当前用户权限列表
     *
     * @return 权限列表
     */
    public static List<Permission> getCurrentUserPermissionList() throws NoLoginException {
        if (Objects.isNull(staticRpaAuthFeign)) {
            throw new NoLoginException("Feign客户端未初始化");
        }

        try {
            AppResponse<List<Permission>> response = staticRpaAuthFeign.getCurrentUserPermissionList();
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn(
                        "获取当前用户权限列表失败, 响应: {}",
                        response != null ? response.getMessage() : "null");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("获取当前用户权限列表失败", e);
            throw new NoLoginException("获取当前用户权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色列表
     *
     * @return 角色列表
     */
    public static List<Role> getCurrentUserRoleList() throws NoLoginException {
        if (Objects.isNull(staticRpaAuthFeign)) {
            throw new NoLoginException("Feign客户端未初始化");
        }

        try {
            AppResponse<List<Role>> response = staticRpaAuthFeign.getUserRoleList();
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("获取用户角色列表失败, 响应: {}", response != null ? response.getMessage() : "null");
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("获取用户角色列表失败", e);
            throw new NoLoginException("获取用户角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据电话获取用户信息
     *
     * @param PhoneNumber
     * @return
     */
    public static User getUserInfoByPhone(String PhoneNumber) {
        if (Objects.isNull(staticRpaAuthFeign) || Objects.isNull(PhoneNumber)) {
            return null;
        }

        try {
            AppResponse<User> response = staticRpaAuthFeign.getUserInfoByPhone(PhoneNumber);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据用户电话获取用户信息失败: {}, 响应: {}", PhoneNumber, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据用户电话获取用户信息失败: {}", PhoneNumber, e);
            return null;
        }
    }

    /**
     * 根据电话获取用户姓名
     *
     * @param phoneNumber
     * @return
     */
    public static String getRealNameByPhone(String phoneNumber) {
        if (Objects.isNull(staticRpaAuthFeign) || Objects.isNull(phoneNumber)) {
            return null;
        }

        try {
            AppResponse<String> response = staticRpaAuthFeign.getRealNameByPhone(phoneNumber);
            if (response != null && response.ok() && response.getData() != null) {
                return response.getData();
            } else {
                log.warn("根据电话获取用户姓名失败: {}, 响应: {}", phoneNumber, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据电话获取用户姓名失败: {}", phoneNumber, e);
            return null;
        }
    }

    /**
     * 通过name获取用户
     */
    public static User getUserByName(String name) throws IOException {
        if (Objects.isNull(staticRpaAuthFeign) || StringUtils.isBlank(name)) {
            return null;
        }

        try {
            // 使用搜索接口，根据name搜索用户
            AppResponse<List<User>> response = staticRpaAuthFeign.searchUserByName(name, null);
            if (response != null
                    && response.ok()
                    && response.getData() != null
                    && !response.getData().isEmpty()) {
                // 返回第一个匹配的用户
                return response.getData().get(0);
            } else {
                log.warn("根据用户名获取用户失败: {}, 响应: {}", name, response != null ? response.getMessage() : "null");
                return null;
            }
        } catch (Exception e) {
            log.error("根据用户名获取用户失败: {}", name, e);
            return null;
        }
    }
}
