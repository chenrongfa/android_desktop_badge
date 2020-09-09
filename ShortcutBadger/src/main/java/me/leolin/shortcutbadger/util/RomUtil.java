package me.leolin.shortcutbadger.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Properties;



public class RomUtil {
	private static final String TAG = "Rom";

	public static final String ROM_MIUI = "MIUI";
	public static final String ROM_EMUI = "EMUI";
	public static final String ROM_FLYME = "FLYME";
	public static final String ROM_OPPO = "OPPO";
	public static final String ROM_SMARTISAN = "SMARTISAN";
	public static final String ROM_VIVO = "VIVO";
	public static final String ROM_QIKU = "QIKU";
	public static final String ROM_JINLI = "AMIGO";

	private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
	private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
	private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
	private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
	private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

	private static String sName;
	private static String sVersion;



	private final static String ZTEC2016 = "zte c2016";
	private final static String ZUKZ1 = "zuk z1";
	private final static String ESSENTIAL = "essential";
	private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX"};

	private static boolean sIsTabletChecked = false;
	private static boolean sIsTabletValue = false;
	private static final String BRAND = Build.BRAND.toLowerCase();

	//华为
	public static boolean isEmui() {
		return check(ROM_EMUI);
	}

	//小米
	public static boolean isMiui() {
		return check(ROM_MIUI);
	}

	//vivo
	public static boolean isVivo() {
		return check(ROM_VIVO);
	}

	//oppo
	public static boolean isOppo() {
		return check(ROM_OPPO);
	}

	//魅族
	public static boolean isFlyme() {
		return check(ROM_FLYME);
	}

	//360手机
	public static boolean is360() {
		return check("360");
	}

	public static boolean isQiku() {
		return check(ROM_QIKU);
	}

	public static boolean isSamsung() {
		return check("samsung");
	}

	public static boolean isHtc() {
		return check("htc");
	}

	public static boolean isCoolpad() {
		return check("coolpad");
	}

	//金立
	public static boolean isGionee() {
		return check("gionee");
	}

	public static boolean isAsus() {
		return check("asus");
	}

	public static boolean isLge() {
		return check("lge");
	}

	public static boolean isZte() {
		return check("zte");
	}

	public static boolean isZuk() {
		return check("zuk");
	}

	public static boolean isSony() {
		return check("sony");
	}

	public static boolean isSmartisan() {
		return check(ROM_SMARTISAN);
	}


	public static String getName() {
		if (sName == null) {
			check("");
		}
		return sName;
	}

	public static String getVersion() {
		if (sVersion == null) {
			check("");
		}
		return sVersion;
	}

	public static boolean check(String rom) {
		if (sName != null) {
			return sName.equals(rom);
		}

		if (ROM_MIUI.equals(rom) && !TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
			sName = ROM_MIUI;
		} else if (ROM_EMUI.equals(rom) && !TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
			sName = ROM_EMUI;
		} else if (ROM_OPPO.equals(rom) && !TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
			sName = ROM_OPPO;
		} else if (ROM_VIVO.equals(rom) && !TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
			sName = ROM_VIVO;
		} else if (ROM_SMARTISAN.equals(rom) && !TextUtils.isEmpty(sVersion =
				getProp(KEY_VERSION_SMARTISAN))) {
			sName = ROM_SMARTISAN;
		} else {
			sVersion = Build.DISPLAY;
			if (sVersion.toUpperCase().contains(ROM_FLYME)) {
				sName = ROM_FLYME;
			} else if (sVersion.toUpperCase().contains(ROM_JINLI)) {
				sName = ROM_JINLI;
			} else {
				sVersion = Build.UNKNOWN;
				sName = Build.MANUFACTURER.toUpperCase();
			}
		}
		return sName.equals(rom);
	}


	public static boolean isEssentialPhone() {
		return BRAND.contains("essential");
	}

	public static String getProp(String name) {
		String line = null;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + name);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			Log.e(TAG, "Unable to read prop " + name, ex);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return line;
	}


	/**
	 * 判断是否为 ZUK Z1 和 ZTK C2016。
	 * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
	 */
	public static boolean isZUKZ1() {
		final String board = Build.MODEL;
		return board != null && board.toLowerCase().contains(ZUKZ1);
	}

	public static boolean isZTKC2016() {
		final String board = Build.MODEL;
		return board != null && board.toLowerCase().contains(ZTEC2016);
	}

	private static boolean isPhone(String[] boards) {
		final String board = Build.BOARD;
		if (board == null) {
			return false;
		}
		for (String board1 : boards) {
			if (board.equals(board1)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	private static String getLowerCaseName(Properties p, Method get, String key) {
		String name = p.getProperty(key);
		if (name == null) {
			try {
				name = (String) get.invoke(null, key);
			} catch (Exception ignored) {
			}
		}
		if (name != null)
			name = name.toLowerCase();
		return name;
	}

	private static boolean _isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
				Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * 判断是否为平板设备
	 */
	public static boolean isTablet(Context context) {
		if (sIsTabletChecked) {
			return sIsTabletValue;
		}
		sIsTabletValue = _isTablet(context);
		sIsTabletChecked = true;
		return sIsTabletValue;
	}
}