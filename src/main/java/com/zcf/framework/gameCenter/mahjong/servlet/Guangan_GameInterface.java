package com.zcf.framework.gameCenter.mahjong.servlet;

import com.google.gson.Gson;
import com.zcf.framework.gameCenter.mahjong.bean.UserBean;
import com.zcf.framework.gameCenter.mahjong.dao.M_GameDao;
import com.zcf.framework.gameCenter.mahjong.dao.M_LoginDao;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import com.zcf.framework.gameCenter.mahjong.service.Mahjong_Socket;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import util.BaseDao;
import util.UtilClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前端接口
 * 
 * @author Administrator
 *
 */
@WebServlet("/GameInterface")
public class Guangan_GameInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> returnMap = new HashMap<String, Object>();
	private Gson gson = new Gson();

	public Guangan_GameInterface() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 输出转码
		response.setContentType("text/json;charset=UTF-8");
		// 接受转码
		request.setCharacterEncoding("UTF-8");
		String type = request.getParameter("type");
		/* 允许跨域的主机地址 */
		response.setHeader("Access-Control-Allow-Origin", "*");
		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		response.setHeader("Access-Control-Allow-Methods", "*");
		/* 重新预检验跨域的缓存时间 (s) */
		response.setHeader("Access-Control-Max-Age", "3600");
		/* 允许跨域的请求头 */
		response.setHeader("Access-Control-Allow-Headers", "*");
		/* 是否携带cookie */
		response.setHeader("Access-Control-Allow-Credentials", "true");
		BaseDao baseDao = new BaseDao();
		// 接口
		M_GameDao gameDao = new M_GameDao(baseDao);
		// 接收参数(解密后返回)
		Map<String, Object> requestmap = null;
		returnMap.clear();
		if (type != null) {
			// 接收参数(解密后返回)
			requestmap = UtilClass.utilClass.getRequestAdd(request, "/request.properties", type);
			System.out.println(requestmap.toString());
			// 检测参数是否合格(不合格则会更改type的值并增加返回map错误提示error)
			UtilClass.utilClass.isRequest("/request.properties", type, requestmap, returnMap);
			if (returnMap.get("error") != null) {
				type = "";
			}
		} else {
			returnMap.put("error", "参数错误");
			returnMap.put("state", "-1");
			type = "";
		}
		// 登录
		if ("login".equals(type)) {
			M_LoginDao loginDao = new M_LoginDao(baseDao);
			Map<String, Object> map = new HashMap<String, Object>();
			UserBean userBean = loginDao.getUser(requestmap);
			if (userBean == null) {
				// 添加此用户
				String state = loginDao.adduser(requestmap);
				if (!state.equals("success")) {
					// 记录错误日志
					// 异常
					returnMap.put("state", "999");
				} else {
					// 重新查询此用户
					userBean = loginDao.getUser(requestmap.get("openid").toString());
				}
			}
			if (userBean.getState() == 1) {
				// 账号被封
				map.put("state", "101");
				returnMap.put("user", map);
			} else {
				//删除三天前战绩记录
				gameDao.delRecord();
				returnMap.put("state", "0");
				userBean.getUser_Custom("nickname-avatarurl-userid-diamond-openid-sex-money", returnMap);
				if(Public_State.clients.get(userBean.getOpenid())!=null){
					returnMap.put("state", "320"); //重复登录
				}
				// 检测是否需要重连
				String roomno = Public_State.ISUser_Room(userBean.getUserid());
				// 加入登陆日志
				// loginDao.addLogin_Log(userBean.getUserid());
				// 当天首次登陆领取日常任务
				//loginDao.addDaily_User(userBean.getUserid());
				// 需要重连
				if (roomno != null) {
					returnMap.put("state", "310");
					returnMap.put("roomno", roomno);
				}
				System.out.println(returnMap);
			}
		}
		// 查看玩家信息
		if ("getUser".equals(type)) {
			returnMap.put("type_s", type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			Map<String, Object> user = gameDao.getUserById(userid);
			returnMap.put("UserBean", user);
			returnMap.put("user_state", user==null?0:1);
			returnMap.put("state", "-1");
		}
		// 查看玩家房卡
		if ("getUserDiamond".equals(type)) {
			returnMap.put("type_s",type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("UserBean", gameDao.getUserDiamond(userid));
			returnMap.put("state", "1001");
		}
		// 查看游戏规则1
		if ("ckgui".equals(type)) {
			String gui_type = request.getParameter("gui_type");
			returnMap.put("type_s", type);
			returnMap.put("ckgui", gameDao.ckgui(gui_type));
			returnMap.put("state", "-1");
		}

		/******************************* 公告 ****************************/
		// 查看公告1
		if ("ckgong".equals(type)) {
			returnMap.put("type_s",type);
			returnMap.put("ckgong", gameDao.ckgong());
			returnMap.put("state", "-1");
		}
		/************************** 商城 **********************************/

		// 查看金币商品1
		if ("ckjin".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckjin", gameDao.ckjin());
		}

		// 查看钻石商品1
		if ("ckzuan".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckzuan", gameDao.ckzuan());
			//returnMap.put("diamond", gameDao.getUserDiamond(Integer.parseInt(request.getParameter("userid"))));
			returnMap.put("state", "-1");
		}

		// 查看客服信息1
		if ("ckservice".equals(type)) {
			returnMap.put("type_s",type);
			returnMap.put("ckservice", gameDao.ckservice());
			returnMap.put("state", "-1");
		}

		/************************* 俱乐部 **********************************/

		// 创建牌友圈--参数: 牌友圈名称,用户id
		if ("createClub".equals(type)) {
			returnMap.put("type_s", type);
			String circlename = request.getParameter("circlename");
			int userid = Integer.parseInt(request.getParameter("userid"));
			int game_type = Integer.parseInt(request.getParameter("game_type"));
			returnMap.put("state", gameDao.createClub(circlename, userid,game_type));
		}

		// 查看已加入的牌友圈列表1
		if ("ckclub".equals(type)) {
			returnMap.put("type_s", "ckclub");
			int userid = Integer.parseInt(request.getParameter("userid"));
			int game_type = Integer.parseInt(request.getParameter("game_type"));
			returnMap.put("ckclub", gameDao.ckclub(userid,game_type));
			returnMap.put("state", "-1");
		}

		// 申请加入牌友圈1
		if ("joinCircleid".equals(type)) {
			returnMap.put("type_s", type);
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("state", gameDao.joinCircleid(userid, circlenumber));
		}

		// 圈主查看俱乐部申请
		if ("circleApplication".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("state", "-1");
			returnMap.put("circleApplication",
					gameDao.circleApplication(Integer.parseInt(request.getParameter("circlenumber")),
							Integer.parseInt(request.getParameter("userid"))));
		}

		// 同意加入
		if ("passjoinCard".equals(type)) {
			returnMap.put("state",
                    type+gameDao.passjoinCard(
                            Integer.parseInt(request.getParameter("userid")),
							Integer.parseInt(request.getParameter("applyid")),
							Integer.parseInt(request.getParameter("circlenumber"))));
            returnMap.put("type_s", type);
		}

		// 拒绝加入
		if ("downjoinCard".equals(type)) {
			returnMap.put("state", type+gameDao.downjoinCard(Integer.parseInt(request.getParameter("applyid"))));
            returnMap.put("type_s", type);
		}

		// 查看牌友圈的成员
		if ("selectcarduser".equals(type)) {
			returnMap.put("type_s", type);
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			returnMap.put("selectcardUser", gameDao.selectcarduser(circlenumber));
			returnMap.put("state", "-1");
		}
		// 设置管理员
		if ("setRole".equals(type)) {
			returnMap.put("type_s", type);
			int id = Integer.parseInt(request.getParameter("id"));
			gameDao.setRole(id);
			returnMap.put("state", "-1");
		}
        // 踢出俱乐部
        if ("downcricle".equals(type)) {
            // 牌友-俱乐部绑定id
            int id = Integer.parseInt(request.getParameter("id"));
            // 自身id
            int userid = Integer.parseInt(request.getParameter("userid"));
            // 俱乐部编号
            int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
            returnMap.put("state", gameDao.downcricle(id, userid, circlenumber));
            returnMap.put("type_s", "-1");
        }
		// 退出俱乐部0
		if ("exitClub".equals(type)) {
			int userid = Integer.parseInt(request.getParameter("userid"));
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			returnMap.put("state", gameDao.exitClub(userid, circlenumber));
			returnMap.put("type_s", "exitClub");
		}
		/********************************** 我的战绩 *****************************************/
		// 查看战绩列表
		if ("selectPK".equals(type)) {
			int userid = Integer.valueOf(request.getParameter("userid"));
			int record_type = Integer.valueOf(request.getParameter("record_type"));//0普通战绩 1俱乐部战绩
			int game_type = Integer.valueOf(request.getParameter("game_type"));//0麻将 1炸金花
			List<Map<String, Object>> list = gameDao.getRecordRoom(userid,record_type,game_type);
			returnMap.put("type_s",type);
			returnMap.put("state","-1");
			returnMap.put("selectPK",list);
		}

		baseDao.CloseAll();
		String json = gson.toJson(returnMap).toString();
		System.out.println(json);
		response.getWriter().println(json);
	}



	public void init() throws ServletException {
	}
}