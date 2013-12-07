package com.mome.core.dao.support;

import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

/**
 * 
 * <p>
 * 查询条件定义类,用来定义操作数据库的查询条件<br>
 * Description:用来定义SQL查询条件<br>
 * </p>
 *
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-9-11 下午4:44:25
 * @version V1.0
 */
public class PropertyFilter {

	/** 查询条件字段名称(有可能为定义的别名) */
	private String propertyName;

	/** 查询条件字段值 */
	private Object propertyValue;

	/** 查询条件字段原名称 */
	private String originalName;

	/** 查询条件匹配方式 */
	private MatchType matchType = null;
	
	private PropertyFilterProcessor processor;

	PropertyFilter(String propertyName, Object propertyValue,
			MatchType matchType) {
		this(propertyName, null, propertyValue, matchType);
	}

	/**
	 * 查询条件构造函数
	 * 
	 * @param propertyName
	 *            字段名称
	 * @param originalName
	 *            字段原名称
	 * @param propertyValue
	 *            字段值
	 * @param matchType
	 *            匹配方式
	 */
	public PropertyFilter(String propertyName, String originalName,
			Object propertyValue, MatchType matchType) {
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.matchType = matchType;
		this.originalName = originalName;
	}

	/**
	 * 获取查询条件字段
	 * 
	 * @return 查询条件字段
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the 原始参数名，用于IBatis
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * 获取查询字段值
	 * 
	 * @return 查询字段值
	 */
	public Object getPropertyValue() {
		return propertyValue;
	}

	/**
	 * 获取查询条件匹配方式
	 * 
	 * @return 匹配方式
	 */
	public MatchType getMatchType() {
		return matchType;
	}

	
	public PropertyFilterProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(PropertyFilterProcessor processor) {
		this.processor = processor;
	}

	public String toString() {
		return "{matchType=" + matchType + ", propertyName=" + propertyName
				+ ", propertyValue=" + propertyValue + "}";
	}
}
