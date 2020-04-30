package com.zcf.framework.bean;

import javax.websocket.Session;
import java.util.ArrayList;
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
	// 封号状态0正常1封号
	private int state;
	// 用户状态0默认未准备1准备
	private int ready_state;
	// 掉线状态默认0掉线1
	private int exit_state;
	// 用户解散状态默认0未应答1同意2不同意
	private int exit_game;
	// ip
	private String ip;
	// 坐标
	private String log_lat;
	// 金币
	private int money;
	// 钻石
	private int diamond;
	// 奖券
	private int award;
	// 分
	private int number;
	//当局分
	private int dqnumber;
	// 庄家标识
	private boolean banker;
	// 用户手里的牌
	protected List<Integer> brands;
	//用户起始的牌，用于对局回放
	protected List<Integer> copy_brands;
	// 用户吃的牌
	protected List<Integer> eat_brands;
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
	// 胡牌状态
	private int hu_state;
	// 胡牌类型
	private int hu_type;
	//ishu
	private int is_hustate;

	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	private Lock lock;

	public UserBean() {
		this.brands = new ArrayList<Integer>();
		this.copy_brands=new ArrayList<Integer>();
		this.eat_brands = new ArrayList<Integer>();
		this.bump_brands = new ArrayList<Integer>();
		this.show_brands = new ArrayList<Integer>();
		this.hide_brands = new ArrayList<Integer>();
		// 已出的牌
		this.out_brands = new ArrayList<Integer>();
		lock = new ReentrantLock(true);
	}

	/**
	 * 初始化用户
	 */
	public void Initialization() {
		this.brands.clear();
		this.copy_brands.clear();
		this.eat_brands.clear();
		this.bump_brands.clear();
		this.show_brands.clear();
		this.hide_brands.clear();
		this.out_brands.clear();
		this.power_number = 1;
		this.hu_state = 0;
		this.is_hustate=0;
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
			if (key.equals("avatarurl"))
				map.put(key, avatarurl);
			if (key.equals("money"))
				map.put(key, money);
			if (key.equals("brands"))
				map.put(key, brands);
			if (key.equals("brands_length"))
				map.put(key, brands.size());
			if (key.equals("eat_brands"))
				map.put(key, eat_brands);
			if (key.equals("bump_brands"))
				map.put(key, bump_brands);
			if (key.equals("show_brands"))
				map.put(key, show_brands);
			if (key.equals("hide_brands"))
				map.put(key, hide_brands);
			if (key.equals("hide_brands_length"))
				map.put(key, eat_brands.size());
			if (key.equals("out_brands"))
				map.put("out_brands", out_brands);
			if (key.equals("power_number"))
				map.put(key, power_number);
			if (key.equals("ip"))
				map.put(key, ip);
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
			if (key.equals("award"))
				map.put(key, award);
			if (key.equals("diamond"))
				map.put(key, diamond);
			if (key.equals("hu_state"))
				map.put(key, hu_state);
			if (key.equals("is_hustate"))
				map.put(key, is_hustate);
			if (key.equals("exit_game"))
				map.put(key, exit_game+"");
			if (key.equals("dqnumber"))
				map.put(key, dqnumber+"");
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
	 * 加番
	 * 
	 * @param fan
	 */
	public void setPower(int fan) {
		this.power_number += fan;
	}

	/**
	 * 获取癞子的数量（杠掉的癞子）
	 * 
	 * @return
	 */
	public int getDraw_count(int draw) {
		int count = 0;
		for (int index : out_brands) {
			if (draw == index)
				count++;
		}
		return count;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLog_lat() {
		return log_lat;
	}

	public void setLog_lat(String log_lat) {
		this.log_lat = log_lat;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getAward() {
		return award;
	}

	public void setAward(int award) {
		this.award = award;
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

	public List<Integer> getEat_brands() {
		return eat_brands;
	}

	public void setEat_brands(List<Integer> eat_brands) {
		this.eat_brands = eat_brands;
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

	public int getHu_type() {
		return hu_type;
	}

	public void setHu_type(int hu_type) {
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
		

	@Override
	public String toString() {
		return "UserBean [userid=" + userid + ", openid=" + openid + ", nickname=" + nickname + ", avatarurl="
				+ avatarurl + ", state=" + state + ", ready_state=" + ready_state + ", exit_state=" + exit_state
				+ ", exit_game=" + exit_game + ", ip=" + ip + ", log_lat=" + log_lat + ", money=" + money + ", diamond="
				+ diamond + ", award=" + award + ", number=" + number + ", banker=" + banker + ", brands=" + brands
				+ ", eat_brands=" + eat_brands + ", bump_brands=" + bump_brands + ", show_brands=" + show_brands
				+ ", hide_brands=" + hide_brands + ", out_brands=" + out_brands + ", power_number=" + power_number
				+ ", hu_state=" + hu_state + ", hu_type=" + hu_type + "]";
	}

}
