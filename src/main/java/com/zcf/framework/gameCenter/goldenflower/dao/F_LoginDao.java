package com.zcf.framework.gameCenter.goldenflower.dao;

import java.sql.SQLException;
import java.util.Map;

import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import util.BaseDao;
import util.UtilClass;

public class F_LoginDao{
	private BaseDao baseDao = null;
	public F_LoginDao(BaseDao baseDao){
		this.baseDao = baseDao;
	}
	/**
     * 查询用户
     * @param openid
     * @param bool
     * @return
     */
    public UserBean getUser(int userid){
    	String sql = "select * from user_table where userid=?";
    	baseDao.executeAll(sql, new Object[]{userid});
		try {
			if(baseDao.resultSet.next()){
				return (UserBean) UtilClass.utilClass.getSqlBean(UserBean.class,"/sql.properties", baseDao.resultSet, "sql_getUser");
			}
		} catch (SQLException e) {
    		System.out.println(e.getMessage());
		}
		return null;
    }
    /**
     * 查询用户
     * @param openid
     * @param bool
     * @return
     */
    public UserBean getUser(String openid){
    	String sql = "select * from user_table where openid=?";
    	baseDao.executeAll(sql, new Object[]{openid});
		try {
			if(baseDao.resultSet.next()){
				return (UserBean) UtilClass.utilClass.getSqlBean(UserBean.class,"/sql.properties", baseDao.resultSet, "sql_getUser");
			}
		} catch (SQLException e) {
			//添加错误日志
    		//LogDao.logDao.addLog_Error(openid, "登陆用户异常",e.getMessage());
			UserBean userBean = new UserBean();
			userBean.setUserid(-1);
			return userBean;
		}
		return null;
    }
    /**
     * 修改用户昵称及头像
     * @param map
     * @param bool
     * @return
     */
    public String updateUser(UserBean userBean){
    	String sql = "update user_table set nickname=?,avatarurl=? where openid=?";
		return baseDao.executeUpdate(sql, new Object[]{
				userBean.getNickname(),
				userBean.getAvatarurl(),
				userBean.getOpenid()
		});
    }
    /**
	 * 添加用户
	 * @return
	 */
	public String adduser(Map<String,Object> userMap) {
		String sql="insert into game_usertable(openid,nickname,avatarurl,date,sdk,money,state,statetext,pay)values(?,?,?,NOW(),?,?,0,'',0) ";
		return baseDao.executeUpdate(sql, new Object[] { 
				userMap.get("openid").toString(),
				userMap.get("nickname").toString(),
				userMap.get("avatarurl").toString(),
				userMap.get("sdk").toString(),
				Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "userMoney_Initialization"))
				
				});
	}
	
	/**
     * 添加用户
     * @param userBean
     * @return
     */
    public String addUser2(UserBean userBean){
    	String sql = "insert into user_table(nickname,openid,avatarurl,lv,exp,state) values(?,?,?,1,0,0) ";
    	return baseDao.executeUpdate(sql, new Object[]{userBean.getNickname(),userBean.getOpenid(),userBean.getAvatarurl()});
    }
    /**
     * 结算用户信息
     * @param userBean
     * @return
     */
    public String endUser(UserBean userBean){
    	String sql = "update user_table set lv=?,exp=? where openid=?";
    	return baseDao.executeUpdate(sql, new Object[]{
    			userBean.getOpenid()
    	});
    }

	public int getRole(int userid,int club_number) {

		String s = "select count(*) count from game_card_circle where circlenumber=? and userid = ?";
		baseDao.executeAll(s, new Object[]{club_number,userid});
		try {
			if (baseDao.resultSet.next()) {
				int count = baseDao.resultSet.getInt("count");
				if(count!=0){
					return 2;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String sql = "select * from game_card_user where circlenumber=? and userid = ?";
		baseDao.executeAll(sql, new Object[]{club_number,userid});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("role");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}
}
