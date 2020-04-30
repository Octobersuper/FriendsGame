package com.zcf.framework.gameCenter.mahjong.dao;


import util.BaseDao;
import util.UtilClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class M_UserDao {
	private BaseDao baseDao = null;
	public M_UserDao(BaseDao baseDao){
		this.baseDao = baseDao;
	}
	/**
	 * 获取用户排行榜
	 * @return
	 */
	public List<Map<String,Object>> getRanking(){
		List<Map<String,Object>> userList = new ArrayList<Map<String,Object>>();
		String sql = "select * from user_table where state=0 ORDER BY money desc LIMIT 0,20";
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
