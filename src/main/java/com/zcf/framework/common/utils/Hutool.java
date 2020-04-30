package com.zcf.framework.common.utils;

import com.zcf.framework.common.json.Body;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Hutool {

	/**
	 * 时间转String（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @return
	 */
	public static String parseDateToString() {
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String time = dtf.format(date);
		return time;
	}
	/**
	 * 生成订单号
	 * @return
	 */
	public static String getOrderIdByTime() {
		String result = "";
		Random random = new Random();
		for (int i = 0; i < 3; i++) {
			result += random.nextInt(10);
		}
		return System.currentTimeMillis() +result ;
	}

	/**
	 * 时间转String（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @return
	 */
	public static String DateToString(LocalDateTime date) {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String time = dtf.format(date);
		return time;
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		// 定义一个字符串（A-Z，a-z，0-9）即63位；
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
		// 由Random生成随机数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		// 长度为几就循环几次
		for (int i = 0; i < length; ++i) {
			// 产生0-61的数字
			int number = random.nextInt(63);
			// 将产生的数字通过length次承载到sb中
			sb.append(str.charAt(number));
		}
		// 将承载的字符转换成字符串
		return sb.toString();
	}

	/**
	 * 生成Id
	 * 
	 * @return
	 */
	public static String getId() {
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String time = dtf.format(date);
		StringBuffer bf = new StringBuffer();
		int len = time.length();
		bf.append(time.substring(0, 4));
		bf.append(getRandomString(2));
		bf.append(time.substring(4, 8));
		bf.append(getRandomString(2));
		bf.append(time.substring(8, 12));
		bf.append(getRandomString(2));
		bf.append(time.substring(12, len));

		return bf.toString();
	}

	/**
	 * 时间转String（yyyyMMddHHmmss）
	 * 
	 * @param date
	 * @return
	 */
	public static String parseDateForString(LocalDateTime date) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String time = dtf.format(date);
		return time;
	}

	/**
	 * String（yyyy-MM-dd HH:mm:ss）转时间（LocalDateTime）
	 * 
	 * @param time
	 * @return
	 */
	public static LocalDateTime parseStringToDate(String time) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime ldt = LocalDateTime.parse(time, dtf);
		return ldt;
	}

	/**
	 * String + String =String 加(会判断是否是纯数字)
	 */
	public static String add(String str1, String str2) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum1 = pattern.matcher(str1);
		Matcher isNum2 = pattern.matcher(str2);
		if (!isNum1.matches() && !isNum2.matches()) {
			return "NO";
		}
		int len1 = Integer.parseInt(str1);
		int len2 = Integer.parseInt(str2);
		int sum = len1 + len2;
		return String.valueOf(sum);

	}

	/**
	 * str1>str2?true:false
	 */
	public static Boolean compare(String str1, String str2) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum1 = pattern.matcher(str1);
		Matcher isNum2 = pattern.matcher(str2);
		if (!isNum1.matches() && !isNum2.matches()) {
			return null;
		}
		int len1 = Integer.parseInt(str1);
		int len2 = Integer.parseInt(str2);

		return len1 > len2;

	}

	/**
	 * str1-str2
	 */
	public static String subtract(String str1, String str2) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum1 = pattern.matcher(str1);
		Matcher isNum2 = pattern.matcher(str2);
		if (!isNum1.matches() && !isNum2.matches()) {
			return null;
		}
		int len1 = Integer.parseInt(str1);
		int len2 = Integer.parseInt(str2);
		return String.valueOf(len1 - len2);

	}

	/**
	 * 时间戳
	 */
	public static String getTime() {
		long time = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
		String date = String.valueOf(time);
		return date;
	}

	/**
	 * 返回map<String,String>类型
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static Map<String, String> returnMap(String str1, String str2) {
		Map<String, String> map = new HashMap<>();
		map.put("code", str1);
		if (null != str2) {
			map.put("msg", str2);
		}
		return map;
	}

	/**
	 * 判断map中code值返回Body
	 * 
	 * @param map
	 * @return
	 */
	public static Body returnBody(Map<String, String> map) {
		if (map.get("code").equals("1")) {
			return Body.BODY_200;
		} else {
			return Body.newInstance(0, map.get("msg"));
		}
	}
	/**
	 * 将一个数字拆分成随机的几个数字和
	 * n表示分成的个数，L表示数字
	 * @param n
	 * @param L
	 * @return
	 */
	public static List<Integer> randomSum(int n, int L) {
		Random rand = new Random();
		int temp = L;
		List<Integer> list=new ArrayList<>();
		for (int i = 1, j; i < n; i++) {
			j = rand.nextInt(temp - 1) + 1;

			if (j > temp - (n - i)) {// 保证每个随机数最小为1，那么j就不能大于temp-(n-i)
				j = temp - (n - i);
			} else if (j <= 0) {
				j = 1;
			}
			temp -= j;
			list.add(j);
		}
		list.add(temp);
		return list;
	}

	/**
	 * ] sumMoney 总钱数 layNum雷数
	 */
	public static int randomBag(int sumMoney, int layNum, Boolean bl, int remainNum, int bagSum, Boolean finalbl) {

		String money = String.valueOf(sumMoney);
		int len = money.length();
		StringBuilder sbmoney = new StringBuilder();
		int remainm = 0;// 抽取红包金额
		String str5 = "";
		Random random = new Random();
		StringBuilder sb = new StringBuilder();

		// -------------------------第三次没踩到---------------------
		if (!bl && remainNum == 3) {
			Boolean Be = true;
			int lens = 10;
			List<Integer> list = new ArrayList<>();
			while (Be) {
				Be = (layNum + lens) < sumMoney;
				if (Be) {
					list.add(layNum + lens);
					lens += 10;
				}
			}
			if (layNum == 1 && sumMoney < 10) {
				remainm = 2;
			} else {
				int a=list.size();
				int number = random.nextInt(list.size());
				remainm = sumMoney - list.get(number);
				sb.append(remainm);
				String str = sb.substring(sb.length() - 1, sb.length());
				if (String.valueOf(layNum).equals(str)) {
					remainm++;
				}
			}
		}
		// -------------------------第三次踩到---------------------
		if (bl && remainNum == 3) {
			Boolean Be = true;
			int lens = 0;
			List<Integer> list = new ArrayList<>();
			while (Be) {
				Be = (layNum + lens) < sumMoney;
				if (Be) {
					list.add(layNum + lens);
					lens += 10;
				}
			}
			remainm = random.nextInt(list.size());
		}

		// -------------------------第二次没踩到---------------------
		if (!bl && remainNum == 2) {
			if (finalbl) {
				Boolean Be = true;
				int lens = 0;
				List<Integer> list = new ArrayList<>();
				while (Be) {
					Be = (layNum + lens) < sumMoney;
					if (Be) {
						list.add(layNum + lens);
						lens += 10;
					}
				}
				for (int j = 0; j < list.size(); j++) {
					remainm = sumMoney - list.get(j);
					sb.append(remainm);
					String str = sb.substring(sb.length() - 1, sb.length());
					if (!String.valueOf(layNum).equals(str)) {
						remainm = sumMoney - remainm;
						break;
					}
				}
			} else {
				int lens = random.nextInt(sumMoney);
				remainm = sumMoney - lens;
				Boolean bn = true;
				while (bn) {
					sb.append(lens);
					String str = sb.substring(sb.length() - 1, sb.length());
					if (String.valueOf(layNum).equals(str)) {
						lens = random.nextInt(sumMoney);
						continue;
					} else {
						sb.setLength(0);
						sb.append(remainm);
						String str1 = sb.substring(sb.length() - 1, sb.length());
						if (!String.valueOf(layNum).equals(str1)) {
							bn = false;
						}
					}
				}
			}

		}
		// -------------------------第二次踩到---------------------
		if (bl && remainNum == 2) {
			Boolean Be = true;
			int lens = 0;
			List<Integer> list = new ArrayList<>();
			while (Be) {
				Be = (layNum + lens) < sumMoney;
				if (Be) {
					list.add(layNum + lens);
					lens += 10;

				}
			}
			for (int j = 0; j < list.size(); j++) {
				remainm = sumMoney - list.get(j);
				sb.append(remainm);
				String str = sb.substring(sb.length() - 1, sb.length());
				if (!String.valueOf(layNum).equals(str)) {
					remainm = list.get(j);
					break;
				}
			}

		}
		// ------------------------前3次踩到或者没踩到处理----------------------------------------------------------
		if (bl && remainNum > 3) {// true 踩到雷
			int number = random.nextInt(sumMoney - (layNum * (remainNum - 2) * remainNum));
			while (number == 0) {
				number = random.nextInt(sumMoney - (layNum * (remainNum - 2) * remainNum));
			}
			sb.append(number);
			sb.replace(sb.length() - 1, sb.length(), String.valueOf(layNum));
			remainm = Integer.parseInt(sb.toString());
		}
		if (!bl && remainNum > 3) {// false 没踩到雷
			int randomAll=sumMoney - (layNum * (remainNum - 2) * remainNum);
			for(int z =7;z>3;z--) {
				if(z==7) {
					randomAll=Math.round(sumMoney/3);
				}
			}
			remainm = 1 + random.nextInt(randomAll);
			while (remainm == 0) {
				remainm = random.nextInt(randomAll);
			}
			String si = String.valueOf(remainm);
			int les = si.length();
			sb.append(si);
			String str = sb.substring(sb.length() - 1, sb.length());
			if (String.valueOf(layNum).equals(str)) {
				remainm++;
			}
		}
		return remainm;
	}
	/**
	 * 字符串中随机生成
	 * @param str  字符串
	 * @param length  生成的长度
	 * @return
	 */
	public static String randomNum(String str, int length) {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			// 产生0-4的数字
			int number = random.nextInt(str.length());
			// 将产生的数字通过length次承载到sb中
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	
public static void main(String[] args) {
	 int a=4563;
	    int b=100;
	    DecimalFormat df=new DecimalFormat("0.00");

	    System.out.println(df.format((float)a/b));
	    System.out.println(df.format(a/(float)b));
	    System.out.println(df.format((float)a/(float)b));
	    System.out.println(df.format((float)(a/b)));

	
}
}
