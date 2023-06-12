package com.egrand.sweetapi.core.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 类型转换
 */
public class ObjectConvertExtension {

	private static ObjectConvertExtension instance;

	public static ObjectConvertExtension getInstance() {
		if(instance == null)
			return new ObjectConvertExtension();
		return instance;
	}

	/**
	 * 将值转换为int类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 * @return
	 */
	public static int asInt(Object val, int defaultValue) {
		try {
			return asDecimal(val).intValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将对象转为double类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 */
	public static double asDouble(Object val, double defaultValue) {
		try {
			return asDecimal(val).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将对象转为long类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 */
	public static long asLong(Object val, long defaultValue) {
		try {
			return asDecimal(val).longValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将对象转为String类型
	 * @param val 值
	 */
	public static String asString(Object val) {
		return asString(val, null);
	}

	/**
	 * 将对象转为Date类型，默认字符串格式为yyyy-MM-dd HH:mm:ss
	 * @param val 值
	 */
	public static Date asDate(Object val) {
		return asDate(val, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将对象转为BigDecimal类型
	 * @param val 值
	 */
	public static BigDecimal asDecimal(Object val) {
		if (val instanceof BigDecimal) {
			return (BigDecimal) val;
		}
		return new BigDecimal(asString(val));
	}

	/**
	 * 将对象转为BigDecimal类型
	 * @param val 值
	 * @param defaultVal 转换失败时的默认值
	 */
	public static BigDecimal asDecimal(Object val, BigDecimal defaultVal) {
		if (val instanceof BigDecimal) {
			return (BigDecimal) val;
		}
		try {
			return new BigDecimal(asString(val));
		} catch (Exception e) {
			return defaultVal;
		}
	}

	/**
	 * 将对象转为Date类型,支持String、10位、13位时间戳
	 * @param val
	 * @param format 日期格式，如yyyy-MM-dd HH:mm:ss
	 */
	public static Date asDate(Object val, String format) {
		if (val == null) {
			return null;
		}
		if (val instanceof String) {
			try {
				return new SimpleDateFormat(format).parse(val.toString());
			} catch (ParseException e) {
				long longVal = asLong(val, -1);
				if (longVal > 0) {
					return asDate(longVal, format);
				}
			}
		} else if (val instanceof Date) {
			return (Date) val;
		} else if (val instanceof Number) {
			Number number = (Number) val;
			if (val.toString().length() == 10) { //10位时间戳
				return new Date(number.longValue() * 1000L);
			} else if (val.toString().length() == 13) {    //13位时间戳
				return new Date(number.longValue());
			}
		} else if (val instanceof LocalDateTime) { //LocalDateTime类型
			return Date.from(((LocalDateTime) val).atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}

	/**
	 * 将对象转为String类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 */
	public static String asString(Object val, String defaultValue) {
		return val == null ? defaultValue : val.toString();
	}

	/**
	 * 将值转换为int类型，转换失败时为0
	 * @param val 值
	 */
	public int asInt(Object val) {
		return asInt(val, 0);
	}

	/**
	 * 将对象转为double类型，转换失败时为0.0
	 * @param val 值
	 */
	public double asDouble(Object val) {
		return asDouble(val, 0.0);
	}

	/**
	 * 将对象转为long类型，转换失败时为0L
	 * @param val 值
	 */
	public long asLong(Object val) {
		return asLong(val, 0L);
	}

	/**
	 * 将对象转为byte类型，转换失败时默认为0
	 * @param val 值
	 */
	public byte asByte(Object val) {
		return asByte(val, (byte) 0);
	}

	/**
	 * 将对象转为byte类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 */
	public byte asByte(Object val, byte defaultValue) {
		try {
			return asDecimal(val).byteValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将对象转为short类型，转换失败时默认为0
	 * @param val 值
	 */
	public short asShort(Object val) {
		return asShort(val, (short) 0);
	}

	/**
	 * 将对象转为short类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 */
	public short asShort(Object val, short defaultValue) {
		try {
			return asDecimal(val).shortValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将对象转为float类型，转换失败默认为0.0f
	 * @param val 值
	 */
	public float asFloat(Object val) {
		return asFloat(val, 0.0f);
	}

	/**
	 * 将对象转为float类型
	 * @param val 值
	 * @param defaultValue 转换失败时的默认值
	 */
	public float asFloat(Object val, float defaultValue) {
		try {
			return asDecimal(val).floatValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
