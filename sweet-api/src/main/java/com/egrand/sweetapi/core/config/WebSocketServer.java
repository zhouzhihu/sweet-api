package com.egrand.sweetapi.core.config;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/imserver/{userId}")
@Component
@Slf4j
public class WebSocketServer {
    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String, List<WebSocketServer>> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        List<WebSocketServer> webSocketList = webSocketMap.get(userId);
        if (null == webSocketList)
            webSocketList = new ArrayList<>();
        // 加入set中
        if (!CollUtil.contains(webSocketList, this)) {
            webSocketList.add(this);
        }
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.put(userId, webSocketList);
        } else {
            webSocketMap.put(userId, webSocketList);
            // 在线数加1
            addOnlineCount();
        }
        log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "连接成功");
            sendMessage(jsonObject.toJSONString());
        } catch (IOException e) {
            log.error("用户:"+userId+",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            List<WebSocketServer> webSocketList = webSocketMap.get(userId);
            webSocketList.remove(this);
            if (CollUtil.isEmpty(webSocketList)) {
                webSocketMap.remove(userId);
                // 从set中删除
                subOnlineCount();
                log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
            } else {
                webSocketMap.put(userId, webSocketList);
            }
        } else {
            log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if(StringUtils.isNotBlank(message)){
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId",this.userId);
                String toUserId = jsonObject.getString("toUserId");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserId) && webSocketMap.containsKey(toUserId)) {
                    List<WebSocketServer> webSocketList = webSocketMap.get(toUserId);
                    if (CollUtil.isNotEmpty(webSocketList)) {
                        for (WebSocketServer webSocketServer : webSocketList) {
                            webSocketServer.sendMessage(jsonObject.toJSONString());
                        }
                    }
                } else {
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     * */
    public static Boolean sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            List<WebSocketServer> webSocketList = webSocketMap.get(userId);
            if (CollUtil.isNotEmpty(webSocketList)) {
                for (WebSocketServer webSocketServer : webSocketList) {
                    webSocketServer.sendMessage(message);
                }
            }
            return true;
        } else {
            log.error("用户" + userId + ",不在线！");
            return false;
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
