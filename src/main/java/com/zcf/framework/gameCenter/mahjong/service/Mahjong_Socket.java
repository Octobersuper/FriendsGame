package com.zcf.framework.gameCenter.mahjong.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.dao.M_LoginDao;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import org.springframework.stereotype.Component;
import util.BaseDao;
import util.MahjongUtils;
import util.MapHelper;
import util.System_Mess;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static util.Mahjong_Util.mahjong_Util;

@ServerEndpoint("/Mahjong_Socket/{openid}")
@Component
public class Mahjong_Socket {
    private Gson gson = new Gson();// json转换
    public Session session;
    // 用户信息
    public UserBean userBean;
    // 自己进入的房间
    public RoomBean roomBean;
    private BaseDao baseDao = new BaseDao();
    // 登陆dao
    private M_LoginDao loginDao = new M_LoginDao(baseDao);
    // 游戏逻辑类
    public M_GameService gameService = new M_GameService(baseDao);
    private Map<String, Object> returnMap = new HashMap<String, Object>();

    /**
     * 连接
     *
     * @param openid
     * @param session
     * @throws IOException
     */
    @OnOpen
    public void onOpen(@PathParam("openid") String openid, Session session) {
        boolean bool = true;
        if (openid != null) {
            // 查询出用户信息t
            userBean = loginDao.getUser(openid);
            baseDao.CloseAll();
            if (userBean != null) {
                String openids = userBean.getOpenid();
                //验证是否已经在线
                if (Public_State.clients.get(openids) != null) {
                    userBean.setHu_state(-1);//临时使用 含义为当前用户处在大厅当中
                    //将自己放入客户端集合中
                    Public_State.clients.put(openids, this);
                    this.session = session;
                    returnMap.put("type", "contion");
                    returnMap.put("state", "0");
                    sendMessageTo(returnMap);
                    System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接(麻将)重复登陆");
                    bool = false;
                } else {
                    userBean.setHu_state(-1);//临时使用 含义为当前用户处在大厅当中
                    //将自己放入客户端集合中
                    Public_State.clients.put(openids, this);
                    this.session = session;
                    returnMap.put("type", "contion");
                    returnMap.put("state", "0");
                    sendMessageTo(returnMap);
                    System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接(麻将)");
                    bool = false;
                }
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
    public void onClose() {
        if (userBean != null) {
            //删除自己
            Public_State.clients.remove(userBean.getOpenid() + "");
            //标识自己已经掉线
            userBean.setExit_state(1);
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "断开连接");
            //如果已加入房间则通知其他人自己退出
            Exit_Room();
        }
    }

    /**
     * 接收消息
     *
     * @param message
     * @throws IOException
     * @throws InterruptedException
     */
    @OnMessage
    public void onMessage(String message) {
        session.setMaxIdleTimeout(60000);
        returnMap.clear();
        if (!message.contains("heartbeat")) {
            System_Mess.system_Mess.ToMessagePrint(userBean.getBrands().size() + "接收" + userBean.getNickname() +
                    "(state:" + userBean.getHu_state() + ")" + "的手牌" + userBean.getBrands().toString());
            System_Mess.system_Mess.ToMessagePrint("接收" + userBean.getNickname() + "的消息" + message);
        }
        Map<String, String> jsonTo = gson.fromJson(message, new TypeToken<Map<String, String>>() {
        }.getType());

        // 心跳连接
        if ("heartbeat".equals(jsonTo.get("type"))) {
            returnMap.put("type", "heartbeat");
            sendMessageTo(returnMap);
        }
        // 创建房间
        if ("Establish".equals(jsonTo.get("type"))) {
            returnMap.put("type", "Establish");
            returnMap.put("exittype", 0);
            returnMap.put("state", "0");
            userBean.setExit_game(0);
            if (roomBean != null && Public_State.PKMap.get(roomBean.getRoomno()) != null &&Integer.parseInt(jsonTo.get("clubid"))==0 ) {
                // 重复创建
                returnMap.put("state", "106");
            } else {
                // 创建房间
                roomBean = gameService.Establish(jsonTo, userBean, Integer.parseInt(jsonTo.get("clubid")));
                if (roomBean == null) {
                    // 房卡不足
                    returnMap.put("state", "105");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(new Date());
                    roomBean.setData(format);
                    roomBean.getRoomBean_Custom("clubid-roomno-user_positions-fen-roomParams-sex", returnMap,
                            "userid-nickname-avatarurl-number-money");
                    sendMessageTo(returnMap, roomBean);
                }
            }
            sendMessageTo(returnMap);
            room_change(roomBean, 4);
        }
        // 加入房间
        if ("Matching".equals(jsonTo.get("type"))) {
            returnMap.put("state", "0");
            // 经纬度
            userBean.setLog_lat(jsonTo.get("log_lat"));
            userBean.setExit_game(0);
            int state = gameService.ISMatching_Money(userBean, jsonTo.get("roomno"));
            if (state == 0) {
                roomBean = gameService.Matching(jsonTo, userBean);
                if (roomBean == null) {
                    returnMap.put("state", "104");// 房间已满
                } else {
                    userBean.setReady_state(0);
                    // 将自己的信息推送给房间内其他玩家
                    userBean.getUser_Custom("money-userid-nickname-avatarurl-number-log_lat", returnMap);
                    roomBean.getRoomBean_Custom("user_positions-clubid", returnMap);
                    returnMap.put("type", "Matching_User");
                    sendMessageTo(returnMap, roomBean);
                    returnMap.clear();
                    returnMap.put("state", "0");
                    returnMap.put("exittype", 1);
                    roomBean.getRoomBean_Custom("clubid-roomno-max_person-user_positions-max_number-fen-roomParams-sex" +
                                    "-user_log", returnMap,
                            "userid-nickname-avatarurl-ready_state-log_lat-number-money");
                }
            } else {
                // 100=房间不存在114=重复加入106=需要定位 123房卡不足
                returnMap.put("state", String.valueOf(state));
            }
            returnMap.put("type", "Matching");
            sendMessageTo(returnMap);
            room_change(roomBean, 0);
        }
        // 准备
        if ("ready".equals(jsonTo.get("type"))) {
            returnMap.put("type", "ready");
            // 如果处于等待加入或准备阶段
            if(userBean.getMoney()<=0){
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("ready_state",123);
            }else{
                if (roomBean.getState() == 0 || roomBean.getState() == 1) {
                    int count = gameService.Ready(userBean, roomBean);
                    if (count == roomBean.getMax_person()) {
                        if(roomBean.getRoomParams().get("p4")!=0){
                            returnMap.put("type", "allReady");
                            returnMap.put("fen", roomBean.getFen());
                        }else{
                            returnMap.put("type", "startgame");
                            returnMap.put("exittype", 3);
                            // 执行游戏开始
                            int state = gameService.StartGame(roomBean);
                            if (state == 0) {
                                roomBean.getRoomBean_Custom("game_number-max_number-brands_count-banker-brands-fen", returnMap,
                                        "brands-number-userid-money");
                            }
                        }
                    } else {
                        returnMap.put("userid", userBean.getUserid());
                    }
                }
            }
            Random random = new Random();
            int num;
            returnMap.put("dice", num = random.nextInt(12) + 2);
            roomBean.setDice(num);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }

        //扎针辅分
        if("zha_fu".equals(jsonTo.get("type"))){
            roomBean.getLock().lock();
            int zha = Integer.valueOf(jsonTo.get("zha"));
            int fu = Integer.valueOf(jsonTo.get("fu"));
            if(fu==1){
                int fen = roomBean.getFen();
                if (fen==1){
                    fu = 5;
                }
                if (fen==2){
                    fu = 10;
                }
                if (fen==5){
                    fu = 20;
                }
            }
            userBean.setZha(zha);
            userBean.setFu(fu);
            returnMap.put("state","0");
            returnMap.put("type","zha_fu");
            returnMap.put("userid",userBean.getUserid());
            returnMap.put("zha",jsonTo.get("zha"));
            returnMap.put("fu",fu);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap,roomBean);
            returnMap.clear();
            roomBean.setZhanum(roomBean.getZhanum()+1);
            roomBean.getLock().unlock();
            if(roomBean.getZhanum()==roomBean.getGame_userlist().size()){
                returnMap.put("type", "startgame");
                returnMap.put("exittype", 3);
                // 执行游戏开始
                int state = gameService.StartGame(roomBean);
                if (state == 0) {
                    roomBean.getRoomBean_Custom("game_number-max_number-brands_count-banker-brands-fen", returnMap,
                            "brands-number-userid-money");
                }
                Random random = new Random();
                returnMap.put("dice", random.nextInt(12) + 2);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap,roomBean);
            }

        }

        // 退出房间
        if ("exit_room".equals(jsonTo.get("type"))) {
            Exit_Room();
        }
        // 解散房间
        if ("exit_all".equals(jsonTo.get("type"))) {
            // 房间在等待加入阶段并且自己是房主才可以解散
            if (roomBean.getState() == 0 && roomBean.getHouseid() == userBean.getUserid()) {
                Exit_All();
            }
        }
        // 游戏开始解散
        if ("exit_game".equals(jsonTo.get("type"))) {
            returnMap.put("type", "exit_game");
            // 添加自己的解散状态
            userBean.setExit_game(Integer.parseInt(jsonTo.get("exit_game")));
            // 301-发起 302-同意 303-不同意 304-解散
            int state = gameService.Exit_GameUser(userBean, roomBean);
            // 执行解散
            if (state == 304) {
                Exit_All();
            } else {
                if (state == 301) {
                    roomBean.getRoomBean_Custom("userList", returnMap, "userid-nickname-avatarurl-exit_game");
                    returnMap.put("applyuserid", userBean.getUserid());
                } else {
                    returnMap.put("userid", userBean.getUserid());
                }
                returnMap.put("state", String.valueOf(state));
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }
        /****************************************出牌*******************************************/
        // 出牌
        if ("out_brand".equals(jsonTo.get("type"))) {
            if (roomBean.IS_Bar_Userid(userBean.getUserid()) == 0) {
                roomBean.RemoveAll_Bar();
            }
            roomBean.setRepair_baruserid(0);
            roomBean.setHucount(0);
            String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
            List<Integer> tingList = new ArrayList<>();
            for (int i = 0; i < ting_brands.length; i++) {
                if (ting_brands[i].equals("")) {
                    continue;
                }
                tingList.add(Integer.parseInt(ting_brands[i]));
            }
            userBean.setTingCards(tingList);
            returnMap.put("type", "out_brand");
            int outbrand = Integer.parseInt(jsonTo.get("outbrand"));
            int before_type = Integer.parseInt(jsonTo.get("before_type"));
            roomBean.setLastBrand(outbrand);
            roomBean.setLastUserid(userBean.getUserid());
            // 出牌并且判定是否有碰或杠 0有碰 300可摸牌
            returnMap.put("before_type", before_type);
            // 出的牌
            returnMap.put("outbrand", outbrand);
            // 出牌人id
            returnMap.put("out_userid", userBean.getUserid());
            // 出牌类型
            returnMap.put("out_type", 0);
            //检测是否有人可胡 可胡的话发送胡牌消息   0没人能胡  1有人能胡
            //List<UserBean> checkHu = roomBean.checkHu(outbrand, userBean);
            ArrayList<Integer> hulist = new ArrayList<>();
            roomBean.getHu_user_list().clear();
            for (UserBean user :
                    roomBean.getGame_userlist()) {
                if (user.getTingCards().size() != 0 && user.getUserid() != userBean.getUserid()) {
                    List<Integer> list = new ArrayList<>();
                    for (Integer card :
                            user.getTingCards()) {
                        list.add(mahjong_Util.getBrand_Value(card));
                    }
                    if (list.contains(mahjong_Util.getBrand_Value(outbrand))) {
                        int s = mahjong_Util.checkHu(user, outbrand);
                        if (s != -1) {
                            hulist.add(user.getUserid());
                        }
                    }
                }
            }
            System.err.println("checkHu2:" + hulist.toString());
            int state = gameService.OutBrand(roomBean, outbrand, userBean, returnMap,hulist);
            System_Mess.system_Mess.ToMessagePrint("碰牌状态" + state);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        /****************************************碰*****************************************/
        // 碰
        if ("bump".equals(jsonTo.get("type"))) {
            // 碰的用户id
            int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
            // 碰牌
            int p_brand = Integer.parseInt(jsonTo.get("brand"));
            int[] brands = gameService.Bump_Brand(userBean, Integer.parseInt(jsonTo.get("userid")),
                    Integer.parseInt(jsonTo.get("brand")), roomBean);
            returnMap.put("type", "bump");
            returnMap.put("bumpuserid", jsonTo.get("userid"));
            returnMap.put("brand", p_brand);
            returnMap.put("brands", brands);
            returnMap.put("userid", userBean.getUserid());
            // 清空碰牌信息
            roomBean.getNextMap().clear();
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }

        // 弃碰
        if ("giveup_bump".equals(jsonTo.get("type"))) {
            userBean.getNoany().add(mahjong_Util.getBrand_Value(Integer.parseInt(jsonTo.get("brand"))));//一轮不能胡这张
            gameService.OutBrand(roomBean.getUserBean(roomBean.getLastUserid()), roomBean.getLastBrand());
            jsonTo.put("type", "brand_nextid");
            roomBean.getNextMap().clear();
        }
        /****************************************胡牌**************************************/
        // 客户端推送胡牌
        if ("is_hu".equals(jsonTo.get("type"))) {
            int state = Integer.parseInt(jsonTo.get("state"));
            userBean.setIs_hustate(state);
            int count = gameService.Is_Hu(roomBean, state, userBean);
            System.out.println("hucount:" + count);
            boolean bool = false;
            if (count == 3)
                bool = true;
            if (count == 2 && roomBean.getGame_userlist().size() == 3)
                bool = true;
            if (count == 1 && roomBean.getGame_userlist().size() == 2)
                bool = true;
            if (bool) {
                if (roomBean.getBrands().size() == 0 && state == 0) {
                    roomBean.setFlowNum(roomBean.getFlowNum() + 1);
                    if (roomBean.getFlowNum() == roomBean.getGame_userlist().size() - 1) {
                        jsonTo.put("type", "flow");
                    }
                } else {
                    if (Integer.valueOf(jsonTo.get("before_type")) == 2) {
                        returnMap.put("type", "repair_bar_bump_pass");
                        returnMap.put("brand_nextid", roomBean.getRepair_baruserid());
                        sendMessageTo(returnMap);
                        sendMessageTo(returnMap, roomBean);
                    } else {
                        jsonTo.put("type", "brand_nextid");
                        roomBean.setHucount(0);
                    }
                }
            } else {
                if (roomBean.getBrands().size() == 0 && state == 0) {
                    roomBean.setFlowNum(roomBean.getFlowNum() + 1);
                    if (roomBean.getFlowNum() == roomBean.getGame_userlist().size() - 1) {
                        jsonTo.put("type", "flow");
                    }
                }
                returnMap.clear();
                returnMap.put("type", "is_hu");
                returnMap.put("hu_list", roomBean.getHu_user_list());
                sendMessageTo(returnMap);
            }
        }

        // 胡牌_点炮
        if ("endhu".equals(jsonTo.get("type"))) {
            if (roomBean.getHu_user_list().size() != 0 && roomBean.getState() != 4) {
                int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
                // 增加用户点炮
                roomBean.getUserBean(p_userid).getRecordMsgList().add("点炮");
                returnMap.put("type", "endhu");
                List<Integer> brandList = userBean.getBrands();
                List<Integer> list2 = new ArrayList<Integer>();
                for (Integer in : brandList) {
                    if (!list2.contains(in)) {
                        list2.add(in);
                    }
                }
                brandList.clear();
                for (Integer in : list2) {
                    brandList.add(in);
                }
                //听牌
                String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
                List<Integer> tingList = new ArrayList<>();
                for (int i = 0; i < ting_brands.length; i++) {
                    if (ting_brands[i].equals("")) {
                        continue;
                    }
                    tingList.add(Integer.parseInt(ting_brands[i]));
                }
                // 结算检测
                MahjongUtils mahjongUtils = new MahjongUtils();
                mahjongUtils.getBrandKe(roomBean, userBean, Integer.parseInt(jsonTo.get("brand")), tingList, 0);


                userBean.setPower_number(userBean.getPower_number()*roomBean.getFen());//几倍场
                //是否杠上炮*2
                if (Integer.parseInt(jsonTo.get("before_type")) == 1) {
                    userBean.setPower_number(userBean.getPower_number()*2);
                    userBean.getRecordMsgList().add("杠上炮*2");
                    userBean.setHu_type(userBean.getHu_type()+" 杠上炮");
                }
                if(userBean.getZha()!=0){
                    userBean.setPower_number(userBean.getPower_number()*2);//扎针
                }
                userBean.setPower_number(userBean.getPower_number()+userBean.getFu());//辅分


                List<Integer> brandValue = mahjong_Util.User_Brand_Value(userBean.getBrands());
                Collections.sort(brandValue);
                brandValue.remove(Integer.valueOf(jsonTo.get("brand")));
                brandValue.add(Integer.valueOf(jsonTo.get("brand")));
                System.out.println(">>>>>>>牌型检测完成啦");
                // 结算500=已经结算 501=等待别人胡牌操作 502=等待别人结算
                int state = gameService.End_Game(userBean, roomBean, p_userid, 1);
                returnMap.put("state", String.valueOf(state));
                System_Mess.system_Mess.ToMessagePrint("点炮状态" + state);
                userBean.setIshu(2);
                // 成功结算
                if (state == 0) {
                    roomBean.getHu_user_list().clear();
                    roomBean.getRoomBean_Custom("", returnMap, "money-userid-dqnumber-number-nickname-avatarurl-hu_type-ishu" +
                            "-power_number");
                    roomBean.setVictoryid(userBean.getUserid());
                    returnMap.put("type", "endhu");
                    returnMap.put("brands", brandValue);
                    returnMap.put("is_last", roomBean.getGame_number() >= roomBean.getMax_number() ? 1 : 0);
                    sendMessageTo(returnMap);
                    sendMessageTo(returnMap, roomBean);
                    roomBean.addLog(returnMap);
                    // 记录战绩
                    gameService.addRecord(roomBean);
                    roomBean.InItReady();
                }
            } else {
                returnMap.put("state", "999");
                sendMessageTo(returnMap);
            }
        }

        // 胡牌_自摸
        if ("endhu_this".equals(jsonTo.get("type"))) {
            returnMap.put("type", "endhu_this");
            List<Integer> brandList = userBean.getBrands();
            List<Integer> list2 = new ArrayList<Integer>();
            for (Integer in : brandList) {
                if (!list2.contains(in)) {
                    list2.add(in);
                }
            }
            brandList.clear();
            for (Integer in : list2) {
                brandList.add(in);
            }
            //听牌
            String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
            List<Integer> tingList = new ArrayList<>();
            for (int i = 0; i < ting_brands.length; i++) {
                if (ting_brands[i].equals("")) {
                    continue;
                }
                tingList.add(Integer.parseInt(ting_brands[i]));
            }
            // 结算检测
            MahjongUtils mahjongUtils = new MahjongUtils();
            mahjongUtils.getBrandKe(roomBean, userBean, Integer.parseInt(jsonTo.get("brand")), tingList, 1);


            userBean.setPower_number(userBean.getPower_number()*roomBean.getFen());//几倍场
            //杠上花
            int before_type = Integer.parseInt(jsonTo.get("before_type"));
            if (before_type == 1) {
                userBean.setPower_number(userBean.getPower_number()*2);
                userBean.getRecordMsgList().add("杠上花*2");
                userBean.setHu_type(userBean.getHu_type()+" 杠上花");
            }
            if(userBean.getZha()!=0){
                userBean.setPower_number(userBean.getPower_number()*2);//扎针
            }
            userBean.setPower_number(userBean.getPower_number()+userBean.getFu());//辅分

            List<Integer> brandValue = mahjong_Util.User_Brand_Value(userBean.getBrands());
            Collections.sort(brandValue);
            brandValue.remove(Integer.valueOf(jsonTo.get("brand")));
            brandValue.add(Integer.valueOf(jsonTo.get("brand")));
            System.out.println(">>>>>>>牌型检测完成啦");
            // 自摸结算
            int state = gameService.End_Game_This(userBean, roomBean);
            userBean.setIshu(1);
            System_Mess.system_Mess.ToMessagePrint("自摸状态" + state);
            if (state == 0) {
                roomBean.getHu_user_list().clear();
                roomBean.getRoomBean_Custom("", returnMap, "money-userid-dqnumber-number-nickname-avatarurl-hu_type-ishu" +
                        "-power_number");
                roomBean.setVictoryid(userBean.getUserid());
                returnMap.put("type", "endhu");
                returnMap.put("brands", brandValue);
                returnMap.put("is_last", roomBean.getGame_number() >= roomBean.getMax_number() ? 1 : 0);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
                roomBean.addLog(returnMap);
                // 记录战绩
                gameService.addRecord(roomBean);
                roomBean.InItReady();
            } else {
                returnMap.put("state", "999");
                sendMessageTo(returnMap);
            }
        }

        // 弃胡
        if ("giveup_hu".equals(jsonTo.get("type"))) {
            if (roomBean.getHu_user_list().size() != 0) {
                returnMap.put("type", "giveup_hu");
                int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
                userBean.getNohu().add(Integer.parseInt(jsonTo.get("brand")));//一轮不能胡这张
                returnMap.put("nohu", userBean.getNohu());
                int state = gameService.OutBrand2(roomBean, roomBean.getLastBrand(),
                        roomBean.getUserBean(roomBean.getLastUserid()), returnMap);
                System_Mess.system_Mess.ToMessagePrint("碰牌状态" + state);
                if (state == 300) {
                    jsonTo.put("type", "brand_nextid");
                } else {
                    returnMap.put("type", "out_brand");
                    // 出的牌
                    returnMap.put("outbrand", roomBean.getLastBrand());
                    // 出牌人id
                    returnMap.put("out_userid", roomBean.getLastUserid());
                    // 出牌类型0正常出牌   1弃胡检测
                    returnMap.put("out_type", 1);
                    sendMessageTo(returnMap);
                    sendMessageTo(returnMap, roomBean);
                }
                roomBean.HuState_InIt();
                roomBean.getNextMap().clear();
            }
        }
        if ("Settlement".equals(jsonTo.get("type"))) {
            roomBean.getRoomBean_Custom("roomno-fen-data-max_number-houseid", returnMap, "number-userid-nickname" +
                    "-avatarurl");
            returnMap.put("type", "Settlement");
            sendMessageTo(returnMap);
        }

        /***********************************杠***************************************/
        // 暗杠
        if ("hide_bar".equals(jsonTo.get("type"))) {
            roomBean.setHide_baruserid(userBean.getUserid());
            //触发杠的牌
            int gang_brand = Integer.parseInt(jsonTo.get("brand"));
            userBean.setPower(2);
            userBean.getRecordMsgList().add("暗杠+2");
            userBean.setHu_type(userBean.getHu_type()+" 暗杠");
            int[] brands = gameService.Hide_Bar(userBean, gang_brand, roomBean);
            returnMap.put("type", "hide_bar");
            returnMap.put("brands", brands);
            returnMap.put("userid", userBean.getUserid());
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        // 明杠
        if ("show_bar".equals(jsonTo.get("type"))) {
            roomBean.setShow_baruserid(userBean.getUserid());
            //触发杠的牌
            int gang_brand = Integer.parseInt(jsonTo.get("brand"));
            userBean.setPower(1);
            userBean.getRecordMsgList().add("明杠+1");
            userBean.setHu_type(userBean.getHu_type()+" 明杠");
            gameService.Show_Bar(userBean, Integer.parseInt(jsonTo.get("userid")), gang_brand, roomBean);
            returnMap.put("type", "show_bar");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("baruserid", jsonTo.get("userid"));
            returnMap.put("brand", gang_brand);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        // 抢杠过胡
        if ("repair_bar_bump_pass".equals(jsonTo.get("type"))) {
            returnMap.put("type", "repair_bar_bump_pass");
            userBean.getNohu().add(Integer.parseInt(jsonTo.get("brand")));//一轮不能胡这张
            returnMap.put("brand_nextid", roomBean.getRepair_baruserid());
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        // 补杠
        if ("repair_bar_bump".equals(jsonTo.get("type"))) {
            //触发杠的牌
            int gang_brand = Integer.parseInt(jsonTo.get("brand"));
            //牌值转换
            int brand_value = mahjong_Util.getBrand_Value(gang_brand);
            userBean.setPower(1);
            userBean.getRecordMsgList().add("明杠+1");
            userBean.setHu_type(userBean.getHu_type()+" 补杠");
            roomBean.setRepair_baruserid(userBean.getUserid());
            gameService.Repair_Bar_Bump(userBean, gang_brand, roomBean);
            returnMap.put("type", "repair_bar_bump");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("brand", gang_brand);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        /*********************************************房间操作******************************************/

        // 摸牌
        if ("get_brand".equals(jsonTo.get("type"))) {
            // 摸牌
            int brand = roomBean.getBrand_Random();
            if (brand == -1) {
                jsonTo.put("type", "flow");
            } else {/*if (roomBean.getBrands().size() <= roomBean.getGame_userlist()
                    .size()) {
                // 分牌
                returnMap.put("brand_nextid", roomBean.getNextUserId());
                returnMap.put("type", "share_brand");
                returnMap.put("user_brands", roomBean.GrantBrand(userBean));
            } else {*/
                userBean.getBrands().add(brand);
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("brand", brand);
                returnMap.put("type", "get_brand");
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }
        if ("flow".equals(jsonTo.get("type"))) {
            // 流局
            if (roomBean.getBrands().size() == 0) {
                returnMap.put("type", "flow");
                // 设置流局
                roomBean.setFlow(1);
            }
            roomBean.addLog(returnMap);
            // 记录战绩
            gameService.addRecord(roomBean);
            roomBean.InItReady();
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }

        // 需要摸牌
        if ("brand_nextid".equals(jsonTo.get("type"))) {
            roomBean.setHucount(0);
            // 查找下一个摸牌用户
            returnMap.put("type", "brand_nextid");
            if (roomBean.getRepair_baruserid() != 0) {
                returnMap.put("brand_nextid", roomBean.getRepair_baruserid());
            } else {
                returnMap.put("brand_nextid", roomBean.getNextUserId());
            }
            for (UserBean user :
                    roomBean.getGame_userlist()) {
                returnMap.put("nohu", user.getNohu());
                if (user.getUserid() == Integer.valueOf(String.valueOf(returnMap.get("brand_nextid")))) {
                    user.getNoany().clear();
                    user.getNohu().clear();
                    returnMap.put("nohu", user.getNohu());
                }
                Public_State.clients.get(user.getOpenid()).sendMessageTo(returnMap);
            }
        }

        // 获取位置距离
        if ("get_position".equals(jsonTo.get("type"))) {
            returnMap.put("type", "get_position");
            if (roomBean.getGame_userlist().size() == 2) {
                List<String> list = new ArrayList<String>();
                double distance = MapHelper.GetPointDistance(roomBean
                        .getGame_userlist().get(0).getLog_lat(), roomBean
                        .getGame_userlist().get(1).getLog_lat());
                String re = roomBean.getGame_userlist().get(0).getNickname()
                        + "-" + roomBean.getGame_userlist().get(1).getNickname()
                        + "-" + String.valueOf(distance);
                list.add(String.valueOf(re));
                returnMap.put("list", list);
            } else if (roomBean.getGame_userlist().size() == 4) {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < roomBean.getGame_userlist().size(); i++) {
                    UserBean user = roomBean.getGame_userlist().get(i);
                    for (int j = i; j < roomBean.getGame_userlist().size(); j++) {
                        UserBean user2 = roomBean.getGame_userlist().get(j);
                        if (user.getUserid() != user2.getUserid()) {
                            double distance = MapHelper.GetPointDistance(
                                    user.getLog_lat(), user2.getLog_lat());
                            String re = user.getNickname() + "-"
                                    + user2.getNickname() + "-"
                                    + String.valueOf(distance);
                            list.add(re);
                        }
                    }
                }
                returnMap.put("list", list);
            }
            returnMap.put("position", roomBean.getUser_positions());
            sendMessageTo(returnMap);
        }

        //查看所有房间
        if ("selectRooms".equals(jsonTo.get("type"))) {
            returnMap.clear();
            List<RoomBean> list = new ArrayList<>();
            int state = Integer.valueOf(jsonTo.get("state"));//0普通 1俱乐部
            if (state == 0) {
                String number = jsonTo.get("number");//2 二人场  4 四人场
                userBean.setDttype(number);
                for (String s : Public_State.PKMap.keySet()) {
                    if (Public_State.PKMap.get(s).getMax_person() == Integer.valueOf(number) && Public_State.PKMap.get(s).getNumber_3() == 0) {
                        list.add(Public_State.PKMap.get(s));
                    }
                }
                returnMap.put("role", -1);
            } else {
                int club_number = Integer.valueOf(jsonTo.get("club_number"));//亲友圈号
                userBean.setNumber_3(club_number);
                String fen = jsonTo.get("fen");//查询几倍场的房间  默认0全部  其他 0.5  1  2  5
                userBean.setDttype(fen);
                Public_State.checkRoom(club_number);
                for (String s : Public_State.PKMap.keySet()) {
                    if (!fen.equals("0")) {
                        if (Public_State.PKMap.get(s).getNumber_3() == club_number && String.valueOf(Public_State.PKMap.get(s).getFen()).equals(fen)) {
                            list.add(Public_State.PKMap.get(s));
                        }
                    } else {
                        if (Public_State.PKMap.get(s).getNumber_3() == club_number) {
                            list.add(Public_State.PKMap.get(s));
                        }
                    }
                }
                returnMap.put("role", loginDao.getRole(userBean.getUserid(), club_number));
            }
            RoomBean r = new RoomBean();
            List<Map<String, Object>> maps = r.getrooms(list, "roomno-fen-max_number-game_number", "avatarurl");
            returnMap.put("rooms", maps);
            returnMap.put("type", "selectRooms");
            sendMessageTo(returnMap);
        }

        //查看所有玩家(玩家列表)
        if ("selectUsers".equals(jsonTo.get("type"))) {
            returnMap.clear();
            returnMap.put("userList", Public_State.getAlinenum(userBean));
            returnMap.put("type", "selectUsers");
            sendMessageTo(returnMap);
        }

        //邀请某个玩家
        if ("invite".equals(jsonTo.get("type"))) {
            returnMap.clear();
            returnMap.put("type", "invite");
            returnMap.put("openid", userBean.getOpenid());
            returnMap.put("nickname", userBean.getNickname());
            returnMap.put("roomno", roomBean.getRoomno());
            returnMap.put("game_type", 0);
            String openid = jsonTo.get("openid");//被邀请人的openid
            sendMessageTo(returnMap, openid);
        }
        //拒绝邀请
        if ("refuse".equals(jsonTo.get("type"))) {
            returnMap.clear();
            returnMap.put("type", "refuse");
            returnMap.put("nickname", userBean.getNickname());
            String openid = jsonTo.get("openid");//邀请人的openid
            sendMessageTo(returnMap, openid);
        }

        // 记录操作日志
        if (roomBean != null) {
            for (String value : Public_State.types) {
                if (value.equals(jsonTo.get("type"))) {
                    roomBean.addLog(returnMap);
                    break;
                }
            }
        }
        // 消息通道
        if ("message".equals(jsonTo.get("type"))) {
            returnMap.put("type", "message");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("text", jsonTo.get("text"));
            sendMessageTo(returnMap, roomBean);
            sendMessageTo(returnMap);
        }
        // 断线重连
        if ("end_wifi".equals(jsonTo.get("type"))) {
            roomBean = Public_State.PKMap.get(jsonTo.get("roomno"));
            if (roomBean == null) {
                returnMap.put("state", "100");
            } else {
                returnMap.put("type", "con_wifi");
                returnMap.put("userid", userBean.getUserid());
                // 告知其他人我已经在线
                userBean = roomBean.getUserBean(userBean.getUserid());
                userBean.setExit_state(0);

                sendMessageTo(returnMap, roomBean);
                // 查询出房间信息返回
                roomBean.getRoomBean_Custom(
                        "clubid-cannon-dice-exit_game-end_userid-roomno-user_positions-brands_count-fen-fan-money-banker" +
                                "-game_number-max_number-max_person-state-end_userid-game_type-user_log",
                        returnMap,
                        "userid-nickname-avatarurl-brands-bump_brands-show_brands-ishu-hide_brands-out_brands-log_lat" +
                                "-number-hu_state");
            }
            returnMap.put("type", "end_wifi");
            sendMessageTo(returnMap);
        }
        baseDao.CloseAll();
    }

    private void room_change(RoomBean room,int type) {
        returnMap.clear();
        returnMap.put("type","room_change");
        returnMap.put("change_type",type);
        room.getRoomBean_Custom2("avatarurl",returnMap,"roomno-game_number-fen-max_number");

        if(room.getNumber_3()!=0){//证明是俱乐部房间
            for(Map.Entry<String, Mahjong_Socket> entry : Public_State.clients.entrySet()){
                Mahjong_Socket ws = entry.getValue();
                if(type!=4){
                    if (ws.userBean.getUserid()==userBean.getUserid())continue;
                }else{
                    returnMap.put("change_type",type-1);
                }
                if(ws.userBean.getDttype()!=null) {//查询全部
                    if(ws.userBean.getDttype().equals("0")){//查询全部
                        if(ws.userBean.getNumber_3()==room.getNumber_3()&&Public_State.noRoom(ws.userBean)){//证明在同一个俱乐部
                            ws.sendMessageTo(returnMap,ws.userBean.getOpenid());
                        }
                    }else{//查询相同底分的
                        if(ws.userBean.getNumber_3()==room.getNumber_3() && Integer.valueOf(ws.userBean.getDttype()) == room.getFen() &&Public_State.noRoom(ws.userBean)){//证明在同一个俱乐部
                            ws.sendMessageTo(returnMap,ws.userBean.getOpenid());
                        }
                    }
                }
            }
        }else{//证明是普通房间
            for(Map.Entry<String, Mahjong_Socket> entry : Public_State.clients.entrySet()){
                Mahjong_Socket ws = entry.getValue();
                if (ws.userBean.getUserid()==userBean.getUserid())continue;
                if(ws.userBean.getDttype()!=null){
                    String renshu = ws.userBean.getDttype();
                    if(renshu!=null){
                        if(Integer.valueOf(renshu) == room.getMax_person() &&Public_State.noRoom(ws.userBean)){//证明在同一个俱乐部
                            ws.sendMessageTo(returnMap,ws.userBean.getOpenid());
                        }
                    }
                }
            }
        }
    }

    /***
     * 退出房间
     *
     * @throws IOException
     */
    public void Exit_Room() {
        returnMap.clear();
        // 已加入房间且房间在等待加入阶段则退出
        if (roomBean != null && roomBean.getState() == 0 && roomBean.getHouseid() != userBean.getUserid()) {
            // 将自己从房间内清除
            roomBean.User_Remove(userBean.getUserid());
            returnMap.put("type", "exit");
            returnMap.put("userid", userBean.getUserid());
            sendMessageTo(returnMap, roomBean);
            room_change(roomBean, 0);
            roomBean = null;
            userBean.Initialization();
            userBean.setHu_state(-1);
            // sendMessageTo(returnMap);
        } else if (roomBean != null && roomBean.getState() != 0) {
            // 告知其他人我已经掉线
            returnMap.put("type", "toUser_exit");
            returnMap.put("end_user", userBean.getUserid());
            sendMessageTo(returnMap, roomBean);
            // 房间存在且房间为开始且自己是房主的情况则解散房间
        } else if (roomBean != null && roomBean.getState() == 0 && roomBean.getHouseid() == userBean.getUserid()) {
            Exit_All();
        }
        roomBean = null;
    }

    /**
     * 解散房间
     */
    public void Exit_All() {
        if (userBean != null) {
            userBean.setHu_state(-1);
        }
        if (roomBean != null) {
            returnMap.put("type", "exit_all");
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
            Public_State.PKMap.remove(roomBean.getRoomno());
            room_change(roomBean, 3);
        }
        roomBean = null;
    }

    @OnError
    public void onError(Session session, Throwable error) {
        if (userBean != null && userBean.getHu_state() == -1) {
            Public_State.clients.remove(userBean.getOpenid());
        } else if (roomBean != null) {
            Exit_Room();
        }
        if (!"远程主机强迫关闭了一个现有的连接".equals(error.getMessage()) && error.getMessage() != null) {
            error.printStackTrace();
            System_Mess.system_Mess.ToMessagePrint(
                    userBean.getNickname() + "异常" + error.getLocalizedMessage() + "***" + error.getMessage());
        }
    }

    /**
     * 发送消息(房间所有人)
     *
     * @param returnMap
     * @param roomBean
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, RoomBean roomBean) {
        for (UserBean user : roomBean.getGame_userlist()) {
            Mahjong_Socket webSocket = (Mahjong_Socket) Public_State.clients.get(user.getOpenid());
            if (webSocket != null && webSocket.session.isOpen()) {
                // 不等于自己的则发送消息
                if (!user.getOpenid().equals(userBean.getOpenid())) {
                    webSocket.sendMessageTo(returnMap);
                }
            }
        }
    }

    /**
     * 给自己返回信息
     *
     * @param returnMap
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap) {
        if (session.isOpen()) {
            String returnjson = gson.toJson(returnMap).toString();
            // 加密
            // returnjson=AES.encrypt(returnjson);
            try {
                session.getBasicRemote().sendText(returnjson);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if (!returnjson.contains("heartbeat")) {
                System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "返回消息(自己)" + returnjson);
            }
        }
    }

    /**
     * 给指定用户发送
     *
     * @param returnMap
     * @param openid
     * @throws IOException
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, String openid) {
        Mahjong_Socket websocket = (Mahjong_Socket) Public_State.clients.get(openid);
        if (websocket != null && websocket.session.isOpen()) {
            String returnjson = gson.toJson(returnMap).toString();
            try {
                websocket.session.getBasicRemote().sendText(returnjson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "(指定)返回消息" + returnjson);
        }
    }
}
