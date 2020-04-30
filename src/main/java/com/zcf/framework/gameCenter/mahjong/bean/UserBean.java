package com.zcf.framework.gameCenter.mahjong.bean;

import util.Mahjong_Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserBean {
	// 用户id
	private int userid;
	// 微信openid
	private String openid;
	// 用户昵称
	private String nickname;
	// 用户头像
	private String avatarurl;
	// 用户性别
	private  int sex;
	// 封号状态0正常1封号
	private int state;
	// 用户状态0默认未准备1准备
	private int ready_state;
	// 掉线状态默认0掉线1
	private int exit_state;
	// 用户解散状态默认0未应答1同意2不同意
	private int exit_game;
	// 坐标
	private String log_lat;
	// 房卡
	private int diamond;
	// 分
	private int number;
	// 当局分
	private int dqnumber;
	// 庄家标识
	private boolean banker;
	// 用户手里的牌
	protected List<Integer> brands;
	// 用户起始的牌，用于对局回放
	protected List<Integer> copy_brands;
	// 用户碰的牌
	protected List<Integer> bump_brands;
	// 用户明杠的牌
	protected List<Integer> show_brands;
	// 用户暗杠的牌
	protected List<Integer> hide_brands;
	// 桌上已出的牌
	private List<Integer> out_brands;
	// 用户番数
	private int power_number;
	// 胡牌状态（server）
	private int hu_state;
	// 胡牌类型
	private String hu_type;
	// ishu（client）
	private int is_hustate;
	// lock锁
	private Lock lock;
	//是否点炮
	private int  is_pao;
	// 结算讯息集合
	private List<String> recordMsgList;
	//听牌列表
	private List<Integer> tingCards;
	//不能碰杠的牌
	private List<Integer> noany;
	//不能胡的牌
	private List<Integer> nohu;
	//是否已经胡牌
	private int ishu;
	//积分
	private int money;
	//亲友圈id
	private int number_3;
	//大厅类型  2   4人场
	private String dttype;
	private int zha = 1;//扎针
	private int fu;//辅分

	public int getZha() {
		return zha;
	}

	public void setZha(int zha) {
		this.zha = zha;
	}

	public int getFu() {
		return fu;
	}

	public void setFu(int fu) {
		this.fu = fu;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getNumber_3() {
		return number_3;
	}

	public void setNumber_3(int number_3) {
		this.number_3 = number_3;
	}

	public String getDttype() {
		return dttype;
	}

	public void setDttype(String dttype) {
		this.dttype = dttype;
	}

	public List<Integer> getNoany() {
		return noany;
	}

	public void setNoany(List<Integer> noany) {
		this.noany = noany;
	}

	public List<Integer> getNohu() {
		return nohu;
	}

	public void setNohu(List<Integer> nohu) {
		this.nohu = nohu;
	}

	public int getIshu() {
		return ishu;
	}

	public void setIshu(int ishu) {
		this.ishu = ishu;
	}

	public List<Integer> getTingCards() {
		return tingCards;
	}

	public void setTingCards(List<Integer> tingCards) {
		this.tingCards = tingCards;
	}

	public UserBean() {
		this.brands = new ArrayList<Integer>();
		this.copy_brands = new ArrayList<Integer>();
		this.bump_brands = new ArrayList<Integer>();
		this.show_brands = new ArrayList<Integer>();
		this.hide_brands = new ArrayList<Integer>();
		// 已出的牌
		this.out_brands = new ArrayList<Integer>();
		// 结算信息集合
		this.recordMsgList= new ArrayList<String>();
		lock = new ReentrantLock(true);
		this.tingCards = new ArrayList<>();
		this.noany = new ArrayList<>();
		this.nohu = new ArrayList<>();
	}

	/**
	 * 初始化用户
	 */
	public void Initialization() {
		this.brands.clear();
		this.copy_brands.clear();
		this.bump_brands.clear();
		this.show_brands.clear();
		this.hide_brands.clear();
		this.out_brands.clear();
		this.nohu.clear();
		this.noany.clear();
		this.recordMsgList.clear();
		this.tingCards.clear();
		this.power_number = 0;
		this.hu_state = 0;
		this.is_hustate = 0;
		this.is_pao=0;
		this.dqnumber = 0;
		this.ishu = 0;
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
			if (key.equals("sex"))
				map.put(key, sex);
			if (key.equals("is_pao"))
				map.put(key, is_pao);
			if (key.equals("avatarurl"))
				map.put(key, avatarurl);
			if (key.equals("money"))
				map.put(key, money);
			if (key.equals("hu_type"))
				map.put(key, hu_type);
		}
		return map;
	}

	/***
	 * 获取用户自定义信息
	 * 
	 * @param tablename
	 * @param map
	 */
	public void getUser_Custom(String tablename, Map<String, Object> map) {
		String names[] = tablename.split("-");
		for (String key : names) {
			if (key.equals("userid"))
				map.put(key, userid);
			if (key.equals("nickname"))
				map.put(key, nickname);
			if (key.equals("openid"))
				map.put(key, openid);
			if (key.equals("sex"))
				map.put(key, sex);
			if (key.equals("is_pao"))
				map.put(key, is_pao);
			if (key.equals("avatarurl"))
				map.put(key, avatarurl);
			if (key.equals("brands"))
				map.put(key, brands);
			if (key.equals("brands_length"))
				map.put(key, brands.size());
			if (key.equals("bump_brands"))
				map.put(key, bump_brands);
			if (key.equals("show_brands"))
				map.put(key, show_brands);
			if (key.equals("hide_brands"))
				map.put(key, hide_brands);
			if (key.equals("out_brands"))
				map.put("out_brands", out_brands);
			if (key.equals("power_number"))
				map.put(key, power_number);
			if (key.equals("log_lat"))
				map.put(key, log_lat);
			if (key.equals("ready_state"))
				map.put(key, ready_state);
			if (key.equals("exit_state"))
				map.put(key, exit_state);
			if (key.equals("banker"))
				map.put(key, banker);
			if (key.equals("number"))
				map.put(key, number);
			if (key.equals("diamond"))
				map.put(key, diamond);
			if (key.equals("hu_state"))
				map.put(key, hu_state);
			if (key.equals("is_hustate"))
				map.put(key, is_hustate);
			if (key.equals("exit_game"))
				map.put(key, exit_game + "");
			if (key.equals("dqnumber"))
				map.put(key, dqnumber + "");
			if (key.equals("recordMsgList"))
				map.put(key, recordMsgList );
			if (key.equals("nohu"))
				map.put(key, nohu );
			if (key.equals("ishu"))
				map.put(key, ishu );
			if (key.equals("hu_type"))
				map.put(key, hu_type );
			if (key.equals("money"))
				map.put(key, money );
		}
	}

	/**
	 * 删除指定的一张牌(手里的牌)
	 * 
	 * @param brand_index
	 */
	public void Remove_Brands(int brand_index) {
		for (int i = 0; i < this.brands.size(); i++) {
			if (this.brands.get(i) == brand_index) {
				this.brands.remove(i);
				break;
			}
		}
	}

	/**
	 * 删除指定的一张牌(出的牌)
	 * 
	 * @param brand_index
	 */
	public int Remove_Brands_Out(int brand_index) {
		for (int i = 0; i < this.out_brands.size(); i++) {
			if (this.out_brands.get(i) == brand_index) {
				this.out_brands.remove(i);
				return 0;
			}
		}
		return 1;
	}

	/**
	 * 删除指定的一张牌(碰的牌)
	 * 
	 * @param brand_index
	 */
	public int Remove_Brands_Bump(int brand_index) {
		for (int i = 0; i < this.bump_brands.size(); i++) {
			if (this.bump_brands.get(i).intValue() == brand_index) {
				this.bump_brands.remove(i);
				return 0;
			}
		}
		return 1;
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:检测手牌中发白张数
	 *@ Params:
	 *@ Description:
	 *@ Return:
	 *@ Date:2019/8/9
	 */
	public void getZFBnum(List<Integer> s,UserBean user,int brand,int i) {
		List<Integer> brands = new ArrayList<>();
		brands.addAll(s);
		if(i!=1){
			brands.add(brand);
		}
		int num = 0;
		int z = 0;//31
		int f = 0;//32
		int b = 0;//33
		for (Integer card:brands){
			int brand_value = Mahjong_Util.mahjong_Util.getBrand_Value(card);
			switch (brand_value){
				case 31:
					z++;
					break;
				case 32:
					f++;
					break;
				case 33:
					b++;
					break;
			}
		}
		if(z>=3){
			user.getRecordMsgList().add("手牌三红+"+1);
			num++;
		}
		if(f>=3){
			user.getRecordMsgList().add("手牌三发+"+1);
			num++;
		}
		if(b>=3){
			user.getRecordMsgList().add("手牌三白+"+1);
			num++;
		}
		user.setPower(num);
	}

	/**
	 * 加番
	 * 
	 * @param fan
	 */
	public void setPower(int fan) {
		System.err.println(nickname+"--------------加"+fan+"----------------");
		this.power_number += fan;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatarurl() {
		return avatarurl;
	}

	public void setAvatarurl(String avatarurl) {
		this.avatarurl = avatarurl;
	}

	public int getState() {
		return state;
	}

	public int getDqnumber() {
		return dqnumber;
	}

	public void setDqnumber(int dqnumber) {
		this.dqnumber = dqnumber;
	}

	public int getIs_hustate() {
		return is_hustate;
	}

	public void setIs_hustate(int is_hustate) {
		this.is_hustate = is_hustate;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getReady_state() {
		return ready_state;
	}

	public void setReady_state(int ready_state) {
		this.ready_state = ready_state;
	}

	public int getExit_state() {
		return exit_state;
	}

	public void setExit_state(int exit_state) {
		this.exit_state = exit_state;
	}

	public int getExit_game() {
		return exit_game;
	}

	public void setExit_game(int exit_game) {
		this.exit_game = exit_game;
	}

	public String getLog_lat() {
		return log_lat;
	}

	public void setLog_lat(String log_lat) {
		this.log_lat = log_lat;
	}

	public int getIs_pao() {
		return is_pao;
	}

	public void setIs_pao(int is_pao) {
		this.is_pao = is_pao;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isBanker() {
		return banker;
	}

	public void setBanker(boolean banker) {
		this.banker = banker;
	}

	public List<Integer> getBrands() {
		return brands;
	}

	public void setBrands(List<Integer> brands) {
		this.brands = brands;
	}

	public List<Integer> getBump_brands() {
		return bump_brands;
	}

	public void setBump_brands(List<Integer> bump_brands) {
		this.bump_brands = bump_brands;
	}

	public List<Integer> getShow_brands() {
		return show_brands;
	}

	public void setShow_brands(List<Integer> show_brands) {
		this.show_brands = show_brands;
	}

	public List<Integer> getHide_brands() {
		return hide_brands;
	}

	public void setHide_brands(List<Integer> hide_brands) {
		this.hide_brands = hide_brands;
	}

	public List<Integer> getOut_brands() {
		return out_brands;
	}

	public void setOut_brands(List<Integer> out_brands) {
		this.out_brands = out_brands;
	}

	public int getPower_number() {
		return power_number;
	}

	public void setPower_number(int power_number) {
		this.power_number = power_number;
	}

	public int getHu_state() {
		return hu_state;
	}

	public void setHu_state(int hu_state) {
		this.hu_state = hu_state;
	}

	public String getHu_type() {
		return hu_type;
	}

	public void setHu_type(String hu_type) {
		this.hu_type = hu_type;
	}

	public List<Integer> getCopy_brands() {
		return copy_brands;
	}

	public void setCopy_brands(List<Integer> copy_brands) {
		this.copy_brands = copy_brands;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	public List<String> getRecordMsgList() {
		return recordMsgList;
	}

	public void setRecordMsgList(List<String> recordMsgList) {
		this.recordMsgList = recordMsgList;
	}

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    /*用户是否缺一门*/
	public boolean getIsque1() {
		//手牌整合
		List<Integer> userBrand = Mahjong_Util.mahjong_Util.User_Brand_Value(this.getBrands());
		for (Integer integer : Mahjong_Util.mahjong_Util.User_Brand_Value(this.getShow_brands())) {
			userBrand.add(integer);
		}
		for (Integer integer : Mahjong_Util.mahjong_Util.User_Brand_Value(this.getBump_brands())) {
			userBrand.add(integer);
		}
		for (Integer integer : Mahjong_Util.mahjong_Util.User_Brand_Value(this.getHide_brands())) {
			userBrand.add(integer);
		}
		//去重
		List<Integer> newList = new ArrayList<>();
		for (Integer ban : userBrand) {
			if (!newList.contains(ban)) {
				newList.add(ban);
			}
		}
		int sum = 0;
		for (int i = 0; i < 9; i++) {
			if(newList.contains(i)){
				sum++;
				break;
			}
		}
		for (int i = 9; i < 18; i++) {
			if(newList.contains(i)){
				sum++;
				break;
			}
		}
		for (int i = 18; i < 27; i++) {
			if(newList.contains(i)){
				sum++;
				break;
			}
		}
		if(sum==3){
			return false;
		}
		return true;
	}
}
