package com.zcf.framework.common.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密解密工具类
 *
 * @author shenguohao
 */
public class DesUtils {

	private static String DES = "DES";
	private static String SECRET_KEY = "hUfNg?;!-@#$*%2@%^*(*()P";

	private DesUtils() {
	}

	public static void main(String[] args) throws Exception {
		// String code = "3~41~" + System.currentTimeMillis();
//		String code = "2你好是的大方";
//		System.out.println(code);
//		String token = DesUtils.encrypt(code);
//		System.out.println(token);
//		System.out.println(token.length());
//		String six = DesUtils.decrypt(token);
//		System.out.println(six);

		/*
		 * String token = "O.n-6MC6dp5y2XFh5MlE.66BYZhNFVW6";
		 * System.out.println(URLEncoder.encode(token)); code = DesUtils.decrypt(token);
		 * System.out.println(code);
		 */
	}

	/**
	 * 加密用户名+id
	 * 
	 * @param uid
	 * @param username
	 */
	public String jiami(Long uid, String username) {
		String id = uid.toString();
		String lastStr = "";
		try {
			//lastStr = DesUtils.encrypt(username + id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastStr;
	}

	public void check() {

	}

	/**
	 * DES加密
	 */
//	public static String encrypt(String data) throws Exception {
//		String encrypt = DesUtils.encrypt(data, DesUtils.SECRET_KEY);
//		return encrypt.replace("+", "-").replace("/", ".");
//	}

	/**
	 * DES加密
	 */
//	private static String encrypt(String data, String key) throws Exception {
//		byte[] bt = DesUtils.encrypt(data.getBytes(), key.getBytes());
//		return Base64.encodeBase64String(bt);
//	}

	/**
	 * DES解密
	 */
//	public static String decrypt(String data) throws Exception {
//		String decrypt = data.replace("-", "+").replace(".", "/");
//		return DesUtils.decrypt(decrypt, DesUtils.SECRET_KEY);
//	}

	/**
	 * DES解密
	 */
//	public static String decrypt(String data, String key) throws Exception {
//		byte[] buf = Base64.decodeBase64(data);
//		return new String(DesUtils.decrypt(buf, key.getBytes()));
//	}

	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DesUtils.DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DesUtils.DES);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(data);
	}

	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DesUtils.DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DesUtils.DES);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(data);
	}
}
