package com.zcf.framework.gameCenter.goldenflower.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.BaseDao;
import util.UtilClass;

public class F_UserDao {
	private BaseDao baseDao = null;
	public F_UserDao(BaseDao baseDao){
		this.baseDao = baseDao;
	}
	/**
	 * 获取用户排行榜
	 * @return
	 */
	public List<Map<String,Object>> getRanking(){
		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
		String sql = "select * from game_usertable where state=0 ORDER BY money desc LIMIT 0,20";
		baseDao.executeAll(sql, null);
		try {
			while(baseDao.resultSet.next()){
				userList.add(UtilClass.utilClass.getSqlMap("/sql.properties", baseDao.resultSet, "sql_getUserRanking"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
}
