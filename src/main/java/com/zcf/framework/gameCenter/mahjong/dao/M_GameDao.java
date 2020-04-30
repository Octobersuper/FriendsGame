package com.zcf.framework.gameCenter.mahjong.dao;

import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.Golden_room;
import com.zcf.framework.gameCenter.goldenflower.bean.Player;
import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import util.AES;
import util.BaseDao;
import util.UtilClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台接口对接管理
 *
 * @author Administrator
 */
public class M_GameDao {
    private BaseDao baseDao;

    public M_GameDao(BaseDao baseDao) {
        this.baseDao = baseDao;
    }

    /**
     * 扣除用户金币
     *
     * @param userid
     * @param money
     * @return
     */
    public String UpdateUserMoney(int userid, int money, int type) {
        int money1 = getUserMoney(userid);
        if(money1-money<=0){
            money = money1;
        }
        String sql = "update user_table set money=money" + (type == 0 ? '-' : '+') + "? where userid=?";
        return baseDao.executeUpdate(sql, new Object[]{money, userid});
    }

    /**
     * 插入积分变更记录
     *
     */
    public void insertmoney(int money, int userid,int type) {
        String sql = "insert into game_recharge_money(userid,money,date,type) values(?,?,NOW(),?) ";
        baseDao.executeUpdate(sql, new Object[] {userid, money,type });
    }

    /**
     * 扣除用户钻石
     *
     * @param userid
     * @param diamond
     * @return
     */
    public String UpdateUserDiamond(int userid, int diamond, int type) {
        String sql = "update user_table set diamond=diamond" + (type == 0 ? '-' : '+') + "? where userid=? and diamond >= 0";
        return baseDao.executeUpdate(sql, new Object[]{diamond, userid});
    }

