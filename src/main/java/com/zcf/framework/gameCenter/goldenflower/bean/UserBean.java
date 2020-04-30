package com.zcf.framework.gameCenter.goldenflower.bean;

import com.zcf.framework.gameCenter.goldenflower.util.Util_Brand;

import java.util.HashMap;
import java.util.Map;



/**
 * 用户类
 * 
 * @author Administrator
 *
 */
public class UserBean {
	private int userid;
	private String nickname;// 微信昵称
	private String openid;// 微信唯一标示
	private String avatarurl;// 微信头像
	private String date;// 注册时间
	private int sdk = 0;// 渠道表示
	private int state = 0;// 状态0正常1封号
	private String statetext;// state=1时有值
	private int gametype = -1;// 游戏中的状态0默认1游戏中-1不可游戏
	private String lat;
	private String log;
	private int money;// 金币
	private int bets;// 下注
	private int[] brand;// 手里的牌
	private int brandtype = 1;//2=明牌
	private int brandstatus=0;// 操作状态默認0
	private int winnum;//当前局数的输赢分数
	private String[] brand_custom;//自定义牌型
	private Util_Brand util_brand;//牌型算法
	private int user_brand_type;//用户的牌型
	private int q_id;//亲友圈id
	private int number_3;//亲友圈号码
	private int gps;//当前所在位置
	private String log_lat;//经纬度
	private int user_type;//0观战 1游戏
	private int win_number;//截止目前输赢金额
	private String dttype;//当前所在场次 1 2 3 初中高级场

	public String getDttype() {
		return dttype;
	}

	public void setDttype(String dttype) {
		this.dttype = dttype;
	}

	public int getWin_number() {
		return win_number;
	}

	public void setWin_number(int win_number) {
		this.win_number = win_number;
	}

