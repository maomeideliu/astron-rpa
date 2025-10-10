package com.iflytek.rpa.robot.service;

import com.iflytek.rpa.monitor.entity.DeptUser;
import com.iflytek.rpa.monitor.entity.dto.HisBaseDto;
import com.iflytek.rpa.robot.entity.dto.*;
import com.iflytek.rpa.robot.entity.vo.RobotExecuteByNameNDeptVo;
import com.iflytek.rpa.starter.exception.NoLoginException;
import com.iflytek.rpa.starter.utils.response.AppResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 云端机器人表(RobotExecute)表服务接口
 *
 * @author mjren
 * @since 2024-10-22 16:07:33
 */
public interface RobotExecuteService {
    AppResponse<?> executeList(ExecuteListDto queryDto) throws NoLoginException;

    AppResponse<?> updateRobotByPull(String robotId) throws NoLoginException;

    AppResponse<?> deleteRobotRes(String robotId) throws NoLoginException;

    AppResponse<?> deleteRobot(DeleteDesignDto queryDto) throws Exception;

    AppResponse<?> robotDetail(String robotId) throws Exception;

    AppResponse<?> executeUpdateCheck(ExeUpdateCheckDto queryDto) throws NoLoginException;

    List<HisBaseDto> countRobotTotalNumByDate(String endOfDay, List<DeptUser> userIdList);

    AppResponse<List<RobotExecuteByNameNDeptVo>> getRobotExecuteList(@RequestBody RobotExecuteByNameNDeptDto queryDto) throws NoLoginException;

    AppResponse<List<RobotNameDto>> getRobotNameListByName(String robotName, String deptId);

    AppResponse<?> transferRobot(TransferRobotDto transferRobotDto) throws Exception;

}
