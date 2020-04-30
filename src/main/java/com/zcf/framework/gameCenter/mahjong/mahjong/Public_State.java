package com.zcf.framework.gameCenter.mahjong.mahjong;

import com.zcf.framework.gameCenter.goldenflower.bean.Golden_room;
import com.zcf.framework.gameCenter.goldenflower.service.F_WebSocket;
import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.dao.M_GameDao;
import com.zcf.framework.gameCenter.mahjong.service.Mahjong_Socket;
import util.BaseDao;
import util.UtilClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Public_State {
    // 客户端的线程池(麻将)
    public static Map<String, Mahjong_Socket> clients = new ConcurrentHashMap<String, Mahjong_Socket>();
    // 客户端的线程池(炸金花)
    public static Map<String, F_WebSocket> Golden_clients = new ConcurrentHashMap<String, F_WebSocket>();
    // 客户端的房间（麻将）
    public static Map<String, RoomBean> PKMap = new LinkedHashMap<String, RoomBean>();
    // 客户端的房间（炸金花）
    public static Map<String, Golden_room> golden_room = new LinkedHashMap<String, Golden_room>();
    // windows窗口
    //public static CreaWindow window = new CreaWindow();
    // 是否开启记录
    public static boolean record_bool = false;
    // 解散时间等待
    public static int exit_time;
    // 2人支付
    public static String[] establish_two;
    // 4人支付
    public static String[] establish_four;
    public static int time = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "exit_time"));
    // 初始钻石
    public static int diamond;
    public static String[] sorts = new String[]{"1-2-3", "1-3-2", "2-1-3", "2-3-1", "3-1-2", "3-2-1", "4-2-3",
            "4-3-2", "2-4-3", "2-3-4", "3-4-2", "3-2-4"};
    // 操作日志记录类型
    public static String[] types = new String[]{"out_brand", "bump", "endhu", "hide_bar", "show_bar",
            "show_bar_brand", "repair_bar_bump", "get_brand"};

    static {
        // CheckOut.checkOut();
        //diamond = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "diamond"));
        // 启动windows窗口
        // window.CreateWindow();
        BaseDao baseDao = new BaseDao();
        M_GameDao gameDao = new M_GameDao(baseDao);
        // 读取配置
        gameDao.getConfig();
        baseDao.CloseAll();
        baseDao = null;
        gameDao = null;
    }

    /**
     * 检测是否在房间中
     *
     * @param userid
     * @return
     */
    public static String ISUser_Room(int userid) {
        for (String key : PKMap.keySet()) {
            RoomBean roomBean = PKMap.get(key);
            for (UserBean userBean : roomBean.getGame_userlist()) {
                if (userBean.getUserid() == userid)
                    return roomBean.getRoomno();
            }
        }
        return null;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:查询亲友圈在线人数
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2019/11/22
     */
    public static List<Map> getAlinenum(UserBean u) {
        BaseDao baseDao = new BaseDao();
        M_GameDao gameDao = new M_GameDao(baseDao);
        List<Map> allUsers = gameDao.getAllUsers(u);
        for (Map user :
                allUsers) {
            if (clients.get(user.get("openid").toString())!=null) {
                if (clients.get(user.get("openid").toString()).userBean.getHu_state() == -1) {
                    user.put("line_state", 1);//大厅中可邀请
                } else {
                    user.put("line_state", 2);//游戏中
                }
            }else{
                user.put("line_state", 0);//不在线
            }
        }
        baseDao.CloseAll();
        baseDao = null;
        gameDao = null;
        return allUsers;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:查询亲友圈在线人数(麻将)
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2019/11/22
     */
    public static int getAlinenum2(int number_3) {
        List<Map> list = new ArrayList<>();
        for (String key : clients.keySet()) {
            Mahjong_Socket mahjong_socket = clients.get(key);
            if (mahjong_socket.userBean.getNumber_3() == number_3) {
                list.add(mahjong_socket.userBean.getUserCustom("userid-money-nickname-avatarurl-openid"));
            }
        }
        return list.size();
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:查询亲友圈在线人数(炸金花)
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2019/11/22
     */
    public static int getAlinenum3(int number_3) {
        List<Map> list = new ArrayList<>();
        for (String key : Golden_clients.keySet()) {
            F_WebSocket socket = Golden_clients.get(key);
            if (socket.userBean.getQ_id() == number_3) {
                list.add(socket.userBean.getUserCustom("userid-money-nickname-avatarurl-openid"));
            }
        }
        return list.size();
    }


    /**
     * @ Author:ZhaoQi
     * @ methodName:查询亲友圈在线玩家
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2019/11/22
     */
    public static List<Map> getAlinenum_online_golden(int number_3) {
        List<Map> list = new ArrayList<>();
        for (String key : Golden_clients.keySet()) {
            F_WebSocket socket = Golden_clients.get(key);
            if (socket.userBean.getNumber_3() == number_3 && socket.userBean.getGps() == -1) {
                list.add(socket.userBean.getUserCustom("userid-money-nickname-avatarurl-openid"));
            }
        }
        return list;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:亲友圈游戏中桌数
     * @ Description:
     * @ Return:type 1游戏中桌数  2等待桌数
     * @ Date:2020/4/13
     */
    public static int getGameing(Object circlenumber, int type) {
        int num = 0;
        for (Map.Entry<String, RoomBean> entry : PKMap.entrySet()) {
            RoomBean f_pkBean = entry.getValue();
            if (String.valueOf(f_pkBean.getNumber_3()).equals(circlenumber)) {
                if (type == 1) {
                    if (f_pkBean.getGame_number() >= 1) {
                        num++;
                    }
                } else {
                    if (f_pkBean.getGame_number() < 1) {
                        num++;
                    }
                }
            }
        }
        return num;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:判断用户在没在房间内
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/4/28
     */
    public static boolean noRoom(UserBean userBean) {
        for (Map.Entry<String, RoomBean> entry : PKMap.entrySet()) {
            RoomBean f_pkBean = entry.getValue();
            List<UserBean> userlist = f_pkBean.getGame_userlist();
            for (UserBean user :
                    userlist) {
                if (user.getUserid()==userBean.getUserid()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:
     *@ Params:检测亲友圈内有没有房间 没有的话创建一个
     *@ Description:
     *@ Return:
     *@ Date:2020/4/30
     */
    public static void checkRoom(int clubnumber) {
        int count = 0;
        for (Map.Entry<String, RoomBean> entry : PKMap.entrySet()) {
            RoomBean f_pkBean = entry.getValue();
            if (f_pkBean.getNumber_3()==clubnumber){
                if(f_pkBean.getGame_userlist().size()<4){
                    count++;
                }
            }
        }
        if(count==0){

        }
    }
}
