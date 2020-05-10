package com.zcf.framework.gameCenter.mahjong.backservlet;

import com.google.gson.Gson;
import com.zcf.framework.gameCenter.mahjong.dao.M_GameDao;
import com.zcf.framework.gameCenter.mahjong.dao.Mg_GameDao;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import org.apache.commons.lang3.StringUtils;
import util.BaseDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zhanghu
 * @createtime 2019年5月23日 上午9:40:23
 */
@WebServlet("/admin")
public class BackUserServlet extends HttpServlet {
	private static final long serialVersionUID = -1410484180195040338L;
	private Map<String, Object> returnMap = new HashMap<String, Object>();
	private BaseDao baseDao = new BaseDao();
	private Gson gson = new Gson();

	public BackUserServlet() {
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
		String type = request.getParameter("back");
		BackUserDao backUserDao = new BackUserDao(baseDao);
		// 接口
		M_GameDao gameDao = new M_GameDao(baseDao);
		returnMap.clear();
		// 后台管理登录
		if ("login".equals(type)) {
			String account = request.getParameter("account");
			String pwd = request.getParameter("pwd");
			if (backUserDao.login(account, pwd) != null) {
				BackUserBean userbean = backUserDao.login(account, pwd);
				if(userbean.getRole()!=1){
					returnMap.put("data", "null");
				}else{
					backUserDao.insertUser(userbean, request);
					returnMap.put("data", userbean);
				}
			} else {
				returnMap.put("data", "null");
			}
		}
		// 后台代理登录
		if ("daililogin".equals(type)) {
			String account = request.getParameter("account");
			String pwd = request.getParameter("pwd");
			int role = Integer.valueOf(request.getParameter("role"));
			if (backUserDao.login(account, pwd) != null) {
				BackUserBean userbean = backUserDao.login(account, pwd);
				backUserDao.insertUser(userbean, request);
				if(userbean.getRole()!=role){
					returnMap.put("data", "null");
				}else{
					backUserDao.insertUser(userbean, request);
					returnMap.put("data", userbean);
				}
			} else {
				returnMap.put("data", "null");
			}
		}

		// 查看提现申请列表
		if ("getwithdraw".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			}
			returnMap.put("list",
					backUserDao.getwithdraw(Integer.parseInt(pageNum), request.getParameter("backuserid")));
		}
		// 拒绝提现申请
		if ("downwithdraw".equals(type)) {
			// 审核状态
			int state = Integer.parseInt(request.getParameter("state"));
			// withdrawid
			int withdrawid = Integer.parseInt(request.getParameter("withdrawid"));
			returnMap.put("status", backUserDao.downwithdraw(withdrawid, state));
		}
		// 同意提现申请
		if ("passwithdraw".equals(type)) {
			returnMap.put("status", backUserDao.passwithdraw(Integer.parseInt(request.getParameter("withdrawid")),
					Integer.parseInt(request.getParameter("state")),request.getParameter("number_3"),
					Integer.parseInt(request.getParameter("userid"))));
		}

		// 后台管理修改昵称
		if ("upuser".equals(type)) {
			returnMap.put("state", backUserDao.upuser(Integer.parseInt(request.getParameter("backuserid")),
					request.getParameter("backname")));
		}

		// 后台管理修改密码
		if ("uppwd".equals(type)) {
			returnMap.put("state", backUserDao.uppwd(Integer.parseInt(request.getParameter("backuserid")),
					request.getParameter("oldPwd"), request.getParameter("newPwd")));
		}

