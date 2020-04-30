package com.zcf.framework.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class JsonUtils {

	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper mapper;
	private final static ObjectMapper objectMapper = new ObjectMapper();

	public static String writeValueAsString(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Map<String, String> readValue(String content) {
		try {
			return objectMapper.readValue(content, Map.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Maps.newHashMap();
	}

	public static <T> T readValue(String content, Class<T> valueType) {
		try {
			return objectMapper.readValue(content, valueType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Object toObj(String jsonStr, Class z) throws IOException {
		return objectMapper.readValue(jsonStr, z);
	}

	/**
	 * 获取ObjectMapper实例
	 *
	 * @param createNew
	 *            方式：true，新实例；false,存在的mapper实例
	 * @return
	 */
	public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
		if (createNew) {
			return new ObjectMapper();
		} else if (JsonUtils.mapper == null) {
			JsonUtils.mapper = new ObjectMapper();
		}
		return JsonUtils.mapper;
	}

	/**
	 * 将java对象转换成json字符串
	 *
	 * @param obj
	 *            准备转换的对象
	 * @return json字符串
	 * @throws Exception
	 */
	public static String beanToJson(Object obj) throws Exception {
		try {
			ObjectMapper objectMapper = JsonUtils.getMapperInstance(false);
			String json = objectMapper.writeValueAsString(obj);
			return json;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 将java对象转换成json字符串
	 *
	 * @param obj
	 *            准备转换的对象
	 * @param createNew
	 *            ObjectMapper实例方式:true，新实例;false,存在的mapper实例
	 * @return json字符串
	 * @throws Exception
	 */
	public static String beanToJson(Object obj, Boolean createNew) throws Exception {
		try {
			ObjectMapper objectMapper = JsonUtils.getMapperInstance(createNew);
			String json = objectMapper.writeValueAsString(obj);
			return json;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 将json字符串转换成java对象
	 *
	 * @param json
	 *            准备转换的json字符串
	 * @param cls
	 *            准备转换的类
	 * @return
	 * @throws Exception
	 */
	public static Object jsonToBean(String json, Class<?> cls) throws Exception {
		try {
			ObjectMapper objectMapper = JsonUtils.getMapperInstance(false);
			Object vo = objectMapper.readValue(json, cls);
			return vo;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 将json字符串转换成java对象
	 *
	 * @param json
	 *            准备转换的json字符串
	 * @param cls
	 *            准备转换的类
	 * @param createNew
	 *            ObjectMapper实例方式:true，新实例;false,存在的mapper实例
	 * @return
	 * @throws Exception
	 */
	public static Object jsonToBean(String json, Class<?> cls, Boolean createNew) throws Exception {
		try {
			ObjectMapper objectMapper = JsonUtils.getMapperInstance(createNew);
			Object vo = objectMapper.readValue(json, cls);
			return vo;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

}
