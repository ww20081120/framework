package com.hbasesoft.framework.wechat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 * 
 * @author unknown
 */
public class MD5Encoder {

    /**
     * Byte2hex.
     * 
     * @param b
     *            需要加密的byte数组
     * @return 加密后得到的字符串
     */
    private static String byte2hex(byte[] b) {
	String hs = "";
	String stmp = "";
	for (int n = 0; n < b.length; n++) {
	    stmp = (Integer.toHexString(b[n] & 0XFF));
	    if (stmp.length() == 1) {
		hs = hs + "0" + stmp;
	    } else {
		hs = hs + stmp;
	    }
	}
	// 2014年6月17日修改，用于与审批系统对接
	// return hs.toUpperCase();
	return hs.toLowerCase();
    }

    /**
     * 加密方法.
     * 
     * @param input
     *            需要加密的字符串
     * @return 加密后得到的字符串
     */
    public static String encode(String input) {
    	byte[] digesta = null;
    	try {
    	    MessageDigest alga = MessageDigest.getInstance("MD5");
    	    alga.update(input.getBytes());
    	    digesta = alga.digest();
    	} catch (NoSuchAlgorithmException e) {
    	    // throw new SystemException(ErrorCode.UNKNOW_ERROR, e);
    	}
    	if(digesta != null){
    	    return byte2hex(digesta);
    	}
    	return null;
    }

    /**
     * 加密方法.
     * 
     * @param input
     *            需要加密的字符串
     * @return 加密后得到的字符串
     */
    public static String encodeEx(String input) {
    	byte[] digesta = null;
    	try {
    	    MessageDigest alga = MessageDigest.getInstance("MD5");
    	    alga.update(input.getBytes());
    	    digesta = alga.digest();
    	} catch (NoSuchAlgorithmException e) {
    	    // throw new SystemException(ErrorCode.UNKNOW_ERROR, e);
    	}
    	if(digesta != null){
            return byte2hex(digesta);
        }
        return null;
    }

    /**
     * 测试方法
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
	// System.out.println(encode("1"));
    }

}
