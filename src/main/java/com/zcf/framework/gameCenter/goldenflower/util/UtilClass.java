package com.zcf.framework.gameCenter.goldenflower.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import util.RC4;


public class UtilClass {
	public static final UtilClass utilClass = new UtilClass(); 
	private Gson gson = new Gson();
	/**
	 * 获取提交请求的传递参数
	 * @param request
	 * @param tablename(数据库列保存的配置文件名)
	 *  @param columnname(键值名)
	 * @return
	 */
	public Map<String,Object> getRequestAdd(HttpServletRequest request,String tablename,String columnname){
		Map<String,Object> map = new HashMap<String,Object>();
		String[] tablenames = getTableName(tablename,columnname).split("-");
		for(int i=0;i<tablenames.length;i++){
			map.put(tablenames[i], request.getParameter(tablenames[i])==null||request.getParameter(tablenames[i]).equals("")?"-1":request.getParameter(tablenames[i]));
		}
		return map;
	}
	/**
	 * 获取提交请求的传递参数
	 * @param request
	 * @param tablename(数据库列保存的配置文件名)
	 *  @param columnname(键值名)
	 * @return
	 */
	public Map<String,Object> getRequestAdd(String json,String tablename,String columnname){
		Map<String,Object> map = gson.fromJson(RC4.decry_RC4(json), new TypeToken<Map<String,Object>>() {}.getType()) ;
		System.out.println("map"+map);
		String[] tablenames = getTableName(tablename,columnname).split("-");
		for(int i=0;i<tablenames.length;i++){
			if(map.get(tablenames[i])==null||map.get(tablenames[i]).equals("")){
				map.put(tablenames[i], "-1");
			}
		}
		return map;
	}
	/**
	 * 检测参数是否不为空
	 * @param tablename
	 * @param columnname
	 * @param map
	 * @param returnMap
	 * @return
	 */
	public void isRequest(String tablename,String columnname,Map<String,Object> map,Map<String,Object> returnMap){
		String[] tablenames = getTableName(tablename,columnname).split("-");
		for(int i=0;i<tablenames.length;i++){
			if("-1".equals(map.get(tablenames[i]).toString())){
				returnMap.put("error", "参数错误");
				returnMap.put("state", "100");
				columnname="";
				break;
			}
		}
	}
	/**
	 * 获取提交请求的传递参数(返回实体类)
	 * @param request
	 * @param tablename(数据库列保存的配置文件名)
	 *  @param columnname(键值名)
	 * @return
	 */
	public Object getRequestAdd(HttpServletRequest request,String tablename,String columnname,Class<?> type){
		Map<String,Object> map = new HashMap<String,Object>();
		String[] tablenames = getTableName(tablename,columnname).split("-");
		for(int i=0;i<tablenames.length;i++){
			map.put(tablenames[i], request.getParameter(tablenames[i])==null||request.getParameter(tablenames[i]).equals("")?"-1":request.getParameter(tablenames[i]));
		}
		try {
			return getBean(type, map);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	} 
	public Object getBean_E(Class<?> type,Map<String,Object> map){
		try {
			return getBean(type,map);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 反射出实体类
	 * @param type
	 * @param map
	 * @return
	 * @throws IntrospectionException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NumberFormatException 
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object getBean(Class<?> type,Map<String,Object> map) throws IntrospectionException, InstantiationException, IllegalAccessException, NumberFormatException, IllegalArgumentException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象    
        // 给 JavaBean 对象的属性赋值    
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        //循环获取到的map
        for(String key:map.keySet()){
        	//循环反射来的实体类
	        for(int i=0;i<propertyDescriptors.length;i++){
	        	//判断是否有和map键值一样的参数
	        	if(!"class".equals(propertyDescriptors[i].getName())&&key.equals(propertyDescriptors[i].getName())){
	        		//判断是否是数值
	        		if(propertyDescriptors[i].getPropertyType().getName().equals("int")){
	        			propertyDescriptors[i].getWriteMethod().invoke(obj,Integer.parseInt(map.get(key).toString()));
	        		}else if(propertyDescriptors[i].getPropertyType().getName().equals("java.lang.String")){
	        			//字符
	        			propertyDescriptors[i].getWriteMethod().invoke(obj,map.get(key).toString());
	        		}
	        	}
	        }
        }
		return obj;
	}
	/**
	 * 剔除map不需要的元素
	 * @param map
	 * @param name
	 * @return
	 */
	public Map<String,Object> removeMap(Map<String,Object> map,String[] name){
		for(int i=0;i<name.length;i++){
			map.remove(name[i]);
		}
		return map;
	}
	/**
	 * DES解密传输
	 * @param request
	 * @return
	 */
	public Map<String,Object> getRequestAdd_DES(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String,Object>();
		String desvalue = request.getParameter("value");
		if(desvalue!=null&&!"".equals(desvalue)){
		//desvalue = new DES().Decode(desvalue).toString();
			int index = desvalue.indexOf("*");
			if(index!=-1){
				desvalue = desvalue.substring(0,index);
			}
			String[] strs = desvalue.split("&");
			for(int i=0;i<strs.length;i++){
				String[] st = strs[i].split("=");
				map.put(st[0],st[1]==null||"".equals(st[1])?"-1":st[1]);
			}
		}
		return map;
	}
	/**
	 * 检测request接受值是否存在
	 * @param map
	 * @param tablename
	 * @param columnname
	 * @return
	 */
	public String checkRequest(Map<String,Object> map,String tablename,String columnname){
		String error = null;
		String[] tablenames = getTableName(tablename,columnname).split("-");
		for(int i=0;i<tablenames.length;i++){
			if(map.get(tablenames[i])==null||"-1".equals(map.get(tablenames[i]).toString())){
				error = tablenames[i]+"_error";
			}
		}
		return error;
	}
	/**
	 * 用于自动添加Object数组
	 * @param map
	 * @param type
	 * @param tablename
	 * @param columnname
	 * @return
	 */
	public Object[] addObjects(Map<String,Object> map,String tablename,String columnname){
		//获取键值
		String[] tablenames = getTableName(tablename,columnname).split("-");
		Object[] object = new Object[tablenames.length];
		for(int i=0;i<tablenames.length;i++){
			if(StringUtils.isNumeric(map.get(tablenames[i]).toString())){
				object[i]=Integer.parseInt(map.get(tablenames[i]).toString());
			}else{
				object[i]=map.get(tablenames[i]).toString();
			}
			
		}
		return object;
	}
	/**
	 * 获取数据表列名
	 * @param tablename
	 * @return
	 */
	public  String getTableName(String tablename,String columnname){
		try {    
				 InputStream in = getClass().getResourceAsStream(tablename);    
				 Properties props = new Properties();    
				 props.load(in);    
				 in.close();   
				 return props.getProperty(columnname);    
		} catch (IOException e) {    
			          e.printStackTrace();    
				}
		return null;
	}
	/**
	 * 接收数据库查询信息封装到map
	 * @param tablename(存放数据库列名的配置文件名)
	 * @param resultSet(数据集合)
	 * @param columnname(列名)
	 * @return
	 * @throws SQLException
	 */
	public Map<String,Object> getSqlMap(String tablename,ResultSet resultSet,String columnname) throws SQLException{
		Map<String,Object> map = new HashMap<String,Object>();
		String name = getTableName(tablename,columnname);
		String[] names = name.split("-");
		for(int i=0;i<names.length;i++){
				map.put(names[i], resultSet.getString(names[i]));
		}
		return map;
	}
	public Object getSqlBean(Class<?> type,String tablename,ResultSet resultSet,String columnname) throws SQLException{
		Map<String,Object> map = new HashMap<String,Object>();
		String name = getTableName(tablename,columnname);
		String[] names = name.split("-");
		for(int i=0;i<names.length;i++){
				map.put(names[i], resultSet.getString(names[i]));
		}
		try {
			return getBean(type, map);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 截取附件名称的长度
	 * @param fileName
	 * @return
	 */
	public String interceptFileName(String fileName){
		if(fileName.length() < 15){
			return fileName;
		}else{
			return fileName.substring(0,15)+"...";
		}
	}
	
	/**
	 * 将时间转换成年龄
	 * @param data
	 * @return
	 */
	public String DataOfAge(String data){
		Integer age = null;
		int birthdayYear = Integer.parseInt(data.substring(0,data.indexOf('-')));
		int birthdayMonth = Integer.parseInt(data.substring(data.indexOf('-')+1,data.lastIndexOf ('-')));
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = (calendar.get(Calendar.MONTH)+1);
		if(year-birthdayYear>0){
			if(month >= birthdayMonth){
				age = year-birthdayYear;
			}else{
				age = year-birthdayYear-1;
			}
		}else{
			age = 0;
		}
		return age.toString();
	}
	/**
	 * 计算2个经纬度的距离（米）
	 */
	private final static double PI = 3.14159265358979323; // 圆周率
    private final static double R = 6371229; // 地球的半径
    public int checklatlng(double lat1,double lng1,double lat2,double lng2){
//    	 lat1=34.276245;
//		 lng1=117.185185;
//		 lat2=34.275146;
//		 lng2=117.188438;
		double x, y, distance;
        x = (lat1 - lat2) * PI * R
                * Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
        y = (lat2 - lat1) * PI * R / 180;
        distance = Math.hypot(x, y);
        return (int) distance;
    }
 
	/** 
     * 计算两个日期之间相差的天数 
     * @param date1 
     * @param date2 
     * @return 
     */  
    public static int daysBetween(Date date1,Date date2)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date1);  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(date2);  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);  
          
       return Integer.parseInt(String.valueOf(between_days));         
    }  
    /**
     * 获取当前系统时间
     * @return
     */
    public String getSystemDate(){
    	SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return sdf.format(Calendar.getInstance().getTime());
    }
    public String getSystemDate(String format){
    	SimpleDateFormat sdf =   new SimpleDateFormat(format);
    	return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * 地图坐标转换（百度、腾讯）
     * @param lat
     * @param lon
     * @return
     */
    public String map_tx2bd(double lat, double lon){  
        double bd_lat;  
        double bd_lon;  
        double x_pi=3.14159265358979324;  
        double x = lon, y = lat;  
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);  
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);  
        bd_lon = z * Math.cos(theta) + 0.0065;  
        bd_lat = z * Math.sin(theta) + 0.006;   
        return bd_lat+"-"+bd_lon;  
    }  
	public String map_bd2tx(double lat, double lon){  
        double tx_lat;  
        double tx_lon;  
        double x_pi=3.14159265358979324;  
        double x = lon - 0.0065, y = lat - 0.006;  
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);  
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);  
        tx_lon = z * Math.cos(theta);  
        tx_lat = z * Math.sin(theta);  
        return tx_lat+","+tx_lon;  
    }
	
	/** 
     * 计算2个日期之间相差的秒数 
     * @param date 
     * @return 
     */  
    public long daysBetween_S(String indate,String outdate){
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long interval=0;
		try {
			interval = (sdf.parse(outdate).getTime() - sdf.parse(indate).getTime())/1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return interval;
    }
    /**
     * 判断当前时间是否包含在指定时间之内
     * @param date
     * @param tdate
     * @return
     */
    public boolean IFTDate(String date,String tdate){
    	 SimpleDateFormat sdf =  new SimpleDateFormat("HH:mm:ss");
    	 SimpleDateFormat sdf_HH =  new SimpleDateFormat("HH");
    	 SimpleDateFormat sdf_mm =  new SimpleDateFormat("mm");
    	 try {
			int HH = Integer.parseInt(sdf_HH.format(sdf.parse(date).getTime()));
			//获取当前系统小时
			int s_HH = Integer.parseInt(getSystemDate("HH"));
			//获取当前系统分钟
			int s_mm = Integer.parseInt(getSystemDate("mm"));
			//判断当前系统小时是否小于第一个时间的小时
			if(s_HH<HH){
				return false;
			}
			//获取第二个时间的小时和分钟
			HH = Integer.parseInt(sdf_HH.format(sdf.parse(tdate).getTime()));
			int mm = Integer.parseInt(sdf_mm.format(sdf.parse(tdate).getTime()));
			//判断统时间和分钟都不大于第二个时间的小时和分钟
			if(s_HH>=HH){
				if(s_mm>mm){
					return false;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return true;
    }
    /**
     * 从身份证号获取出生日期和性别
     * @param idnumber
     * @return
     */
    public Map<String,Object> editidnumber(String idnumber){
    	Map<String,Object> map = new HashMap<String,Object>();
		String number = idnumber.substring(6, 14);
		String year = number.substring(0, 4);
		String month = number.substring(4, 6);
		String day = number.substring(6, 8);
		map.put("birthday", year+"-"+month+"-"+day);
		//从身份证号中获取性别
		String sex = idnumber.substring(16,17);
		map.put("sex", Integer.parseInt(sex)%2==0?0:1);
		return map;

    }
    /**
	 * 获得IP地址
	 * @param request
	 * @return
	 */
	public  String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		} else {
			return ip;
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		} else {
			int index = ip.indexOf(',');
			if (index != -1) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}
	/**
	 * 将数据map转换成字符串(修改保存原值)
	 * @param map
	 * @param tablename
	 * @param columnname
	 * @return
	 */
	public String getSqlMap(Map<String,Object> map,String tablename,String columnname){
		StringBuffer stringmap = new StringBuffer();
		String name = getTableName(tablename,columnname);
		String[] names = name.split("-");
		for(int i=0;i<names.length;i++){
			if(i>0){
				stringmap.append(",");
			}
			stringmap.append(names[i]+"-"+map.get(names[i]));
		}
		return stringmap.toString();
	}
	/**
	 * 剔除map里未修改的值
	 * @param map
	 * @param stringmap
	 * @param sql
	 * @return
	 */
	public Map<String,Object> getSqlMap_update(Map<String,Object> map,String stringmap,StringBuffer sql){
		Map<String,Object> returnMap = new HashMap<String, Object>();
		//修改前原值
		String[] stringname = stringmap.split(",");
		boolean bool = false;
		for(int i=0;i<stringname.length;i++){
			String[] maovalue = stringname[i].split("-");
			//如果修改过的map值与修改前的值比较如果不同则代表修改否则不添加修改列
			if(!map.get(maovalue[0]).equals(maovalue[1])){
				if(bool){
					sql.append(",");
				}
				sql.append(maovalue[0]+"="+map.get(maovalue[0]));
				returnMap.put(maovalue[0],maovalue[1]);
				bool=true;
			}
		}
		return returnMap;
	}
	/**
	 * 判断char类型是否是英文和简体中文
	 * @param ch
	 * @return
	 */
	public boolean isValidChar(char ch) {
        if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')|| (ch >= 'a' && ch <= 'z'))
            return true;
//        if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
//            return true;// 简体中文汉字编码
        if((ch >= 0x4e00)&&(ch <= 0x9fbb))
            return true;// 简体中文汉字编码
        return false;
    }
	/**
	 * 判断字符值是否是整数
	 * @param str
	 * @return
	 */
	 public  boolean isInteger(String str) {  
	        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
	        return pattern.matcher(str).matches();  
	  }
	 public int Transformation(int bands_index){
		//计算单副牌的下标
		 bands_index-=54*(bands_index/54);
//		 if(bands_index!=52&&bands_index!=53){
//			 bands_index = bands_index-13*(bands_index/13);
//		}
		return bands_index;
	 }
	 public static void main(String[] args) {
		 
		 int number = UtilClass.utilClass.Transformation(185);
		 System.out.println(number);
	}
}	
