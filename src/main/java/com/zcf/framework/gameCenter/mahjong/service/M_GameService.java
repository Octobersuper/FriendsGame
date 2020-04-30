package com.zcf.framework.gameCenter.mahjong.service;

import com.zcf.framework.bean.User;
import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.dao.ClubDao;
import com.zcf.framework.gameCenter.mahjong.dao.M_GameDao;
import com.zcf.framework.gameCenter.mahjong.mahjong.Establish_PK;
import com.zcf.framework.gameCenter.mahjong.mahjong.Matching_PK;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import io.swagger.models.auth.In;
import util.BaseDao;
import util.Mahjong_Util;

import java.util.*;

import static util.Mahjong_Util.mahjong_Util;

public class M_GameService {
    private M_GameDao gameDao;
    private ClubDao clubDao;

    public M_GameService(BaseDao baseDao) {
        this.gameDao = new M_GameDao(baseDao);
        this.clubDao = new ClubDao(baseDao);
    }

    /**
     * 创建房间
     *
     * @return
     */
    public RoomBean Establish(Map<String, String> map, UserBean userBean, int clubid) {
        Integer paytype = 0;//Integer.valueOf(map.get("paytype"));//0房主  1AA
        // 创建房间所需房卡(2圈-4圈)
        int fangka;
        if (Integer.parseInt(map.get("max_number")) == 8)
            fangka = 4;
        else
            fangka = 6;

        if (clubid == 0) {
            int money = gameDao.getUserMoney(userBean.getUserid());
            // 金币不足
            if (money < fangka) {
                return null;
            }
        } else {
            int diamond = gameDao.getUserDiamond(userBean.getUserid());
            // 房卡不足
            if (diamond < fangka) {
                return null;
            }
        }

        // 创建房间
        RoomBean roomBean = Establish_PK.Establish();
        if (clubid != 0) {
            roomBean.setNumber_3(clubid);
        }
        roomBean.setPaytype(paytype);//支付方式
        if (clubid == 0) {
            userBean.setDttype(map.get("max_person"));
            // 指定房主
            roomBean.setHouseid(userBean.getUserid());
            // 加入自己
            roomBean.getGame_userlist().add(userBean);
            // 更新分数
            userBean.setNumber(0);
            // 经纬度
            userBean.setLog_lat(map.get("log_lat"));
            // 加入座位
            roomBean.setUser_positions(userBean.getUserid());
        } else {
            userBean.setDttype(map.get("fen"));
        }
        // 房间消耗房卡
        roomBean.setRoom_card(fangka);
        // 房间最大局数
        roomBean.setMax_number(Integer.parseInt(map.get("max_number")));
        // 底分
        roomBean.setFen(Integer.parseInt(map.get("fen")));
        // 人数
        roomBean.setMax_person(Integer.parseInt(map.get("max_person")));
        // 0为个人创建
        roomBean.setClubid(clubid);
        //点炮  报听 一炮多响 扎针
        roomBean.getRoomParams().put("p1", Integer.valueOf(map.get("p1")));
        roomBean.getRoomParams().put("p2", Integer.valueOf(map.get("p2")));
        roomBean.getRoomParams().put("p3", Integer.valueOf(map.get("p3")));
        roomBean.getRoomParams().put("p4", Integer.valueOf(map.get("p4")));
        return roomBean;
    }

    /**
     * 加入房间
     *
     * @param map
     * @param userBean
     * @return
     */
    public RoomBean Matching(Map<String, String> map, UserBean userBean) {
        // 初始化分数
        userBean.setNumber(0);
        // 加入房间
        return Matching_PK.Matching(userBean, map.get("roomno"));
    }

    /**
     * 检测房卡是否可以加入房间
     *
     * @param userBean
     * @param roomno
     * @return
     */
    public int ISMatching_Money(UserBean userBean, String roomno) {

        if (Public_State.PKMap.get(roomno) == null) {
            return 100;// 房间不存在
        }
        if (Public_State.PKMap.get(roomno).getPaytype() == 1) {
            if (gameDao.getUserDiamond(userBean.getUserid()) < Public_State.PKMap.get(roomno).getRoom_card()) {
                return 123;
            }
        }
        if (ISMatching(roomno, userBean)) {
            return 114;// 重复加入
        }
        return 0;
    }


