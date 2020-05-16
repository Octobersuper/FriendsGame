package com.zcf.framework.gameCenter.goldenflower.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import com.zcf.framework.gameCenter.goldenflower.comm.Establish_PK;
import com.zcf.framework.gameCenter.goldenflower.dao.F_LoginDao;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import com.zcf.framework.gameCenter.mahjong.service.Mahjong_Socket;
import org.springframework.stereotype.Component;
import util.BaseDao;
import util.MapHelper;
import util.System_Mess;
import util.UtilClass;

@ServerEndpoint("/WebSoket_F/{openid}")
@Component
public class F_WebSocket {
    private Gson gson = new Gson();// json转换
    public Session session;
    // 用户信息
    public UserBean userBean;
    // 自己进入的房间
    private F_PKBean pkBean;
    private BaseDao baseDao = new BaseDao();
    // 登陆dao
    private F_LoginDao loginDao = new F_LoginDao(baseDao);
    // 游戏逻辑类
    private F_GameService gameService = new F_GameService(baseDao);
    private Map<String, Object> returnMap = new HashMap<String, Object>();

    /**
     * 连接
     *
     * @param
     * @param session
     * @throws IOException
     */
    @OnOpen
    public void onOpen(@PathParam("openid") String openid, Session session) throws IOException {
        boolean bool = true;
        if (openid != null) {
            // 查询出用户信息t
            userBean = loginDao.getUser(openid);
            baseDao.CloseAll();
            if (userBean != null) {
                //验证是否已经在线
                if (F_PublicState.clients.get(openid) != null) {
                    F_PublicState.clients.put(openid, this);
                    this.session = session;
                    returnMap.put("type", "contion");
                    returnMap.put("state", "0");
                    sendMessageTo(returnMap);
                    System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接(扎金花)(重复)");
                    bool = false;
                } else {
                    //userBean.setHu_state(-1);//临时使用 含义为当前用户处在大厅当中
                    //将自己放入客户端集合中
                    F_PublicState.clients.put(openid, this);
                    this.session = session;
                    returnMap.put("type", "contion");
                    returnMap.put("state", "0");
                    sendMessageTo(returnMap);
                    //selectRooms(userBean.getNumber_3(),userBean.getDttype());
                    //selectRooms(userBean.getNumber_3(),3);
                    System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接(扎金花)");
                    bool = false;
                }
            } else {
                bool = false;
            }
        }
        //如果没走正常业务则归类非法连接
        if (bool) {
            try {
                session.close();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                System.out.println("xiaoxi" + e.getMessage());
            }
            System_Mess.system_Mess.ToMessagePrint("非法连接");
        }
    }

