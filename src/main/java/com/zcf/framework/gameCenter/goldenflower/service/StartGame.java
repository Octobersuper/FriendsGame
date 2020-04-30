package com.zcf.framework.gameCenter.goldenflower.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import com.zcf.framework.gameCenter.goldenflower.dao.F_GameDao;
import util.BaseDao;
/***
 * 房间自动检测开始
 * @author Administrator
 *
 */
public class StartGame extends Thread {
	F_PKBean pkBean;
	F_GameService gs;
	public StartGame(F_PKBean pkBean,F_GameService gs){
		this.pkBean=pkBean;
		this.gs = gs;
	}
	@Override
	public void run() {
		while(true){
			//每一秒检测一次
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//如果当前房间人数有2人已经就位就开始游戏
			int count = 0;
			for(UserBean userBean:pkBean.getGame_userList(0)){
				//初始状态
				if(userBean.getGametype()==0){
					count++;
				}
			}
			if(count>1){
				Map<String,Object> returnMap = new HashMap<String, Object>();
				//延迟5秒开始游戏
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				pkBean.setRoom_num(pkBean.getRoom_num()+1);
				pkBean.setRoom_state(1);
				//发牌
				pkBean.GrantBrand(3);
				//如果没有胜利得人则随机指定
				if(pkBean.getVictoryid()==0){
					int index = (int)(Math.random() * (pkBean.getGame_userList(0).size()));
					pkBean.setBanker_id(pkBean.getGame_userList(0).get(index).getUserid());
				}else{
					//庄家为上一把胜利人得id
					//判断上一把胜利得是否存在
					boolean bool = true;
					for(UserBean user:pkBean.getGame_userList(0)){
						user.setGametype(1);
						if(user.getUserid()==pkBean.getVictoryid()){
							bool=false;
							pkBean.setBanker_id(pkBean.getVictoryid());
							break;
						}
					}
					if(bool){
						pkBean.setBanker_id(pkBean.getGame_userList(2).get(0).getUserid());
					}
				}
				//开局每人扣除底注数
				BaseDao baseDao = new BaseDao();
				pkBean.DeductionBottom_Pan(new F_GameDao(baseDao));
				baseDao.CloseAll();
				//加入开局信息底注-加注数
				pkBean.getPKBean_Custom("userid-brand-money","bets-zbets-time-game_userList-operation_userid-bottom_pan-room_num",returnMap);
				for(UserBean userBean:pkBean.getGame_userList(0)){
					F_WebSocket websocket = F_PublicState.clients.get(userBean.getOpenid());
					returnMap.put("type", "startgame");
					returnMap.put("state","0");
					returnMap.put("game_type",1);
					if(userBean.getUserid()==pkBean.getOperation_userid()){
						//为操作人启动一个计时器
						//new Time_User(userBean, pkBean,gs,F_PublicState.time).start();
					}
					websocket.sendMessageTo(returnMap);
				}
				System.out.println("开始游戏跳出线程");
				break;
			}
		}
	}
}
