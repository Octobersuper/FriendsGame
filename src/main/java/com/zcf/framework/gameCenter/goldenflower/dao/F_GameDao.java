package com.zcf.framework.gameCenter.goldenflower.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import util.BaseDao;
import util.UtilClass;

public class F_GameDao {
	private BaseDao baseDao;

	public F_GameDao(BaseDao baseDao) {
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
		return baseDao.executeUpdate(sql, new Object[]{money, userid});
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

	public List<Map> getAllUsers(UserBean user) {
		String sql = "SELECT ga.userid,ga.circlenumber,t.avatarurl,t.nickname,t.openid FROM game_card_user ga RIGHT JOIN (SELECT * FROM game_card_user where userid = ?) gc ON ga.circlenumber = gc.circlenumber LEFT JOIN user_table t on ga.userid =t.userid  where ga.userid != ? GROUP BY ga.userid";
		ArrayList<Object> objects = new ArrayList<>();
		objects.add(user.getUserid());
		objects.add(user.getUserid());
		if (user.getNumber_3() != 0) {
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
