package com.zcf.framework.gameCenter.goldenflower.bean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 房间类
 */
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import com.zcf.framework.gameCenter.goldenflower.dao.F_GameDao;
import com.zcf.framework.gameCenter.goldenflower.service.F_GameService;
import com.zcf.framework.gameCenter.goldenflower.service.StartGame;
import util.BaseDao;
import util.UtilClass;


public class F_PKBean {
	public F_PKBean(){
		//最大游戏局数
		max_number=Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "max_number"));
		//初始化游戏房间信息
		Initialization();
	}
	private List<UserBean> game_userList;// 参与用户的集合
	private String room_number;//房间号
	private int banker_id;//庄家id
	private int[] user_positions;//用户位置0-4下标代表1-5的座位，值代表用户id
	private int game_number;//当前游戏局数
	private int max_number=0;//最大游戏轮数
	private int state;//房间状态0未开始1已开始2等待发牌3已发牌4等待初始化
	private int bottom_pan=0;//房间总注
	private int bets=0;//房间当前注数
	private int onkey=-1;//作弊值（如值>-1则下一次房间的牌值就是指定的牌）
	private int room_type;//房间类型0=初级，1=中，2=高
	private Lock ready_lock;//房间锁
	private String brands;//游戏局的牌 
	private int operation_userid;//操作人id
	private int victoryid;//胜利的人
	private int zbets;// 底注
	private int max_zbet;//封注
	private String room_money_parameter;//房间金币配置
	private String[] cheats = new String[6];//作弊
	private int max_person = 8;//最大人数
	private int leopard;//豹子喜钱
	private int time;//房间倒计时
	private int before_userid;//上一轮操作人id
	private int room_num;//局数
	private int number_3;//亲友圈号
	private int room_state;//放间状态  0未开始  1已开始

	public int getRoom_state() {
		return room_state;
	}

	public void setRoom_state(int room_state) {
		this.room_state = room_state;
	}

	public int getNumber_3() {
		return number_3;
	}

	public void setNumber_3(int number_3) {
		this.number_3 = number_3;
	}

	public int getRoom_num() {
		return room_num;
	}

	public void setRoom_num(int room_num) {
		this.room_num = room_num;
	}

	public int getBefore_userid() {
		return before_userid;
	}

	public void setBefore_userid(int before_userid) {
		this.before_userid = before_userid;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getLeopard() {
		return leopard;
	}

	public void setLeopard(int leopard) {
		this.leopard = leopard;
	}

	public int getMax_person() {
		return max_person;
	}

	public void setMax_person(int max_person) {
		this.max_person = max_person;
	}

	/***********************************get/set工具区***********************************************/
	//获取下个操作人的id
	public int getNextUserId(){
		int userid=-1;
		System.out.println("上一轮操作人id"+operation_userid);
		before_userid = operation_userid;
		int thisindex=-1;
		for(int i=0;i<user_positions.length;i++){
			if(user_positions[i]==operation_userid){
				thisindex=i;
			}
		}
		for(int i=0;i<9;i++){
			if((thisindex-1)<0){
				thisindex=8;
			}
			if(user_positions[thisindex-1]!=-1 && getUserBean(user_positions[thisindex-1]).getGametype()!=-1){
				userid = user_positions[thisindex-1];
				break;
			}
			thisindex--;
		}
		//为此用户创建一个倒计时线程
		//new Time_Game(this,userid);
		operation_userid=userid;
		return userid;
	}
	//初始化房间
	public void Initialization(){
		//初始化扑克牌1副牌
		setBrands(1);
		bottom_pan = 0;
		//当前轮数
		game_number=1;
		bets = zbets;
		if(game_userList!=null){
			for (UserBean user :
					game_userList) {
				user.Initialization();
			}
		}
		//启用一个房间线程用来开始游戏
		StartGame startGame=new StartGame(this,new F_GameService(new BaseDao()));
		startGame.start();
	}
	//获取房间自定义信息
	public void getPKBean_Custom(String usertable,String tablename,Map<String,Object> map){
		String names[] = tablename.split("-");
		for(String key:names){
			if(key.equals("game_userList"))map.put(key,getGame_userList(usertable));
			//if(key.equals("sidelines_userList"))map.put(key,sidelines_userList);
			if(key.equals("room_number"))map.put(key,room_number); 
			if(key.equals("banker_id"))map.put(key,banker_id);
			if(key.equals("user_positions"))map.put(key,user_positions);
			if(key.equals("state"))map.put(key,state);
			if(key.equals("bottom_pan"))map.put(key,bottom_pan);
			if(key.equals("game_number"))map.put(key,game_number);
			if(key.equals("room_type"))map.put(key,room_type);
			if(key.equals("bets"))map.put(key,bets);
			if(key.equals("max_zbet"))map.put(key,max_zbet);
			if(key.equals("zbets"))map.put(key,zbets);
			if(key.equals("operation_userid"))map.put(key,operation_userid);
			if(key.equals("max_zbet"))map.put(key,max_zbet);
			if(key.equals("leopard"))map.put(key,leopard);
			if(key.equals("max_person"))map.put(key,max_person);
			if(key.equals("room_num"))map.put(key,room_num);
			if(key.equals("person_num"))map.put(key,game_userList.size());
		}
	}
	//获取用户集合自定义信息
	public List<Map<String,Object>> getGame_userList(String tablename) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<getGame_userList(0).size();i++){
			Map<String,Object> map = new HashMap<String, Object>();
			getGame_userList(0).get(i).getUser_Custom(tablename, map);
			list.add(map);
		}
		return list;
	}
	//初始化用户位置
	public void setUser_positions(int[] user_positions){
		this.user_positions = user_positions;
	}
	//剔除指定用户的位置
	public void remove_opsitions(int userid){
		for(int i=0;i<user_positions.length;i++){
			if(user_positions[i]==userid){
				user_positions[i]=-1;
			}
		}
	}
	//初始化扑克牌
	public void setBrands(int number) {
		StringBuffer brand = new StringBuffer();
		for(int i=0;i<52*number;i++){
			if(i>0){
				brand.append("-");
			}
			brand.append(i);
		}
		this.brands = brand.toString();
	}
	//加入用户位置-顺序加入
	public void setUser_positions(UserBean userBean){
		for(int i=0;i<user_positions.length;i++){
			if(user_positions[i]==-1){
				user_positions[i]=userBean.getUserid();
				break;
			}
		}
	}
	//返回最后一张牌值最大的玩家id
	public String[] getMaxBrandUser(){
		List<UserBean> users = getGame_userList(1);
		String[] max_user = new String[users.size()];
		for(int i=0;i<users.size();i++){
			max_user[i]=users.get(i).getUserid()+"-"+users.get(i).getBrand_M(1);
		}	
		for(int i=0;i<max_user.length;i++){
			for(int j=i;j<max_user.length;j++){
				if((j+1)<max_user.length){
					int number = Integer.parseInt(max_user[i].split("-")[1]);
					int number2 = Integer.parseInt(max_user[j+1].split("-")[1]);
					if(number<number2){
						String user = max_user[j+1];
						max_user[j+1]=max_user[i];
						max_user[i]=user;
					}
				}
			}
		}
		//判断是否有相同大小的牌
		int count =0;
		for(int i=1;i<max_user.length;i++){
			int number = Integer.parseInt(max_user[0].split("-")[1]);
			int number2 = Integer.parseInt(max_user[0].split("-")[1]);
			if(number==number2)count++;
		}
		//如果有相同大小的牌那么寻找上一位操作人id是否存在其中
		if(count>0){
			for(int i=0;i<count;i++){
				int userid = Integer.parseInt(max_user[i].split("-")[0]);
				if(operation_userid==userid){
					String user = max_user[0];
					max_user[0]=max_user[i];
					max_user[i]=user;
					return max_user;
				}
			}
		}
		//执行到此步代表没有找到上一位操作人的id
		max_user[0]=getNextUserId()+"-0";
		operation_userid=Integer.parseInt(max_user[0].split("-")[0]);
		//为此用户创建一个倒计时线程
		//new Time_Game(this,operation_userid);
		return max_user;
	}
	//发牌
	public void GrantBrand(int number){
		for(UserBean user:getGame_userList(0)){
			//-1不可参与游戏
			if(user.getGametype()!=-1){
				for(int i=0;i<number;i++){
					user.setBrand(getBrand());
				}
			}
		}
		//game_number+=number;
	}
	//随机获取一张牌(返回单副牌的下标)
	public int getBrand(){
		String[] brand = brands.split("-");
		int index = (int)(Math.random() * brand.length);
		//获取总牌的下标
		int bands_index = Integer.parseInt(brand[index]);
		//剔除此牌
		RemoveBrand(bands_index);
		//计算单副牌的下标
		//System.out.println("单牌下标"+UtilClass.utilClass.Transformation(bands_index));
		return bands_index;
	}
	//剔除一张牌
	public void RemoveBrand(int index){
		String[] brand = brands.split("-");
		StringBuffer brand_str = new StringBuffer();
		for(int i=0;i<brand.length;i++){
			if(Integer.parseInt(brand[i])!=index){
				if(!"".equals(brand_str.toString()))brand_str.append("-");
				brand_str.append(brand[i]);
			}
		}
		brands=brand_str.toString();
	}
	//扣除底注
	public void DeductionBottom_Pan(F_GameDao gameDao){
		for(UserBean userBean:game_userList){
			if(userBean.getGametype()!=-1){
				gameDao.UpdateUserMoney(userBean.getUserid(),bets,0);
				userBean.setMoney(userBean.getMoney()-bets);
				bottom_pan+=bets;
			}
		}
	}
	//将所有玩家的下注放入底锅
	public void NewDeductionBottom_Pan(F_GameDao gameDao){
		for(UserBean userBean:game_userList){
			bottom_pan+=userBean.getBets();
			userBean.setBets(0);
		}
	}
	//用户下注
	public void DeductionBottom_Pan(UserBean userBean,int bets,int betstype){
		//加注
		if(betstype==2){
			//将房间底注追加
			this.bets=bets;
		}else if(betstype==1){//跟注
			bets=this.bets;
		}
		//增加用户下注数
		userBean.setBets(userBean.getBets()+bets);
		//添加用户下注总额
		userBean.setWinnum(userBean.getWinnum()+bets);
		//减去用户金钱
		userBean.setMoney(userBean.getMoney()-bets);
		//增加房间总注
		bottom_pan+=bets;
	}
	/**
	 * 返回某一个用户
	 * @param userid
	 * @return
	 */
	public UserBean getUserBean(int userid){
		for(UserBean userBean:game_userList){
			if(userBean.getUserid()==userid){
				return userBean;
			}
		}
		return null;
	}
	//获取房间内用户集合type=0返回实例1返回过滤
	public List<UserBean> getGame_userList(int type) {
		if(game_userList==null){
			game_userList = new ArrayList<UserBean>();
		}
		//返回本实例参与游戏的玩家
		if(type==0){
			List<UserBean> list = new ArrayList<UserBean>();
			for(UserBean userBean:game_userList){
				//没有弃牌且已参与游戏的用户
				if(userBean.getUser_type()==1){
					list.add(userBean);
				}
			}
			return list;
		}else if(type==1){
			return game_userList;
		}
		List<UserBean> list = new ArrayList<UserBean>();
		for(UserBean userBean:game_userList){
			//没有弃牌且已参与游戏的用户
			if(userBean.getBrandstatus()!=2&&userBean.getGametype()!=-1){
				list.add(userBean);
			}
		}
		return list;
	}
	
	//
	/***********************************get/set***********************************************/
	
	public int getState() {
		return state;
	}
	public int getMax_zbet() {
		return max_zbet;
	}
	public void setMax_zbet(int max_zbet) {
		this.max_zbet = max_zbet;
	}
	public int getOperation_userid() {
		return operation_userid;
	}
	public void setOperation_userid(int operation_userid) {
		this.operation_userid = operation_userid;
	}
	public int getZbets() {
		return zbets;
	}
	public void setZbets(int zbets) {
		this.zbets = zbets;
	}
	public int getMax_number() {
		return max_number;
	}
	public void setMax_number(int max_number) {
		this.max_number = max_number;
	}
	public int getBets() {
		return bets;
	}
	public void setBets(int bets) {
		this.bets = bets;
	}
	public String getRoom_money_parameter() {
		return room_money_parameter;
	}
	public void setRoom_money_parameter(String room_money_parameter) {
		this.room_money_parameter = room_money_parameter;
	}
	public String[] getCheats() {
		return cheats;
	}
	public void setCheats(String[] cheats) {
		this.cheats = cheats;
	}
	public int getVictoryid() {
		return victoryid;
	}
	public void setVictoryid(int victoryid) {
		this.victoryid = victoryid;
	}
	public String getBrands() {
		return brands;
	}
	public Lock getReady_lock() {
		return ready_lock;
	}
	public void setReady_lock(Lock ready_lock) {
		this.ready_lock = ready_lock;
	}
	public int getRoom_type() {
		return room_type;
	}

	public void setRoom_type(int room_type) {
		this.room_type = room_type;
	}

	public int getBottom_pan() {
		return bottom_pan;
	}

	public void setBottom_pan(int bottom_pan) {
		this.bottom_pan = bottom_pan;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getOnkey() {
		return onkey;
	}

	public void setOnkey(int onkey) {
		this.onkey = onkey;
	}

	public int getGame_number() {
		return game_number;
	}

	public void setGame_number(int game_number) {
		this.game_number = game_number;
	}

	public String getRoom_number() {
		return room_number;
	}

	public void setRoom_number(String room_number) {
		this.room_number = room_number;
	}

	public int getBanker_id() {
		return banker_id;
	}

	public void setBanker_id(int banker_id) {
		this.operation_userid=banker_id;
		this.banker_id = banker_id;
	}

	public int[] getUser_positions() {
		return user_positions;
	}
	

	public void setGame_userList(List<UserBean> game_userList) {
		this.game_userList = game_userList;
	}


	/**
	 * getrooms
	 *
	 * @param
	 * @param
	 * @param
	 */
	public List<Map<String, Object>> getrooms(List<F_PKBean> list,String table, String usertable) {
		List<Map<String, Object>> map = new ArrayList<>();
		if(list.size()!=0){
			for (F_PKBean room:list) {
				Map<String, Object> returnMap = new HashMap<>();
				room.getPKBean_Custom(usertable,table, returnMap);
				map.add(returnMap);
			}
			return map;
		}else
			return map;
	}

	/**
	 * 获取自定义的房间信息和自定义的用户信息
	 *
	 * @param table
	 * @param returnMap
	 * 			@param usertable @throws
	 */
	public void getRoomBean_Custom2(String usertable, Map<String, Object> returnMap, String table) {
		getPKBean_Custom(usertable,table, returnMap);
	}

	public void removeOutUser() {
		if(game_userList.size()!=0){
			for (int i = game_userList.size()-1; i >= 0; i--) {
				if(game_userList.get(i).getGametype()==2){
					game_userList.remove(game_userList.get(i));
					remove_opsitions(game_userList.get(i).getUserid());
				}
			}
			if(game_userList.size()==0){
				F_PublicState.PKMap.remove(this.getRoom_number());
			}
		}else{
			F_PublicState.PKMap.remove(this.getRoom_number());
		}
	}
}
