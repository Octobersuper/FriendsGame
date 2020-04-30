package com.zcf.framework.gameCenter.goldenflower.comm;


import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;

public class Matching_PK {
	/**
	 * 加入房间
	 * @param userBean
	 * @param roomno
	 * @return
	 */
	public static F_PKBean Matching(UserBean userBean, String roomno){
		F_PKBean pkbean = F_PublicState.PKMap.get(roomno);
		pkbean.getReady_lock().lock();
		//当前房间用户数量未满
		if(pkbean.getGame_userList(0).size()<6){
			pkbean.getGame_userList(0).add(userBean);
			//添加自己的座位
			pkbean.setUser_positions(userBean);
			//初始化用户
			userBean.Initialization();
			pkbean.getReady_lock().unlock();
			return pkbean;
		}
		pkbean.getReady_lock().unlock();
		return null;
		
	}
}
