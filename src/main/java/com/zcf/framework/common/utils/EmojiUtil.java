package com.zcf.framework.common.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Emoji表情处理类
 *
 */
public class EmojiUtil {

	/**
	 * 将str中的emoji表情转为byte数组
	 *
	 * @param source
	 * @return
	 */
	public static String resolveToByteFromEmoji(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		Pattern pattern = Pattern.compile(
				"[^(\u2E80-\u9FFF\\w\\s`~!@#\\$%\\^&\\*\\(\\)_+-？（）——=\\[\\]{}\\|;。，、《》”：；“！……’:'\"<,>\\.?/\\\\*)]");
		Matcher matcher = pattern.matcher(source);
		StringBuffer sb2 = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb2, resolveToByte(matcher.group(0)));
		}
		matcher.appendTail(sb2);
		return sb2.toString();
	}

	/**
	 * 将str中的byte数组类型的emoji表情转为正常显示的emoji表情
	 *
	 * @param source
	 * @return
	 */
	public static String resolveToEmojiFromByte(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		Pattern pattern2 = Pattern.compile("<:([[-]\\d*[,]]+):>");
		Matcher matcher2 = pattern2.matcher(source);
		StringBuffer sb3 = new StringBuffer();
		while (matcher2.find()) {
			matcher2.appendReplacement(sb3, resolveToEmoji(matcher2.group(0)));
		}
		matcher2.appendTail(sb3);
		return sb3.toString();
	}

	private static String resolveToByte(String str) {
		byte[] b = str.getBytes();
		StringBuffer sb = new StringBuffer();
		sb.append("<:");
		for (int i = 0; i < b.length; i++) {
			if (i < b.length - 1) {
				sb.append(Byte.valueOf(b[i]).toString() + ",");
			} else {
				sb.append(Byte.valueOf(b[i]).toString());
			}
		}
		sb.append(":>");
		return sb.toString();
	}

	private static String resolveToEmoji(String str) {
		str = str.replaceAll("<:", "").replaceAll(":>", "");
		String[] s = str.split(",");
		byte[] b = new byte[s.length];
		for (int i = 0; i < s.length; i++) {
			b[i] = Byte.valueOf(s[i]);
		}
		return new String(b);
	}

	public static void main(String[] args) {
		/*System.out.println(EmojiUtil.resolveToByteFromEmoji("😆"));
		System.out.println(EmojiUtil.resolveToByteFromEmoji("😂"));
		System.out.println(EmojiUtil.resolveToByteFromEmoji("🐶"));
		System.out.println(EmojiUtil.resolveToEmojiFromByte("哈哈<:-16,-97,-104,-122:>哈哈，。"));*/
	}
}