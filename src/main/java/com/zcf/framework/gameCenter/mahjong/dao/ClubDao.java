package com.zcf.framework.gameCenter.mahjong.dao;


import util.BaseDao;

import java.sql.SQLException;

public class ClubDao {
	private BaseDao baseDao;

	public ClubDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	/**
	 * 查询茶馆钻石数量
	 * @param circleid
	 * @return
	 */
	public int getClub_diamond(int circleid){
		String sql = "select diamond from game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[]{circleid});
		try {
			if(baseDao.resultSet.next()){
				return baseDao.resultSet.getInt("diamond");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * 扣除圈主钻石
	 * @param circleid
	 * @param diamond
	 * @param type
	 * @return
	 */
	public int Update_Club_Money(int circleid,int diamond,int type){
		String sql = "update user_table set diamond = diamond - ? where userid = (SELECT userid from game_card_circle " +
				"where circlenumber = ?)\n";
		String state = baseDao.executeUpdate(sql, new Object[]{diamond,circleid});
		return state.equals("success")?0:-1;
	}
}