    /**
     * 准备
     *
     * @param userBean
     * @param roomBean
     * @return
     */
    public int Ready(UserBean userBean, RoomBean roomBean) {
        // 准备
        userBean.setReady_state(1);
        int count = 0;
        // 判断是否所有人都准备
        roomBean.getLock().lock();
        for (UserBean user : roomBean.getGame_userlist()) {
            if (user.getReady_state() == 1)
                count++;
        }
        roomBean.getLock().unlock();
        return count;
    }

    /**
     * 检测是否有人可以胡牌
     *
     * @param roomBean
     * @param state
     * @return
     */
    public int Is_Hu(RoomBean roomBean, int state, UserBean user) {
        roomBean.getLock().lock();
        if (state == 0) {
            roomBean.setHucount(roomBean.getHucount() + 1);
        } else {
            if (roomBean.getRoomParams().get("p3") != 0) {
                roomBean.getHu_user_list().add(user);
                user.setHu_state(0);
            } else {
                if (roomBean.getHu_user_list().size() == 0) {
                    roomBean.getHu_user_list().add(user);
                    user.setHu_state(0);
                } else if (roomBean.getHu_user_list().size() >= 1) {
                    int hu_id = roomBean.getNextUserId3();
                    for (UserBean userBean :
                            roomBean.getHu_user_list()) {
                        if (userBean.getUserid() != hu_id) {
                            roomBean.getHu_user_list().remove(userBean);
                        }
                    }
                }
            }
        }
        roomBean.getLock().unlock();
        return roomBean.getHucount();
    }

    /**
     * 检测是否有人可以胡牌
     *
     * @param roomBean
     * @param
     * @return
     */
    public int Is_Hunum(RoomBean roomBean) {
        roomBean.getLock().lock();
        roomBean.setHunum(roomBean.getHunum() + 1);
        roomBean.getLock().unlock();
        return roomBean.getHunum();
    }

    /**
     * 胡检测(点炮)
     *
     * @param userBean
     * @param brand
     * @param roomBean
     * @param userid
     * @return
     */
    public int End_Hu(UserBean userBean, int brand, RoomBean roomBean, int userid) {
        //将点炮牌放入玩家手里
        //userBean.getBrands().add(brand);
        // 检测胡牌人数(不包含点炮人自己)
        roomBean.IS_HU(userBean, brand, roomBean, userid);
        //userBean.Remove_Brands(brand);
        //返回胡牌状态
        //return userBean.getHu_type();
        return 0;
    }

    /**
     * 胡检测(自摸)
     *
     * @param userBean
     * @param roomBean
     * @return
     */

    public int End_Hu_This(UserBean userBean, RoomBean roomBean, int brand) {
        return Mahjong_Util.mahjong_Util.IS_Victory(userBean.getBrands(), userBean, brand);
    }