	public int getUser_type() {
		return user_type;
	}

	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}

	public String getLog_lat() {
		return log_lat;
	}

	public void setLog_lat(String log_lat) {
		this.log_lat = log_lat;
	}

	public int getGps() {
		return gps;
	}

	public void setGps(int gps) {
		this.gps = gps;
	}

	public int getQ_id() {
		return q_id;
	}

	public void setQ_id(int q_id) {
		this.q_id = q_id;
	}

	public int getNumber_3() {
		return number_3;
	}

	public void setNumber_3(int number_3) {
		this.number_3 = number_3;
	}

	/***
	 * 获取用户自定义信息
	 *
	 * @param tablename
	 * @param
	 */
	public Map getUserCustom(String tablename) {
		String names[] = tablename.split("-");
		HashMap<String, Object> map = new HashMap<>();
		for (String key : names) {
			if (key.equals("userid"))
				map.put(key, userid);
			if (key.equals("nickname"))
				map.put(key, nickname);
			if (key.equals("openid"))
				map.put(key, openid);
			if (key.equals("avatarurl"))
				map.put(key, avatarurl);
			if (key.equals("money"))
				map.put(key, money);
			if (key.equals("win_number"))
				map.put(key, win_number);
		}
		return map;
	}

	/***********************************get/set工具区***********************************************/
	//获取用户自定义信息
	public void getUser_Custom(String tablename,Map<String,Object> map){
		String names[] = tablename.split("-");
		for(String key:names){
			if(key.equals("userid"))map.put(key,userid);
			if(key.equals("nickname"))map.put(key,nickname);
			if(key.equals("openid"))map.put(key,openid);
			if(key.equals("avatarurl"))map.put(key,avatarurl);
			if(key.equals("date"))map.put(key,date);
			if(key.equals("sdk"))map.put(key,sdk);
			if(key.equals("state"))map.put(key,state);
			if(key.equals("statetext"))map.put(key,statetext);
			if(key.equals("gametype"))map.put(key,gametype);
			if(key.equals("money"))map.put(key,money);
			if(key.equals("bets"))map.put(key,bets);
			if(key.equals("brand"))map.put(key,brand);
			if(key.equals("brandtype"))map.put(key,brandtype);
			if(key.equals("winnum"))map.put(key,winnum);
			if(key.equals("user_brand_type"))map.put(key,user_brand_type);
			if (key.equals("win_number"))
				map.put(key, win_number);
		}
	}
	//初始化用户
	public void Initialization() {
		//初始化手中得牌
		this.brand = new int[]{-1,-1,-1};
		//初始化用户状态
		this.gametype=0;
		//
		this.brandtype = 1;
		//下注
		this.bets=0;
		//每一轮的操作状态
		this.brandstatus=0;
		//当局的输赢分数
		this.winnum=0;
	}
	//放入一张牌到用户手中
	public void setBrand(int brand) {
		int count = 0;
		for (int i : this.brand) {
			if (i != -1) {
				count++;
			}
		}
		if(count<this.brand.length){
			this.brand[count] = brand;
		}
	}
	/**
	 * 返回用户手里的最后一张牌的牌值或下标0
	 * type=0返回下标=1返回牌值
	 * @return
	 */
	public int getBrand_M(int type){
		int count = -1;
		for (int i : this.brand) {
			if (i != -1) {
				count++;
			}
		}
		if(type==0){
			//单副牌的下标
			return this.brand[count];
		}else{
			//牌值
			int brand_m = this.brand[count]-13*(this.brand[count]/13);
			//大王-小王-A都按15分计算
			if(this.brand[count]==52||this.brand[count]==53||brand_m==0){
				brand_m=15;
			}else{
				brand_m++;
			}
			return brand_m;
		}
	}
	/***
	 * 返回用户手里的牌型
	 * @return
	 */
	public int BrandCount(){
		util_brand=new Util_Brand(this.brand);
		//豹子算法
		int brand_state_a = util_brand.GodenFlower_A();
		if(brand_state_a!=-1)return brand_state_a;
		//检测金花
		int brand_state_b = util_brand.GodenFlower_B();
		//检测顺子
		int brand_state_c = util_brand.GodenFlower_C();
		//为顺子且金花存在就返回同花顺 次之金花在次之顺子
		if(brand_state_c!=-1&&brand_state_b!=-1){
			return brand_state_c;
		}else if(brand_state_b!=-1){
			return brand_state_b;
		}else if(brand_state_c!=-1){
			return brand_state_c;
		}
		//检测对子
		int brand_state_d = util_brand.GodenFlower_D();
		if(brand_state_d!=-1){
			return brand_state_d;
		}
		//检测单张或特殊235
		int brand_state_e = util_brand.GodenFlower_E();
		return brand_state_e;
	}
	public boolean ISMoney(int money){
		return this.money>=money;
	}
	/***********************************get/set***********************************************/
	
	public int getWinnum() {
		return winnum;
	}
	public int getUser_brand_type() {
		return user_brand_type;
	}
	public void setUser_brand_type(int user_brand_type) {
		this.user_brand_type = user_brand_type;
	}
	public String[] getBrand_custom() {
		return brand_custom;
	}
	public void setBrand_custom(String[] brand_custom) {
		this.brand_custom = brand_custom;
	}
	public int getBrandtype() {
		return brandtype;
	}
	public void setBrandtype(int brandtype) {
		this.brandtype = brandtype;
	}
	public void setWinnum(int winnum) {
		this.winnum = winnum;
	}

	public int getBrandstatus() {
		return brandstatus;
	}

	public void setBrandstatus(int brandstatus) {
		this.brandstatus = brandstatus;
	}

	public int[] getBrand() {
		return brand;
	}
	//返回最后一张牌
	public int getfinaBrand() {
		int count = 0;
		for (int i = 0; i < brand.length; i++) {
			if (brand[i] == 99) {
				count++;
			}
		}
		// 没牌返回-1
		if (count == 5) {
			return -1;
		}
		return brand[brand.length - (count + 1)];
	}

	public void setBrand(int[] brand) {
		this.brand = brand;
	}

	

	public int getBets() {
		return bets;
	}

	public void setBets(int bets) {
		this.bets = bets;
	}

	public String getStatetext() {
		return statetext;
	}

	public void setStatetext(String statetext) {
		this.statetext = statetext;
	}

	public int getGametype() {
		return gametype;
	}

	public void setGametype(int gametype) {
		this.gametype = gametype;
	}

	public String getIP() {
		return userid + "";
	}


	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getAvatarurl() {
		return avatarurl;
	}

	public void setAvatarurl(String avatarurl) {
		this.avatarurl = avatarurl;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getSdk() {
		return sdk;
	}

	public void setSdk(int sdk) {
		this.sdk = sdk;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

}
