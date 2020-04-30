package com.zcf.framework.gameCenter.goldenflower.bean;

import com.google.gson.Gson;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import com.zcf.framework.gameCenter.mahjong.service.Exit_Game;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2020/3/3
 * Time:9:14
 */
public class Golden_room {
    // 房间号
    private String roomno;
    //房间创建时间
    private String data;
    // 玩家集合
    private List<Player> game_userlist;
    // 观战玩家集合
    private List<Player> look_userlist;
    // 房间操作记录
    private List<Map<String, Object>> user_log;
    // GPS 0默认不启用1启用
    private int gps;
    // 当前庄家id
    private int banker;
    // 房主id
    private int houseid;
    // 当前局数
    private int game_number;
    // 房间最大局数
    private int max_number = 15;
    //房间最大注数
    private int max_zhu;
    //豹子喜钱
    private int aaa_money;
    // 房间最大人数
    private int max_person = 8;
    // 房间状态0等待加入1准备阶段2已开始3正在结算4已结算
    private int state;
    // 房间解散状态0未发起1正在解散
    private int exit_game;
    // 解散房間倒計時
    private Exit_Game exit_time;
    // 最后操作人的id
    private int end_userid;
    // 底分
    private int fen;
    // 房间锁
    private Lock lock;
    // 俱乐部id房间类型默认个人创建0
    private int clubid;
    // 用户位置0-7下标代表1-8的座位，值代表用户id
    private int[] user_positions;
    // 最后一次操作
    private Map<String, Object> nextMap;
    //最后一次出的牌
    private int lastBrand;
    //最后一次出牌人的ID
    private int lastUserid;
    // 胜利的人
    private int victoryid = -1;
    // 亲友圈id
    private int q_id;
    // 房间类型 1亲友圈 2普通金币模式
    private int room_type;

    public List<Player> getLook_userlist() {
        return look_userlist;
    }

    public void setLook_userlist(List<Player> look_userlist) {
        this.look_userlist = look_userlist;
    }

    public int getRoom_type() {
        return room_type;
    }

    public void setRoom_type(int room_type) {
        this.room_type = room_type;
    }

    public int getMax_zhu() {
        return max_zhu;
    }

    public void setMax_zhu(int max_zhu) {
        this.max_zhu = max_zhu;
    }

    public int getAaa_money() {
        return aaa_money;
    }

    public void setAaa_money(int aaa_money) {
        this.aaa_money = aaa_money;
    }

    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public int getLastUserid() {
        return lastUserid;
    }

    public void setLastUserid(int lastUserid) {
        this.lastUserid = lastUserid;
    }

    public int getLastBrand() {
        return lastBrand;
    }