    /**
     * 关闭
     *
     * @throws IOException
     */
    @OnClose
    public void onClose() throws IOException {
        if (userBean != null) {
            //如果房间存在则退出剔除自己
            if (pkBean != null){
                if(pkBean.getRoom_state()==0){
                    if (userBean.getUser_type() == 1) {
                        returnMap.put("userid", userBean.getUserid());
                        returnMap.put("state", "0");
                        returnMap.put("type", "exit");
                        sendMessageTo(returnMap,pkBean);
                        returnMap.clear();
                        pkBean.remove_opsitions(userBean.getUserid());
                    }
                    pkBean.getGame_userList(1).remove(userBean);
                }else{
                    if(userBean.getUser_type()==1){
                        userBean.setGametype(2);
                    }else{
                        pkBean.getGame_userList(1).remove(userBean);
                    }
                }
            }else{
                F_PublicState.clients.remove(userBean.getOpenid());
            }
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "断开连接");
        }
    }


    /**
     * 接收消息
     *
     * @param message
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message) {
        session.setMaxIdleTimeout(60000);
        returnMap.clear();
        // 解密
        // message = RC4.decry_RC4(message);
        System_Mess.system_Mess.ToMessagePrint("接收" + userBean.getNickname() + "的消息" + message);
        Map<String, String> jsonTo = gson.fromJson(message, new TypeToken<Map<String, String>>() {
        }.getType());
        returnMap.put("game_type", 1);
        // 创建房间
        // 心跳连接
        if ("heartbeat".equals(jsonTo.get("type"))) {
            returnMap.put("state", "0");
            returnMap.put("type", "heartbeat");
            sendMessageTo(returnMap);
        }
        if ("Establish".equals(jsonTo.get("type"))) {
            returnMap.put("type", "Establish");
            returnMap.put("state", "0");
            if (pkBean != null && F_PublicState.PKMap.get(pkBean.getRoom_number()) != null) {
                // 重复创建
                returnMap.put("state", "106");
            } else {
                // 创建房间
                pkBean = Establish_PK.Establish(userBean, Integer.valueOf(jsonTo.get("room_type")),
                        Integer.valueOf(jsonTo.get("clubid")));
                userBean.setLog(jsonTo.get("log_lat"));
                int max_zbet = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties",
                        "max_zbet" + jsonTo.get("max_zbet")));
                int leopard = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties",
                        "leopard" + jsonTo.get("leopard")));
                pkBean.setMax_zbet(max_zbet);
                pkBean.setLeopard(leopard);
                if (pkBean == null) {
                    // 房卡不足
                    returnMap.put("state", "105");
                } else {
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //String format = sdf.format(new Date());
                    //pkBean.setData(format);
                    pkBean.getPKBean_Custom("userid-nickname-avatarurl-money", "game_userList-max_zbet-leopard" +
                                    "-room_type-room_number-max_person-user_positions-max_number-bets",
                            returnMap);
                    sendMessageTo(returnMap, pkBean);
                }
            }
            sendMessageTo(returnMap);
            //room_change(pkBean, 3);
        }
        // 加入房间
        if ("joinRoom".equals(jsonTo.get("type"))) {
            returnMap.put("state", "0");
            // 经纬度
            userBean.setLog_lat(jsonTo.get("log_lat"));
            int state = gameService.ISMatching(userBean, jsonTo.get("room_number"));
            if (state == 0) {
                pkBean = gameService.ISMatching_Money(userBean, jsonTo.get("room_number"));
                returnMap.put("state", "0");
                pkBean.getPKBean_Custom("uservid-nickname-avatarurl-money", "game_userList-max_zbet-leopard" +
                                "-room_type-room_number-max_person-user_positions-max_number-bets",
                        returnMap);
            } else {
                // 100=房间不存在114=重复加入106=需要定位
                returnMap.put("state", String.valueOf(state));
            }
            returnMap.put("type", "joinRoom");
            sendMessageTo(returnMap);
        }

        // 坐下
        if ("Sit_down".equals(jsonTo.get("type"))) {
            int down = gameService.Sit_down(userBean, pkBean, Integer.valueOf(jsonTo.get("position")));
            if (down == 0) {
                pkBean.getPKBean_Custom("userid-nickname-avatarurl-money", "game_userList",
                        returnMap);
                returnMap.put("user_positions", pkBean.getUser_positions());
                returnMap.put("type", "Sit_down");
                returnMap.put("state", "0"); // 坐下成功
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, pkBean);
                //room_change(pkBean, 0);
            } else {
                returnMap.put("type", "Sit_down");
                returnMap.put("state", "131"); // 坐下失败
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, pkBean);
            }
        }

        // 匹配房间
        if ("matching".equals(jsonTo.get("type"))) {
            pkBean = gameService.Matching(userBean, Integer.parseInt(jsonTo.get("room_type")));
            if (pkBean != null) {
                returnMap.put("type", "matching_user");
                pkBean.getPKBean_Custom("", "user_positions", returnMap);
                userBean.getUser_Custom("userid-nickname-avatarurl-money-gametype", returnMap);
                sendMessageTo(returnMap, pkBean);
                returnMap.clear();
                pkBean.getPKBean_Custom("userid-nickname-avatarurl-money-gametype", "room_number-user_positions" +
                        "-game_userList-max_zbet", returnMap);
            } else {
                returnMap.put("state", "103");//(金币不足)
            }
            returnMap.put("type", "matching");
            sendMessageTo(returnMap);
        }
        if ("startGame".equals(jsonTo.get("type"))) {
            new StartGame(pkBean, gameService).start();
        }
        //下注或跟注
        if ("bets".equals(jsonTo.get("type"))) {
            //避免重复触发
            if (pkBean.getOperation_userid() == userBean.getUserid()) {
                //1跟注2加注
                int betstype = Integer.parseInt(jsonTo.get("betstype"));
                int bets = 0;
                if (betstype == 1) {//跟注
                    //明牌底注*2 判断上家是否看牌
                    if (pkBean.getBefore_userid() != 0) {
                        UserBean bean = pkBean.getUserBean(pkBean.getBefore_userid());
                        if (bean.getBrandtype() == 1) {//上家闷牌
                            bets = pkBean.getBets() * this.userBean.getBrandtype();
                        } else {//上家看牌  判断我是否看牌 如果看牌正常跟  闷牌除以二
                            if (userBean.getBrandtype() == 1) {
                                bets = pkBean.getBets() / 2;
                            } else {
                                bets = pkBean.getBets();
                            }
                        }
                    } else {//证明是第一个玩家
                        bets = pkBean.getBets();
                    }
                } else if (betstype == 2) {//加注
                    bets = Integer.parseInt(jsonTo.get("bets"));
                }
                //-1继续进行
                int state = gameService.bets(betstype, bets, userBean, pkBean);
                System_Mess.system_Mess.ToMessagePrint("(下注)房间状态" + state);
                //金币不足 告诉玩家只能比牌或者弃牌
                if (state == 809) {
                    returnMap.put("operation_userid", pkBean.getOperation_userid());
                    returnMap.put("type", "no_money");
                    returnMap.put("pk_type", 1);
                    returnMap.put("state", "0");
                    sendMessageTo(returnMap);
                } else if (state == 0) {//已结算
                    //开牌
                    jsonTo.put("type", "Vincent");
                    jsonTo.put("state", state + "");
                } else {
                    pkBean.getPKBean_Custom("", "bottom_pan-game_number", returnMap);
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("bets", bets);
                    returnMap.put("user_bets", userBean.getWinnum());
                    returnMap.put("money", userBean.getMoney());
                    returnMap.put("state", "0");
                    returnMap.put("betstype", betstype);
                    returnMap.put("type", "filling");
                    if (state == -1) {
                        //找下一个操作人
                        returnMap.put("nextid", pkBean.getNextUserId());
                    }
                    sendMessageTo(returnMap);
                    sendMessageTo(returnMap, pkBean);
                }
            }
        }
        //弃牌
        if ("giveup".equals(jsonTo.get("type"))) {
            returnMap.put("type", "giveup");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("state", "0");
            //避免重复触发
            if (pkBean.getOperation_userid() == userBean.getUserid()) {
                //弃牌
                userBean.setBrandstatus(2);
                //不可游戏
                userBean.setGametype(-1);
                //检测是否结算
                int state = gameService.NewGame(userBean, pkBean);
                System_Mess.system_Mess.ToMessagePrint("(弃牌)房间状态" + state);
                if (state == -1) {
                    //找下一个操作人
                    returnMap.put("nextid", pkBean.getNextUserId());
                    sendMessageTo(returnMap);
                    sendMessageTo(returnMap, pkBean);
                } else {
                    sendMessageTo(returnMap);
                    sendMessageTo(returnMap, pkBean);
                    //开牌
                    jsonTo.put("type", "Vincent");
                    jsonTo.put("state", state + "");
                }
            }
        }
        //比牌
        if ("pk".equals(jsonTo.get("type"))) {
            //避免重复触发
            if (pkBean.getOperation_userid() == userBean.getUserid()) {
                //指定比牌人的id
                int pkuserid = Integer.parseInt(jsonTo.get("pkuserid"));
                Integer pk_type = Integer.valueOf(jsonTo.get("pk_type"));//0正常比牌 1金币不足比牌
                //执行比牌
                int state = gameService.PKUser(pkuserid, userBean, pkBean, pk_type);
                if (state == 0) {
                    //判断是否只剩2个人就亮牌
                    int count = 0;
                    for (UserBean user :
                            pkBean.getGame_userList(2)) {
                        count++;
                    }
                    System.out.println("count:" + count);
                    returnMap.put("state", state + "");
                    returnMap.put("type", "pk");
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("avatarurl", userBean.getAvatarurl());
                    returnMap.put("pkavatarurl", pkBean.getUserBean(pkuserid).getAvatarurl());
                    returnMap.put("pkuserid", pkuserid);
                    returnMap.put("user_bets", userBean.getWinnum());
                    returnMap.put("money", userBean.getMoney());
                    returnMap.put("victoryid", userBean.getGametype() != -1 ? userBean.getUserid() : pkuserid);
                    returnMap.put("loserid", userBean.getGametype() != -1 ? pkuserid : userBean.getUserid());
                    if (count != 1) {
                        if (pk_type == 1) {
                            returnMap.put("nextid", userBean.getUserid());
                        } else {
                            //找下一个操作人
                            returnMap.put("nextid", pkBean.getNextUserId());
                        }
                    }
                    sendMessageTo(returnMap);
                    sendMessageTo(returnMap, pkBean);
                    if (count == 1) {
                        returnMap.clear();
                        returnMap.put("game_type", 1);
                        jsonTo.put("type", "Vincent");
                        jsonTo.put("state", 809 + "");
                    }
                }
            }
        }
        //贯穿执行
        if ("Vincent".equals(jsonTo.get("type"))) {
            //系统开牌结算
            int state = Integer.parseInt(jsonTo.get("state"));
            if (state == 809) {//金币不足
                //结算0结算成功201=和局
                state = gameService.OpenBrand(pkBean);
            }
            //结算
            if (state == 0) {
                returnMap.put("type", "end_game");
                returnMap.put("state", "0");
                //胜利人id-胜利人的money-用户手里的牌型
                pkBean.getPKBean_Custom("userid", "game_userList", returnMap);
                returnMap.put("victoryid", pkBean.getVictoryid());
                returnMap.put("money", pkBean.getUserBean(pkBean.getVictoryid()).getMoney());
            } else if (state == 202) {
                //弃牌胜利
                returnMap.put("type", "end_game");
                returnMap.put("state", "0");
                returnMap.put("victoryid", pkBean.getVictoryid());
                returnMap.put("money", pkBean.getUserBean(pkBean.getVictoryid()).getMoney());
            }
            //和局
            if (state == 201) {
                System.out.println("和局");
            }
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, pkBean);
            pkBean.removeOutUser();//清除掉线的玩家
            if(pkBean!=null){
                pkBean.Initialization();
                //room_change(pkBean, 1);
            }
        }
        //明牌
        if ("openbrand".equals(jsonTo.get("type"))) {
            //明牌
            userBean.setBrandtype(2);
            returnMap.put("type", "openbrand");
            returnMap.put("state", "0");
            returnMap.put("userid", userBean.getUserid());
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, pkBean);
        }
        // 消息通道
        if ("message".equals(jsonTo.get("type"))) {
            returnMap.put("type", "message");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("state", "0");
            returnMap.put("text", jsonTo.get("text"));
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, pkBean);
        }
        //查看战绩
        if ("Settlement".equals(jsonTo.get("type"))) {
            returnMap.put("state", "0");
            pkBean.getPKBean_Custom("userid-nickname-avatarurl-win_number", "game_userList", returnMap);
            returnMap.put("type", "Settlement");
            sendMessageTo(returnMap);
        }
        // 获取位置距离
        if ("get_position".equals(jsonTo.get("type"))) {
            returnMap.put("type", "get_position");
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < pkBean.getGame_userList(0).size(); i++) {
                UserBean user = pkBean.getGame_userList(0).get(i);
                for (int j = i + 1; j < pkBean.getGame_userList(0).size(); j++) {
                    UserBean user2 = pkBean.getGame_userList(0).get(j);
                    double distance = MapHelper.GetPointDistance(
                            user.getLog_lat(), user2.getLog_lat());
                    String re = user.getNickname() + "距离"
                            + user2.getNickname() + ""
                            + String.valueOf(distance) + "KM";
                    list.add(re);
                }
            }
            returnMap.put("list", list);
            returnMap.put("position", pkBean.getUser_positions());
            sendMessageTo(returnMap);
        }

        //查看所有房间
        if ("selectRooms".equals(jsonTo.get("type"))) {
            returnMap.clear();
            List<F_PKBean> list = new ArrayList<>();
            int state = Integer.valueOf(jsonTo.get("state"));//0普通 1俱乐部
            if (state == 0) {
                String room_type = jsonTo.get("room_type");// 1 2 3  初中高级场
                userBean.setDttype(room_type);
                for (String s : F_PublicState.PKMap.keySet()) {
                    if (F_PublicState.PKMap.get(s).getRoom_type() == Integer.valueOf(room_type) && F_PublicState.PKMap.get(s).getNumber_3() == 0) {
                        list.add(F_PublicState.PKMap.get(s));
                    }
                }
                returnMap.put("role", -1);
            } else {
                int club_number = Integer.valueOf(jsonTo.get("club_number"));//亲友圈号
                userBean.setNumber_3(club_number);
                String fen = jsonTo.get("fen");//查询几倍场的房间  默认0全部  其他 0.5  1  2  5
                userBean.setDttype(fen);
                for (String s : F_PublicState.PKMap.keySet()) {
                    if (!fen.equals("0")) {
                        if (F_PublicState.PKMap.get(s).getNumber_3() == club_number && String.valueOf(F_PublicState.PKMap.get(s).getZbets()).equals(fen)) {
                            list.add(F_PublicState.PKMap.get(s));
                        }
                    } else {
                        if (F_PublicState.PKMap.get(s).getNumber_3() == club_number) {
                            list.add(F_PublicState.PKMap.get(s));
                        }
                    }
                }
                returnMap.put("role", loginDao.getRole(userBean.getUserid(), club_number));
            }
            F_PKBean r = new F_PKBean();
            List<Map<String, Object>> maps = r.getrooms(list, "room_number-zbets-user_positions-game_number" +
                    "-person_num", "");
            returnMap.put("rooms", maps);
            returnMap.put("type", "selectRooms");
            sendMessageTo(returnMap);
        }


        //查看所有玩家(玩家列表)
        if ("selectUsers".equals(jsonTo.get("type"))) {
            returnMap.clear();
            returnMap.put("userList", F_PublicState.getAlinenum(userBean));
            returnMap.put("type", "selectUsers");
            sendMessageTo(returnMap);
        }

        //邀请某个玩家
        if ("invite".equals(jsonTo.get("type"))) {
            returnMap.clear();
            returnMap.put("type", "invite");
            returnMap.put("openid", userBean.getOpenid());
            returnMap.put("nickname", userBean.getNickname());
            returnMap.put("roomno", pkBean.getRoom_number());
            returnMap.put("game_type", 1);
            returnMap.put("state", "0");
            String openid = jsonTo.get("openid");//被邀请人的openid
            Mahjong_Socket ws = Public_State.clients.get(openid);
            if (ws != null) {
                ws.sendMessageTo(returnMap, openid);
            }
            F_WebSocket socket = F_PublicState.clients.get(openid);
            if (socket != null) {
                try {
                    socket.sendMessageTo(returnMap,openid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //退出房间
        if ("exit_room".equals(jsonTo.get("type"))) {
            if(pkBean.getRoom_state()==0){
                if (userBean.getUser_type() == 1) {
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("state", "0");
                    returnMap.put("type", "exit");
                    sendMessageTo(returnMap,pkBean);
                    returnMap.clear();
                    pkBean.remove_opsitions(userBean.getUserid());
                }
                pkBean.getGame_userList(1).remove(userBean);
            }else{
                if(userBean.getUser_type()==1){
                    userBean.setGametype(2);
                }else{
                    pkBean.getGame_userList(1).remove(userBean);
                }
            }
            //room_change(pkBean, 0);
        }
        baseDao.CloseAll();
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        if (error.getMessage() != null) {
            error.printStackTrace();
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "异常" + error.getLocalizedMessage() + "***");
        }
    }

    private void room_change(F_PKBean room, int type) {
        returnMap.clear();
        returnMap.put("type", "room_change");
        returnMap.put("change_type", type);
        room.getRoomBean_Custom2("game_userList-avatarurl", returnMap, "room_number-game_number");

        if (room.getNumber_3() != 0) {//证明是俱乐部房间
            for (Map.Entry<String, F_WebSocket> entry : F_PublicState.clients.entrySet()) {
                F_WebSocket ws = entry.getValue();
                if (userBean.getDttype().equals("0")) {//查询全部
                    if (ws.userBean.getNumber_3() == room.getNumber_3()) {//证明在同一个俱乐部
                        ws.sendMessageTo(returnMap);
                    }
                } else {//查询相同底分的
                    if (ws.userBean.getNumber_3() == room.getNumber_3() && Integer.valueOf(ws.userBean.getDttype()) == room.getZbets() &&ws.userBean.getUser_type()==0) {//证明在同一个分数场
                        ws.sendMessageTo(returnMap);
                    }
                }
            }
        } else {//证明是普通房间
            String renshu = userBean.getDttype();
            for (Map.Entry<String, F_WebSocket> entry : F_PublicState.clients.entrySet()) {
                F_WebSocket ws = entry.getValue();
                if (Integer.valueOf(renshu) == room.getRoom_type() && ws.userBean.getUser_type()==0) {//证明在同一个场次
                    ws.sendMessageTo(returnMap);
                }
            }
        }
    }

    /**
     * 退出房间清除自己
     */
    private void exit_Room() {
        pkBean.getReady_lock().lock();
        //剔除自己
        for (int i = 0; i < pkBean.getGame_userList(0).size(); i++) {
            if (pkBean.getGame_userList(0).get(i).getUserid() == userBean.getUserid()) {
                pkBean.remove_opsitions(userBean.getUserid());
                pkBean.getGame_userList(0).remove(i);
                returnMap.put("type", "exit");
                returnMap.put("userid", userBean.getUserid());
                //给房间所有人发
                sendMessageTo(returnMap, pkBean);
                break;
            }
        }
        //代表房间无人
        if (pkBean.getGame_userList(0).size() == 0) {
            F_PublicState.PKMap.remove(pkBean.getRoom_number());
            System_Mess.system_Mess.ToMessagePrint("房间清除");
        }
        pkBean.getReady_lock().unlock();
    }

    /**
     * 发送消息(定向)
     *
     * @param
     * @param
     * @throws IOException
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, String openid) throws IOException {
        String returnjson = gson.toJson(returnMap).toString(); // RC4.encry_RC4_string(gson.toJson(returnMap)
        // .toString());
        System.out.println(returnjson);
        F_WebSocket webSocket = F_PublicState.clients.get(openid);
        webSocket.session.getBasicRemote().sendText(returnjson);
    }

    /**
     * 发送消息(房间所有人)
     *
     * @param
     * @param
     * @throws IOException
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, F_PKBean pkBean) {
        String returnjson = gson.toJson(returnMap).toString(); // RC4.encry_RC4_string(gson.toJson(returnMap)
        // .toString());
        for (UserBean user : pkBean.getGame_userList(1)) {
            F_WebSocket webSocket = F_PublicState.clients.get(user.getOpenid());
            // 不等于自己的则发送消息
            if (webSocket != null && !user.getOpenid().equals(userBean.getOpenid())) {
                try {
                    webSocket.session.getBasicRemote().sendText(returnjson);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System_Mess.system_Mess.ToMessagePrint(user.getNickname() + "返回:" + returnjson);
            }
        }
    }

    /**
     * 给自己返回信息
     *
     * @param returnMap
     * @param
     * @throws IOException
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap) {
        String returnjson = gson.toJson(returnMap).toString();// RC4.encry_RC4_string(gson.toJson(returnMap).toString
        // ());
        try {
            session.getBasicRemote().sendText(returnjson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System_Mess.system_Mess.ToMessagePrint(userBean.getUserid() + "(自己)返回消息" + returnjson);
    }

    /**
     * 向所有人发送
     *
     * @param message
     * @throws IOException
     */
    public void sendMessageAll(String message) throws IOException {
//		for (PK_WebSocket item : PublicState.clients.values()) {
//			item.session.getAsyncRemote().sendText(message);
//		}
    }
}