    /**
     * 查询用户金币
     *
     * @param userid
     * @return
     */
    public int getUserMoney(int userid) {
        String sql = "select money from user_table where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询用户钻石
     *
     * @param userid
     * @return
     */
    public int getUserDiamond(int userid) {
        String sql = "select diamond from user_table where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("diamond");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查看游戏规则
     *
     * @return
     */
    public Object ckgui(String type) {
        String sql = "SELECT * FROM game_introduce where introduceid = ?";
        String executeAll = baseDao.executeAll(sql, new Object[]{type});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                return UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getgui");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看公告
     *
     * @return
     */
    public Object ckgong() {
        String sql = "SELECT * FROM game_notice";
        String executeAll = baseDao.executeAll(sql, new Object[]{});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                return UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getnotice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看客服信息
     *
     * @return
     */
    public Object ckservice() {
        String sql = "SELECT * FROM service";
        String executeAll = baseDao.executeAll(sql, new Object[]{});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> sql_getservice = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getservice");
                return sql_getservice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 绑定邀请码
     *
     * @param userid
     * @param code
     * @return
     */
    public String bindinguser(int userid, int code) {
        // 首先判断 邀请码是否存在 或者是否填自己
        boolean b = selectcode(userid, code);
        if (!b) {
            return "101";// 邀请码不存在
        }
        boolean c = selfcode(userid, code);
        if (!c) {
            return "102";// 不可填自己
        }
        // 首先根据userid判断pid是否为空
        String sql = "SELECT * from  user_table where  userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                int pid = baseDao.resultSet.getInt("pid");
                if (pid == 0) {
                    int userids = getupuser(code);
                    Object success = bduser(userid, userids);
                    if ("success".equals(success)) {
                        return "0";// 绑定成功
                    }
                } else {
                    return "103";// 请勿重复绑定
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    /**
     * code是否存在
     *
     * @param userid
     * @param code
     * @return
     */
    private boolean selectcode(int userid, int code) {
        // code是否存在
        String sql = "select * from user_table where code=?";
        baseDao.executeAll(sql, new Object[]{code});
        try {
            return baseDao.resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否是自己
     *
     * @param userid
     * @param code
     */
    private boolean selfcode(int userid, int code) {
        String sql = "select * from user_table where userid=? and code=?";
        baseDao.executeAll(sql, new Object[]{userid, code});
        try {
            if (baseDao.resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 查看用户邀请码
     *
     * @param code
     * @return
     */
    private int getupuser(int code) {
        String sql = "SELECT * from  user_table where code=?";
        baseDao.executeAll(sql, new Object[]{code});
        try {
            if (baseDao.resultSet.next()) {
                int userids = baseDao.resultSet.getInt("userid");
                return userids;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 绑定上级推荐用户
     *
     * @param userid
     * @param userids
     * @return
     */
    private Object bduser(int userid, int userids) {
        int money = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "award_money"));
        String sql = "update  user_table SET pid=?,money=money+? where userid=?";
        return baseDao.executeUpdate(sql, new Object[]{userids, money, userid});
    }

    /**
     * 查看个人信息
     *
     * @param userid
     * @return
     */
    public Object getuser(int userid) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String sql = "select * from user_table where userid=?";
        String executeAll = baseDao.executeAll(sql, new Object[]{userid});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_getUserList");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 个人充值记录
     *
     * @param userid
     * @return
     */
    public Object getrecord(int userid) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String sql = "select * from game_record where uid=?";
        String executeAll = baseDao.executeAll(sql, new Object[]{userid});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_getrecord");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    public void getConfig() {
        String sql = "select * from config_table limit 0,1";
        baseDao.executeAll(sql, null);
        try {
            if (baseDao.resultSet.next()) {
                Public_State.exit_time = baseDao.resultSet.getInt("exit_time");
                Public_State.establish_two = baseDao.resultSet.getString("establish_two").split("-");
                Public_State.establish_four = baseDao.resultSet.getString("establish_four").split("-");
                Public_State.diamond = baseDao.resultSet.getInt("card_diamond");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询用户金额
     *
     * @return
     */
    public int ckmoney(int userid) {
        String sql = "SELECT * FROM user_table where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 查看俱乐部
     *
     * @param userid
     * @return
     */
    public Object getClub(int userid) {
        String sql = "SELECT gcc.circleid,gcc.circlename,gcc.circlenumber,gcc.date,gcc.diamond,gcc.userid from game_card_user AS gcu INNER JOIN game_card_circle AS gcc ON gcc.circlenumber=gcu.circlenumber WHERE gcu.userid=?";
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{userid});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getClub");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加到中间表
     *
     * @param userid
     */
    private void insertcircleuser(int userid, int circlenumber) {
        String sql = "INSERT INTO game_card_user(userid,circlenumber,date) value(?,?,NOW())";
        baseDao.executeUpdate(sql, new Object[]{userid, circlenumber});
    }

    /**
     * 创建牌友圈
     *
     * @param userid
     * @return
     */
    public String createClub(String circlename, int userid ,int type) {
        int userDiamond = getUserDiamond(userid);
        if(userDiamond<80){
            return "333";//房卡不足
        }
        UpdateUserDiamond(userid,80,0);//扣除用户房卡

        String random = UtilClass.utilClass.getRandom(6);
        if(random.length()<6){
            random = UtilClass.utilClass.getRandom(6);
        }
        int randoming = Integer.parseInt(random);
        String sql = "INSERT INTO game_card_circle(circlename,circlenumber,date,userid,type) VALUE(?,?," +
                "NOW(),?,?)";
        String str = baseDao.executeUpdate(sql, new Object[]{circlename,randoming, userid,type});
        if (str.equals("success")) {
            insertcircleuser(userid, randoming);
            return "0";
        }
        return "-1";
    }

    /**
     * 查看牌友圈
     *
     * @param userid
     * @return
     */
    public Object ckclub(int userid,int type) {
        String sql = "SELECT u.nickname,u.avatarurl,gcc.circlename,gcc.circlenumber,gcc.date,gcc.userid,gcu.id,\n" +
                "gcc.type,(SELECT count(*) c from game_card_user where circlenumber = gcu.circlenumber) as num,gcu" +
                ".istop\n" +
                "from game_card_user AS gcu\n" +
                "LEFT JOIN game_card_circle AS gcc\n" +
                "ON gcc.circlenumber=gcu.circlenumber\n" +
                "LEFT JOIN user_table as u\n" +
                "ON gcc.userid = u.userid\n" +
                "WHERE gcu.userid=? and gcc.type = ? order by gcu.istop desc";
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{userid,type});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getClub");
                if(type==1){//扎金花
                    int onlineNum = F_PublicState.selectOnlineNum(map.get("circlenumber"));
                    int gameing = F_PublicState.getGameing(map.get("circlenumber"),1);
                    int waiting = F_PublicState.getGameing(map.get("circlenumber"),2);
                    map.put("onlineNum",onlineNum);
                    map.put("gameing",gameing);
                    map.put("waiting",waiting);
                }else{//麻将
                    int onlineNum = Public_State.getAlinenum2(Integer.valueOf(String.valueOf(map.get("circlenumber"))));
                    int gameing = Public_State.getGameing(map.get("circlenumber"),1);
                    int waiting = Public_State.getGameing(map.get("circlenumber"),2);
                    map.put("onlineNum",onlineNum);
                    map.put("gameing",gameing);
                    map.put("waiting",waiting);
                }
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 成功扣除用戶金幣
     *
     * @param userid
     */
    private void downUserMoney(int userid) {
        String sql = "update user_table set diamond=diamond-188 where userid=?";
        baseDao.executeUpdate(sql, new Object[]{userid});
    }

    /**
     * 查询用户钻石
     *
     * @param userid
     * @return boolean
     */
    public int selectUserFangka(int userid) {
        String sql = "SELECT * FROM user_table where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("diamond");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 用户创建俱乐部次数zz
    public int selectClub(int userid) {
        String sql = "SELECT count(*) as counts from game_card_circle where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 俱乐部名称是否重复zz
    public int selectClubName(String clubName) {
        String sql = "SELECT count(circlename) as counts FROM game_card_circle where circlename=?";
        baseDao.executeAll(sql, new Object[]{clubName});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 生成俱乐部编号是否重复zz
    public boolean selectClubNO(int id) {
        String sql = "SELECT * FROM game_card_circle where circlenumber=?";
        baseDao.executeAll(sql, new Object[]{id});
        try {
            return baseDao.resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加俱乐部金币
     *
     * @param circlenumber
     * @param userid
     * @param diamond
     * @return
     */
    public String addClub_Money(String circlenumber, int userid, int diamond) {
        //获取俱乐部圈主
        int userId=getClubUser(circlenumber);
        if (userId!=userid){
            return "555";
        }

        int userDiamond = getUserDiamond(userid);
        if (userDiamond < diamond) {
            return "222";//钻石不足
        }
        //减少用户钻石
        UpdateUserDiamond(userid, diamond, 0);
        //增加 俱乐部钻石
        String sql = "update game_card_circle set diamond=diamond+? where circlenumber=?";
        String str = baseDao.executeUpdate(sql, new Object[]{diamond, circlenumber});
        if (str.equals("success")) {
            return "333";
        }
        return "444";
    }

    /**
     * 获取圈主id
     * @param circlenumber
     * @return
     */
    private int getClubUser(String circlenumber) {
        String sql = "select * from game_card_circle where circlenumber=?";
        baseDao.executeAll(sql, new Object[]{ circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("userid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 加入牌友圈
     *
     * @param userid
     * @return
     */
    public String joinCircleid(int userid, int circlenumber) {
        // 判断此牌友圈是否存在
        if (selectCardId(circlenumber) == 0) {
            return "201";
        }
        // 判断玩家是否已经存在此牌友圈
        if (isInClub(userid, circlenumber) >= 1) {
            return "204";
        }

        // 每個玩家最多加入10個牌友圈
        if (joinClub(userid) >= 5) {
            return "202";
        }
        // 是否重复申请
        if (circleCount(userid, circlenumber) > 0) {
            return "1";
        }
        // 插入申请表格
        String sql = "INSERT INTO game_card_apply(userid,circlenumber,date,state) VALUE(?,?,NOW(),0)";
        String executeUpdate = baseDao.executeUpdate(sql, new Object[]{userid, circlenumber});
        if ("success".equals(executeUpdate)) {
            return "2";
        }
        return "-1";
    }

    /**
     * 判断玩家是否已经存在此俱乐部zz
     *
     * @param userid
     * @param circlenumber
     * @return
     */
    private int isInClub(int userid, int circlenumber) {
        String sql = "select count(*) as counts from game_card_user where userid=? and circlenumber=?";
        baseDao.executeAll(sql, new Object[]{userid, circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 申请的次数
    public int circleCount(int userid, int circlenumber) {
        String sql = "SELECT count(*) as counts FROM game_card_apply where userid=? and circlenumber=? and state= 0 ";
        baseDao.executeAll(sql, new Object[]{userid, circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 牌主查看俱乐部 申请
    public Object circleApplication(int circlenumber, int userid) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 圈主id
        int clubuserid = selectclubuseridid(circlenumber);
        if (clubuserid != userid) {
            map.put("state", "101");// 没有权限
            return map;
        }
        String sql = "SELECT gca.applyid,gu.userid,gu.nickname,gu.avatarurl,gca.state FROM game_card_apply as gca\n" +
                "LEFT JOIN user_table gu\n" +
                "on gca.userid = gu.userid\n" +
                "where gca.circlenumber = ?\n" +
                "ORDER BY gca.date desc";
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> maps = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                maps = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getClubApplication");
                list.add(maps);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 圈主id
     *
     * @param circlenumber
     * @return
     */
    private int selectclubuseridid(int circlenumber) {
        String sql = "SELECT userid FROM game_card_circle WHERE circlenumber=?";
        baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("userid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 同意加入
     *
     * @param applyid
     * @param circlenumber
     * @return
     */
    public Object passjoinCard(int userids, int applyid, int circlenumber) {
        // 修改申请表状态 1
        updateApplyState(applyid);
        int userid = getUserid(applyid);/*
        String s = "update user_table set number_3=? where userid=?";
        baseDao.executeUpdate(s, new Object[]{circlenumber, userids});
*/
        String sql = "INSERT INTO game_card_user(userid,circlenumber,date) VALUES(?,?,NOW());";
        return baseDao.executeUpdate(sql, new Object[]{userid, circlenumber});
    }

    /**
     * 返回userid
     *
     * @param applyid
     * @return
     */
    private int getUserid(int applyid) {
        String sql = "SELECT userid from game_card_apply where applyid=?";
        baseDao.executeAll(sql, new Object[]{applyid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("userid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 修改申请表
     *
     * @param applyid
     */
    private void updateApplyState(int applyid) {
        String sql = "update game_card_apply SET state=1  where applyid=?";
        baseDao.executeUpdate(sql, new Object[]{applyid});
    }

    /**
     * 拒绝加入
     *
     * @param applyid
     * @return
     */
    public Object downjoinCard(int applyid) {
        String sql = "update game_card_apply SET state=2 where applyid=? ";
        return baseDao.executeUpdate(sql, new Object[]{applyid});
    }

    public Object setRole(int id) {
        String sql = "update game_card_user SET role=1 where id=? ";
        return baseDao.executeUpdate(sql, new Object[]{id});
    }

    /**
     * 查看牌友圈成员
     *
     * @param circlenumber
     * @return
     */
    public Object selectcarduser(int circlenumber) {
        String sql =
                "SELECT gcu.id,gcu.circlenumber,gcu.role,gu.avatarurl,gu.nickname,gu.userid FROM game_card_user AS gcu LEFT " +
                        "JOIN user_table AS gu ON gcu.userid = gu.userid WHERE gcu.circlenumber = ?";
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_selectcarduser");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看用户加入俱乐部的次数
     *
     * @param userid
     * @return
     */
    public int joinClub(int userid) {
        String sql = "SELECT COUNT(*) AS counts FROM game_card_user where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获得俱乐部的id是否存在
     *
     * @param circlenumber
     * @return
     */
    public int selectCardId(int circlenumber) {
        String sql = "SELECT count(*) as counts from game_card_circle where circlenumber=?";
        baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获得俱乐部的id
     *
     * @param circlenumber
     * @return
     */
    public int getCardId(int circlenumber) {
        String sql = "SELECT circleid from game_card_circle where circlenumber=?";
        baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("circleid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    // 查看是否有该俱乐部
    public int selectRoomno(int circleid) {
        String sql = "SELECT count(*) as counts FROM game_card_circle where circleid=?";
        baseDao.executeAll(sql, new Object[]{circleid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 查看玩家信息zh
     *
     * @param userid
     * @return
     */
    public Map<String, Object> getUserById(int userid) {
        String sql = "SELECT * FROM user_table where userid=?";
        baseDao.executeAll(sql, new Object[]{userid});
        try {
            if (baseDao.resultSet.next()) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_getUser");
                HashMap<String, Object> usermap = new HashMap<String, Object>();
                int money = Integer.parseInt(map.get("money").toString());
                int diamond = Integer.parseInt(map.get("diamond").toString());
                String openid = String.valueOf(map.get("openid").toString());
                String isPay = String.valueOf(map.get("isPay").toString());
                String number_3 = String.valueOf(map.get("number_3").toString());
                usermap.put("money", money);
                usermap.put("diamond", diamond);
                usermap.put("openid", openid);
                usermap.put("isPay", isPay);
                usermap.put("number_3", Integer.valueOf(number_3));
                return usermap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 查看公告ZZ
     *
     * @return
     */
    public Object getNotice() {
        String sql = "SELECT * FROM game_notice";
        String executeAll = baseDao.executeAll(sql, new Object[]{});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                return UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getnotice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看商城zz
     *
     * @return
     */
    public Object getDiamond() {
        String sql = "SELECT * FROM game_diamond_shop";
        // 使用集合存储
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getdiamondshop");
                list.add(map);
            }
            return list;// 返回集合
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查看金币商品
     *
     * @return
     */
    public Object ckjin() {
        String sql = "SELECT * FROM game_money_shop";
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getckjin");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查看钻石商品
     * 192.168.31.254
     *
     * @return
     */
    public Object ckzuan() {
        String sql = "SELECT * FROM game_diamond_shop";
        // 使用集合存储
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        String executeAll = baseDao.executeAll(sql, new Object[]{});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getdiamondshop");
                list.add(map);
            }
            return list;// 返回集合
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取战绩列表-- 房间号 局数 支付类型 结束时间 用户头像 用户名 用户id
     *
     * @param userid 用户id
     * @return
     */
    public List<Map<String, Object>> getRecordRoom(int userid,int record_type,int game_type) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String sql =
                "select p.* ,(select SUM(number) from pk_record_table where roomid = p.pkid and userid = pt.userid) number  from pk_table p LEFT JOIN pk_record_table pt on p.pkid = pt.roomid where pt.userid = ? and p.roomtype = ?  and p.gametype = ? GROUP BY p.roomno ORDER BY p.start_date DESC";
        String executeAll = baseDao.executeAll(sql, new Object[]{userid,record_type,game_type});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_recordroom");
                if(map!=null){
                    list.add(map);
                }
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getRecordByRoomid(Object roomid) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String sql = "select p.roomid,p.recordid,SUM(p.number) number,p.userid,u.avatarurl,u.nickname " +
                "from pk_record_table p " +
                "LEFT JOIN user_table u " +
                "on p.userid = u.userid " +
                "where p.roomid = ? " +
                "GROUP BY p.userid";
        String executeAll = baseDao.executeAll(sql, new Object[]{roomid});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_recorduserlist");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加每局用户战绩
     *
     * @param roomBean
     * @return
     */
    public int addPK_Record(Golden_room roomBean) {
        //插入房间信息
        int roomid = add_PK_Room(roomBean);
        String sql = "insert into pk_record_table values(null,?,?,?,?,?)";
        for (Player userBean : roomBean.getGame_userlist()) {
            baseDao.executeUpdate(sql, new Object[]{
                    userBean.getUserid(),
                    userBean.getNumber(),
                    roomBean.getGame_number(),
                    roomBean.getBanker() == userBean.getUserid() ? 1 : 0,
                    roomid
            });
        }
        return 0;
    }

    /**
     * 添加每局用户战绩
     *
     * @param roomBean
     * @return
     */
    public int addPK_Record(RoomBean roomBean) {
        //插入房间信息
        int roomid = roomBean.getRoomid();
        if(roomBean.getGame_number()==1){
            roomid = add_PK_Room(roomBean);
            roomBean.setRoomid(roomid);
        }
        String sql = "insert into pk_record_table values(null,?,?,?,?,?)";
        for (UserBean userBean : roomBean.getGame_userlist()) {
            baseDao.executeUpdate(sql, new Object[]{
                    userBean.getUserid(),
                    userBean.getDqnumber(),
                    roomBean.getGame_number(),
                    roomBean.getBanker() == userBean.getUserid() ? 1 : 0,
                    roomid
            });
        }
        return 0;
    }

    /**
     * 插入房间信息
     *
     * @param roomBean
     * @return
     */
    public int add_PK_Room(Golden_room roomBean) {
        String sql = "insert into pk_table values(null,?,NOW(),?,?,?,?,?,?,?)";
        String state = baseDao.executeUpdate(sql, new Object[]{
                roomBean.getRoomno(),
                roomBean.getMax_person(),
                roomBean.getHouseid(),
                roomBean.getFen(),
                roomBean.getMax_number(),
                AES.encrypt(roomBean.getUser_log_text()),
                roomBean.getClubid(),
                roomBean.getGame_number()

        });
        if ("success".equals(state)) {
            sql = "SELECT LAST_INSERT_ID() as id";
            baseDao.executeAll(sql, null);
            try {
                if (baseDao.resultSet.next()) {
                    return baseDao.resultSet.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 插入房间信息
     *
     * @param roomBean
     * @return
     */
    public int add_PK_Room(RoomBean roomBean) {
        String sql = "insert into pk_table values(null,?,?,NOW(),?,0)";
        String state = baseDao.executeUpdate(sql, new Object[]{
                roomBean.getRoomno(),
                roomBean.getFen(),
                roomBean.getClubid()==0?0:1
        });
        if ("success".equals(state)) {
            sql = "SELECT LAST_INSERT_ID() as id";
            baseDao.executeAll(sql, null);
            try {
                if (baseDao.resultSet.next()) {
                    return baseDao.resultSet.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 查看用户的牌友圈详情
     *
     * @return
     */
    public Map<String, Object> ckclubPlay(int circlenumber) {
        String sql = "SELECT gcc.circleid,gcc.circlename,gcc.diamond,gcc.circlenumber,gcc.date,gcc.userid FROM game_card_circle AS gcc LEFT JOIN game_card_user AS gcu ON gcu.circlenumber = gcc.circlenumber where gcc.circlenumber=?";
        String executeAll = baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> clubMap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getClubPlay");
                int  clubid=Integer.parseInt(clubMap.get("circlenumber").toString());
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (String key : Public_State.PKMap.keySet()) {
                    RoomBean pkBean = Public_State.PKMap.get(key);
                    if (pkBean.getClubid() == clubid) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        // 房间内用户头象
                        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
                        for (UserBean userBean : pkBean.getGame_userlist()) {
                            Map<String,Object> userMap= new  HashMap<String,Object>();
                            userMap.put("userid",userBean.getUserid());
                            userMap.put("avatarurl",userBean.getAvatarurl());
                            list1.add(userMap);
                        }
                        map.put("userlist", list1);
                        // 房间号
                        map.put("roomno", pkBean.getRoomno());
                        // 房间座位
                        map.put("positions",pkBean.getUser_positions());
                        list.add(map);
                    }
                }
                //根据俱乐部编号查询人数
                String counts=getUserNnum(circlenumber)+"";
                clubMap.put("counts",counts);
                clubMap.put("roomlist",list);
                return  clubMap;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param circlenumber
     * @return
     */
    private int getUserNnum(int circlenumber) {
        String sql="SELECT COUNT(id) AS counts FROM game_card_user WHERE circlenumber=?";
        baseDao.executeAll(sql, new Object[] { circlenumber });
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 踢出牌友圈
     *
     * @param userid
     * @param circlenumber
     * @return
     */
    public Object downcricle(int id, int userid, int circlenumber) {
        // 如果是否为圈主
        int zhuangid = getZhuang(circlenumber);
        if (zhuangid != userid) {
            return "101";
        }
        String sql = "delete  FROM game_card_user where userid=? and circlenumber=?";
        return baseDao.executeUpdate(sql, new Object[]{id, circlenumber});
    }

    /**
     * 获取圈主id
     *
     * @param circlenumber
     * @return
     */
    private int getZhuang(int circlenumber) {
        String sql = "SELECT * FROM game_card_circle where circlenumber=?";
        baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("userid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 退出俱乐部
     *
     * @param userid
     * @param circlenumber
     * @return
     */
    public Object exitClub(int userid, int circlenumber) {
        //获取圈主id
        int clubid = selectclubuseridid(circlenumber);
        //获取俱乐部成员人数
        int clubCount = clubCount(circlenumber);
        //普通成员直接退
        if(userid!=clubid){
            exitcricles(userid,circlenumber);
        }else{
            //圈主
            if (userid==clubid&&clubCount==1){
                //删除俱乐部信息
                exitcricle(userid,circlenumber);
                delClub(circlenumber);
            }else{
                //请先踢出其他成员
                return  "5";
            }
        }
        return "4";
    }

    /**
     * 踢出牌友圈
     *
     * @param userid
     * @param circlenumber
     * @return
     */
    public void exitcricles(int userid, int circlenumber) {
        String sql = "delete  FROM game_card_user where userid=? and circlenumber=?";
        baseDao.executeUpdate(sql, new Object[]{userid, circlenumber});
    }




    /**
     * 删除俱乐部
     * @param circlenumber
     */
    private void delClub(int circlenumber) {
        String sql = "delete  FROM game_card_circle where  circlenumber=?";
        baseDao.executeUpdate(sql, new Object[]{circlenumber});
    }

    /**
     * 退出牌友圈
     * @param userid
     * @param circlenumber
     */
    private void exitcricle(int userid, int circlenumber) {
        String sql = "delete  FROM game_card_user where userid=? and circlenumber=?";
        baseDao.executeUpdate(sql, new Object[]{userid, circlenumber});
    }

    /**
     * 俱乐部玩家户数量
     *
     * @param circlenumber
     * @return
     */
    private int clubCount(int circlenumber) {
        String sql = "select count(*) as counts from game_card_user where circlenumber=?";
        baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("counts");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 修改用户金币
     * @param userid
     * @param diamondid
     * @return
     */
    public Object UpdateUserDiamonds(int userid, int diamondid) {
        int realvalue = getShopDiamond(diamondid);
        String sql = "update user_table set diamond=diamond+? where userid=?";
        return baseDao.executeUpdate(sql, new Object[]{realvalue, userid});
    }

    /**
     *查询商品信息
     * @param diamondid
     * @return
     */
    public int getShopDiamond(int diamondid) {
        String sql = "select realvalue from game_diamond_shop where diamondid=?";
        baseDao.executeAll(sql, new Object[]{diamondid});
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("realvalue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除三天前记录
     */
    public void delRecord() {
        //获取当前时间
        String sql = "delete from pk_table where datediff(curdate(), start_date)>=3";
        baseDao.executeUpdate(sql, new Object[]{});
    }

    /**
     * 查看当局战绩
     * @param roomno
     * @return
     */
    public List<Map<String, Object>> finalScore(int roomno) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * from  pk_table AS pt INNER JOIN pk_record_table AS prt ON prt.roomid=pt.pkid  WHERE  pt.roomno=?";
        baseDao.executeAll(sql, new Object[]{roomno});
        try {
            while (baseDao.resultSet.next()) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_scorelist");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addNum(int userid) {
        String sql = "update user_table set number_1=number_1+1,number_5=number_5+1 where userid=?";
        baseDao.executeUpdate(sql, new Object[]{userid});
    }

    public void UpdateAllUser() {
        int money = Public_State.diamond;
        String sql = "update user_table set money=?,number_4=0,number_5=0";
        baseDao.executeUpdate(sql, new Object[]{money});

        //修改用户的每日输赢和每日局数
    }

    /**
     * 查询初始积分
     *
     * @param
     * @return
     */
    public int getInitMoney() {
        String sql = "select * from config_table limit 0,1";
        baseDao.executeAll(sql, null);
        try {
            if (baseDao.resultSet.next()) {
               return baseDao.resultSet.getInt("card_diamond");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 100;
    }

    public void updateNumber_1(int userid,int dqnumber) {
        String sql = "update user_table set number_4=number_4+? where userid = ?";
        baseDao.executeUpdate(sql, new Object[]{dqnumber,userid});
    }
    public void updateNumber_1_2(int userid,int dqnumber) {
        String sql = "update user_table set number_4=number_4-? where userid = ?";
        baseDao.executeUpdate(sql, new Object[]{dqnumber,userid});
    }

    public List<Map> getAllUsers(UserBean user) {
        String sql = "SELECT ga.userid,ga.circlenumber,t.avatarurl,t.nickname,t.openid FROM game_card_user ga RIGHT JOIN (SELECT * FROM game_card_user where userid = ?) gc ON ga.circlenumber = gc.circlenumber LEFT JOIN user_table t on ga.userid =t.userid  where ga.userid != ? GROUP BY ga.userid";
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(user.getUserid());
        objects.add(user.getUserid());
        if(user.getNumber_3()!=0){
            sql += " and ga.circlenumber = ?";
            objects.add(user.getNumber_3());
        }
        ArrayList<Map> list = new ArrayList<>();
        String executeAll = baseDao.executeAll(sql, objects.toArray());
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> sqlMap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getUser_number3");
                UserBean userBean = new UserBean();
                Object userid = sqlMap.get("userid");
                Object nickname = sqlMap.get("nickname");
                Object avatarurl = sqlMap.get("avatarurl");
                Object openid = sqlMap.get("openid");
                userBean.setUserid(Integer.parseInt(userid.toString()));
                userBean.setNickname(nickname.toString());
                userBean.setAvatarurl(avatarurl.toString());
                userBean.setOpenid(openid.toString());
                list.add(userBean.getUserCustom("userid-nickname-avatarurl-openid"));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}