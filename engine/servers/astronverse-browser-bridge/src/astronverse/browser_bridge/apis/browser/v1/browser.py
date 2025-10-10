import asyncio
from fastapi import APIRouter, Depends, Request
from astronverse.browser_bridge.apis.context import get_svc, ServiceContext
from astronverse.browser_bridge.apis.response import CustomResponse
from astronverse.websocket_server.ws_service import BaseMsg
from astronverse.browser_bridge.apis.ws_route import wsmg, error_to_base_error
from astronverse.browser_bridge.error import *

router = APIRouter()


@router.post("/transition")
async def transition(request: Request, svc: ServiceContext = Depends(get_svc)):
    req_data = await request.json()
    key = req_data.get("key", "")
    data = req_data.get("data", {})
    data_path = req_data.get("data_path", "")
    time_out_ws = req_data.get("time_out", 10)
    if data_path and not data:
        with open(data_path, "r", encoding="utf-8") as file:
            # 读取文件内容
            data = file.read()
    browser_type = req_data.get("browser_type", "")
    if (not key) or (not data) or (not browser_type):
        raise BaseException(
            PARAMETER_ERROR_FORMAT.format((key, data, browser_type)),
            "error: PARAMETER ERROR FORMAT {}".format((key, data, browser_type)),
        )

    wait = asyncio.Event()
    res = {}
    res_e = None

    base_msg = BaseMsg(
        channel="browser", key=key, uuid="$root$", send_uuid="${}$".format(browser_type), need_ack=False, data=data
    ).init()

    # 回调事件
    def callback(watch_msg: BaseMsg = None, e: Exception = None):
        nonlocal wait, res, res_e
        if watch_msg:
            res = watch_msg.data
        if e:
            res_e = e
        wait.set()

    # 发送逻辑
    try:
        await wsmg.send_reply(base_msg, time_out_ws, callback)
    except Exception as e:
        raise error_to_base_error(e)

    # 等待
    await wait.wait()
    if res_e:
        raise error_to_base_error(res_e)

    # 正常回复
    return CustomResponse.tojson(res)


@router.get("/health")
async def health():
    return "ok"
