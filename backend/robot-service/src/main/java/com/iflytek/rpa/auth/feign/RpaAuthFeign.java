package com.iflytek.rpa.auth.feign;

import com.iflytek.rpa.auth.feign.entity.*;
import com.iflytek.rpa.auth.feign.entity.dto.*;
import com.iflytek.rpa.starter.utils.response.AppResponse;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @desc: TODO
 * @author: weilai <laiwei3@iflytek.com>
 * @create: 2025/12/18 17:23
 */
@FeignClient( name="rpa-auth",
        url = "http://rpaauth-service:10251"
//        url = "http://localhost:10251"
        , configuration = FeignAutoConfiguration.class)
public interface RpaAuthFeign {
    // ==================== UserController 用户相关接口 ====================

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/api/rpa-auth/user/info")
    AppResponse<User> getLoginUser();

    /**
     * 获取用户详细信息
     * @param tenantId
     * @param dto
     * @return
     */
    @PostMapping("/api/rpa-auth/user/getUserExtendInfo")
    AppResponse<UserExtendDto> getUserExtendInfo(@RequestParam("tenantId")String tenantId, @RequestBody GetUserDto dto);

    /**
     * 注册(Uap的注册接口，仅作兼容备用)
     * @param registerDto 注册DTO
     * @return 注册结果
     */
    @PostMapping("/api/rpa-auth/user/register")
    AppResponse<String> registerUap(@RequestBody Object registerDto);

    /**
     * 新增员工
     * @param createUapUserDto 创建用户DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/add")
    AppResponse<String> addUser(@RequestBody Object createUapUserDto);

    /**
     * 编辑员工
     * @param updateUapUserDto 更新用户DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/edit")
    AppResponse<String> editUser(@RequestBody Object updateUapUserDto);

    /**
     * 删除员工
     * @param userDto 用户删除DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/delete")
    AppResponse<String> deleteUser(@RequestBody Object userDto);

    /**
     * 启用/禁用员工
     * @param userDto 用户启用DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/enable")
    AppResponse<String> enableUser(@RequestBody Object userDto);

    /**
     * 变更部门
     * @param userDto 用户变更部门DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/changeDept")
    AppResponse<String> changeDept(@RequestBody Object userDto);

    /**
     * 查询当前机构的全部用户(部门新增，部门负责人下拉框)
     * @param orgId 部门ID
     * @return 用户列表
     */
    @GetMapping("/api/rpa-auth/user/queryAllListByOrgId")
    AppResponse<List<User>> queryUserDetailListByOrgId(@RequestParam("orgId") String orgId);

    /**
     * 分页查询当前机构的用户
     * @param listUserDto 查询用户DTO
     * @return 用户分页列表
     */
    @PostMapping("/api/rpa-auth/user/queryListByOrgId")
    AppResponse<PageDto<DeptUserDto>> queryUserListByOrgId(@RequestBody Object listUserDto);

    /**
     * 分页获取角色绑定的用户列表，可根据登录名或姓名模糊查询
     * @param listUserByRoleDto 查询用户DTO
     * @return 用户分页列表
     */
    @PostMapping("/api/rpa-auth/user/queryBindListByRole")
    AppResponse<PageDto<User>> queryBindListByRole(@RequestBody Object listUserByRoleDto);

    /**
     * 人员解绑角色
     * @param bindRoleDto 绑定角色DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/unbindRole")
    AppResponse<String> unbindRole(@RequestBody Object bindRoleDto);

    /**
     * 按名称模糊搜索所有员工或部门
     * @param name 名称
     * @return 搜索结果
     */
    @GetMapping("/api/rpa-auth/user/searchDeptOrUser")
    AppResponse<GetDeptOrUserDto> searchDeptOrUser(@RequestParam("name") String name);

    /**
     * 角色管理-根据部门id查询部门下的人员和子部门
     * @param id 部门ID
     * @return 部门和人员列表
     */
    @GetMapping("/api/rpa-auth/user/queryUserAndDept")
    AppResponse<List<CurrentDeptUserDto>> queryUserAndDept(@RequestParam("id") String id);

    /**
     * 角色管理-根据名字或手机号模糊查询员工
     * @param keyWord 关键字
     * @return 用户列表
     */
    @GetMapping("/api/rpa-auth/user/searchUserWithStatus")
    AppResponse<List<CurrentDeptUserDto>> searchUserWithStatus(@RequestParam("keyWord") String keyWord);

