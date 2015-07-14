package org.together.pub.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/** 
	 *  MD5加码 生成32位md5码 
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	/** 
	 * 加密解密算法 执行一次加密，两次解密 
	 */
	public static String convertMD5(String inStr) {

		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;

	}

	public static String getOriginalStr(String md5Str) {
		return convertMD5(convertMD5(md5Str));

	}

	public static void main(String[] args) {
		String s = new String("123456");
		System.out.println("原始：" + s);
		System.out.println("MD5后：" + getMD5Str(s));
		System.out.println("加密的：" + convertMD5(s));
		System.out.println("解密的：" + getOriginalStr(s));

		// String jm = MD5Utils.JM("e10adc3949ba59abbe56e057f20f883e");
		// System.out.println(jm);
	}
}