    /**
     * 结算(点炮)1
     *
     * @param userBean
     * @param roomBean
     * @param p_userid
     * @param state
     * @return
     */
    public int End_Game(UserBean userBean, RoomBean roomBean, int p_userid, int state) {
        roomBean.getLock().lock();
        // 设置自己已经同意结算
        userBean.setHu_state(state);
        if (roomBean.getState() == 4) {
            roomBean.getLock().unlock();
            return 500;// 已经结算
        }
        if (roomBean.getState() == 3) {
            roomBean.getLock().unlock();
            return 502;// 等待结算
        }
        // 胡牌人数大于1的时候需要检测
        if (roomBean.getHu_user_list().size() > 1) {
            for (int i = 0; i < roomBean.getHu_user_list().size(); i++) {
                //当前用户的胡牌状态
                if (roomBean.getHu_user_list().get(i).getHu_state() == 0) {
                    roomBean.getLock().unlock();
                    return 501;// 等待胡牌
                } else if (roomBean.getHu_user_list().get(i).getHu_state() == 2) {
                    roomBean.getHu_user_list().remove(roomBean.getHu_user_list().get(i));
                    i--;
                }
            }
        } else {
            if (state == 2) {
                roomBean.getLock().unlock();
                return 503;//弃
            }
        }
        //弃胡
        if (roomBean.getHu_user_list().size() == 0) {
            return 503;
        }
        // 结算用户分数及总数
        int sum_num = 0;
        // 正在结算
        //roomBean.setState(3);
        UserBean loser = roomBean.getUserBean(p_userid);//失败者
        for (UserBean user : roomBean.getHu_user_list()) {
            // 计算分数=房间底分*用户番数
            System.out.println("玩家" + user.getNickname() + "的番数为" + user.getPower_number());
            int fufen = 0;
            if (loser.getZha() != 0) {
                fufen += roomBean.getFen();
            }
            if (loser.getFu() != 0) {
                fufen += loser.getFu();
            }
            int fen = user.getPower_number() + fufen;
            user.setNumber(user.getNumber() + fen);
            user.setDqnumber(fen + user.getDqnumber());
            sum_num += fen;
        }
        // 扣除失败者分数
        loser.setNumber(loser.getNumber() - sum_num);
        loser.setDqnumber(-sum_num + loser.getDqnumber());
        roomBean.getLock().unlock();
        //更新数据库分数

        return EndGame(roomBean, userBean);
    }

    /**
     * 结算(自摸)
     *
     * @param userBean
     * @param roomBean
     * @return
     */
    public int End_Game_This(UserBean userBean, RoomBean roomBean) {
        // 计算分数=房间底分*用户番数
        int fen = userBean.getPower_number();
        for (UserBean user : roomBean.getGame_userlist()) {
            if (user.getUserid() != userBean.getUserid()) {
                int fufen = 0;
                if (user.getZha() != 0) {
                    fufen += roomBean.getFen();
                }
                if (user.getFu() != 0) {
                    fufen += user.getFu();
                }
                userBean.setNumber(fen + userBean.getNumber() +fufen);
                userBean.setDqnumber(fen + userBean.getDqnumber() + fufen);

                // 扣除失败者分数
                user.setNumber(user.getNumber() - fen - fufen);
                user.setDqnumber(-fen + user.getDqnumber() - fufen);
            }
        }
        return EndGame(roomBean, userBean);
    }

    /**
     * 扣除开房房卡
     *
     * @param roomBean
     * @return
     */
    private int EndGame(RoomBean roomBean, UserBean userBean) {
        int state = 0;
        // 判断是什么模式0普通 其他俱乐部
        if (roomBean.getClubid() == 0) {
            // 判断当前第一局则扣除金币
            if (roomBean.getGame_number() == 1) {
                if (roomBean.getPaytype() == 1) {
                    //AA
                    for (UserBean user : roomBean.getGame_userlist()) {
                        // 扣除用户金币
                        gameDao.UpdateUserMoney(user.getUserid(),
                                roomBean.getRoom_card() / roomBean.getGame_userlist().size(), 0);
                        user.setMoney(user.getMoney() - roomBean.getRoom_card() / roomBean.getGame_userlist().size());
                    }
                } else {
                    // 扣除房主金币
                    gameDao.UpdateUserMoney(roomBean.getHouseid(), roomBean.getRoom_card(), 0);
                    UserBean userb = roomBean.getUserBean(roomBean.getHouseid());
                    userb.setMoney(userb.getMoney() - roomBean.getRoom_card() / roomBean.getGame_userlist().size());
                }
            }
        } else {
            // 俱乐部模式扣除俱乐部房卡
            if (roomBean.getGame_number() == 1) {
                clubDao.Update_Club_Money(roomBean.getClubid(), roomBean.getRoom_card(), 0);
            }
        }
        //roomBean.setState(4);
        return state;
    }

    public void addRecord(RoomBean roomBean) {
        // 记录战绩
        gameDao.addPK_Record(roomBean);
    }