    /**
     * 角色管理-添加成员
     * @param bindUserListDto 绑定用户列表DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/user/batchBindRole")
    AppResponse<String> bindUserListRole(@RequestBody Object bindUserListDto);

    /**
     * 卓越中心-机器人看板-所有者下拉选择-查询接口
     * 根据输入的关键字（姓名或手机号）查询用户
     * @param keyword 关键字
     * @param deptId 部门ID
     * @return 用户列表
     */
    @PostMapping("/api/rpa-auth/user/getUserByNameOrPhone")
    AppResponse<List<User>> getUserByNameOrPhone(@RequestParam("keyword") String keyword, @RequestParam(value = "deptId", required = false) String deptId);

    /**
     * 获取当前登录用户
     * @return 当前登录用户信息
     */
    @GetMapping("/api/rpa-auth/user/current")
    AppResponse<User> getCurrentLoginUser();

    /**
     * 获取当前登录用户ID
     * @return 当前登录用户ID
     */
    @GetMapping("/api/rpa-auth/user/current/id")
    AppResponse<String> getCurrentUserId();

    /**
     * 获取当前登录用户名
     * @return 当前登录用户名
     */
    @GetMapping("/api/rpa-auth/user/current/username")
    AppResponse<String> getCurrentLoginUsername();

    /**
     * 根据用户ID查询登录名
     * @param id 用户ID
     * @return 登录名
     */
    @GetMapping("/api/rpa-auth/user/loginName")
    AppResponse<String> getLoginNameById(@RequestParam("id") String id);

    /**
     * 根据用户ID查询姓名
     * @param id 用户ID
     * @return 用户姓名
     */
    @GetMapping("/api/rpa-auth/user/realName")
    AppResponse<String> getRealNameById(@RequestParam("id") String id);

    /**
     * 根据用户ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/api/rpa-auth/user/infoById")
    AppResponse<User> getUserInfoById(@RequestParam("id") String id);

    /**
     * 根据手机号查询用户姓名
     * @param phone 手机号
     * @return 用户姓名
     */
    @GetMapping("/api/rpa-auth/user/phone/realName")
    AppResponse<String> getRealNameByPhone(@RequestParam("phone") String phone);

    /**
     * 根据手机号查询登录名
     * @param phone 手机号
     * @return 登录名
     */
    @GetMapping("/api/rpa-auth/user/phone/loginName")
    AppResponse<String> getLoginNameByPhone(@RequestParam("phone") String phone);

    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("/api/rpa-auth/user/phone/info")
    AppResponse<User> getUserInfoByPhone(@RequestParam("phone") String phone);

    /**
     * 根据用户ID列表查询用户信息列表（最多支持100个id）
     * @param userIdList 用户ID列表
     * @return 用户信息列表
     */
    @PostMapping("/api/rpa-auth/user/queryByIds")
    AppResponse<List<User>> queryUserListByIds(@RequestBody List<String> userIdList);

    /**
     * 根据姓名模糊查询人员
     * @param keyword 关键字
     * @param deptId 部门ID（可选）
     * @return 用户信息列表
     */
    @GetMapping("/api/rpa-auth/user/search/name")
    AppResponse<List<User>> searchUserByName(@RequestParam("keyword") String keyword,
                                             @RequestParam(value = "deptId", required = false) String deptId);

    /**
     * 根据手机号模糊查询人员
     * @param keyword 关键字
     * @param deptId 部门ID（可选）
     * @return 用户信息列表
     */
    @GetMapping("/api/rpa-auth/user/search/phone")
    AppResponse<List<User>> searchUserByPhone(@RequestParam("keyword") String keyword,
                                              @RequestParam(value = "deptId", required = false) String deptId);

    /**
     * 根据姓名或手机号模糊查询人员
     * @param keyword 关键字
     * @param deptId 部门ID（可选）
     * @return 用户信息列表
     */
    @GetMapping("/api/rpa-auth/user/search")
    AppResponse<List<User>> searchUserByNameOrPhone(@RequestParam("keyword") String keyword,
                                                    @RequestParam(value = "deptId", required = false) String deptId);