		/**
		 * 查看后台管理员列表
		 */
		if ("getbacklist".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			}
			returnMap.put("list",
					backUserDao.getbacklist(request.getParameter("backuserid"), Integer.parseInt(pageNum)));
		}

		/**
		 * 开启超级管理员权限
		 */
		if ("onrole".equals(type)) {
			returnMap.put("state", backUserDao.onrole(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 关闭超级管理员权限
		 */
		if ("offrole".equals(type)) {
			returnMap.put("state", backUserDao.offrole(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 开启游戏统计权限
		 */
		if ("onmenuone".equals(type)) {
			returnMap.put("state", backUserDao.onmenuone(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭游戏统计权限
		 */
		if ("offmenuone".equals(type)) {
			returnMap.put("state", backUserDao.offmenuone(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启金币统计权限
		 */
		if ("onmenutwo".equals(type)) {
			returnMap.put("state", backUserDao.onmenutwo(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭金币统计权限
		 */
		if ("offmenutwo".equals(type)) {
			returnMap.put("state", backUserDao.offmenutwo(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启用户列表
		 */
		if ("onmenuthree".equals(type)) {
			returnMap.put("state", backUserDao.onmenuthree(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭用户列表
		 */
		if ("offmenuthree".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuthree(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启充值提现记录
		 */
		if ("onmenufour".equals(type)) {
			returnMap.put("state", backUserDao.onmenuFour(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭充值提现记录
		 */
		if ("offmenufour".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuFour(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启提现申请权限
		 */
		if ("onmenufive".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onmenuFive(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭提现申请权限
		 */
		if ("offmenufive".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuFive(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启房间列表权限
		 */
		if ("onmenusix".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onmenuSix(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭房间列表权限
		 */
		if ("offmenusix".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuSix(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启俱乐部列表
		 */
		if ("onmenuseven".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onmenuSeven(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭俱乐部列表
		 */
		if ("offmenuseven".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuSeven(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启金币充值配置
		 */
		if ("onmenueight".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onmenuEight(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭金币充值配置
		 */
		if ("offmenueight".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuEight(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启房卡充值配置
		 */
		if ("onmenunine".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onmenuNine(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭房卡充值配置
		 */
		if ("offmenunine".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuNine(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启轮播图管理
		 */
		if ("onmenuten".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onmenuTen(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭轮播图管理
		 */
		if ("offmenuten".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offmenuTen(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启客服管理
		 */
		if ("oneleven".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onEleven(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭客服管理
		 */
		if ("offeleven".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offEleven(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}
		/**
		 * 开启添加管理员
		 */
		if ("onetwelve".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.onTwelve(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/**
		 * 关闭添加管理员
		 */
		if ("offtwelve".equals(type)) { // 操作用户
			returnMap.put("state", backUserDao.offTwelve(Integer.parseInt(request.getParameter("backuserids")),
					Integer.parseInt(request.getParameter("backuserid"))));
		}

		/************************************* 用户 **************************************/
		// 用户封禁
		if ("banned".equals(type)) {
			returnMap.put("status", backUserDao.banned(request.getParameter("userid"), request.getParameter("banned")));
		}
		// 用户解封
		if ("deblocking".equals(type)) {
			returnMap.put("status", backUserDao.deblocking(request.getParameter("userid")));
		}

		// 开启透视
		if ("open".equals(type)) {
			returnMap.put("status", backUserDao.open(request.getParameter("userid")));
		}
		// 关闭透视
		if ("onclose".equals(type)) {
			returnMap.put("status", backUserDao.onclose(request.getParameter("userid")));
		}

		// 用户列表
		if ("getuser".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			String userid = request.getParameter("userid");
			String nickname = request.getParameter("nickname");
			if (pageNum == null) {
				pageNum = "1";
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if (userid != null && !StringUtils.isEmpty(userid)) {
				map.put("userid", userid);
			}
			if (nickname != null && !StringUtils.isEmpty(nickname)) {
				map.put("nickname", nickname);
			}
			returnMap.put("list",
					backUserDao.getuser(Integer.parseInt(pageNum), request.getParameter("backuserid"), map));
		}
		// 充值房卡
		if ("updiamond".equals(type)) {
				returnMap.put("status", backUserDao.updiamond(request.getParameter("userid"),
						request.getParameter("diamond"), request.getParameter("zuserid")));
		}
		// 提现房卡
		if ("downdiamond".equals(type)) {
				returnMap.put("status", backUserDao.downdiamond(request.getParameter("userid"),
						request.getParameter("diamond"), request.getParameter("zuserid")));
		}

		// 充值金币
		if ("updatemoney".equals(type)) {
			returnMap.put("status", backUserDao.upmoney(request.getParameter("userid"), request.getParameter("money"),
					request.getParameter("zuserid"), request.getParameter("nickname")));
		}
		// 提现金币
		if ("downmoney".equals(type)) {
			returnMap.put("status", backUserDao.downmoney(request.getParameter("userid"), request.getParameter("money"),
					request.getParameter("zuserid"), request.getParameter("nickname")));
		}

		// 查看充值提现记录
		if ("getrechargerecord".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			}
			String zuserid = request.getParameter("zuserid");
			String start_time = request.getParameter("start_time");
			returnMap.put("list", backUserDao.getrechargerecord(Integer.parseInt(pageNum),
					request.getParameter("backuserid"), zuserid, start_time));
		}

		// 查看积分变动记录
		if ("getmoneyrecord".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			}
			String zuserid = request.getParameter("zuserid");
			String start_time = request.getParameter("start_time");
			returnMap.put("list", backUserDao.getmoneyrecord(Integer.parseInt(pageNum),
					request.getParameter("backuserid"), zuserid, start_time));
		}


		// 授权
		if ("author".equals(type)) {
			returnMap.put("status", backUserDao.author(request.getParameter("userid")));
		}
		// 取消授权
		if ("delauthor".equals(type)) {
			returnMap.put("status", backUserDao.delauthor(request.getParameter("userid")));
		}

		/**************************** 商城 *********************************/

		// 查看房卡商店信息
		if ("getdiamondshop".equals(type)) {
			returnMap.put("list", backUserDao.getdiamondshop(request.getParameter("backuserid")));
		}
		// 查看房卡商店信息
		if ("getmoneyshop".equals(type)) {
			returnMap.put("list", backUserDao.getmoneyshop(request.getParameter("backuserid")));
		}
		// 修改房卡商品
		if ("updmoney".equals(type)) {
			returnMap.put("state",
					backUserDao.delmoney(Integer.parseInt(request.getParameter("moneyid")),
							request.getParameter("moneyname"), request.getParameter("realvalue"),
							Integer.parseInt(request.getParameter("price"))));
		}

		// 修改房卡商品
		if ("upddiamond".equals(type)) {
			returnMap.put("state",
					backUserDao.deldiamond(Integer.parseInt(request.getParameter("diamondid")),
							request.getParameter("diamondname"), request.getParameter("realvalue"),
							Integer.parseInt(request.getParameter("price"))));
		}

		if ("getmoneyshop".equals(type)) {
			returnMap.put("list", backUserDao.getmoneyshop(request.getParameter("backuserid")));
		}
		// 修改金币商品
		if ("updmoney".equals(type)) {
			returnMap.put("state",
					backUserDao.updmoney(Integer.parseInt(request.getParameter("moneyid")),
							request.getParameter("moneyname"), request.getParameter("realvalue"),
							Integer.parseInt(request.getParameter("price"))));
			BaseDao baseDao = new BaseDao();
			Mg_GameDao asd = new Mg_GameDao(baseDao);
			asd.getConfig();
			//读取配置
			baseDao.CloseAll();
		}

		/************************************* 房间管理 **************************************************/
		// 根据id获得房间信息
		if ("getroomone".equals(type)) {
			String roomnum = request.getParameter("roomno");
			String pageNum = request.getParameter("pageNum");

			if (pageNum == null) {
				pageNum = "1";
			}
			returnMap.put("list",
					backUserDao.getroom(Integer.parseInt(pageNum), request.getParameter("backuserid"), roomnum));
		}
		// 对战房间列表
		if ("getroom".equals(type)) {
			String roomnum = request.getParameter("roomnum");
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			}
			returnMap.put("list",
					backUserDao.getroom(Integer.parseInt(pageNum), request.getParameter("backuserid"), roomnum));
		}
		// 对战房间玩家列表
		if ("getrecorduser".equals(type)) {
			returnMap.put("list", backUserDao.getrecorduser(request.getParameter("roomo")));
			returnMap.put("brands", Public_State.PKMap.get(request.getParameter("roomo")).getBrands());
		}
		// 修改下一张牌
		if ("updatebrand".equals(type)) {
			String roomnum = request.getParameter("roomo");
			int pindex = Integer.parseInt(request.getParameter("index"));
			System.out.println(">>>>>>>>>>>>>>>>>指定的牌值为:"+pindex);
			returnMap.put("state", backUserDao.updatepai(roomnum, pindex));
		}

		/************************************* 俱乐部管理 **************************************************/
		// 获得俱乐部
		if ("getcard".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			String circlenumber = "null".equals(request.getParameter("circlenumber")) ? null
					: request.getParameter("circlenumber");
			if (pageNum == null) {
				pageNum = "1";
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if (circlenumber != null && !StringUtils.isEmpty(circlenumber)) {
				map.put("circlenumber", circlenumber);
			}
			returnMap.put("list",
					backUserDao.getcard(Integer.parseInt(pageNum), map, request.getParameter("backuserid")));
		}
		// 查看俱乐部的用户信息
		if ("lookCircle".equals(type)) {
			String circlenumber = request.getParameter("circlenumber");
			returnMap.put("list", backUserDao.lookCircle(circlenumber));
		}

		// 获取公告
		if ("getnotice".equals(type)) {
			returnMap.put("list", backUserDao.getnotice(request.getParameter("backuserid")));
		}
		// 修改公告
		if ("updatenotice".equals(type)) {
			returnMap.put("status", backUserDao.updnotice(Integer.parseInt(request.getParameter("noticeid")),
					request.getParameter("value")));
		}

		if ("refash".equals(type)) {
			returnMap.put("date", backUserDao.refash(request.getParameter("backuserid")));
		}
		// 查看消耗房卡数量
		if ("getfreediamond".equals(type)) {
			returnMap.put("list", backUserDao.getfreediamond(request.getParameter("backuserid")));
		}
		// 修改
		if ("updfreediamond".equals(type)) {
			String establish_two = request.getParameter("tf") + "-" + request.getParameter("te");
			String establish_four = request.getParameter("ff") + "-" + request.getParameter("fe");
			String configid = request.getParameter("configid");
			returnMap.put("state", backUserDao.updfreediamond(establish_two, establish_four,configid));
			gameDao.getConfig();
		}
		// 修改
		if ("updfreediamond2".equals(type)) {
			String establish_zjh = request.getParameter("ff") + "-" + request.getParameter("fe") + "-" + request.getParameter("fst");
			String configid = request.getParameter("configid");
			returnMap.put("state", backUserDao.updfreediamond2(establish_zjh,configid));
			gameDao.getConfig();
		}


		/************************** 后台管理列表 ****************************/

		// 查看用户权限
		if ("getbacksystemlist".equals(type)) {
			returnMap.put("list", backUserDao.getbacksystem(request.getParameter("backuserid")));
		}

		// 获取后台用户列表
		if ("selectBackUser".equals(type)) {
			String buserid = request.getParameter("backuserid");
			if(!buserid.equals("1")){
				returnMap.put("list", backUserDao.selectBackUser2(buserid));
			}else{
				returnMap.put("list", backUserDao.selectBackUser(buserid));
			}
		}
		// 删除后台用户
		if ("delsystemuser".equals(type)) {
			returnMap.put("state", backUserDao.delsystemuser(Integer.parseInt(request.getParameter("sysuserid")),
					Integer.parseInt(request.getParameter("userid"))));
		}
		// 添加一级代理
		if ("addsysuser".equals(type)) {
			returnMap.put("state", backUserDao.addsysuser(request.getParameter("name"), request.getParameter("account"),
					request.getParameter("pwd"), request.getParameter("backuserid")));
		}
		// 添加二级代理
		if ("addsysuser2".equals(type)) {
			returnMap.put("state", backUserDao.addsysuser2(request.getParameter("name"), request.getParameter("account"),
					request.getParameter("pwd"), request.getParameter("backuserid")));
		}
		// 后台游戏统计用户管理
		if ("selectuser".equals(type)) {
			returnMap.put("list", backUserDao.selectperson(request.getParameter("backuserid")));
		}
		// 金钱统计
		if ("getrechargeinfo".equals(type)) {
			returnMap.put("list",
					backUserDao.getgamerecord(request.getParameter("backuserid"), request.getParameter("start_time")));
		}

		// 查看登录日志
		if ("loginlog".equals(type)) {
			String pageNum = request.getParameter("pageNum");
			if (pageNum == null) {
				pageNum = "1";
			}
			Integer pagenum = Integer.parseInt(pageNum);
			returnMap.put("list", backUserDao.getlogin(request, request.getParameter("backuserid"), pagenum));
		}

		baseDao.CloseAll();
		String returnjson = gson.toJson(returnMap).toString();
		System.out.println("返回的json数据为:" + returnjson);
		response.getWriter().print(returnjson);

	}

	public void init() throws ServletException {
	}

}
