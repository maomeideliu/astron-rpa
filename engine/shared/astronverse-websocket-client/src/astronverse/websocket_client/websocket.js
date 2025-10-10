function gen_event_id() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        let r = Math.random() * 16 | 0,
            v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
    });
}

function gen_ack_msg(event_id = "") {
    let msg = new BaseMsg()
    msg.channel=AckMsg.channel
    msg.event_id=event_id
    return msg
}

function gen_time() {
    return Math.floor(new Date().getTime() / 1000)
}

function default_log(msg) {
    console.log(msg)
}

class BaseMsg {
    constructor(
        reply_event_id=undefined,
        event_id=undefined,
        event_time=undefined,
        channel=undefined,
        key=undefined,
        uuid=undefined,
        send_uuid=undefined,
        need_ack=undefined,
        need_reply=undefined,
        data=undefined
    ) {
        // 回复事件id 已http的形式使用 适合一来一回的消息
        this.reply_event_id = reply_event_id
        // 事件id 唯一值
        this.event_id= event_id
        // 事件发生的时间戳
        this.event_time= event_time
        // 业务 大业务
        this.channel= channel
        // 事件名称 大业务下面的小事件
        this.key= key
        // 用户名，客户标识，同一个客户多个连接，发送时会广播，主要时解决回收不及时问题
        this.uuid= uuid
        // 消息发送给
        this.send_uuid= send_uuid
        // 是否需要ack, 无效
        this.need_ack= need_ack
        // 是否需要回复
        this.need_reply = need_reply
        // 数据
        this.data= data
    }

    to_reply() {
        // 消息回复
        let res_msg = new BaseMsg()
        res_msg.reply_event_id = this.event_id
        res_msg.channel = this.channel
        res_msg.key = this.key
        res_msg.uuid = this.send_uuid
        res_msg.send_uuid = this.uuid
        res_msg.need_ack = this.need_ack
        res_msg.data = undefined
        return res_msg.init()
    }
    init() {
        this.event_id = gen_event_id()
        this.event_time = gen_time()
        return this
    }
    tojson() {
        return JSON.stringify(this);
    }
}

class WsException extends Error {}
class WatchRetry extends WsException {}
class WatchTimeout extends WsException {}
class WsError extends WsException {}
class PingTimeoutError extends WsError {}
class MsgUnlawfulnessError extends WsError {}


// 特殊消息
const PingMsg = new BaseMsg()
PingMsg.channel="ping"
const PongMsg = new BaseMsg()
PongMsg.channel="pong"
const ExitMsg = new BaseMsg()
ExitMsg.channel="exit"
const AckMsg = new BaseMsg()
AckMsg.channel="ack"


class Route {
    constructor(channel, key, func) {
        // 同msg的channel
        this.channel = channel
        // 同msg的key
        this.key = key
        // 回调
        this.func = func
    }
}

class Watch {
    constructor(watch_type, watch_key, func=undefined, retry_time=0) {
        // 监听类型
        this.watch_type = watch_type
        // 监听key
        this.watch_key = watch_key
        // 触发callback
        this.callback = func
        // 额外重试次数
        this.retry_time = retry_time
        // 重试间隔
        this.interval = 10

        // 中间数据
        this.time = 0
        this.timeout = 0
    }

    init(interval){
        this.interval = interval
        this.timeout = gen_time() + this.interval
        return this
    }

    retry() {
        this.time += 1
        this.timeout += this.interval
    }
}

class WsApp {
    constructor(
            url,
            ping_interval=30,
            reconnect_interval=10,
            reconnect_max_time=20,
            log=default_log,
        ) {
        // 配置
        this.url = url
        this.log = log

        // 状态
        this.ws_app = undefined
        this.running = false

        // ping机制
        this.ping_interval = ping_interval

        // 路由管理
        this.routes = {} // map[str, Route]

        // 重启机制
        this.reconnect_time = 0
        this.reconnect_interval = reconnect_interval
        this.reconnect_max_time = reconnect_max_time

        // 消息监听
        this.watch_interval = 1
        this.watch_msg  = {} // map[str, Watch]
        this.watch_msg_queue = [] 

        // 异步任务
        this.start_ping_task = undefined
    }
    _send_text(msg) {
        if (this.ws_app) {
            this.ws_app.send(msg.tojson())
        }
    }

