package com.zcf.framework.gameCenter.mahjong.service;

import com.zcf.framework.gameCenter.mahjong.bean.RoomBean;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;

import java.util.HashMap;
import java.util.Map;

public class Exit_Game extends Thread {
	private RoomBean roomBean;
    private UserBean userBean;
    private int time = 0;
    public Exit_Game(UserBean userBean,RoomBean roomBean) {
		this.userBean = userBean;
		this.roomBean = roomBean;
		// 倒计时时间
		this.time =Public_State.time;
            start();
    }

	public void updateUser(UserBean userBean){
        this.time =Public_State.time;
        this.userBean=userBean;
    }
	public void closeRoomBean(){
		this.roomBean=null;
	}
	@Override
	public void run() {
		boolean bool = false;
		while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			if (userBean==null) {
				//System.out.println(">>>>>>>>>等待");
			        continue;
			}
			if (roomBean==null){
				break;
			}
            this.time--;
           // System.out.println(userBean.getNickname()+"倒計時>>>>"+time);
			if (this.time == 0) {
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("type", "exit_all");
				for (UserBean user : roomBean.getGame_userlist()) {
					Mahjong_Socket webSocket = Public_State.clients.get(user.getOpenid());
					if(webSocket!=null){
						//发送给所有人
						webSocket.sendMessageTo(returnMap,roomBean);
					}
				}
				bool = true;
				break;
			}
		}
		if (bool) {
			System.out.println("剔除房间");
			Public_State.PKMap.remove(roomBean.getRoomno());
		}
	}
}