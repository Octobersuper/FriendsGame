package com.zcf.framework.gameCenter.goldenflower.comm;


import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import util.UtilClass;

import java.util.concurrent.locks.ReentrantLock;


public class Establish_PK {
	/**
	 * 创建房间
	 * 
	 * @param userBean
	 */
	public static synchronized F_PKBean Establish(UserBean userBean, int room_type,int clubid) {
		String roomNo = "";
		//判断准入金币是否合格
		int fen = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "fen"+room_type));
		//用户金币
		while(true){
			for(int i=0;i<6;i++){
				roomNo+=(int)(Math.random() * 10);
			}
			if(F_PublicState.PKMap.get(roomNo) == null){
				//初始化用户
				userBean.Initialization();
				userBean.setUser_type(1);
				F_PKBean pkBean = new F_PKBean();
				pkBean.getGame_userList(1).add(userBean);
				pkBean.setBets(fen);
				pkBean.setZbets(fen);
				pkBean.setNumber_3(clubid);
				//初始化用户位置
				pkBean.setUser_positions(new int[]{userBean.getUserid(),-1,-1,-1,-1,-1,-1,-1});
				//房间类型
				pkBean.setRoom_type(room_type);
				// 放入房间号
				pkBean.setRoom_number(roomNo);
				//准备锁
				pkBean.setReady_lock(new ReentrantLock(true));
				// 将房间实例放入房间map
				F_PublicState.PKMap.put(roomNo, pkBean);
				return pkBean;
			}
		}
	}
}
