package ager.util;

/**
 * 系统工具类
 * 
 * @author lihaitao
 * @since 2019-04-05
 */
public class SystemUtils {
	
	/**
	 * 获取操作系统名称
	 */
	public static String getOsName() {
		String osName = System.getProperty("os.name");
		if (osName == null) {
			return "";
		}
		return osName;
	}
	
	/**
	 * 是否Mac OS操作系统
	 */
	public static boolean isMacOS() {
		if (getOsName().startsWith("Mac")) {
			return true;
		}
		return false;
	}

}
