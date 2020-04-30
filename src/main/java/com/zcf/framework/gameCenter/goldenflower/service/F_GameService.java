package com.zcf.framework.gameCenter.goldenflower.service;

import java.util.List;


import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import com.zcf.framework.gameCenter.goldenflower.comm.Establish_PK;
import com.zcf.framework.gameCenter.goldenflower.comm.Matching_PK;
import com.zcf.framework.gameCenter.goldenflower.dao.F_GameDao;
import util.BaseDao;
import util.UtilClass;

public class F_GameService {
    private F_GameDao gameDao;

    public F_GameService(BaseDao baseDao) {
        this.gameDao = new F_GameDao(baseDao);
    }

    /**
     * 匹配(如果匹配不到则创建一个返回)
     *
     * @param userbean
     * @return
     */
    public F_PKBean Matching(UserBean userbean, int room_type) {
        //判断准入金币是否合格
        int room_money = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties",
                "room_money" + room_type).split("-")[1]);
        //用户金币
        int user_money = gameDao.getUserMoney(userbean.getUserid());
        if (room_money > user_money) {
            return null;
        }
        F_PKBean F_PKBean;
        int while_count = 5;
        //所有房间最多重新匹配5次(预计时间2~5秒)
        while (true) {
            if (while_count == 0) {
                break;
            }
            //遍历所有房间查看是否有符合的房间
            for (String key : F_PublicState.PKMap.keySet()) {
                F_PKBean = F_PublicState.PKMap.get(key);
                //房间类型不一样则不匹配
                if (F_PKBean.getRoom_type() != room_type) {
                    continue;
                }
                List<UserBean> userList = F_PKBean.getGame_userList(0);
                //初次验证房间人数未满
                if (userList.size() < 6) {
                    //检测房间内没有自己
                    boolean userbool = false;
                    for (UserBean user : userList) {
                        if (user.getUserid() == userbean.getUserid()) {
                            userbool = true;
                            break;
                        }
                    }
                    if (userbool) {
                        continue;
                    }
                    //将自己加入进房间
                    F_PKBean = Matching_PK.Matching(userbean, key);
                    //代表没加入进去(并发加入导致)
                    if (F_PKBean == null) {
                        //跳过此次房间继续匹配
                        continue;
                    } else {
                        //返回加入的房间信息
                        return F_PKBean;
                    }
                }
            }
            while_count--;
            //延迟500毫秒继续下一次匹配
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //创建一个新房间并返回
        return Establish_PK.Establish(userbean, room_type,0);
    }

    public Integer ISMatching(UserBean userBean, String roomno) {
        F_PKBean pkBean = F_PublicState.PKMap.get(roomno);
        if (pkBean == null) {
            return 100;// 房间不存在
        }
        //判断准入金币是否合格
        int room_money = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties",
                "room_money" + pkBean.getRoom_type()));
        //用户金币
        int user_money = gameDao.getUserMoney(userBean.getUserid());
        if (room_money > user_money) {
            return 103;
        }
        F_PKBean roomBean = F_PublicState.PKMap.get(roomno);
        for (UserBean user : roomBean.getGame_userList(0)) {
            if (user.getUserid() == userBean.getUserid()) {
                return 144;
            }
        }
        return 0;
    }

    public F_PKBean ISMatching_Money(UserBean userBean, String roomno) {
        F_PKBean roomBean = F_PublicState.PKMap.get(roomno);
        roomBean.getReady_lock().lock();
        // 初始化用户
        userBean.Initialization();
        userBean.setUser_type(0);
        roomBean.getGame_userList(1).add(userBean);
        // 加入用户位置
        //roomBean.setUser_positions(userBean);
        roomBean.getReady_lock().unlock();
        return roomBean;
    }


    /**
     * 坐下座位 @param userBean2 @param rb2 @throws
     */
    public int Sit_down(UserBean userBean, F_PKBean rb, int position) {
        if (rb.getUser_positions()[position] == -1) {
            // 如果座位未满 则按顺序添加用户到座位
            rb.getUser_positions()[position] = userBean.getUserid();
            userBean.setUser_type(1);
            return 0;
        }
        return -1; // 座位已有人
    }

    /**
     * 下注
     *
     * @param bets
     * @param userBean
     * @return
     */
    public int bets(int betstype, int bets, UserBean userBean, F_PKBean pkBean) {
        //检测玩家金币
        if (!userBean.ISMoney(bets)) return 809;//金币不足
        //扣除下注
        String state = gameDao.UpdateUserMoney(userBean.getUserid(), bets, 0);
        if (state.equals("success")) {
            //下注
            pkBean.DeductionBottom_Pan(userBean, bets, betstype);
        } else {
            //异常
            return 999;
        }
        //操作状态
        userBean.setBrandstatus(1);
        //判断是否符合开牌
        return NewGame(userBean, pkBean);
    }

    /**
     * 判断是否进入新一轮或系统自动开牌
     *
     * @param userBean
     * @param F_PKBean
     * @return
     */
    public int NewGame(UserBean userBean, F_PKBean pkBean) {
        //判断所有用户都已经操作
        int brandstatus = 0;//假设所有人都已经下注
        for (UserBean user : pkBean.getGame_userList(2)) {
            //0是未操作状态1下注
            if (user.getBrandstatus() == 0) {
                brandstatus++;//代表不可结算
            }
        }
        //判断当前是否为最后一轮且所有人都已经下注1
        if (pkBean.getGame_number() == pkBean.getMax_number() && brandstatus == 0) {
            //系统开牌结算
            return OpenBrand(pkBean);
        } else if (pkBean.getGame_userList(2).size() == 1) {
            //直接胜利
            int state = EndGame(pkBean.getGame_userList(2).get(0).getUserid(), pkBean);
            return state == 0 ? 202 : state;
        } else if (brandstatus == 0) {
            //增加轮数
            pkBean.setGame_number(pkBean.getGame_number() + 1);
            for (UserBean user : pkBean.getGame_userList(2)) {
                user.setBrandstatus(0);
            }
        }
        return -1;
    }

    /**
     * 开牌比点
     *
     * @param F_PKBean
     * @return
     */
    public int OpenBrand(F_PKBean pkBean) {
        //豹子牌型数量
        int count_baozi = 0;
        //特殊牌型数量
        int count_235 = 0;
        //其他牌型数量
        int count = 0;
        //用户牌值记录
        List<UserBean> userlist = pkBean.getGame_userList(2);
        Object[] brands = new Object[userlist.size()];
        //获取所有用户得牌值
        for (int i = 0; i < userlist.size(); i++) {
            int user_brand = userlist.get(i).BrandCount();
            userlist.get(i).setUser_brand_type(user_brand);
            //特殊牌型235
            if (user_brand == 999) count_235++;
            //豹子
            if (user_brand < 999 && user_brand > 899) count_baozi++;
            //其他牌型
            if (user_brand < 900) count++;
            //记录用户id和牌型值
            brands[i] = new int[]{userlist.get(i).getUserid(), user_brand};
        }
        //和局判断如果235和豹子和其他牌型只有1个得情况下和局
        if (count_baozi == 1 && count_235 == 1 && count == 1) {
            //和局逻辑
            int bet_count = pkBean.getBets();
            for (int i = 0; i < brands.length; i++) {
                int[] brnad_user = (int[]) brands[i];
                String state = gameDao.UpdateUserMoney(brnad_user[0], bet_count / brands.length, 1);
                if (!state.equals("success")) {
                    return 999;
                }
            }
            return 201;//和局
        } else {
            //取冒泡排序区牌值最大
            for (int i = 0; i < brands.length; i++) {
                int[] brnad_user = (int[]) brands[i];
                for (int j = i; j < brands.length; j++) {
                    if ((j + 1) < brands.length) {
                        int[] brand = (int[]) brands[j + 1];
                        if (brnad_user[1] < brand[1]) {
                            brands[i] = brands[j + 1];
                            brands[j + 1] = brnad_user;
                        }
                    }
                }
            }
            //将所有底注奖励给最大值玩家
            return EndGame(((int[]) brands[0])[0], pkBean);
        }
    }

    /**
     * 结算
     *
     * @param
     * @param userid
     * @return
     */
    public int EndGame(int userid, F_PKBean pkBean) {
        pkBean.setVictoryid(userid);
        //将锅底及自己的注数全部奖励给总分最大的玩家
        String state = gameDao.UpdateUserMoney(userid, pkBean.getBottom_pan(), 1);
        if (state.equals("success")) {
            //更新用户金币
            for (UserBean userBean : pkBean.getGame_userList(0)) {
                if (userid == userBean.getUserid()) {
                    userBean.setMoney(userBean.getMoney() + pkBean.getBottom_pan());
                }
                //userBean.setBets(0);
            }
            //锅底清空
            pkBean.setBottom_pan(0);
            //更改房间状态
            pkBean.setState(0);
            return 0;
        }
        return 999;//异常
    }

    /**
     * 比牌
     *
     * @param
     * @param userBean
     * @return
     */
    public int PKUser(int pkuserid, UserBean userBean, F_PKBean pkBean,Integer pktype) {
        /*if(pktype==0){
            //检测玩家金币
            if (!userBean.ISMoney(pkBean.getBets())) return 809;//金币不足
        }*/
        //获取对方用户
        UserBean pkuser = pkBean.getUserBean(pkuserid);

        //如果自己的牌型大于对方则胜利否则失败
        int usercount = userBean.BrandCount();
        int pkusercount = pkuser.BrandCount();
        int userid = userBean.getUserid();
        //自己是235牌型对方不是豹子则输
        if (usercount == 999 && pkusercount > 800 && pkusercount < 999) {
            //userid = pkuser.getUserid();
            userBean.setGametype(-1);
        } else if (usercount <= pkusercount) {//小于等于都输
            //userid = pkuser.getUserid();
            userBean.setGametype(-1);
        } else if (usercount > pkusercount) {//只有大于才赢
            pkuser.setGametype(-1);
        }
        int money = userBean.getMoney();
        if(pktype==0){//正常扣注
            money = pkBean.getBets() * 2;
        }
        //扣除下注
        String state = gameDao.UpdateUserMoney(userid, money, 0);
        if ("success".equals(state)) {
            //下注
            pkBean.DeductionBottom_Pan(userBean, money, 0);
            return 0;//比牌正常结束
        }else
            return 999;
    }

    /**
     * 弃牌
     *
     * @param userBean
     * @param F_PKBean
     * @return
     */
    public int Giveup(UserBean userBean, F_PKBean pkBean) {
        //操作状态
        userBean.setBrandstatus(2);

        return NewGame(userBean, pkBean);
    }

}
