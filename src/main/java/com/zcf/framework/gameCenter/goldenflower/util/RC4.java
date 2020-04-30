package com.zcf.framework.gameCenter.goldenflower.util;

import java.io.UnsupportedEncodingException;

public class RC4 {
	private static final String key="804AED6DEC16BDCD303A631DC16D6FC1";
	public static String decry_RC4(byte[] data, String key) {
        if (data == null || key == null) {
            return null;
        }
        return asString(RC4Base(data));
    }

	/**
	 * 解密
	 * @param data
	 * @return
	 */
    public static String decry_RC4(String data) {
        if (data == null || key == null) {
            return null;
        }
        return new String(RC4Base(HexString2Bytes(data)));
    }


    public static byte[] encry_RC4_byte(String data, String key) throws UnsupportedEncodingException {
        if (data == null || key == null) {
            return null;
        }
        byte b_data[] = data.getBytes("UTF-8");
        return RC4Base(b_data);
    }

    /**
     * 加密
     * @param data
     * @param key
     * @return
     */
    public static String encry_RC4_string(String data) {
        if (data == null || key == null) {
            return null;
        }
        try {
			return toHexString(asString(encry_RC4_byte(data, key)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }


    private static String asString(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length);
        for (int i = 0; i < buf.length; i++) {
            strbuf.append((char) buf[i]);
        }
        return strbuf.toString();
    }


    private static byte[] initKey(String aKey) {
        byte[] b_key=null;
		try {
			b_key = aKey.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
        byte state[] = new byte[256];

        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (b_key == null || b_key.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    private static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch & 0xFF);
            if (s4.length() == 1) {
                s4 = '0' + s4;
            }
            str = str + s4;
        }
        return str;// 0x表示十六进制
    }


    private static byte[] HexString2Bytes(String src) {
        int size = src.length();
        byte[] ret = new byte[size / 2];
        byte[] tmp = null;
        try {
			tmp = src.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        for (int i = 0; i < size / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        char _b0 = (char)Byte.decode("0x" + new String(new byte[] { src0 }))
                .byteValue();
        _b0 = (char) (_b0 << 4);
        char _b1 = (char)Byte.decode("0x" + new String(new byte[] { src1 }))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private static byte[] RC4Base (byte [] input) {
        int x = 0;
        int y = 0;
        byte skey[] = initKey(key);
        int xorIndex;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((skey[x] & 0xff) + y) & 0xff;
            byte tmp = skey[x];
            skey[x] = skey[y];
            skey[y] = tmp;
            xorIndex = ((skey[x] & 0xff) + (skey[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ skey[xorIndex]);
        }
        return result;
    }

public static void main(String[] args) {
	//String inputStr = "中国1234";
    //String key = "804AED6DEC16BDCD303A631DC16D6FC1";
    //String str = encry_RC4_string(inputStr);
   // System.out.println(System.getProperty("file.encoding"));
   // System.out.println(decry_RC4("b0e2feaacc8c73cd05a80525556a396e1845a80b3ab555ebfcbf6b79ffea3dbd1f806d038c122e88eda7527e5be7437766fe5fda0b29e1a610326d80a93f7791bc4c99a6ba61112d6305720b3cc04ac69b1b9756db6dad42de19831defaa244fd9a4eb2aaa694cd9974b1eb310f41433c6e820238605f42be74bee1bc24284b7618d869f3ba69b90c039567777fc50f1c1378d47f11ef458d06e4c49623b45a1a8935af049c42c7e4f0d29a6d7193a6b02"));
}
}