    _call_route(channel, key, msg) {
        let name = `${channel}$$${key}`
        let no_key_name = `${channel}$$`
        if (name in this.routes)
        {
            let func = this.routes[name].func
            return func(msg)
        }
        else if (no_key_name in this.routes)
        {
            let func = this.routes[no_key_name].func
            return func(msg)
        }
        else {
           throw new WsException(`func is not exist: ${channel} ${key}`)
        }
    }

    _call_watch(watch, msg, error) {
        if (watch.callback != undefined) {
            watch.callback(msg, error)
        }
    }

    event(channel, key, func) {
        let temp_channel = `${channel}$$${key}`
        this.routes[temp_channel] = new Route(channel, key, func)
    }

    _add_watch(watch) {
        let name = `${watch.watch_type}$$${watch.watch_key}`
        this.watch_msg[name] = watch
        this.watch_msg_queue.push({time: watch.timeout, name: name})
        this.watch_msg_queue.sort((a, b)=> a.time - b.time)
    }

    _clear_watch() {
        setTimeout(() => {
            while (this.running && this.watch_msg_queue.length > 0)
            {
                let now = gen_time()
                if (this.watch_msg_queue[0].time > now) {
                    break
                }

                try {
                    let name = this.watch_msg_queue[0].name
                    this.watch_msg_queue.shift()

                    if (name in this.watch_msg) {
                        let watch = this.watch_msg[name]
                        watch.retry()
                        if (watch.time > watch.retry_time) {
                            this._call_wait(watch, undefined, WatchTimeout("watch timeout"))
                            delete this.watch_msg[name]
                        } else {
                            this._call_wait(watch, undefined, WatchRetry("retry"))
                            this.watch_msg_queue.push({time: watch.timeout, name: name})
                            this.watch_msg_queue.sort((a, b)=> a.time - b.time)
                        }
                    }
                } catch(error) {
                    this.log(`error _clear_watch: ${error}`)
                }
            }
            this._clear_watch()
        }, this.watch_interval * 1000)
    }

    _close_watch() {
        this.watch_msg  = {} // map[str, Watch]
        this.watch_msg_queue = [] 
    }

    _start_ping() {
        this.start_ping_task = setInterval(() => {
            if (this.running) {
                this.log(this)
                this._send_text(PingMsg)
            }
        }, this.ping_interval * 1000);
    }

    _close_ping() {
        clearInterval(this.start_ping_task)
        this.start_ping_task = undefined
    }

    send(msg) {
        let send = () => {
            this._send_text(msg)
        }
        send()
    }

    send_reply(msg, timeout, success_func, error_func=undefined) {
        msg.need_reply = true
        this._add_watch(new Watch("reply", msg.event_id, (msg, error) => {
            if (error == undefined) {
                success_func(msg)
            } else {
                error_func ? error_func(error) : "" 
            }
        }).init(timeout))
        this.send(msg)
    }

