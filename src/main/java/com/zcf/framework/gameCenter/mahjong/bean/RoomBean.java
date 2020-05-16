package com.zcf.framework.gameCenter.mahjong.bean;

import com.google.gson.Gson;
import com.zcf.framework.gameCenter.mahjong.mahjong.Public_State;
import com.zcf.framework.gameCenter.mahjong.service.Exit_Game;
import util.Mahjong_Util;
import util.UtilClass;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoomBean {
	//战绩记录id
	private Integer roomid = 0;
	// 房间号
	private String roomno;
	//房间创建时间
	private String data;
	// 房间扣费
	private int room_card;
	// 麻将总牌
	private List<Integer> brands;
	// 玩家集合
	private List<UserBean> game_userlist;
	// 房间操作记录
	private List<Map<String, Object>> user_log;
	// 当前庄家id
	private int banker;
	// 房主id
	private int houseid;
	// 当前局数
	private int game_number;
	// 房间最大局数
	private int max_number;
	// 房间最大人数
	private int max_person;
	// 房间状态0等待加入1准备阶段2已开始3正在结算4已结算
	private int state;
	// 房间解散状态0未发起1正在解散
	private int exit_game;
	// 最后操作人的id
	private int end_userid;
	// 底分
	private int fen;
	// 番
	private int fan;
	// 房间锁
	private Lock lock;
	// 胡的人数
	private List<UserBean> hu_user_list;
	// 俱乐部id房间类型默认个人创建0
	private int clubid;
	// 作弊
	private int onkey = -1;
	// 用户位置0-3下标代表1-4的座位，值代表用户id
	private int[] user_positions;
	// 明杠的人
	private int show_baruserid;
	// 暗杠的人
	private int hide_baruserid;
	// 补杠的人
	private int repair_baruserid;
	// 胜利的人
	private int victoryid = -1;
	// 是否有人胡牌
	private int hucount;
	// 是否平局
	private int flow;
	// 最后一次操作
	private Map<String, Object> nextMap;
	// 骰子数
	private int dice;
	//检测胡人数
	private int flowNum;
	//已经胡牌人数
	private int hunum;
	//最后一次出的牌
    private int lastBrand;
    //最后一次出牌人的ID
    private int lastUserid;
	//亲友圈ID
	private int number_3;
	//房间参数
	private Map<String,Integer> roomParams;
	//支付方式
	private Integer paytype;
	//是否全部扎针
	private int zhanum;

	public int getZhanum() {
		return zhanum;
	}

	public void setZhanum(int zhanum) {
		this.zhanum = zhanum;
	}

	public Integer getPaytype() {
		return paytype;
	}

	public void setPaytype(Integer paytype) {
		this.paytype = paytype;
	}

	public Map<String, Integer> getRoomParams() {
		return roomParams;
	}

	public Integer getRoomid() {
		return roomid;
	}

	public void setRoomid(Integer roomid) {
		this.roomid = roomid;
	}

	public void setRoomParams(Map<String, Integer> roomParams) {
		this.roomParams = roomParams;
	}

	public int getNumber_3() {
		return number_3;
	}

	public void setNumber_3(int number_3) {
		this.number_3 = number_3;
	}

	public int getLastUserid() {
        return lastUserid;
    }

    public void setLastUserid(int lastUserid) {
        this.lastUserid = lastUserid;
    }

    public int getLastBrand() {
        return lastBrand;
    }

    public void setLastBrand(int lastBrand) {
        this.lastBrand = lastBrand;
    }

    public int getHunum() {
		return hunum;
	}

	public void setHunum(int hunum) {
		this.hunum = hunum;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	public int getFlowNum() {
		return flowNum;
	}

	public void setFlowNum(int flowNum) {
		this.flowNum = flowNum;
	}

	Map<String, Object> map = new HashMap<String, Object>();

	public RoomBean() {
		// 用户座位
		this.user_positions = new int[] { -1, -1, -1, -1 };
		// 麻将集合
		this.brands = new ArrayList<Integer>();
		// 房间锁
		this.lock = new ReentrantLock(true);
		// 玩家集合
		this.game_userlist = new ArrayList<UserBean>();
		// 房间操作记录
		this.user_log = new ArrayList<Map<String, Object>>();
		// 胡牌人数
		this.hu_user_list = new ArrayList<UserBean>();
		// 记录碰的操作
		this.nextMap = new HashMap<String, Object>();
		this.roomParams = new HashMap<>();
		roomParams.put("p1",0);
		roomParams.put("p2",0);
		roomParams.put("p3",0);
		roomParams.put("p4",0);
	}

	/**
	 * 房间信息初始化
	 */
	public void Initialization() {
		// 初始化麻将
		Init_Brands();
		// 初始化用户
		for (UserBean user : game_userlist) {
			user.Initialization();
		}
		// 操作日志
		this.user_log.clear();
		this.game_number++;
		// 胡牌人数
		this.hu_user_list.clear();
		// 清空杠的人
		this.show_baruserid = 0;
		this.hide_baruserid = 0;
		this.repair_baruserid = 0;
		this.onkey = -1;
		this.state = 1;
		this.flowNum = 0;
		this.hucount = 0;
		this.hunum = 0;
	}

	/**
	 * 初始化一副麻将
	 */
	private void Init_Brands() {
		brands.clear();
		for (int i = 0; i < 136; i++) {
			brands.add(i);
		}
		if (max_person==4){
			// 删除东西南北
			RemoveBrand(new int[] { 27, 28, 29, 30, 61, 62, 63, 64, 95, 96, 97, 98, 129, 130, 131, 132 });
		}
		if (max_person==2){
			// 删除东西南北万   0-8   34-42  68-76 102-110
			RemoveBrand(new int[] {0,1,2,3,4,5,6,7,8,34,35,36,37,38,39,40,41,42,68,69,70,71,72,73,74,75,76,27, 28, 29, 30, 61, 62, 63, 64, 95, 96, 97, 98, 129, 130, 131, 132,102,103,104,105,106,107,108,109,110});
		}

	}

	/**
	 * 发牌
	 */
	public void Deal() {
		int number;
		for (UserBean userBean : this.game_userlist) {
			if (userBean.getUserid() == this.banker) {
				number = 14;
			} else {
				number = 13;
			}
			for (int i = 0; i < number; i++) {
				int brands = getBrand_Random();
				userBean.getBrands().add(brands);
				userBean.getCopy_brands().add(brands);
			}
			/*List<Integer> list = new ArrayList<>();
			if (userBean.getUserid() == this.banker) {
				int[] arr = new int[]{31,32,33,65,66,67,99,100,101,133,134,15,16,17};//测火箭 大三元用的
				//int[] arr = new int[]{0,34,1,35,2,36,3,37,4,38,5,70,49,48};//测大板子用的 万
				//int[] arr = new int[]{9,43,10,44,11,45,12,46,13,47,22,23,33,34};//测大板子用的 两人场
				for (int i = 0; i < arr.length; i++) {
					list.add(arr[i]);
				}
				RemoveBrand(arr);
				userBean.getBrands().addAll(list);
				userBean.getCopy_brands().addAll(list);
			} else {
				for (int i = 0; i < 13; i++) {
					int brands = getBrand_Random();
					userBean.getBrands().add(brands);
					userBean.getCopy_brands().add(brands);
				}
			}*/

			// 排序
			Mahjong_Util.mahjong_Util.Order_Brands(userBean.getBrands());

		}
	}

    /**
     * 定庄
     */
    public void Select_Banker(){
        int index = -1;
        //第一局随机坐庄
        if(game_number==1){
			//随机0~3
			index = (int)(Math.random() * this.game_userlist.size());
			this.banker = this.game_userlist.get(index).getUserid();
        }else{
        	if(victoryid!=-1){
				//赢家坐庄
				this.banker=victoryid;
			}else{
				//随机0~3
				index = (int)(Math.random() * this.game_userlist.size());
				this.banker = this.game_userlist.get(index).getUserid();
			}
        }
        this.end_userid=this.banker;
    }
	/**
	 * 作弊发牌
	 */
	public void Deal_Bug() {
		Random random = new Random();
		String[] user1 = new String[14], user2 = new String[13];
		for (int i = 0; i < 14; i++) {
			int nextInt = random.nextInt(brands.size());
			user1[i] = String.valueOf(brands.get(nextInt));
			brands.remove(nextInt);
		}
		for (int i = 0; i < 13; i++) {
			int nextInt = random.nextInt(brands.size());
			user2[i] = String.valueOf(brands.get(nextInt));
			brands.remove(nextInt);
		}
		String[] user3 = UtilClass.utilClass.getTableName("/parameter.properties", "user3").split("-");
		String[] user4 = UtilClass.utilClass.getTableName("/parameter.properties", "user4").split("-");
		int index = 0;
		for (UserBean userBean : this.game_userlist) {
			String[] user = null;
			if (userBean.getUserid() == this.banker) {
				user = user1;
			} else if (index == 0) {
				user = user2;
				index++;
			} else if (index == 1) {
				user = user3;
				index++;
			} else if (index == 2) {
				user = user4;
				index++;
			}
			for (int i = 0; i < user.length; i++) {
				userBean.getBrands().add(Integer.parseInt(user[i]));
				userBean.getCopy_brands().add(Integer.parseInt(user[i]));
			}
			Deal_Bug(user);
		}
	}

	/**
	 * 检测用户胡牌
	 * 
	 * @param userBean
	 * @param brand
	 * @return
	 */
	public int ISBrand(UserBean userBean, int brand, RoomBean roomBean) {
		return Mahjong_Util.mahjong_Util.IS_Victory(userBean, brand);
	}

	/**
	 * 检测胡牌的人数
	 * 
	 * @param userBean
	 * @param brand
	 * @param roomBean
	 * @return
	 */
	public int IS_HU(UserBean userBean, int brand, RoomBean roomBean, int userid) {
		lock.lock();
		if (hu_user_list.size() != 0) {
			lock.unlock();
			return hu_user_list.size();
		}
		for (UserBean user : getGame_userlist()) {
			// 如果用户已弃牌则不检测
			if (user.getHu_state() == 2 || user.getIshu()==1)
				continue;
			if (user.getUserid() == userid)
				continue;
			if (ISBrand(user, brand, roomBean) != 0) {
				hu_user_list.add(user);
			}
		}
		lock.unlock();
		return hu_user_list.size();
	}

	/**
	 * 从主牌中删除作弊牌
	 * 
	 * @param user
	 */
	private void Deal_Bug(String[] user) {
		for (int i = 0; i < user.length; i++) {
			for (int j = 0; j < this.brands.size(); j++) {
				if (Integer.parseInt(user[i]) == this.brands.get(j)) {
					this.brands.remove(j);
					break;
				}
			}
		}
	}

	/**
	 * 非一炮多响模式 寻找能胡的人 按出牌人顺时针寻找
	 * @return
	 */
	public int getNextUserId3(){
		int userid=-1;
		int thisindex=-1;
		for(int i=0;i<user_positions.length;i++){
			if(user_positions[i]==-1)continue;
			if(user_positions[i]==lastUserid){
				thisindex=i;
			}
		}
		for(int i=0;i<user_positions.length;i++){
			if((thisindex+1)==user_positions.length){
				thisindex=0;
			}else{
				thisindex++;
			}
			if(user_positions[thisindex]==-1)continue;
			if(!hu_user_list.contains(getUserBean(thisindex))) continue;
			if(user_positions[thisindex]==lastUserid)continue;
			userid = user_positions[thisindex];
			break;
		}
		return userid;
	}

	/**
	 * 获取下个操作人的id
	 * 
	 * @return
	 */
	public int getNextUserId() {
		int userid = -1;
        UserBean user;
		do{
			int thisindex = -1;
			for (int i = 0; i < user_positions.length; i++) {
				if (user_positions[i] == -1)
					continue;
				if (user_positions[i] == end_userid) {
					thisindex = i;
				}
			}
			for (int i = 0; i < user_positions.length; i++) {
				if ((thisindex + 1) == user_positions.length) {
					thisindex = 0;
				} else {
					thisindex++;
				}
				if (user_positions[thisindex] == -1)
					continue;
				userid = user_positions[thisindex];
				break;

			}
			// 为此用户创建一个倒计时线程
			end_userid = userid;
            user = getUserBean(userid);
        }while (user.getIshu()!=0);
		return userid;
	}

	/**
	 * 删除指定的一张牌(单牌值)
	 * 
	 * @param brand
	 */
	public void Remove_Brands_Index(int brand) {
		for (int i = 0; i < this.brands.size(); i++) {
			int index = Mahjong_Util.mahjong_Util.getBrand_Value(this.brands.get(i));
			if (index == brand) {
				this.brands.remove(i);
				break;
			}
		}
	}

	/**
	 * 删除指定的一张牌(牌id)
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
	 * 随机获取一张牌
	 * @return
	 */
	public int getBrand_Random(){
		if(this.brands.size()==0)return -1;
		int index = (int)(Math.random() * this.brands.size());
		//查看是否有作弊
		if(this.onkey!=-1){
            index=this.onkey;
            this.onkey=-1;
			return index;
		}
		//获取单幅牌值
		int brand = this.brands.get(index);
		this.brands.remove(index);
		return brand;
	}

	/**
	 * 删除指定牌
	 * 
	 * @param remove_brands
	 */
	private void RemoveBrand(int[] remove_brands) {
		for (int i = 0; i < remove_brands.length; i++) {
			for (int j = 0; j < this.brands.size(); j++) {
				// 获取单幅牌值
				int brand = Mahjong_Util.mahjong_Util.getBrand_Value(this.brands.get(j));
				if (brand == remove_brands[i]) {
					this.brands.remove(j);
					j--;
				}
			}
		}
	}

	/**
	 * 自定义获取房间列及用户列
	 * 
	 * @param table
	 * @param returnMap
	 * @param usertable
	 */
	public void getRoomBean_Custom(String table, Map<String, Object> returnMap, String usertable) {
		getRoomBean_Custom(table, returnMap);
		returnMap.put("userList", getGame_userList(usertable, 0));

	}

	/**
	 * getrooms
	 *
	 * @param
	 * @param
	 * @param
	 */
	public List<Map<String, Object>> getrooms(List<RoomBean> list,String table, String usertable) {
		List<Map<String, Object>> map = new ArrayList<>();
		if(list.size()!=0){
			for (RoomBean room:list) {
				Map<String, Object> returnMap = new HashMap<>();
				room.getRoomBean_Custom(table, returnMap);
				returnMap.put("userList", room.getGame_userList(usertable, 0));
				map.add(returnMap);
			}
			return map;
		}else
			return map;
	}

	/**
	 * 自定义获取房间列及用户列(胡牌用户)
	 * 
	 * @param table
	 * @param returnMap
	 * @param usertable
	 */
	public void getRoomBean_Custom_HU(String table, Map<String, Object> returnMap, String usertable) {
		getRoomBean_Custom(table, returnMap);
		returnMap.put("userList", getGame_userList(usertable, 0));
	}

	/**
	 * 自定义获取房间列
	 * 
	 * @param table
	 * @param returnMap
	 */
	public void getRoomBean_Custom(String table, Map<String, Object> returnMap) {
		String[] names = table.split("-");
		for (String name : names) {
			if ("roomno".equals(name))
				returnMap.put("roomno", this.roomno);
			if ("room_card".equals(name))
				returnMap.put("room_card", this.room_card);
			if ("banker".equals(name))
				returnMap.put("banker", this.banker);
			if ("game_number".equals(name))
				returnMap.put("game_number", this.game_number);
			if ("max_number".equals(name))
				returnMap.put("max_number", this.max_number);
			if ("max_person".equals(name))
				returnMap.put("max_person", this.max_person);
			if ("state".equals(name))
				returnMap.put("state", this.state + "");
			if ("end_userid".equals(name))
				returnMap.put("end_userid", this.end_userid);
			if ("fen".equals(name))
				returnMap.put("fen", this.fen);
			if ("fan".equals(name))
				returnMap.put("fan", this.fan);
			if ("user_positions".equals(name))
				returnMap.put("user_positions", this.user_positions);
			if ("brands_count".equals(name))
				returnMap.put("brands_count", this.brands);
			if ("nextMap".equals(name))
				returnMap.put("nextMap", this.nextMap);
			if ("dice".equals(name))
				returnMap.put("dice", this.dice);
			if ("clubid".equals(name))
				returnMap.put("clubid", this.clubid);
			if ("user_log".equals(name)) {
				if (this.user_log.size() == 0) {
					returnMap.put("user_log", null);
				} else {
					returnMap.put("user_log", this.user_log.get(this.user_log.size() - 1));
				}
			}
			if ("exit_game".equals(name))
				returnMap.put("exit_game", this.exit_game);
			if ("data".equals(name))
				returnMap.put("data", this.data);
			if ("roomParams".equals(name))
				returnMap.put("data", this.roomParams);
		}
	}

	/***
	 * 自定义获取用户信息
	 * 
	 * @param tablename
	 * @return
	 */
	public List<Map<String, Object>> getGame_userList(String tablename, int type) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<UserBean> userList;
		userList = type == 1 ? getHu_user_list() : getGame_userlist();
		for (int i = 0; i < userList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			userList.get(i).getUser_Custom(tablename, map);
			list.add(map);
		}
		return list;
	}

	/**
	 * 删除一名用户
	 * 
	 * @param userid
	 */
	public void User_Remove(int userid) {
		for (int i = 0; i < game_userlist.size(); i++) {
			if (game_userlist.get(i).getUserid() == userid) {
				game_userlist.remove(i);
				break;
			}
		}
		this.remove_opsitions(userid);
	}

	/***
	 * 获取指定id的用户实例
	 * 
	 * @param userid
	 * @return
	 */
	public UserBean getUserBean(int userid) {
		for (UserBean userBean : game_userlist) {
			if (userBean.getUserid() == userid)
				return userBean;
		}
		return null;
	}

	/**
	 * 剔除指定用户的位置
	 * 
	 * @param userid
	 */
	public void remove_opsitions(int userid) {
		for (int i = 0; i < user_positions.length; i++) {
			if (user_positions[i] == userid) {
				user_positions[i] = -1;
			}
		}
	}

	/***
	 * 加入用户位置-顺序加入
	 * 
	 */
	public void setUser_positions(int userid) {
		// 2人时坐第三个位置
		if (max_person == 2) {
			if (user_positions[2] == -1) {
				user_positions[2] = userid;
			}else{
                user_positions[1] = userid;
            }
		} else {
			for (int i = 0; i < user_positions.length; i++) {
				if (user_positions[i] == -1) {
					user_positions[i] = userid;
					break;
				}
			}
		}
	}

	/**
	 * 添加操作日志
	 * 
	 * @param logMap
	 */
	public void addLog(Map<String, Object> logMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : logMap.keySet()) {
			map.put(key, logMap.get(key));
		}
		this.user_log.add(map);
	}

	/**
	 * 分牌
	 * 
	 * @throws IOException
	 */
	public List<Map<String, Object>> GrantBrand(UserBean userBean) {
		int index = -1;
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < user_positions.length; i++) {
			if (user_positions[i] == -1)
				continue;
			if (userBean.getUserid() == user_positions[i]) {
				index = i;
				break;
			}
		}
		for (int i = index; i < index + user_positions.length; i++) {
			if (i == user_positions.length)
				i = 0;
			if (user_positions[i] == -1)
				continue;
			UserBean user = getUserBean(user_positions[i]);
			if (this.brands.size() == 0)
				break;
			int brand = getBrand_Random();
			user.getBrands().add(brand);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("brand", brand);
			map.put("userid", user.getUserid());
			listMap.add(map);
		}
		return listMap;
	}

	/**
	 * 结算—减分
	 * 
	 * @param victoryid
	 */
	public void End_Game(int victoryid, int number) {
		for (UserBean userBean : game_userlist) {
			if (userBean.getUserid() != victoryid) {
				userBean.setNumber(number);
			}
		}
	}

	/**
	 * 清空杠记录
	 */
	public void RemoveAll_Bar() {
		show_baruserid = 0;
		hide_baruserid = 0;
		repair_baruserid = 0;
	}

	/**
	 * 检测杠操作是否是自己
	 *
	 * @param userid
	 * @return
	 */
	public int IS_Bar_Userid(int userid) {
		if (show_baruserid == userid)
			return 1;
		if (hide_baruserid == userid)
			return 2;
		if (repair_baruserid == userid)
			return 3;
		return 0;
	}

	// 所有用户重置准备
	public void InItReady() {
		if (this.game_number == this.max_number) {
			// 清空房间信息
			Public_State.PKMap.remove(this.roomno);
		} else {
			this.zhanum = 0;
			// 设置不平局
			this.flow = 0;
			// 设置准备阶段
			this.state = 1;
			Ready_InIt();
		}
	}

	// 初始化所有用户准备状态
	public void Ready_InIt() {
		for (UserBean userBean : this.game_userlist) {
			userBean.setReady_state(0);
			userBean.setExit_game(0);
			userBean.setFu(0);
			userBean.setZha(0);
			userBean.setHu_type("");
		}
	}

	// 初始化所有用户胡状态
	public void HuState_InIt() {
		for (UserBean userBean : this.game_userlist) {
			userBean.setHu_state(0);
		}
		this.hu_user_list.clear();
	}

	/******************************************* get/set ******************************************************/

	public List<Integer> getBrands() {
		return brands;
	}

	public Map<String, Object> getNextMap() {
		return nextMap;
	}

	public void setNextMap(Map<String, Object> nextMap) {
		this.nextMap = nextMap;
	}

	public int getFlow() {
		return flow;
	}

	public void setFlow(int flow) {
		this.flow = flow;
	}

	public int getVictoryid() {
		return victoryid;
	}

	public void setVictoryid(int victoryid) {
		int num = 0;
		int userid = victoryid;
		for (UserBean user :
				game_userlist) {
			if(user.getDqnumber()>num){
				num = user.getDqnumber();
				userid = user.getUserid();
			}
		}
		this.victoryid = userid;
	}

	public int getHucount() {
		return hucount;
	}

	public void setHucount(int hucount) {
		this.hucount = hucount;
	}

	public int getClubid() {
		return clubid;
	}

	public void setClubid(int clubid) {
		this.clubid = clubid;
	}

	public int getMax_number() {
		return max_number;
	}

	public void setMax_number(int max_number) {
		this.max_number = max_number;
	}

	public int getHouseid() {
		return houseid;
	}

	public void setHouseid(int houseid) {
		this.houseid = houseid;
	}

	public int[] getUser_positions() {
		return user_positions;
	}

	public void setUser_positions(int[] user_positions) {
		this.user_positions = user_positions;
	}

	public int getExit_game() {
		return exit_game;
	}

	public void setExit_game(int exit_game) {
		this.exit_game = exit_game;
	}

	public int getFen() {
		return fen;
	}

	public void setFen(int fen) {
		this.fen = fen;
	}

	public int getFan() {
		return fan;
	}

	public void setFan(int fan) {
		this.fan = fan;
	}

	public String getRoomno() {
		return roomno;
	}

	public void setRoomno(String roomno) {
		this.roomno = roomno;
	}

	public int getOnkey() {
		return onkey;
	}

	public void setOnkey(int onkey) {
		this.onkey = onkey;
	}

	public void setBrands(List<Integer> brands) {
		this.brands = brands;
	}

	public List<UserBean> getGame_userlist() {
		return game_userlist;
	}

	public List<UserBean> getHunumList() {
		List<UserBean> list = new ArrayList<>();
		for (UserBean user:game_userlist) {
			if(user.getIshu()==1){
				list.add(user);
			}
		}
		return list;
	}

	public void setGame_userlist(List<UserBean> game_userlist) {
		this.game_userlist = game_userlist;
	}

	public List<Map<String, Object>> getUser_log() {
		return user_log;
	}

	public String getUser_log_text() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < game_userlist.size(); i++) {
			Map<String, Object> usermap = new HashMap<String, Object>();
			usermap.put("userid", game_userlist.get(i).getUserid());
			usermap.put("brands", game_userlist.get(i).getCopy_brands());
			usermap.put("nickName", game_userlist.get(i).getNickname());
			usermap.put("avatarurl", game_userlist.get(i).getAvatarurl());
			list.add(usermap);
		}
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("brands_count", this.brands);
		map1.put("game_number", game_number);
		map1.put("banker", banker);
		map1.put("max_number", max_number);
		map1.put("dice", dice);
		map.put("roominfo", map1);
		map.put("usercards", list);
		map.put("user_positions", user_positions);
		map.put("user_log", this.user_log);
		return new Gson().toJson(map);
	}

	public void setUser_log(List<Map<String, Object>> user_log) {
		this.user_log = user_log;
	}

	public int getBanker() {
		return banker;
	}

	public void setBanker(int banker) {
		this.banker = banker;
	}

	public int getGame_number() {
		return game_number;
	}

	public void setGame_number(int game_number) {
		this.game_number = game_number;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getEnd_userid() {
		return end_userid;
	}

	public void setEnd_userid(int end_userid) {
		this.end_userid = end_userid;
	}

	public int getRoom_card() {
		return room_card;
	}

	public void setRoom_card(int room_card) {
		this.room_card = room_card;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	public int getMax_person() {
		return max_person;
	}

	public void setMax_person(int max_person) {
		this.max_person = max_person;
	}

	public List<UserBean> getHu_user_list() {
		return hu_user_list;
	}

	public void setHu_user_list(List<UserBean> hu_user_list) {
		this.hu_user_list = hu_user_list;
	}

	public int getShow_baruserid() {
		return show_baruserid;
	}

	public void setShow_baruserid(int show_baruserid) {
		this.show_baruserid = show_baruserid;
	}

	public int getHide_baruserid() {
		return hide_baruserid;
	}

	public void setHide_baruserid(int hide_baruserid) {
		this.hide_baruserid = hide_baruserid;
	}

	public int getRepair_baruserid() {
		return repair_baruserid;
	}

	public void setRepair_baruserid(int repair_baruserid) {
		this.repair_baruserid = repair_baruserid;
	}

	public int getDice() {
		return dice;
	}

	public void setDice(int dice) {
		this.dice = dice;
	}

	/*
	* 检测胡牌人数
	* */
    public List<UserBean> checkHu(int brand, UserBean userBean) {
	    List<UserBean> list = new ArrayList<>();
        for (UserBean user : getGame_userlist()) {
            // 如果用户已弃牌则不检测
            if (user.getHu_state() == 2 || user.getIshu()==1)
                continue;
            if (user.getUserid() == userBean.getUserid())
                continue;
            if (ISBrand(user, brand, this) != 0) {
                list.add(user);
            }
        }
        return list;
    }

	/**
	 * 获取自定义的房间信息和自定义的用户信息
	 *
	 * @param table
	 * @param returnMap
	 * 			@param usertable @throws
	 */
	public void getRoomBean_Custom2(String usertable, Map<String, Object> returnMap, String table) {
		getRoomBean_Custom(table, returnMap);
		returnMap.put("userlist", getuser_custom());
	}
	/**
	 * 自定义获取用户列
	 */
	public List<String> getuser_custom() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < getGame_userlist().size(); i++) {
			list.add(getGame_userlist().get(i).getAvatarurl());
		}
		return list;
	}
}
