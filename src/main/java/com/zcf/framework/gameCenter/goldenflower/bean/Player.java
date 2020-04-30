package com.zcf.framework.gameCenter.goldenflower.bean;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IDEA
 * author:ZhaoQ
 * className:
 * Date:2020/3/2
 * Time:14:12
 */
public class Player {
    //玩家手牌
    private ArrayList<Card> handCards;
    //玩家手牌等级
    private int grade;
    // 用户id
    private int userid;
    // 微信openid
    private String openid;
    // 用户昵称
    private String nickname;
    // 用户头像
    private String avatarurl;
    // 用户性别
    private  int sex;
    // 封号状态0正常1封号
    private int state;
    // 用户状态0默认未准备1准备
    private int ready_state;
    // 掉线状态默认0掉线1
    private int exit_state;
    // 用户解散状态默认0未应答1同意2不同意
    private int exit_game;
    // ip
    private String ip;
    // 坐标
    private String log_lat;
    // 房卡
    private int diamond;
    //金币
    private int money;
    // 分
    private int number;
    // 当局分
    private int dqnumber;
    // 庄家标识
    private boolean banker;
    //玩家锁
    private ReentrantLock lock;
    //角色 0观战 1参战
    private int role;
    //当前位置 -1大厅
    private int gps;
    //亲友圈id
    private int number_3;
    private int q_id;
    //所在房间类型  1初级 2中级 3高级
    private int dttype;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getDttype() {
        return dttype;
    }

    public void setDttype(int dttype) {
        this.dttype = dttype;
    }

    public int getNumber_3() {
        return number_3;
    }

    public void setNumber_3(int number_3) {
        q_id = number_3;
        this.number_3 = number_3;
    }

    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public int getGps() {
        return gps;
    }

    public void setGps(int gps) {
        this.gps = gps;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }


    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public ArrayList<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(ArrayList<Card> handCards) {
        this.handCards = handCards;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getReady_state() {
        return ready_state;
    }

    public void setReady_state(int ready_state) {
        this.ready_state = ready_state;
    }

    public int getExit_state() {
        return exit_state;
    }

    public void setExit_state(int exit_state) {
        this.exit_state = exit_state;
    }

    public int getExit_game() {
        return exit_game;
    }

    public void setExit_game(int exit_game) {
        this.exit_game = exit_game;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLog_lat() {
        return log_lat;
    }

    public void setLog_lat(String log_lat) {
        this.log_lat = log_lat;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDqnumber() {
        return dqnumber;
    }

    public void setDqnumber(int dqnumber) {
        this.dqnumber = dqnumber;
    }

    public boolean isBanker() {
        return banker;
    }

    public void setBanker(boolean banker) {
        this.banker = banker;
    }

    public Player(String name ) {
        this.nickname = name;
        this.handCards = new ArrayList<Card>();
        this.lock = new ReentrantLock();
    }

    public Player() {
        this.handCards = new ArrayList<Card>();
        this.lock = new ReentrantLock();
    }

    public ArrayList<Card> getPlayerCards() {
        return handCards;
    }
    public void setPlayerCards(ArrayList<Card> playerCards) {
        this.handCards = playerCards;
    }

    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    @Override
    public String toString() {
        return "玩家：" + nickname + " " + handCards;
    }

    /**
     * 初始化用户
     */
    public void Initialization() {
        this.handCards.clear();
        this.dqnumber = 0;
    }

    /***
     * 获取用户自定义信息
     *
     * @param tablename
     * @param map
     */
    public void getUser_Custom(String tablename, Map<String, Object> map) {
        String names[] = tablename.split("-");
        for (String key : names) {
            if (key.equals("userid"))
                map.put(key, userid);
            if (key.equals("nickname"))
                map.put(key, nickname);
            if (key.equals("openid"))
                map.put(key, openid);
            if (key.equals("sex"))
                map.put(key, sex);
            if (key.equals("avatarurl"))
                map.put(key, avatarurl);
            if (key.equals("ip"))
                map.put(key, ip);
            if (key.equals("log_lat"))
                map.put(key, log_lat);
            if (key.equals("ready_state"))
                map.put(key, ready_state);
            if (key.equals("exit_state"))
                map.put(key, exit_state);
            if (key.equals("banker"))
                map.put(key, banker);
            if (key.equals("number"))
                map.put(key, number);
            if (key.equals("diamond"))
                map.put(key, diamond);
            if (key.equals("role"))
                map.put(key, role);
            if (key.equals("exit_game"))
                map.put(key, exit_game + "");
            if (key.equals("dqnumber"))
                map.put(key, dqnumber + "");
        }
    }
    /***
     * 获取用户自定义信息
     *
     * @param tablename
     * @param
     */
    public Map getUserCustom(String tablename) {
        String names[] = tablename.split("-");
        HashMap<String, Object> map = new HashMap<>();
        for (String key : names) {
            if (key.equals("userid"))
                map.put(key, userid);
            if (key.equals("nickname"))
                map.put(key, nickname);
            if (key.equals("openid"))
                map.put(key, openid);
            if (key.equals("sex"))
                map.put(key, sex);
            if (key.equals("avatarurl"))
                map.put(key, avatarurl);
        }
        return map;
    }
}
