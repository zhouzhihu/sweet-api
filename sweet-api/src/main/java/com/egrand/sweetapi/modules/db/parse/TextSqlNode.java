package com.egrand.sweetapi.modules.db.parse;

import com.egrand.sweetapi.core.exception.APIException;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 普通SQL节点
 *
 * @author jmxd
 * @version : 2020-05-18
 */
public class TextSqlNode extends SqlNode {

	private static final GenericTokenParser CONCAT_TOKEN_PARSER = new GenericTokenParser("${", "}", false);

	private static final GenericTokenParser REPLACE_TOKEN_PARSER = new GenericTokenParser("#{", "}", true);

	private static final GenericTokenParser IF_TOKEN_PARSER = new GenericTokenParser("?{", "}", true);

	private static final GenericTokenParser IF_PARAM_TOKEN_PARSER = new GenericTokenParser("?{", ",", true);

	/**
	 * SQL
	 */
	private final String text;

	public TextSqlNode(String text) {
		this.text = text;
	}

	public static String parseSql(String sql, Map<String, Object> varMap, List<Object> parameters) {
		// 处理?{}参数
		sql = IF_TOKEN_PARSER.parse(sql.trim(), text -> {
			AtomicBoolean ifTrue = new AtomicBoolean(false);
			String val = IF_PARAM_TOKEN_PARSER.parse("?{" + text, param -> {
				ifTrue.set(isTrue(executeExpression(param, varMap)));
				return null;
			});
			return ifTrue.get() ? val : "";
		});
		// 处理${}参数
		sql = CONCAT_TOKEN_PARSER.parse(sql, text -> String.valueOf(executeExpression(text, varMap)));
		// 处理#{}参数
		sql = REPLACE_TOKEN_PARSER.parse(sql, text -> {
			Object value = executeExpression(text, varMap);
			if (value == null) {
				parameters.add(null);
				return "?";
			}
			try {
				//对集合自动展开
				List<Object> objects = arrayLikeToList(value);
				parameters.addAll(objects);
				return IntStream.range(0, objects.size()).mapToObj(t -> "?").collect(Collectors.joining(","));
			} catch (Exception e) {
				parameters.add(value);
				return "?";
			}
		});
		return sql;
	}

	@Override
	public String getSql(Map<String, Object> paramMap, List<Object> parameters) {
		return parseSql(text, paramMap, parameters) + executeChildren(paramMap, parameters).trim();
	}

	public static boolean isTrue(Object object) {
		if (object == null) {
			return false;
		} else if (object instanceof Boolean) {
			return (Boolean)object;
		} else if (object instanceof CharSequence) {
			return ((CharSequence)object).length() != 0;
		} else if (object instanceof Number) {
			return ((Number)object).doubleValue() != 0.0D;
		} else if (object instanceof Collection) {
			return !((Collection)object).isEmpty();
		} else if (object.getClass().isArray()) {
			return Array.getLength(object) > 0;
		} else if (object instanceof Map) {
			return !((Map)object).isEmpty();
		} else {
			return true;
		}
	}

	public static List<Object> arrayLikeToList(Object arrayLike) {
		if (arrayLike == null) {
			return new ArrayList();
		} else if (arrayLike instanceof Collection) {
			return new ArrayList((Collection)arrayLike);
		} else {
			ArrayList list;
			if (arrayLike.getClass().isArray()) {
				list = new ArrayList(Array.getLength(arrayLike));
				IntStream.range(0, Array.getLength(arrayLike)).forEach((i) -> {
					list.add(Array.get(arrayLike, i));
				});
				return list;
			} else if (arrayLike instanceof Iterator) {
				list = new ArrayList();
				Iterator<Object> it = (Iterator)arrayLike;
				it.forEachRemaining(list::add);
				return list;
			} else if (arrayLike instanceof Enumeration) {
				Enumeration<Object> en = (Enumeration)arrayLike;
				return Collections.list(en);
			} else {
				throw new APIException("不支持的类型:" + arrayLike.getClass());
			}
		}
	}

	/**
	 * 执行脚本
	 */
	public static Object executeExpression(String script, Map<String, Object> paramMap) {
		return paramMap.containsKey(script) ? paramMap.get(script) : null;
	}
}
