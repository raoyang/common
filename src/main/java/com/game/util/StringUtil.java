package com.game.util;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.CRC32;

public class StringUtil {

	public static boolean isBlank(String input) {

		if (input == null) {

			return true;
		}
		char[] chs = input.toCharArray();
		for (int i = 0; i < chs.length; i++) {

			if (!Character.isWhitespace(chs[i])) {

				return false;
			}
		}
		return true;
	}

	public static String createStackTrace(Throwable t) {

		PrintWriter pw = null;
		StringWriter sw = null;

		try {

			sw = new StringWriter();
			pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			return sw.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (pw != null) {

					pw.close();
				}
			} finally {

				try {
					if (sw != null) {

						sw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	public static Integer str2Int(String input) {

		try {

			return Integer.parseInt(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Long dateStr2Long(String input, String pattern) {

		if (isBlank(input))
			return null;
		try {

			if (isBlank(pattern)) {

				return DateTime.parse(input).getMillis();
			} else {

				DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
				return DateTime.parse(input, formatter).getMillis();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Long str2Long(String input) {

		if (isBlank(input))
			return null;
		try {
			return Long.parseLong(input);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decodeBase64Str(String input) {

		byte[] res = Base64.decodeBase64(input);

		try {
			return new String(res, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encodeBase64Str(String input) {

		try {
			return Base64.encodeBase64String(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String addSlash(String dirName) {
		if (dirName.endsWith("/"))
			return dirName;
		return dirName + "/";
	}

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// 截取数据库连接IP地址
	public static String parseUrlIpAddr(String url) {
		int syidx = url.indexOf("sybase");
		if (syidx != -1) {
			syidx = url.indexOf(":", syidx + 8);
			return url.substring(syidx + 1, url.indexOf(":", syidx + 1));
		}

		int idx = url.indexOf("://") + 3;
		if (idx < 3)
			idx = url.indexOf(":@") + 2;
		if (idx < 2)
			return url;

		// 处理数据库集群 多IP 输出使用逗号分割
		StringBuilder sb = new StringBuilder();
		if (url.toLowerCase().indexOf("host") != -1) {

			while (url.toLowerCase().contains("host")) {

				url = url.substring(url.toLowerCase().indexOf("host") + 4);
				String ip = url.substring(url.indexOf("=") + 1, url.indexOf(")"));
				sb.append(ip.trim() + ",");
			}

			return sb.toString();
		} else {

			return url.substring(idx, url.indexOf(":", idx + 1));
		}
	}

	public static String arrayListInt2String(List<Integer> list) {
		StringBuilder sb = new StringBuilder();

		if (null == list || !(list.size() > 0)) {
			return sb.toString();
		}

		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(",");
		}

		return sb.toString();

	}

	public static String arrayList2String(List<String> list) {
		
		if (null == list || !(list.size() > 0)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(",");
		}

		return sb.toString();
	}

	public static boolean isFind(String str, String expr) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(expr,
				java.util.regex.Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String deleteSlash(String dirName) {
		if (dirName.endsWith("/"))
			return dirName.substring(0, dirName.length() - 1);
		return dirName;
	}

	/**
	 * 对字符串使用replaceAll()方法,第一个参数存在* ? + |需要加\
	 * 
	 * @param str
	 */
	public static String filterReplaceAllStr(String str) {
		String[] c = new String[] { "*", "?", "+", "|" };

		String returnstr = str;
		for (int i = 0; i < c.length; i++) {
			StringBuffer sb = new StringBuffer();
			StringTokenizer st = new StringTokenizer(returnstr, c[i], true);
			while (st.hasMoreTokens()) {
				String tmp = st.nextToken();
				if (tmp.equals(c[i])) {
					sb.append("\\" + tmp);
				} else {
					sb.append(tmp);
				}
			}
			returnstr = sb.toString();
		}
		return returnstr;
	}

	// 替换指定位置的字符
	public static String replaceIndex(int index, String res, String str) {
		return res.substring(0, index) + str + res.substring(index + 1);
	}

	// 转化为列表
	public static List<String> stringToList(String str) {
		List<String> list = new ArrayList<String>();

		if (str != null && !"".equals(str)) {
			String temp[] = str.split(",");
			for (int i = 0; i < temp.length; i++) {
				list.add(temp[i]);
			}
		}
		return list;
	}

	public static int changeBoolean2Int(boolean flag) {
		if (flag == true) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 用正则表达式判断是否为数字(double)
	 * 
	 * @param str
	 *            字符串
	 * @return 如果是double类型返回true，如果不是返回false
	 * 
	 */
	public static Boolean isDouble(String str) {
		if (str.matches(
				"^[-+]?(\\d+(\\.\\d*)?|(\\.\\d+))([eE]([-+]?([012]?\\d{1,2}|30[0-7])|-3([01]?[4-9]|[012]?[0-3])))?[dD]?$")) {
			return true;
		}
		return false;
	}

	// 从文件路径中取出路径名
	public static String getPath(String filepath) {
		return filepath.substring(0, filepath.lastIndexOf("/") + 1);
	}

	// 从文件路径中提取文件名
	public static String getFileName(String filepath) {
		String array[] = filepath.split("[/|\\\\]");
		return array[array.length - 1];
	}
	/**
	 * 字符转换函数
	 * 
	 * @param s
	 *            对象
	 * @return 如果对象为null,返回为"",否则返回该字符串
	 */
	public static String null2String(String s) {
		if (s == null)
			return "";
		return s;
	}
	public static int getCRC32(String str) {
		if (str == null || str.trim().length() == 0) {
			return 0;
		}
		CRC32 crc = new CRC32();
		crc.update(str.getBytes());
		int l = (int) crc.getValue();
		return l;
	}
	/**
	 * 分解字符串，避免日志超长不可读
	 */
	public static String trunc(String source, int maxLen) {
		return trunc(source, maxLen, "... [Line too long]");
	}

	public static synchronized String trunc(String source, int maxLen,
			String appendMsg) {
		if (source == null)
			return null;
		if (source.length() > maxLen)
			return source.substring(0, maxLen) + appendMsg;
		return source;
	}
	public static InputStream String2Stream(String str) {
		ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
		return stream;
	}
	/**
	 * 获取固定byte长度的字符串
	 */
	public static String getFixedLenStr(String str, int len) {
		byte strs[] = str.getBytes();
		byte buf[] = new byte[len];
		System.arraycopy(strs, 0, buf, 0, len);
		String result = new String(buf);
		return result;
	}
	/**
	 * 输出N个空格的字符串 为 getFormatExport()
	 * 
	 * @param number
	 *            : 输入个数N
	 * @return String : 返回N个空格的字符串....
	 */
	private static String getKG(int number) {
		StringBuffer ss = new StringBuffer();
		for (int vv = 0; vv < number; vv++) {
			ss.append(" ");
		}
		return ss.toString();
	}
	/**
	 * String类型转换为Boolean
	 */
	public static Boolean String2Boolean(String str, Boolean def) {
		if (isBlank(str) || str.equals("null")) {
			return def;
		}
		Boolean i = null;
		try {
			i = Boolean.parseBoolean(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			i = def;
		}
		return i;
	}

	public static Boolean String2Boolean(String str) {
		return String2Boolean(str, null);
	}

	public static String String2HexString(String str) {
		String hexStr = "";
		for (int i = 0; i < str.length(); i++) {
			int ch = (int) str.charAt(i);
			hexStr += Integer.toHexString(ch);
		}

		return hexStr;
	}

}