    public void setLastBrand(int lastBrand) {
        this.lastBrand = lastBrand;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    Map<String, Object> map = new HashMap<String, Object>();

    public Golden_room() {
        // 用户座位
        this.user_positions = new int[] { -1, -1, -1, -1 ,-1,-1,-1,-1};
        // 房间锁
        this.lock = new ReentrantLock(true);
        // 玩家集合
        this.game_userlist = new ArrayList<Player>();
        // 观战玩家集合
        this.look_userlist = new ArrayList<Player>();
        // 房间操作记录
        this.user_log = new ArrayList<Map<String, Object>>();
        // 记录碰的操作
        this.nextMap = new HashMap<String, Object>();
    }

    /**
     * 房间信息初始化
     */
    public void Initialization() {
        // 初始化用户
        for (Player player : game_userlist) {
            player.Initialization();
        }
        // 操作日志
        this.user_log.clear();
        this.game_number++;
        this.state = 1;
    }

    /**
     * 定庄
     */
    public void Select_Banker(){
        int index = -1;
        //第一局随机坐庄
        if(game_number==1){
            //随机
            index = (int)(Math.random() * this.game_userlist.size());
            this.banker = this.game_userlist.get(index).getUserid();
        }else{
            if(victoryid!=-1){
                //赢家坐庄
                this.banker=victoryid;
            }else{
                //随机0~3
                index = (int)(Math.random() * this.game_userlist.size());
                this.banker = this.game_userlist.get(index).getUserid();
            }
        }
        this.end_userid=this.banker;
    }

    /**
     * 获取下个操作人的id
     *
     * @return
     */
    public int getNextUserId() {
        int userid = -1;
        Player player;
        do{
            int thisindex = -1;
            for (int i = 0; i < user_positions.length; i++) {
                if (user_positions[i] == -1)
                    continue;
                if (user_positions[i] == end_userid) {
                    thisindex = i;
                }
            }
            for (int i = 0; i < user_positions.length; i++) {
                if ((thisindex + 1) == user_positions.length) {
                    thisindex = 0;
                } else {
                    thisindex++;
                }
                if (user_positions[thisindex] == -1)
                    continue;
                userid = user_positions[thisindex];
                break;

            }
            // 为此用户创建一个倒计时线程
            end_userid = userid;
            player = getUserBean(userid);
        }while (player.getRole()!=0);
        return userid;
    }

    /**
     * 自定义获取房间列及用户列
     *
     * @param table
     * @param returnMap
     * @param usertable
     */
    public void getRoomBean_Custom(String table, Map<String, Object> returnMap, String usertable) {
        getRoomBean_Custom(table, returnMap);
        returnMap.put("userList", getGame_userList(usertable, 0));

    }

    /**
     * 自定义获取房间列及用户列(胡牌用户)
     *
     * @param table
     * @param returnMap
     * @param usertable
     */
    public void getRoomBean_Custom_HU(String table, Map<String, Object> returnMap, String usertable) {
        getRoomBean_Custom(table, returnMap);
        returnMap.put("userList", getGame_userList(usertable, 1));
    }

    /**
     * 自定义获取房间列
     *
     * @param table
     * @param returnMap
     */
    public void getRoomBean_Custom(String table, Map<String, Object> returnMap) {
        String[] names = table.split("-");
        for (String name : names) {
            if ("roomno".equals(name))
                returnMap.put("roomno", this.roomno);
            if ("banker".equals(name))
                returnMap.put("banker", this.banker);
            if ("game_number".equals(name))
                returnMap.put("game_number", this.game_number);
            if ("max_number".equals(name))
                returnMap.put("max_number", this.max_number);
            if ("max_person".equals(name))
                returnMap.put("max_person", this.max_person);
            if ("state".equals(name))
                returnMap.put("state", this.state + "");
            if ("end_userid".equals(name))
                returnMap.put("end_userid", this.end_userid);
            if ("fen".equals(name))
                returnMap.put("fen", this.fen);
            if ("user_positions".equals(name))
                returnMap.put("user_positions", this.user_positions);
            if ("nextMap".equals(name))
                returnMap.put("nextMap", this.nextMap);
            if ("user_log".equals(name)) {
                if (this.user_log.size() == 0) {
                    returnMap.put("user_log", null);
                } else {
                    returnMap.put("user_log", this.user_log.get(this.user_log.size() - 1));
                }
            }
            if ("exit_game".equals(name))
                returnMap.put("exit_game", this.exit_game);
            if ("data".equals(name))
                returnMap.put("data", this.data);
            if ("max_zhu".equals(name))
                returnMap.put("max_zhu", this.data);
        }
    }

    /***
     * 自定义获取用户信息
     *
     * @param tablename
     * @return
     */
    public List<Map<String, Object>> getGame_userList(String tablename, int type) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Player> userList;
        userList =  getGame_userlist();
        for (int i = 0; i < userList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            userList.get(i).getUser_Custom(tablename, map);
            list.add(map);
        }
        return list;
    }

    /**
     * 删除一名用户
     *
     * @param userid
     */
    public void User_Remove(int userid) {
        for (int i = 0; i < game_userlist.size(); i++) {
            if (game_userlist.get(i).getUserid() == userid) {
                game_userlist.remove(i);
                break;
            }
        }
        this.remove_opsitions(userid);
    }

    /**
     * 删除一名观战用户
     *
     * @param userid
     */
    public void User_Remove_look(int userid) {
        for (int i = 0; i < look_userlist.size(); i++) {
            if (look_userlist.get(i).getUserid() == userid) {
                look_userlist.remove(i);
                break;
            }
        }
    }

    /***
     * 获取指定id的用户实例
     *
     * @param userid
     * @return
     */
    public Player getUserBean(int userid) {
        for (Player player : game_userlist) {
            if (player.getUserid() == userid)
                return player;
        }
        return null;
    }

    /**
     * 剔除指定用户的位置
     *
     * @param userid
     */
    public void remove_opsitions(int userid) {
        for (int i = 0; i < user_positions.length; i++) {
            if (user_positions[i] == userid) {
                user_positions[i] = -1;
            }
        }
    }

    /***
     * 加入用户位置-
     *
     */
    public void setUser_positions(int userid) {
        for (int i = 0; i < user_positions.length; i++) {
            if (user_positions[i] == -1) {
                user_positions[i] = userid;
                break;
            }
        }
        /*if (game_userlist.size() < 3) {
            do{
                int random=(int)(Math.random()*8+1);
                if (user_positions[random] == -1) {
                    user_positions[random] = userid;
                    break;
                }
            }while (true);
        } else {
            for (int i = 0; i < user_positions.length; i++) {
                if (user_positions[i] == -1) {
                    user_positions[i] = userid;
                    break;
                }
            }
        }*/
    }