    /**
     * 开始游戏
     *
     * @param roomBean
     * @return
     */
    public int StartGame(RoomBean roomBean) {
        // 初始化
        roomBean.Initialization();
        // 房间状态
        roomBean.setState(2);
        // 不流局才选庄
        if (roomBean.getFlow() == 0) {
            // 选庄
            roomBean.Select_Banker();
        }
        // 发牌
        roomBean.Deal();
        return 0;
    }

    /**
     * 出牌
     *
     * @param roomBean
     * @param outbrand
     * @param userBean
     * @return
     */
    public int OutBrand(RoomBean roomBean, int outbrand, UserBean userBean, Map<String, Object> returnMap,
                        ArrayList hulist) {
        Map<String, Object> newReturnMap = new HashMap<>();
        if (roomBean.getRoomParams().get("p3") == 0 && hulist.size() > 1) {//没有一炮多响
            Integer hu_id = roomBean.getNextUserId3();
            for (int i = hulist.size() - 1; i >= 0; i--) {
                if (hulist.get(i) != hu_id) {
                    hulist.remove(i);
                }
            }
        }

        if (hulist.size() != 0) {//证明有人胡  判断胡的人能不能碰  不能碰直接推胡 弃胡之后在检测有没有人能碰  如果胡的人数大于一 不检测碰
            if (hulist.size() <= 1) {
                UserBean user = roomBean.getUserBean((int) hulist.get(0));
                newReturnMap = new HashMap<>();
                // 检测碰/杠
                int[] user_bump = Mahjong_Util.mahjong_Util.IS_Bump(
                        user.getBrands(), outbrand);
                if (user_bump != null) {
                    Map<String, Object> userMap2 = new HashMap<String, Object>();
                    // 可碰杠人的id
                    userMap2.put("userid", user.getUserid());
                    userMap2.put("bump", user_bump);
                    newReturnMap.put("bump", userMap2);
                    returnMap.putAll(newReturnMap);
                } else {
                    roomBean.getNextMap().clear();
                    returnMap.putAll(newReturnMap);
                }
            }
        } else {
            // 计算是否有人可碰
            for (UserBean user : roomBean.getGame_userlist()) {
                // 是自己则跳过
                if (user.getUserid() == userBean.getUserid())
                    continue;
                Map<String, Object> userMap = new HashMap<String, Object>();
                newReturnMap = new HashMap<>();
                // 检测碰/杠
                int[] user_bump = Mahjong_Util.mahjong_Util.IS_Bump(
                        user.getBrands(), outbrand);
                if (user_bump != null) {
                    Map<String, Object> userMap2 = new HashMap<String, Object>();
                    // 可碰杠人的id
                    userMap2.put("userid", user.getUserid());
                    userMap2.put("bump", user_bump);
                    newReturnMap.put("bump", userMap2);
                    returnMap.putAll(newReturnMap);
                    break;
                } else {
                    roomBean.getNextMap().clear();
                    returnMap.putAll(newReturnMap);
                }
            }
        }
        // 出的牌
        returnMap.put("outbrand", outbrand);
        // 出牌人id
        returnMap.put("out_userid", userBean.getUserid());
        // 出牌
        OutBrand(userBean, outbrand);
        if (returnMap.get("bump") == null) {
            // 需要摸牌
            return 300;
        }
        // 有碰
        return 0;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:
     * @ Params:弃胡检测
     * @ Description:
     * @ Return:
     * @ Date:2020/4/27
     */
    public int OutBrand2(RoomBean roomBean, int outbrand, UserBean userBean, Map<String, Object> returnMap) {
        Map<String, Object> newReturnMap = new HashMap<>();
        // 计算是否有人可碰
        for (UserBean user : roomBean.getGame_userlist()) {
            // 是自己则跳过
            if (user.getUserid() == userBean.getUserid())
                continue;
            Map<String, Object> userMap = new HashMap<String, Object>();
            newReturnMap = new HashMap<>();
            // 检测碰/杠
            int[] user_bump = Mahjong_Util.mahjong_Util.IS_Bump(
                    user.getBrands(), outbrand);
            if (user_bump != null) {
                Map<String, Object> userMap2 = new HashMap<String, Object>();
                // 可碰杠人的id
                userMap2.put("userid", user.getUserid());
                userMap2.put("bump", user_bump);
                newReturnMap.put("bump", userMap2);
                returnMap.putAll(newReturnMap);
                break;
            } else {
                roomBean.getNextMap().clear();
                returnMap.putAll(newReturnMap);
            }
        }
        // 出的牌
        returnMap.put("outbrand", outbrand);
        // 出牌人id
        returnMap.put("out_userid", userBean.getUserid());

        // 出牌
        OutBrand(userBean, outbrand);
        if (returnMap.get("bump") == null) {
            // 需要摸牌
            return 300;
        }
        // 有碰
        return 0;
    }

    /**
     * 碰牌
     *
     * @param userBean
     * @param userid
     * @param brand
     * @param roomBean
     * @return
     */
    public int[] Bump_Brand(UserBean userBean, int userid, int brand, RoomBean roomBean) {
        int[] brands = new int[2];
        // 获取对方用户
        UserBean user = roomBean.getUserBean(userid);
        // 删除出牌用户的牌
        user.Remove_Brands_Out(brand);
        // 将牌放入碰牌用户
        userBean.getBump_brands().add(brand);
        // 删除碰牌用户手里
        brand = mahjong_Util.getBrand_Value(brand);
        int count = 0;
        for (int i = 0; i < userBean.getBrands().size(); i++) {
            if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
                userBean.getBump_brands().add(userBean.getBrands().get(i));
                brands[count] = userBean.getBrands().get(i);
                userBean.Remove_Brands(userBean.getBrands().get(i));
                i--;
                count++;
                if (count == 2) {
                    break;
                }
            }
        }
        // 设置当前操作用户为自己
        roomBean.setEnd_userid(userBean.getUserid());
        return brands;
    }

