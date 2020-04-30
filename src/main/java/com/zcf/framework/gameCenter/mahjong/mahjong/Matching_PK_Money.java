package com.zcf.framework.gameCenter.mahjong.mahjong;


import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;

public class Matching_PK_Money {
	/**
	 * 加入房间
	 * @param userBean
	 * @param roomno
	 * @return
	 */
	public static RoomBean Matching(UserBean userBean, String roomno){
		RoomBean roomBean = Public_State.PKMap.get(roomno);
		roomBean.getLock().lock();
		//房间是准备状态
		if(roomBean.getState()==0){
			//当前房间用户数量未满
			if(roomBean.getGame_userlist().size()<roomBean.getMax_person()){
				roomBean.getGame_userlist().add(userBean);
				//加入用户位置
				roomBean.setUser_positions(userBean.getUserid());
				//初始化用户
				userBean.Initialization();
				roomBean.getLock().unlock();
				return roomBean;
			}
		}
		roomBean.getLock().unlock();
		return null;
	}
}
