package com.zcf.framework.gameCenter.goldenflower.service;

import com.zcf.framework.gameCenter.goldenflower.bean.F_PKBean;
import com.zcf.framework.gameCenter.goldenflower.bean.F_PublicState;
import com.zcf.framework.gameCenter.goldenflower.bean.UserBean;
import util.System_Mess;

import java.util.HashMap;
import java.util.Map;


public class Time_User extends Thread {
	UserBean userBean;
	F_PKBean pkBean;
	int time;
	F_GameService gs;
	public Time_User(UserBean userBean,F_PKBean pkBean,F_GameService gs,int time){
		this.userBean = userBean;
		this.pkBean = pkBean;
		this.time = time;
		this.gs = gs;
		pkBean.setTime(time);
	}
	@Override
	public void run() {
		while(true){
			System.out.println(userBean.getNickname()+"操作时间剩余："+time);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			time--;
			pkBean.setTime(pkBean.getTime()-1);
			//代表已经操作
			if(userBean.getBrandstatus()!=0){
				System.out.println("用户已经操作");
				break;
			}
			//自动操作弃牌
			if(time==0){
				//避免重复触发
				if(pkBean.getOperation_userid()==userBean.getUserid()){
					System.out.println("系统帮用户弃牌了");
					Map<String,Object> returnMap = new HashMap<String,Object>();
					returnMap.put("type", "giveup");
					returnMap.put("userid",userBean.getUserid());
					F_WebSocket socket = F_PublicState.clients.get(String.valueOf(userBean.getOpenid()));
					//弃牌
					userBean.setBrandstatus(2);
					//不可游戏
					userBean.setGametype(-1);
					//检测是否结算
					int state = gs.NewGame(userBean, pkBean);
					System_Mess.system_Mess.ToMessagePrint("(弃牌)房间状态"+state);
					if(state==-1){
						//找下一个操作人
						int nextUserid = pkBean.getNextUserId();
						returnMap.put("nextid", nextUserid);
						returnMap.put("time", F_PublicState.time);
						//为操作人启动一个计时器
						new Time_User(pkBean.getUserBean(nextUserid), pkBean,gs,F_PublicState.time).start();
						socket.sendMessageTo(returnMap);
						socket.sendMessageTo(returnMap, pkBean);
					}else{
						//开牌
						if(state==809){//金币不足
							//结算0结算成功201=和局
							state = gs.OpenBrand(pkBean);
						}
						//结算
						if(state==0){
							returnMap.put("type", "end_game");
							returnMap.put("state", "0");
							returnMap.put("game_type", 1);
							//胜利人id-胜利人的money-用户手里的牌型
							pkBean.getPKBean_Custom("userid-user_brand_type", "game_userList", returnMap);
							returnMap.put("victoryid", pkBean.getVictoryid());
							returnMap.put("money", pkBean.getUserBean(pkBean.getVictoryid()).getMoney());
						}else if(state==202){
							//弃牌胜利
							returnMap.put("type", "end_game");
							returnMap.put("state", "0");
							returnMap.put("game_type", 1);
							returnMap.put("victoryid", pkBean.getVictoryid());
							returnMap.put("money", pkBean.getUserBean(pkBean.getVictoryid()).getMoney());
						}
						//和局
						if(state==201){
							System.out.println("和局");
						}
						socket.sendMessageTo(returnMap);
						socket.sendMessageTo(returnMap, pkBean);

						pkBean.Initialization();
					}
				}
				break;
			}
		}
	}
}