    /**
     * 获取当前用户权限列表
     * @return 用户权限列表
     */
    @GetMapping("/api/rpa-auth/user/current/permissions")
    AppResponse<List<Permission>> getCurrentUserPermissionList();

    // ==================== TenantController 租户相关接口 ====================

    /**
     * 获取租户ID
     * @return
     */
    @GetMapping("/api/rpa-auth/tenant/getTenantId")
    AppResponse<String> getTenantId();

    /**
     * 根据租户id获取所有组织列表
     * @param tenantId
     * @return
     */
    @GetMapping("/api/rpa-auth/tenant/getAllOrgList")
    AppResponse<List<Org>> queryAllOrgList(@RequestParam("tenantId")String tenantId);

    /**
     * 当前登录用户在此应用的租户列表
     * @return 租户列表
     */
    @GetMapping("/api/rpa-auth/tenant/getTenantListInApp")
    AppResponse<List<Tenant>> getTenantListInApp();

    /**
     * 企业信息查询
     * @return 企业信息
     */
    @GetMapping("/api/rpa-auth/tenant/getTenantInfo")
    AppResponse<TenantInfoDto> getTenantInfo();

    /**
     * 更改企业管理员（暂不支持）
     * @param id 管理员ID
     * @return 操作结果
     */
    @GetMapping("/api/rpa-auth/tenant/changeManager")
    AppResponse<String> changeManager(@RequestParam("id") String id);

    /**
     * 获取所有用户
     * @param userName 用户名
     * @return 用户列表
     */
    @PostMapping("/api/rpa-auth/tenant/all-user")
    AppResponse<List<UserVo>> getAllUser(@RequestParam("userName") String userName);

    /**
     * 获取当前登录的租户ID
     * @return 当前登录的租户ID
     */
    @GetMapping("/api/rpa-auth/tenant/current/id")
    AppResponse<String> getCurrentTenantId();

    /**
     * 获取当前登录的租户名称
     * @return 当前登录的租户名称
     */
    @GetMapping("/api/rpa-auth/tenant/current/name")
    AppResponse<String> getCurrentTenantName();

    /**
     * 根据租户ID查询租户信息
     * @param tenantId 租户ID
     * @return 租户信息
     */
    @GetMapping("/api/rpa-auth/tenant/info")
    AppResponse<Tenant> queryTenantInfoById(@RequestParam("tenantId") String tenantId);

    /**
     * 切换租户
     * @param tenantId 切换租户id
     * @return 切换结果
     */
    @PostMapping("/api/rpa-auth/tenant/switch")
    AppResponse<String> switchTenant(@RequestParam("tenantId") String tenantId);

    // ==================== RoleController 角色相关接口 ====================

    /**
     * 获取用户角色列表
     * @return 角色列表
     */
    @GetMapping("/api/rpa-auth/role/getUserRoleList")
    AppResponse<List<Role>> getUserRoleList();

    /**
     * 查询角色详情
     * @param dto
     * @return
     */
    @PostMapping("/api/rpa-auth/role/queryDetail")
    AppResponse<Role> queryRoleDetail(@RequestBody GetRoleDto dto);

    /**
     * 查询应用内全部角色列表
     * @return 角色列表
     */
    @GetMapping("/api/rpa-auth/role/getUserRoleListInApp")
    AppResponse<List<Role>> queryRoleTreeList();

    /**
     * 新增角色
     * @param createRoleDto 创建角色DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/role/add")
    AppResponse<String> addRole(@RequestBody Object createRoleDto);

    /**
     * 编辑角色
     * @param updateRoleDto 更新角色DTO
     * @return 操作结果
     */
    @PostMapping("/api/rpa-auth/role/update")
    AppResponse<String> updateRole(@RequestBody Object updateRoleDto);

    /**
     * 删除角色
     * @param deleteCommonDto 删除角色DTO
     * @return 删除结果
     */
    @PostMapping("/api/rpa-auth/role/delete")
    AppResponse<String> deleteRole(@RequestBody Object deleteCommonDto);

    /**
     * 根据名称模糊查询角色
     * @param listRoleDto 查询角色DTO
     * @return 角色分页列表
     */
    @PostMapping("/api/rpa-auth/role/search")
    AppResponse<PageDto<Role>> searchRole(@RequestBody Object listRoleDto);
}