    /**
     * getrooms
     *
     * @param
     * @param
     * @param
     */
    public List<Map<String, Object>> getrooms(List<Golden_room> list, String table, String usertable) {
        List<Map<String, Object>> map = new ArrayList<>();
        if(list.size()!=0){
            for (Golden_room room:list) {
                Map<String, Object> returnMap = new HashMap<>();
                room.getRoomBean_Custom(table, returnMap);
                returnMap.put("userList", room.getGame_userList(usertable, 0));
                map.add(returnMap);
            }
            return map;
        }else
            return map;
    }


    /**
     * 添加操作日志
     *
     * @param logMap
     */
    public void addLog(Map<String, Object> logMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : logMap.keySet()) {
            map.put(key, logMap.get(key));
        }
        this.user_log.add(map);
    }


    // 所有用户重置准备
    public void InItReady() {
        if (this.game_number == this.max_number) {
            // 清空房间信息
            Public_State.golden_room.remove(this.roomno);
        } else {
            // 设置准备阶段
            this.state = 1;
            Ready_InIt();
        }
    }

    // 初始化所有用户准备状态
    public void Ready_InIt() {
        for (Player player : this.game_userlist) {
            player.setReady_state(0);
            player.setExit_game(0);
        }
    }

    /******************************************* get/set ******************************************************/

    public Map<String, Object> getNextMap() {
        return nextMap;
    }

    public void setNextMap(Map<String, Object> nextMap) {
        this.nextMap = nextMap;
    }

    public int getVictoryid() {
        return victoryid;
    }

    public void setVictoryid(int victoryid) {
        int num = 0;
        int userid = victoryid;
        for (Player player :
                game_userlist) {
            if(player.getDqnumber()>num){
                num = player.getDqnumber();
                userid = player.getUserid();
            }
        }
        this.victoryid = userid;
    }

    public int getClubid() {
        return clubid;
    }

    public void setClubid(int clubid) {
        this.clubid = clubid;
    }

    public int getMax_number() {
        return max_number;
    }

    public void setMax_number(int max_number) {
        this.max_number = max_number;
    }

    public int getHouseid() {
        return houseid;
    }

    public void setHouseid(int houseid) {
        this.houseid = houseid;
    }

    public int[] getUser_positions() {
        return user_positions;
    }

    public void setUser_positions(int[] user_positions) {
        this.user_positions = user_positions;
    }

    public int getExit_game() {
        return exit_game;
    }

    public void setExit_game(int exit_game) {
        this.exit_game = exit_game;
    }

    public int getGps() {
        return gps;
    }

    public void setGps(int gps) {
        this.gps = gps;
    }

    public int getFen() {
        return fen;
    }

    public void setFen(int fen) {
        this.fen = fen;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public List<Player> getGame_userlist() {
        return game_userlist;
    }

    public void setGame_userlist(List<Player> game_userlist) {
        this.game_userlist = game_userlist;
    }

    public List<Map<String, Object>> getUser_log() {
        return user_log;
    }

    public String getUser_log_text() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < game_userlist.size(); i++) {
            Map<String, Object> usermap = new HashMap<String, Object>();
            usermap.put("userid", game_userlist.get(i).getUserid());
            usermap.put("nickName", game_userlist.get(i).getNickname());
            usermap.put("avatarurl", game_userlist.get(i).getAvatarurl());
            list.add(usermap);
        }
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("game_number", game_number);
        map1.put("banker", banker);
        map1.put("max_number", max_number);
        map.put("roominfo", map1);
        map.put("usercards", list);
        map.put("user_positions", user_positions);
        map.put("user_log", this.user_log);
        return new Gson().toJson(map);
    }

    public void setUser_log(List<Map<String, Object>> user_log) {
        this.user_log = user_log;
    }

    public int getBanker() {
        return banker;
    }

    public void setBanker(int banker) {
        this.banker = banker;
    }

    public int getGame_number() {
        return game_number;
    }

    public void setGame_number(int game_number) {
        this.game_number = game_number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getEnd_userid() {
        return end_userid;
    }

    public void setEnd_userid(int end_userid) {
        this.end_userid = end_userid;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public int getMax_person() {
        return max_person;
    }

    public void setMax_person(int max_person) {
        this.max_person = max_person;
    }

    public Exit_Game getExit_time() {
        return exit_time;
    }

    public void setExit_time(Exit_Game exit_time) {
        this.exit_time = exit_time;
    }

}
