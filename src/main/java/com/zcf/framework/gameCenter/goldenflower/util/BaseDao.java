package com.zcf.framework.gameCenter.goldenflower.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties; 

import org.apache.log4j.Logger;
import util.UtilClass;


/**
 * 
 * 连接DAO
 * <对数据库的操作类>
 * 
 * @author  李挺宇
 * @version  [版本号, 2013-3-19]
 */
public  class BaseDao {
	//log4j
	static Logger log=Logger.getLogger(BaseDao.class.getName());
	//连接池
	public static List<Map<String,Object>> connectionList =  Collections.synchronizedList(new LinkedList<Map<String,Object>>());
	/** 驱动路径 */
	private static String driver = "";
	/** 连接字符串*/
	private static String url = "";
	/** 连接账号 */
	private static String user = "";
	/** 连接密码 */
	private static String password = "";
	/** 最小连接数 */
	private static int min=200;
	/** 最大连接数 */
	private static int max=800;
	/** 放弃连接时间 */
	private static int time=18000;
	/** 配置文件 */
	private static String FILE_PATH_NAME = "/database.properties";   
	private Connection connection = null;
	private PreparedStatement prStatement = null;
	public ResultSet resultSet = null;
	private Map<String,Object> connectionMap = null;
	/**
	 *静态加载驱动
	 */
	static {
		try {    
			 InputStream in = BaseDao.class.getResourceAsStream(FILE_PATH_NAME);    
			 Properties props = new Properties();    
			 props.load(in);    
			 in.close();   
		     driver = props.getProperty("jdbc.drivers");    
		     url = props.getProperty("jdbc.url");    
		     user = props.getProperty("jdbc.username");    
		     password = props.getProperty("jdbc.password");  
		     min = Integer.parseInt(props.getProperty("jdbc.min"));  
		     max = Integer.parseInt(props.getProperty("jdbc.max"));
		     //加载驱动
		 	Class.forName(driver);
		 	//加载初始连接
		 	for(int i=0;i<min;i++){
		 		//放入连接池
		 		connectionList.add(getCon());
		 	}
		 	System.out.println("初始化连接数"+connectionList.size());
		   } catch (IOException e) {    
		          e.printStackTrace();    
		   } catch (ClassNotFoundException e) {
			   	  e.printStackTrace();
		   } 
	}
	/**
	 * 获取连接
	 * @return
	 */
	private static Map<String,Object> getCon(){
		Map<String,Object> map = new HashMap<String, Object>();
 		//获取连接
 		try {
			map.put("connection", DriverManager.getConnection(url, user, password));
		} catch (SQLException e) {
			e.printStackTrace();
		}
 		//获取连接的时间
 		map.put("date", UtilClass.utilClass.getSystemDate());
 		return map;
	}
	/**
	 * 公用增删改
	 * @throws SQLException 
	 */
	public String executeUpdate(String sql,Object[] params)  {
		//获取连接
		while(connectionMap==null){
			try {
				connectionMap =  getConnection();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//取出连接
		this.connection = (Connection) connectionMap.get("connection");
		try {
			prStatement = connection.prepareStatement(sql);
			if(params!=null && params.length>0){
				for (int i = 0; i < params.length; i++) {
					prStatement.setObject(i+1, params[i]);
				}
			}
			log.info(prStatement);
			return prStatement.executeUpdate()!=0?"success":"sql_error";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "error";
	}

	/**
	 * 公用查询
	 */
	public String executeAll(String sql,Object[] params){
		//获取连接
		while(connectionMap==null){
			try {
				Thread.sleep(200);
				connectionMap =  getConnection();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//获取连接
		this.connection = (Connection) connectionMap.get("connection");
		try {
			prStatement=connection.prepareStatement(sql);
			//循环取出条件，并追加到sql语句中
			if(params!=null){
				for(int i=0;i<params.length;i++){
					prStatement.setObject(i+1, params[i]);
				}
			}
			log.info(prStatement);
			this.resultSet = prStatement.executeQuery();
			return "success";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "error";
	}
	public void CloseAll(){
		try {
			if(connectionMap!=null){
				close(connectionMap);
				connectionMap=null;
			}
			if(this.resultSet!=null){
				this.resultSet.close();
				this.resultSet=null;
			}
			if(this.prStatement!=null){
				this.prStatement.close();
				this.prStatement = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取连接
	 */
	private  static synchronized Map<String,Object> getConnection() {
		while(true){	
		//判断连接池是否有连接
			if(connectionList.size()>0){
					//获取第一个连接
					Map<String,Object> map = connectionList.get(0);
					connectionList.remove(0);
					String date = map.get("date").toString();
					//获得距离获取连接时间过了多少秒
					long stime = UtilClass.utilClass.daysBetween_S(date, UtilClass.utilClass.getSystemDate());
					//如果超时则关闭
					if(stime>time){
						Connection conn = (Connection) map.get("connection");
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else{
						System.out.println("获取连接成功当前连接数"+connectionList.size());
						return map;
					}
				}else{
					if(connectionList.size()>=max){
						System.out.println("连接数最大值"+max+"当前连接数"+connectionList.size());
						return null;
					}else{
						//获取连接
						System.out.println("获取新连接");
						return getCon();
					}
				}
			}
	}
	/**
	 * close方法
	 */
	public  static synchronized void close(Map<String,Object> conMap) {
		String date = conMap.get("date").toString();
		//获得距离获取连接时间过了多少秒
		long stime = UtilClass.utilClass.daysBetween_S(date, UtilClass.utilClass.getSystemDate());
		//如果连接使用时间超出抛弃时间则进行抛弃不放入连接池
		if(stime>time){
			Connection conn = (Connection) conMap.get("connection");
			try {
				conn.close();
				System.out.println("连接被抛弃当前连接池数量"+connectionList.size());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			//放入连接池
			connectionList.add(conMap);
			System.out.println("连接放入连接池当前连接池数量"+connectionList.size());
		}
	}
}
