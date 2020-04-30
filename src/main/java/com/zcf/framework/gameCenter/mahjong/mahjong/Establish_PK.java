package com.zcf.framework.gameCenter.mahjong.mahjong;


import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;

public class Establish_PK {
	/**
	 * 创建房间
	 * 
	 */
	public static synchronized RoomBean Establish() {
		String roomNo = "";
		while (true) {
			for (int i = 0; i < 6; i++) {
				roomNo += (int) (Math.random() * 10);
			}
			if (Public_State.PKMap.get(roomNo) == null) {
				RoomBean roomBean = new RoomBean();
				// 放入房间号
				roomBean.setRoomno(roomNo);
				roomBean.setVictoryid(-1);
				// 将房间实例放入房间map
				Public_State.PKMap.put(roomNo, roomBean);
				return roomBean;
			}
		}
	}
}