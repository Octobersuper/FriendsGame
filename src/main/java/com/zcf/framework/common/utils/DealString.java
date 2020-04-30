package com.zcf.framework.common.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 真实字符类
 * 
 * @author Administrator
 *
 */
public class DealString {
	public static String dostring(String str) {
		if (StringUtils.isBlank(str))
			return str;
		str = str.replaceAll(";", "");
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("'", "");
		str = str.replaceAll("--", "");
		str = str.replaceAll("/", "");
		str = str.replaceAll("%", "");
		return str;
	}

	public static boolean hasAttackStr(String str) {
		String attstr = "exec|insert|select|delete|update|truncate|char|declare|--|script|varchar|WHERE|UPDATE%09|%09";
		// String attstr =
		// "'|and|exec|insert|select|delete|update|count|chr|mid|master|truncate|char|declare|--|script|varchar|WHERE|UPDATE%09|%09";
		String[] atts = attstr.split("\\|");
		for (int i = 0; i < atts.length; i++) {
			if (str.toLowerCase().indexOf(atts[i].toLowerCase()) != -1) {
				return true;
			}
		}
		return false;
	}

	public static String listToString(List<String> stringList) {
		if (stringList == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (String string : stringList) {
			if (flag) {
				result.append(",");
			} else {
				flag = true;
			}
			result.append(string);
		}
		return result.toString();
	}

	public static String getCodeMaxPlus(String head, Long sourceNumber, int bitCount) {
		String numStr = String.valueOf(sourceNumber);
		while (numStr.length() < bitCount)
			numStr = "0" + numStr;
		return head + numStr;
	}
}
