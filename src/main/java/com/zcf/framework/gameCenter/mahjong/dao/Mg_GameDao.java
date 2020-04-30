package com.zcf.framework.gameCenter.mahjong.dao;

import com.zcf.framework.gameCenter.mahjong.bean.GameProp;
import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import org.apache.commons.lang3.StringUtils;
import util.BaseDao;
import util.LotteryUtil;
import util.UtilClass;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 前台接口对接管理
 * 
 * @author Administrator
 *
 */
public class Mg_GameDao {
	private BaseDao baseDao;

	public Mg_GameDao(BaseDao baseDao) {
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
		String sql = "update user_table set money=money" + (type == 0 ? '-' : '+') + "? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { money, userid });
	}
	/**
	 * 增加用户金币
	 *
	 * @param userid
	 * @param money
	 * @return
	 */
	public String AddMoney(int userid, int money) {
		String sql = "update user_table set money=money+? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { money, userid });
	}
	/**
	 * 扣除用户钻石
	 * 
	 * @param userid
	 * @param diamond
	 * @return
	 */
	public String UpdateUserDiamond(int userid, int diamond, int type) {
		String sql = "update user_table set diamond=diamond" + (type == 0 ? '-' : '+') + "? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { diamond, userid });
	}

	/**
	 * 查询用户金币
	 * 
	 * @param userid
	 * @return
	 */
	public int getUserMoney(int userid) {
		String sql = "select money from user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
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
		baseDao.executeAll(sql, new Object[] { userid });
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
		baseDao.executeAll(sql, new Object[] { userid });
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
		baseDao.executeAll(sql, new Object[] { code });
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
		baseDao.executeAll(sql, new Object[] { userid, code });
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
		baseDao.executeAll(sql, new Object[] { code });
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
		return baseDao.executeUpdate(sql, new Object[] { userids, money, userid });
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
		String executeAll = baseDao.executeAll(sql, new Object[] { userid });
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
		String executeAll = baseDao.executeAll(sql, new Object[] { userid });
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
				//Public_State.establish_three = baseDao.resultSet.getString("establish_three").split("-");
				Public_State.establish_four = baseDao.resultSet.getString("establish_four").split("-");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加每局用户战绩
	 * 
	 * @param roomBean
	 * @return
	 */
	public int addPK_Record(RoomBean roomBean) {
		// 插入房间信息
		if (roomBean.getGame_number() == 1) {
			add_PK_Room(roomBean);
		}
		String sql = "insert into pk_record_table values(?,?,?,?,?,?)";
		for (UserBean userBean : roomBean.getGame_userlist()) {
			baseDao.executeUpdate(sql,
					new Object[] { userBean.getUserid(), userBean.getNumber(), roomBean.getGame_number(),
							roomBean.getBanker() == userBean.getUserid() ? 1 : 0, roomBean.getRoomno(),
							roomBean.getUser_log_text() });
		}
		return 0;
	}

	/**
	 * 插入房间信息
	 * 
	 * @param roomBean
	 * @return
	 */
	public String add_PK_Room(RoomBean roomBean) {
		String sql = "insert into pk_table values(?,NOW(),?,?,?)";
		return baseDao.executeUpdate(sql,
				new Object[] { roomBean.getRoomno(), roomBean.getMax_person(), roomBean.getHouseid(),
						
						roomBean.getMax_number() });
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
		String executeAll = baseDao.executeAll(sql, new Object[] {});
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
	 *  192.168.31.254
	 * @return
	 */
	public Object ckzuan() {
		String sql = "SELECT * FROM game_diamond_shop";
		// 使用集合存储
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] {});
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

	public Object service() {
		String sql2 = "select * from `service` where id = 1";
		// 使用集合存储
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll2 = baseDao.executeAll(sql2, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll2)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_service");
				list.add(map);
			}
			return list;// 返回集合
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
		String sql = "SELECT * FROM game_service";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				return UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getservice");
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
		String executeAll = baseDao.executeAll(sql, new Object[] {});
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
	 * 查看系统
	 * 
	 * @return
	 */
	public Object cksystem() {
		Map<String, Object> map = new HashMap<String, Object>();
		// 使用集合存储
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT * FROM game_disclaimer order by date desc ";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getdisclaimer");
				list.add(map);
			}
			return list;// 返回集合
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看签到奖励
	 * 
	 * @return
	 */
	public Object cksign() {
		String sql = "SELECT * FROM game_sign";
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getsign");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看轮播图
	 * 
	 * @return
	 */
	public Object cklun() {
		String sql = "SELECT * FROM game_advertising";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				return UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getlun");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看幸运转盘规则
	 * 
	 * @return
	 */
	public Object ckluck() {
		String sql = "SELECT * FROM game_luck";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				return UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getluck");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看幸运转盘奖励
	 * 
	 * @return
	 */
	public List<Map<String, Object>> ckluckpro() {
		String sql = "SELECT * FROM game_prop";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_prop");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看游戏规则
	 * 
	 * @return
	 */
	public Object ckgui() {
		String sql = "SELECT * FROM game_introduce";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
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
	 * 查看提现申请
	 * 
	 * @return
	 */
	public Object ckwithdraw() {
		String sql = "SELECT  * from  game_withdraw";
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_withdraw");
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 提现
	 * 
	 * @param id
	 * @param money
	 * @param account
	 * @return
	 */
	public Object withdraw(int money, String account, String name, int id) {
		// 用户余额
		int moneys = ckmoney(id);
		if (money > moneys) {
			return "101";// 超出用户余额
		}
		// 扣除用户金币
		reduceUserMoney(money, id);
		String userid = "";
		if (id >= 10000 && id < 100000)
			userid = 1 + "" + id;
		if (id >= 1000 && id < 10000)
			userid = 10 + "" + id;
		if (id >= 100 && id < 1000)
			userid = 100 + "" + id;
		if (id >= 10 && id < 100)
			userid = 1000 + "" + id;
		if (id < 10)
			userid = 10000 + "" + id;
		String sql = "INSERT INTO game_withdraw(userid,money,account,realname,date) value(?,?,?,?,NOW())";
		return baseDao.executeUpdate(sql, new Object[] { userid, money, account, name });
	}

	/**
	 * 扣除用户金币
	 * 
	 * @param money
	 * @param id
	 */
	private void reduceUserMoney(int money, int id) {
		String sql = "update user_table set money=money-? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { money, id });
	}

	/**
	 * 查询用户金额
	 * 
	 * @return
	 */
	public int ckmoney(int userid) {
		String sql = "SELECT * FROM user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
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
	 * 查看兑奖
	 * 
	 * @return
	 */
	public Object ckdui() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "SELECT * FROM game_conversion";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map= UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getcon");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看牌友圈
	 * 
	 * @param userid
	 * 
	 * @return
	 */
	public Object ckclub(int userid) {
		String sql = "SELECT gcc.circleid,gcc.circlename,gcc.circlenumber,gcc.date,gcc.diamond,gcc.userid from game_card_user AS gcu INNER JOIN game_card_circle AS gcc ON gcc.circlenumber=gcu.circlenumber WHERE gcu.userid=?";
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] { userid });
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
	 * 查看用户的牌友圈详情
	 * 
	 * @return
	 */
	public Object ckclubPlay(int circlenumber) {
        String sql = "SELECT gcc.circleid,gcc.circlename,gcc.diamond,gcc.circlenumber,gcc.date,gcc.userid FROM game_card_circle AS gcc LEFT JOIN game_card_user AS gcu ON gcu.circlenumber = gcc.circlenumber where gcc.circlenumber=?";
        String executeAll = baseDao.executeAll(sql, new Object[]{circlenumber});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> clubMap = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getClubPlay");
				//根据俱乐部编号查询人数
				int counts=getUserNnum(circlenumber);
				clubMap.put("counts",counts);
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
	 * 累计登陆7天为一轮（中途断签不影响天数) 给用户加金币 显示连续签到天数
	 * 
	 * @param userid
	 * @param signid
	 * @return
	 */
	public String sign(int userid, int signid) {
		// 1. 查询签到天数所对应的金币奖励数
		int moneynum = getSign(signid);
		// 2. 判断是否重复签到
		String datetime = returntime();
		boolean b = repetition(userid, datetime);
		if (b) {
			return "error";// 重复签到
		}
		// 1.2 签到---插入中间表
		signs(signid, userid);
		// 2. 给用户加金币
		String sql = "update user_table set money=money+? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { moneynum, userid });
	}

	/**
	 * 判断是否重复签到 获取当天日期
	 * 
	 * @param userid
	 * @param datetime
	 */
	private boolean repetition(int userid, String datetime) {
		String sql = "SELECT * FROM game_sign_user where userid=? and date=?";
		baseDao.executeAll(sql, new Object[] { userid, datetime });
		try {
			return baseDao.resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 签到---插入中间表
	 * 
	 * @param signid
	 * @param userid
	 */
	private void signs(int signid, int userid) {
		Map<String, Object> map = signrecord(userid);
		String signday = (String) map.get("signnum");
		String datetime = returntime();
		if(signday==null){
			String sql = "INSERT INTO game_sign_user(userid,signid,date,signnum) value(?,?,?,1)";
			baseDao.executeUpdate(sql, new Object[] { userid, signid, datetime});
		}else{
			int n = Integer.parseInt(signday);
			if (n < 7) {
				n = n + 1;
			} else {
				n = 1;
			}
			String sql = "INSERT INTO game_sign_user(userid,signid,date,signnum) value(?,?,?,?)";
			baseDao.executeUpdate(sql, new Object[] { userid, signid, datetime, n });	
		}
	}

	/**
	 * 获取当前时间的方法
	 */
	private String returntime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}

	/**
	 * 查询签到天数所对应的金币奖励数
	 * 
	 * @param signid
	 */
	private int getSign(int signid) {
		String sql = "select * from game_sign where signid=?";
		baseDao.executeAll(sql, new Object[] { signid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 查看已签记录
	 * 
	 * @return
	 */
	public Map<String, Object> signrecord(int userid) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "select * from game_sign_user where userid=?";
		String executeAll = baseDao.executeAll(sql, new Object[] { userid });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getsignrecord");
				list.add(map);
			}
			if(list.size()!=0){
				Map<String, Object> recordmap = list.get(list.size() - 1);
				boolean repetition = repetition(userid,returntime());
				if(repetition){
					recordmap.put("sta", "0");
				}
				return recordmap;
			}
			map.put("signnum", "0");
			map.put("state", "0");
			map.put("sta", "1");
			return map;
////			Map<String, Object> recordmap = list.get(list.size()+1);
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
		baseDao.executeUpdate(sql, new Object[] { userid, circlenumber });
	}

	/**
	 * 创建牌友圈
	 * 
	 * @param userid
	 * @return
	 */
	public String createClub(String circlename, int userid) {
		String random = UtilClass.utilClass.getRandom(6);
		int randoming = Integer.parseInt(random);
		String sql = "INSERT INTO game_card_circle(circlename,circlenumber,date,diamond,userid,maxnum) VALUE(null,?,NOW(),0,?,200)";
		String str = baseDao.executeUpdate(sql, new Object[] { randoming, userid });
		if (str.equals("success")) {
				downUserMoney(userid);
				int circlenumber = getCircle(userid);
				insertcircleuser(userid, randoming);
				return "0";
//			}
			
		}
		return "-1";
	}

    /**
     * 查询玩家是否是代理身份
     * @param userid
     * @return
     */
    private int selectFid(int userid) {
        String sql = "SELECT * FROM user_table where userid=?";
        baseDao.executeAll(sql, new Object[] { userid });
        try {
            if (baseDao.resultSet.next()) {
                return baseDao.resultSet.getInt("fid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
	 * 根据用户id获得牌友圈
	 * 
	 * @param userid
	 * @return
	 */
	private int getCircle(int userid) {
		String sql = "SELECT * FROM game_card_circle where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("circlenumber");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 成功扣除用戶金幣
	 * 
	 * @param userid
	 */
	private void downUserMoney(int userid) {
		String sql = "update user_table set diamond=diamond-5000 where userid=?";
		baseDao.executeUpdate(sql, new Object[] { userid });
	}

	/**
	 * 消耗5000金币 金币是否充足
	 * 
	 * @param userid
	 * @return boolean
	 */
	public int selectMoney(int userid) {
		String sql = "SELECT * FROM user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("money");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 用户创建俱乐部次数
	public int selectClub(int userid) {
		String sql = "SELECT count(*) as counts from game_card_circle where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("counts");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 牌友圈名称是否重复
	public int selectClubName(String clubName) {
		String sql = "SELECT count(circlename) as counts FROM game_card_circle where circlename=?";
		baseDao.executeAll(sql, new Object[] { clubName });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("counts");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 牌友圈是否存在
	public int selectClub(String circlenumber) {
		String sql = "SELECT count(cid) as counts FROM game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[] { circlenumber });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("counts");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 生成牌友圈编号是否重复
	public boolean selectClubNO(int id) {
		String sql = "SELECT * FROM game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[] { id });
		try {
			return baseDao.resultSet.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
		if (isInClub(userid, circlenumber) >=1) {
			return "204";
		}

		// 每個玩家最多加入10個牌友圈
		if (joinClub(userid) >= 10) {
			return "202";
		}

		// 是否重复申请
		if (circleCount(userid, circlenumber) > 0) {
			return "1";
		}
		// 插入申请表格
		String sql = "INSERT INTO game_card_apply(userid,circlenumber,date,state) VALUE(?,?,NOW(),0)";
		String executeUpdate = baseDao.executeUpdate(sql, new Object[] { userid, circlenumber });
		if ("success".equals(executeUpdate)) {
			return "2";
		}
		return "-1";
	}

	/**
	 * 判断玩家是否已经存在此牌友圈
	 * 
	 * @param userid
	 * @return
	 */
	private int isInClub(int userid, int circlename) {
		String circlenumber = "";
		String sqls = "select circlenumber  from game_card_circle where circlename =?";
		baseDao.executeAll(sqls, new Object[] { circlename });
		try {
			if(baseDao.resultSet.next()){
				circlenumber = baseDao.resultSet.getString("circlenumber");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String sql = "select count(*) as counts from game_card_user where userid=? and circlenumber=?";
		baseDao.executeAll(sql, new Object[] { userid, circlenumber });
		try {
			if(baseDao.resultSet.next()){
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
		baseDao.executeAll(sql, new Object[] { userid, circlenumber });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("counts");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 牌主查看牌友圈 申请
	public Object circleApplication(int circlenumber, int userid) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 圈主id
		int clubuserid = selectclubuseridid(circlenumber);
		if (clubuserid != userid) {
			map.put("state", "101");// 没有权限
			return map;
		}
		String sql = "SELECT gcc.circlenumber,gca.applyid,gu.userid,gu.nickname,gu.avatarurl FROM user_table AS gu LEFT JOIN game_card_apply AS gca ON gca.userid = gu.userid LEFT JOIN game_card_circle AS gcc ON gcc.circlenumber = gca.circlenumber WHERE gca.state = 0 and gcc.userid = ?";
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> maps = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] { userid });
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
		baseDao.executeAll(sql, new Object[] { circlenumber });
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
        int userid = getUserid(applyid);
        String sql = "INSERT INTO game_card_user(userid,circlenumber,date) VALUES(?,?,NOW());";
        return baseDao.executeUpdate(sql, new Object[]{userid, circlenumber});
    }

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
		baseDao.executeUpdate(sql, new Object[] { applyid });
	}

	/**
	 * 拒绝加入
	 * 
	 * @param applyid
	 * @return
	 */
	public Object downjoinCard(int applyid) {
		String sql = "update game_card_apply SET state=2 where applyid=? ";
		return baseDao.executeUpdate(sql, new Object[] { applyid });
	}

	/**
	 * 查看牌友圈成员
	 * 
	 * @param circlenumber
	 * @return
	 */
	public Object selectcarduser(int circlenumber) {
		String sql = "SELECT gcu.id,gcu.circlenumber,gu.avatarurl,gu.nickname,gu.userid FROM game_card_user AS gcu LEFT JOIN user_table AS gu ON gcu.userid = gu.userid WHERE gcu.circlenumber = ?";
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] { circlenumber });
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
		baseDao.executeAll(sql, new Object[] { userid });
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
	 * 获得牌友圈的id是否存在
	 * 
	 * @param circlenumber
	 * @return
	 */
	public int selectCardId(int circlenumber) {
		String sql = "SELECT count(*) as counts from game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[] { circlenumber });
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
	 * 获得牌友圈的id
	 * 
	 * @param circlenumber
	 * @return
	 */
	public int getCardId(int circlenumber) {
		String sql = "SELECT circleid from game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[] { circlenumber });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("circleid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
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
		String sql = "delete  FROM game_card_user where id=?";
		return baseDao.executeUpdate(sql, new Object[] { id });
	}

	/**
	 * 获取圈主id
	 * 
	 * @param circlenumber
	 * @return
	 */
	private int getZhuang(int circlenumber) {
		String sql = "SELECT * FROM game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[] { circlenumber });
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
	 * 判断是不是庄家
	 * 
	 * @return
	 */
	public int useridzhuan(int id) {
		String sql = "SELECT userid FROM game_card_circle where circleid=?";
		baseDao.executeAll(sql, new Object[] { id });
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
	 * 兑奖
	 * 
	 * @param userid
	 * @param conversionid
	 * @param phone
	 * @return
	 */
	public Object conversion(int userid, int conversionid, String phone) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 1.用户奖券
		int award = getUseraward(userid);
		// 1.1 用户名称
		String userName = getUserName(userid);
		// 2.兑奖商品信息
		Map<String, Object> awardmap = getaward(conversionid);
		// 2.1 商品奖券
		int awardnum = Integer.parseInt(awardmap.get("connum").toString());
		if (award < awardnum) {
			map.put("state", "101");// 奖券不足
			return map;
		}
		// 2.2 商品类型
		int awardType = Integer.parseInt(awardmap.get("type").toString());
		// 2.3 商品价值
		int awardValue = Integer.parseInt(awardmap.get("convalue").toString());
		// 2.4 商品名称
		String conname = awardmap.get("conname").toString();
		if (awardType == 1) {
			String sql = "update user_table set award=award-?,money=money+? where userid=?";
			return baseDao.executeUpdate(sql, new Object[] { awardnum, awardValue, userid });
		}
		if (awardType == 2) {
			String sql = "update user_table set award=award-?,diamond=diamond+? where userid=?";
			return baseDao.executeUpdate(sql, new Object[] { awardnum, awardValue, userid });
		}
		if (awardType == 3) {
			if (StringUtils.isEmpty(phone) || phone.equals("0")) {
				map.put("state", "100");// 手机号不能为空
				return map;
			}
			String sql = "update user_table set award=award-? where userid=?";
			String str = baseDao.executeUpdate(sql, new Object[] { awardnum, userid });
			if (str.equals("success")) {
				return addconrecord(conname, userName, userid, phone, awardnum);
			}
		}
		return null;
	}

	/**
	 * 添加兑奖卷
	 * 
	 * @param conname
	 * @param userName
	 * @param userid
	 * @param phone
	 * @return
	 */
	private String addconrecord(String conname, String userName, int userid, String phone, int awardnum) {
		String sql = "INSERT INTO game_conversion_user(userid,nickname,phone,content,date,0,ticketnums) value(?,?,?,?,NOW(),?)";
		return baseDao.executeUpdate(sql, new Object[] { userid, userName, phone, conname, awardnum });
	}

	/**
	 * 用户昵称
	 * 
	 * @param userid
	 * @return
	 */
	private String getUserName(int userid) {
		String sql = "Select * from user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getString("nickname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看奖品
	 * 
	 * @param conversionid
	 * @return
	 */
	private Map<String, Object> getaward(int conversionid) {
		String sql = "select * from game_conversion where conversionid=?";
		String executeAll = baseDao.executeAll(sql, new Object[] { conversionid });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getcon");
				return map;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用户奖券
	 * 
	 * @param userid
	 * @return
	 */
	private int getUseraward(int userid) {
		String sql = "Select * from user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("award");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 开始抽奖
	 * 
	 * @param userid
	 *            用户id
	 * @param ratio
	 *            抽奖倍数
	 * @return
	 */
	public Object lottery(int userid, int ratio) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取每次抽奖消耗的金币
		int luckMoney = getluckmoney();
		int luckMoneys = ratio * luckMoney;// 消耗的金币*倍数
		// 金币是否充足
		int userMoney = getUserMoney(userid);
		if (userMoney < luckMoneys) {
			map.put("state", "101");// 金币不足
			return map;
		}else{
			// 扣除用户金币
			subUserMoney(userid, luckMoneys);
			// 抽奖
			List<GameProp> gameProp = gameProp();
			List<Integer> orignalRates = new ArrayList<Integer>(gameProp.size());
			for (GameProp gp : gameProp) {
				int prop = gp.getProp();// 概率
				if (prop < 0) {
					prop = 0;
				}
				orignalRates.add(prop);
			}
			// 抽奖回执
			int lottery = LotteryUtil.lottery(orignalRates);
			int lotteryid = lottery + 1;
			// 根据抽奖返回坐标查询详细奖品信息
			GameProp prop = getProp(lotteryid);
			int type = prop.getType();
			if (type == 1) {// 金币
				int awardvalue = prop.getAwardline();
				addUserMoney(userid, awardvalue,ratio);
			}
			if (type == 2) {// 钻石
				int awardvalue = prop.getAwardline();
				addUserdiamond(userid, awardvalue,ratio);
			}
			if (type == 3) {// 奖券
				int awardvalue = prop.getAwardline();
				addUseraward(userid, awardvalue,ratio);
			}
			if (type == 4) {// 大满贯 ---返还所有奖励
				List<GameProp> gameProps = gameProp();
				for (GameProp gp : gameProps) {
					int types = gp.getType();
					if (types == 1) {
						int awardvalue = gp.getAwardline();
						addUserMoneys(userid, awardvalue);
					}
					if (types == 2) {
						int awardvalue = gp.getAwardline();
						addUserdiamonds(userid, awardvalue);
					}
					if (types == 3) {
						int awardvalue = gp.getAwardline();
						addUserawards(userid, awardvalue);
					}
				}
			}
			map.put("state", "0");
			map.put("prop", prop);
			return map;
		}
	}

	// 修改用户金币
	public void subUserMoney(int userid, int luckMoneys) {
		String sql = "update user_table set money=money-? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { luckMoneys, userid });
	}

	/**
	 * 抽奖消耗
	 * 
	 * @return
	 */
	private int getluckmoney() {
		String sql = "select money from game_luck where cont='单次抽奖消耗'";
		baseDao.executeAll(sql, new Object[] {});
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
	 * 给用户增加奖券
	 * 
	 * @param userid
	 * @param awardvalue
	 * @param ratio 
	 */
	private void addUseraward(int userid, int awardvalue, int ratio) {
		int award=ratio*awardvalue;
		String sql = "update user_table set award=award+? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { award, userid });
	}
	/**
	 * 给用户增加奖券
	 * 
	 * @param userid
	 * @param awardvalue
	 */
	private void addUserawards(int userid, int awardvalue) {
		String sql = "update user_table set award=award+? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { awardvalue, userid });
	}
	/**
	 * 给用户增加钻石
	 * 
	 * @param userid
	 * @param awardvalue
	 */
	private void addUserdiamonds(int userid, int awardvalue) {
		String sql = "update user_table set diamond=diamond+? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { awardvalue, userid });
	}
	/**
	 * 给用户增加钻石
	 * 
	 * @param userid
	 * @param awardvalue
	 * @param ratio 
	 */
	private void addUserdiamond(int userid, int awardvalue, int ratio) {
		int diamond=awardvalue*ratio;
		String sql = "update user_table set diamond=diamond+? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { diamond, userid });
	}
	/**
	 * 给用户加金币
	 * 
	 * @param awardvalue
	 * @param userid
	 * @param ratio 
	 * 
	 */
	private void addUserMoney(int userid, int awardvalue, int ratio) {
		int money=awardvalue*ratio;
		String sql = "update user_table set money=money+? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { money, userid });
	}
	/**
	 * 给用户加金币
	 * 
	 * @param awardvalue
	 * @param userid
	 *
	 */
	private void addUserMoneys(int userid, int awardvalue) {
		String sql = "update user_table set money=money+? where userid=?";
		baseDao.executeUpdate(sql, new Object[] { awardvalue, userid });
	}

	/**
	 * 获取奖品信息
	 * 
	 */
	private GameProp getProp(int lotteryid) {
		String sql = "select * from game_prop where proid=?";
		baseDao.executeAll(sql, new Object[] { lotteryid });
		try {
			if (baseDao.resultSet.next()) {
				return (GameProp) UtilClass.utilClass.getSqlBean(GameProp.class, "/sql.properties", baseDao.resultSet,
						"sql_prop");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * 获取奖励信息 proid prop awardline awardname type
	 * 
	 */
	public List<GameProp> gameProp() {
		List<GameProp> list = new ArrayList<GameProp>();
		// 查看幸运奖励
		List<Map<String, Object>> propMapList = ckluckpro();
		for (Map<String, Object> map : propMapList) {
			// 创建概率实体
			GameProp prop = new GameProp();
			prop.setProid(Integer.parseInt(map.get("proid").toString()));
			prop.setProp(Integer.parseInt(map.get("prop").toString()));
			prop.setAwardline(Integer.parseInt(map.get("awardline").toString()));
			prop.setAwardname(map.get("awardname").toString());
			prop.setType(Integer.parseInt(map.get("type").toString()));
			// 存入集合
			list.add(prop);
		}
		return list;
	}

	/**
	 * 获取战绩列表-- 房间号 局数 支付类型 结束时间 用户头像 用户名 用户id
	 * 
	 * @param userid
	 *            用户id
	 * @return
	 */
	public List<Map<String, Object>> getRecordRoom(int userid) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String sql = "SELECT pt.roomno,pt.game_number, prt.number,pt.start_date,pt.log,pt.clubid,prt.userid,(SELECT nickname FROM user_table WHERE userid = prt.userid) nickname from pk_table pt LEFT JOIN pk_record_table prt ON pt.pkid = prt.roomid WHERE pt.pkid IN (SELECT roomid FROM pk_record_table WHERE userid = ?)";
        String executeAll = baseDao.executeAll(sql, new Object[]{userid});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_recordroom");
                list.add(map);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
	}
	public static void main(String[] args) {
		String ss = "\"dsds\"dsds";
		ss=ss.replace("\\", "");
		ss=ss.replace('"', ' ');
		ss=ss.replaceAll(" ", "");
		System.out.println(ss);
	}
	// 查看是否有该牌友圈
	public int selectRoomno(int circleid) {
		String sql = "SELECT count(*) as counts FROM game_card_circle where circleid=?";
		baseDao.executeAll(sql, new Object[] { circleid });
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
	 * 查看当日任务
	 * 
	 * @param userid
	 * @return
	 */
	public Object getdaily(int userid) {
		String sql = "SELECT gd.content,gd.type,gd.awardnum,gd.count,gdu.dailynum FROM game_daily_user AS gdu LEFT JOIN game_daily AS gd ON gdu.dailyid = gd.dailyid WHERE gdu.userid = ?";
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String executeAll = baseDao.executeAll(sql, new Object[] { userid });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getdailys");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取战绩详情
	 * 
	 * @return
	 */
	public Object selectJoinPK(int houseid, int roomno) {
		if (selectRoomno(roomno) > 0) {
			String sql = "SELECT * from pk_table  where houseid=? and roomno=?";
			ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			String executeAll = baseDao.executeAll(sql, new Object[] { houseid, roomno });
			try {
				while (baseDao.resultSet.next() && "success".equals(executeAll)) {
					map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_selectJoinPK");
					list.add(map);
				}
				return list;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return "-1";
		}
		return "0";// 没有房间
	}

	/**
	 * 战绩记录
	 * 
	 * @param onwer
	 * @return
	 */
	public List<Map<String, Object>> getRecordDetails(String onwer) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT pt.roomno,prt.game_number,gu.nickname,prt.number,pt.start_date FROM user_table AS gu INNER JOIN pk_record_table AS prt ON prt.userid = gu.userid INNER JOIN pk_table AS pt ON pt.roomno = prt.roomno WHERE pt.roomno =? ORDER BY prt.recordid DESC";
		String executeAll = baseDao.executeAll(sql, new Object[] { onwer });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getRecordDetail");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看玩家信息
	 * 
	 * @param userid
	 * @return
	 */
	public Map<String, Object> getUserById(int userid) {
		String sql = "SELECT * FROM user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { userid });
		try {
			if (baseDao.resultSet.next()) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getUser");
				HashMap<String, Object> usermap = new HashMap<String, Object>();
				int money = Integer.parseInt(map.get("money").toString());
				int diamond = Integer.parseInt(map.get("diamond").toString());
				usermap.put("money", money);
				usermap.put("diamond", diamond);
				return usermap;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查看牌友圈房间信息
	 *
	 * @return
	 */
	public List<RoomBean> selectclubroom(int clubid) {
        List<RoomBean> list = new ArrayList<>();
        // 获取房间列表
        Map<String, RoomBean> roomBeanmap = Public_State.PKMap;
        for (Map.Entry<String, RoomBean> entry : roomBeanmap.entrySet()) {
            RoomBean roomBean = entry.getValue();
            if(roomBean.getClubid()==clubid){
                RoomBean room= new RoomBean();
                room.setRoomno(roomBean.getRoomno());
                room.getGame_userlist().addAll(roomBean.getGame_userlist());
                list.add(room);
            }
        }
        return list;
	}

	/**
	 * 添加俱乐部圈主金币
	 * @param money
	 */
	public String addClub_Money(int clubid,int money){
		String sql = "update user_table set money=money+? where userid in (select user_id from game_card_circle where id=?)";
		return baseDao.executeUpdate(sql, new Object[]{money,clubid});
	}

	/**
	 * 牌友圈战绩详情
	 * @param circlenumber
	 * @return
	 */
	public Object getClubDetails(int circlenumber) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT gu.nickname,pt.game_type,pt.fen,pt.start_date FROM user_table AS gu INNER JOIN pk_record_table AS prt ON prt.userid = gu.userid INNER JOIN pk_table AS pt ON pt.roomno = prt.roomno WHERE pt.clubid =? ORDER BY prt.recordid DESC";
		String executeAll = baseDao.executeAll(sql, new Object[] { circlenumber });
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getClubDetail");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 购买金币
	 * @return
	 */
	public Object byMoney() {
		
		return null;
	}
	/**
	 * 购买钻石
	 * @return
	 */
	public Object byDiamond() {

		return null;
	}

	/**
	 * 分享  好友点击注册、下载》好友充值达到10元以上，按充值额度返给上级奖励。每日最多获得5次奖励，返还比例后台设置。
	 * @return
	 */
	public Object getShare() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "SELECT * FROM game_share ";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getShare");
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 查看招募代理
	 * @return
	 */
	public Object getAgency() {
		String sql="select * from game_agency";
		String executeAll = baseDao.executeAll(sql, new Object[] {});
		try {
			while (baseDao.resultSet.next() && "success".equals(executeAll)) {
				Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
						"sql_getAgency");
				return map;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

    /**
     * 查看活动
     * @return
     */
    public Object getActivily() {
        String sql="select * from game_activity";
        String executeAll = baseDao.executeAll(sql, new Object[] {});
        try {
            while (baseDao.resultSet.next() && "success".equals(executeAll)) {
                Map<String, Object> map = UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet,
                        "sql_getActivity");
                return map;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * 添加俱乐部房卡
	 * @param circlenumber
	 * @param userid
	 * @param diamond
	 * @return
	 */
	public Object addClubDiamond(String circlenumber, int userid, int diamond) {
		int userDiamond = getUserDiamond(userid);
		if (userDiamond < diamond) {
			return "222";
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
	 * 绑定俱乐部
	 *
	 * @param circlenumber
	 * @param userid
	 * @return
	 */
	public String addClub(String circlenumber, int userid) {
		if(selectClub(circlenumber)<0){
			return "451";
		}
		String sql = "update user_table set number_3= ? where userid=?";
		return baseDao.executeUpdate(sql, new Object[] { circlenumber, userid });
	}
}