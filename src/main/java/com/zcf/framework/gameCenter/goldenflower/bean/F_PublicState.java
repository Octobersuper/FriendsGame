package com.zcf.framework.gameCenter.goldenflower.bean;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.zcf.framework.gameCenter.goldenflower.dao.F_GameDao;
import com.zcf.framework.gameCenter.goldenflower.service.F_WebSocket;
import com.zcf.framework.gameCenter.goldenflower.util.CreaWindow;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import util.BaseDao;
import util.UtilClass;

public class F_PublicState {
	// 客户端的线程池
	public static Map<String, F_WebSocket> clients = new ConcurrentHashMap<String, F_WebSocket>();
	// 客户端的房间
	public static Map<String, F_PKBean> PKMap = new LinkedHashMap<String, F_PKBean>();
	//windows窗口
	//public static CreaWindow window = new CreaWindow();
	public static int time=10;
	// 是否开启记录
	public static boolean record_bool = false;
	static {
		// 启动窗口组件
		//window.CreateWindow();
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:
	 *@ Params:亲友圈在线人数
	 *@ Description:
	 *@ Return:
	 *@ Date:2020/4/13
	 */
	public static int selectOnlineNum(Object circlenumber) {
		return 0;
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:
	 *@ Params:亲友圈游戏中桌数
	 *@ Description:
	 *@ Return:type 1游戏中桌数  2等待桌数
	 *@ Date:2020/4/13
	 */
	public static int getGameing(Object circlenumber,int type) {
		int num = 0;
		for(Map.Entry<String, F_PKBean> entry : PKMap.entrySet()){
			F_PKBean f_pkBean = entry.getValue();
			if(String.valueOf(f_pkBean.getNumber_3()).equals(circlenumber)){
				if(type==1){
					if(f_pkBean.getGame_number()>=1){
						num++;
					}
				}else{
					if(f_pkBean.getGame_number()<1){
						num++;
					}
				}
			}
		}
		return num;
	}

	/**
	 * @ Author:ZhaoQi
	 * @ methodName:查询亲友圈在线人数
	 * @ Params:
	 * @ Description:
	 * @ Return:
	 * @ Date:2019/11/22
	 */
	public static List<Map> getAlinenum(UserBean u) {
		BaseDao baseDao = new BaseDao();
		F_GameDao gameDao = new F_GameDao(baseDao);
		List<Map> allUsers = gameDao.getAllUsers(u);
		for (Map user :
				allUsers) {
			if (clients.get(user.get("openid").toString())!=null) {
				if (clients.get(user.get("openid").toString()).userBean.getUser_type() == 0) {
					user.put("line_state", 1);//大厅中可邀请
				} else {
					user.put("line_state", 2);//游戏中
				}
			}else if(Public_State.clients.get(user.get("openid").toString())!=null){
				if (Public_State.clients.get(user.get("openid").toString()).userBean.getHu_state() == -1) {
					user.put("line_state", 1);//大厅中可邀请
				} else {
					user.put("line_state", 2);//游戏中
				}
			}else{
				user.put("line_state", 0);//不在线
			}
		}
		baseDao.CloseAll();
		baseDao = null;
		gameDao = null;
		return allUsers;
	}
}