    /**
     * 明杠（手中3张）
     *
     * @param userBean
     * @param userid
     * @param brand
     * @param roomBean
     * @return
     */
    public int Show_Bar(UserBean userBean, int userid, int brand, RoomBean roomBean) {
        // 获取出牌用户
        UserBean user = roomBean.getUserBean(userid);
        // 删除出牌用户出得牌
        user.Remove_Brands(brand);
        // 将牌放入自己明杠牌集合
        userBean.getShow_brands().add(brand);
        // 删除碰牌用户手里
        brand = mahjong_Util.getBrand_Value(brand);
        for (int i = 0; i < userBean.getBrands().size(); i++) {
            if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
                // 将牌放入自己明杠牌集合
                userBean.getShow_brands().add(userBean.getBrands().get(i));
                // 删除手中得牌
                userBean.Remove_Brands(userBean.getBrands().get(i));
                i--;
            }
        }
        // 设置当前操作用户为自己
        roomBean.setEnd_userid(userBean.getUserid());
        return 0;
    }

    /**
     * 补杠（碰3张）
     *
     * @param userBean
     * @param brand
     * @param roomBean
     * @return
     */
    public int Repair_Bar_Bump(UserBean userBean, int brand, RoomBean roomBean) {
        brand = mahjong_Util.getBrand_Value(brand);
        for (int i = 0; i < userBean.getBrands().size(); i++) {
            if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
                // 将牌放入自己得明杠牌集合
                userBean.getShow_brands().add(userBean.getBrands().get(i));
                // 删除用户手里得牌
                userBean.Remove_Brands(userBean.getBrands().get(i));
                i--;
            }
        }
        // 删除碰的牌
        for (int i = 0; i < userBean.getBump_brands().size(); i++) {
            if (brand == mahjong_Util.getBrand_Value(userBean.getBump_brands().get(i))) {
                // 将牌放入自己得明杠牌集合
                userBean.getShow_brands().add(userBean.getBump_brands().get(i));
                // 删除用户手里碰得牌
                userBean.Remove_Brands_Bump(userBean.getBump_brands().get(i));
                i--;
            }
        }
        // 设置当前操作用户为自己
        roomBean.setEnd_userid(userBean.getUserid());
        return 0;
    }

    /**
     * 杠牌（暗）（手中4张牌）
     *
     * @param userBean
     * @param brand
     * @param roomBean
     * @return
     */
    public int[] Hide_Bar(UserBean userBean, int brand, RoomBean roomBean) {
        int[] brands = new int[4];
        int count = 0;
        brand = mahjong_Util.getBrand_Value(brand);
        for (int i = 0; i < userBean.getBrands().size(); i++) {
            if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
                brands[count] = userBean.getBrands().get(i);
                count++;
                // 将牌放入自己得暗杠牌集合
                userBean.getHide_brands().add(userBean.getBrands().get(i));
                // 删除用户手里得牌
                userBean.Remove_Brands(userBean.getBrands().get(i));
                i--;
            }
        }
        return brands;
    }

    /**
     * 出牌
     *
     * @param userBean
     * @param index
     */
    public void OutBrand(UserBean userBean, int index) {
        if (!userBean.getOut_brands().contains(index)) {
            userBean.getOut_brands().add(index);
        }
        if (userBean.getBrands().contains(index)) {
            userBean.Remove_Brands(index);
        }
    }

    /**
     * 检测是否重复加入
     *
     * @param userBean
     * @return
     */
    public boolean ISMatching(String roomno, UserBean userBean) {
        RoomBean roomBean = Public_State.PKMap.get(roomno);
        for (UserBean user : roomBean.getGame_userlist()) {
            if (user.getUserid() == userBean.getUserid()) {
                return true;
            }
        }
        return false;
    }

    /***
     * 发起/同意/解散房间
     *
     * @param userBean
     * @param roomBean
     * @return
     */
    public int Exit_GameUser(UserBean userBean, RoomBean roomBean) {
        roomBean.getLock().lock();
        // 不同意
        if (userBean.getExit_game() == 2) {
            roomBean.setExit_game(0);
            for (UserBean user : roomBean.getGame_userlist()) {
                user.setExit_game(0);
            }
            roomBean.getLock().unlock();
            // 取消解散
            return 303;
        } else {
            // 第一次发起
            if (roomBean.getExit_game() == 0) {
                roomBean.setExit_game(1);
                //開啟解散倒計時
                //roomBean.getExit_time().start();
                roomBean.getLock().unlock();
                // 发起解散
                return 301;
            }
            // 检测是否都同意解散
            for (UserBean user : roomBean.getGame_userlist()) {
                // 只要有一个用户未操作都返回同意
                if (user.getExit_game() == 0) {
                    // user.setExit_game(1);
                    roomBean.getLock().unlock();
                    return 302;
                }
            }
            roomBean.getLock().unlock();
            roomBean.setExit_game(0);
            return 304;
        }
    }

    /**
     * 拆分缺牌
     *
     * @param brandsList 用户手里的牌
     * @param list       缺牌
     * @return
     */
    private List<Integer> lackBrands(List<Integer> brandsList, List<Integer> list) {
        List<Integer> cardslist = new ArrayList<Integer>();
        for (int j = 0; j < brandsList.size(); j++) {
            for (Integer i : list) {
                if (brandsList.get(j) == i) {
                    cardslist.add(brandsList.get(j));
                }
            }
        }
        // 删除缺牌
        brandsList.removeAll(cardslist);
        // 缺牌非缺拆分完毕 重新放进玩家手中
        for (Integer i : cardslist) {
            brandsList.add(i);
        }
        return cardslist;
    }

    //添加房间记录
    public void addPkRoom(RoomBean roomBean) {
        gameDao.add_PK_Room(roomBean);
    }

    /**
     * 是否海底炮
     *
     * @param roomBean
     * @param p_userid
     */
    public void isHaidipao(RoomBean roomBean, int p_userid) {
        List<UserBean> game_userlist = roomBean.getGame_userlist();
        for (UserBean userBean : game_userlist) {
            if (userBean.getUserid() == p_userid) {
                userBean.setPower(5);
                userBean.getRecordMsgList().add("海底炮");
            }
        }
    }
}
