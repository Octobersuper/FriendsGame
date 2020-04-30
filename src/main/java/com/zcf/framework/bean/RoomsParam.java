package com.zcf.framework.bean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/*
 *@Author:ZhaoQi
 *@methodName:房间属性
 *@Params:
 *@Description:
 *@Return:
 *@Date:2019/2/12
 */
public class RoomsParam {
    private Integer isStart;//游戏是否开始   0等人阶段  1定庄阶段  2下注阶段发牌  3开牌倒计时
    private String roomName;//房间号
    private Integer gameId;//对局ID
    private Integer pkNum;//已经比过的人数
    private Long isUp;//是否已有人当庄，有的话存储庄家ID
    private Integer talkNum;//话事人数
    private Lock lock;//房间内的锁
    private List<Integer> roomCards;//房间的牌
    private Map<String, UserBean> users;//房间的玩家
    private Map<String,Integer> roomType;//开房的参数类型
    private List<Integer> postions;//房间内的所有位置
    private List<Integer> downCards;//底牌
    private Integer timer;//倒计时时间

    public RoomsParam getCoutom(){
        RoomsParam room = new RoomsParam();
        room.setIsStart(isStart);
        room.setRoomName(roomName);
        room.setGameId(gameId);
        room.setIsUp(isUp);
        room.setTimer(timer);
        return room;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<Integer> getDownCards() {
        return downCards;
    }

    public void setDownCards(List<Integer> downCards) {
        this.downCards = downCards;
    }

    public List<Integer> getPostions() {
        return postions;
    }

    public void setPostions(List<Integer> postions) {
        this.postions = postions;
    }

    public Map<String, Integer> getRoomType() {
        return roomType;
    }

    public void setRoomType(Map<String, Integer> roomType) {
        this.roomType = roomType;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public List<Integer> getRoomCards() {
        return roomCards;
    }

    public void setRoomCards(List<Integer> roomCards) {
        this.roomCards = roomCards;
    }

    public Map<String, UserBean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserBean> users) {
        this.users = users;
    }

    public Integer getTalkNum() {
        return talkNum;
    }

    public void setTalkNum(Integer talkNum) {
        this.talkNum = talkNum;
    }

    public Long getIsUp() {
        return isUp;
    }

    public void setIsUp(Long isUp) {
        this.isUp = isUp;
    }

    public Integer getPkNum() {
        return pkNum;
    }

    public void setPkNum(Integer pkNum) {
        this.pkNum = pkNum;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getIsStart() {
        return isStart;
    }

    public void setIsStart(Integer isStart) {
        this.isStart = isStart;
    }

    @Override
    public String toString() {
        return "RoomsParam{" +
                "isStart=" + isStart +
                ", gameId='" + gameId + '\'' +
                ", pkNum=" + pkNum +
                ", isUp=" + isUp +
                ", talkNum=" + talkNum +
                ", lock=" + lock +
                ", roomCards=" + roomCards +
                ", users=" + users +
                ", roomType=" + roomType +
                ", postions=" + postions +
                '}';
    }
}