    _on_message() {
        let that = this
        return function(event) {
            let data = JSON.parse(event.data)
            let msg = new BaseMsg(
                data.reply_event_id,
                data.event_id,
                data.event_time ,
                data.channel,
                data.key,
                data.uuid,
                data.send_uuid,
                data.need_ack,
                data.data
            )
            setTimeout(function() {
                // 拦截特殊消息
                if (msg.channel == PongMsg.channel) {
                    return
                }
                else if (msg.channel == AckMsg.channel) {
                    let name = `${"ack"}$$${msg.event_id}`
                    if (name in that.watch_msg) {
                        let watch = that.watch_msg[name]
                        that._call_wait(watch, msg, undefined)
                        delete that.watch_msg[name]
                    } 
                    return
                }
                else if (msg.channel == ExitMsg.channel) {
                    that.log(`error ExitMsg: ${msg.tojson}`)
                    return
                }

                // 拦截reply消息
                if (msg.reply_event_id) {
                    let name = `${"reply"}$$${msg.reply_event_id}`
                    if (name in that.watch_msg) {
                        let watch = that.watch_msg[name]
                        that._call_watch(watch, msg, undefined)
                        delete that.watch_msg[name]
                    }
                    return
                }
                // 分发消息
                let res_msg = msg.to_reply()
                try {
                    let res = that._call_route(msg.channel, msg.key, msg)
                    res_msg.data = res
                } catch (error) {
                    res_msg.data = String(error)
                }

                // 消息返回
                try {
                    if (res_msg.data != undefined) {
                        that.send(res_msg)
                    }
                } catch(error) {
                    that.log(`error _call_route: ${error}`)
                }
            }, 0)
        }
    }

    _on_open() {
        let that = this
        return function(event) {
            that.running = true
            that._start_ping()
            that._clear_watch()
            try {
                that._call_route("open", "", undefined)
            } catch (error) {
                that.log(`error _on_open _call_route: ${error}`)
            }
        }
    }
    
    _on_close() {
        let that = this
        return function(event) {
            that.running = false
            that._close_ping()
            that._close_watch()
            that.ws_app = undefined
            try {
                that._call_route("close", "", undefined)
            } catch (error) {
                that.log(`error _on_close _call_route: ${error}`)
            }
            that._reconnect()
        }
    }

    _on_error() {
        let that = this
        return function(event) {
            try {
                that._call_route("error", "", undefined)
            } catch (error) {
                that.log(`error _on_error _call_route: ${error}`)
            }
            that.log(`error _on_error:${event}`)
        }
    }

    _reconnect() {
        setTimeout(()=>{
            if (this.reconnect_max_time < 0) {
                this.log(`_reconnect star: ${this.reconnect_time}`)
                this.start()
                this.log(`_reconnect end: ${this.reconnect_time}`)
            } else if (this.reconnect_time < this.reconnect_max_time) {
                this.reconnect_time += 1
                this.log(`_reconnect star: ${this.reconnect_time}`)
                this.start()
                this.log(`_reconnect end: ${this.reconnect_time}`)
            }
        }, this.reconnect_interval * 1000)
    }

    start() {
        try {
            this.ws_app = new WebSocket(this.url)
            this.ws_app.onopen = this._on_open()
            this.ws_app.onmessage = this._on_message()
            this.ws_app.onclose = this._on_close()
            this.ws_app.onerror = this._on_error()
        } catch (error) {
            that.log(`error start: ${error}`)
        }
    }
}

//(function(){
//    // 例子1
//
//    // 1. new一个实例，参数请查看WsApp内注释
//    app = new WsApp("ws://127.0.0.1:8087/?token={you_token}", 5)
//
//    // 2. 监听系统事件
//    app.event("open", "", function(){
//
//        // 2.1 从零构建一个消息体
//        send_msg = new BaseMsg()
//        send_msg.channel = "browser"
//        send_msg.key = "transition"
//        send_msg.need_ack = false
//        send_msg.data = {
//            "a": 2
//        }
//
//        // 2.2 发送并关心服务端返回结果
//        app.send_reply(send_msg.init(), 10, function(msg){
//            console.log("send_reply", msg)
//        }, function(error) {
//            console.log("send_reply", error)
//        })
//
//        // 2.3 发送不关心服务端返回结果
//        app.send(send_msg.init())
//    })
//
//    // 3. 监听业务事件
//    app.event("browser", "transition2", function(msg){
//        // 3.1 如果return 有值会自动发送出去(接受后发送)，如果return 为空就不会发送(只接受不发送)
//        return {"a": 1}
//
//        // 3.2 和3.1的作用一样，手动写，提高灵活度
//        new_msg = msg.to_reply()
//        new_msg.data = {a: "1"}
//        app.send(new_msg)
//        return
//
//    })
//
//    // 4. 启动
//    app.start()
//})()