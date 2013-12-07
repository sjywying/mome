package com.mome.core.dao.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

/**
 * Title:查询条件定义类集合<br>
 * 
 * Description:将一个完整的查询条件的每一个单个的查询条件封装到List中，支持连续操作。本类只支持查询条件之间的and关系，不支持or及()<br>
 * 
 * Company: 亚信联创集团股份有限公司<br>
 * 
 * @author author
 * @see
 * @CreateDate 2011-8-26 下午05:47:43
 * 
 */
public class PropertyFilterList {

	/** 记录单个查询条件的集合，最用形成完整的查询条件 */
	private List<PropertyFilter> filters = new ArrayList<PropertyFilter>();

	private Map<String, Object> attributes = new HashMap<String,Object>(0);
	
	public static final String ATTR_KEY_CLASS = "class";
	
	public static final String ATTR_KEY_ALIAS = "alias";
	
	public static final String ATTR_KEY_CACHEABLE = "cacheable";
	
	public static final String ATTR_KEY_SEE_LOGICAL_REMOVED = "seeLogicalRemoved";
	
	public static final String ATTR_KEY_JOINTYPE_PREFIX = "JOINTYPE#";
	
	public static final String ATTR_KEY_JOINTYPE_PATH_DEFAULT = "ALL";
	
	private LogicType logicType = LogicType.and;
	
	private Projections projections;

	/**
	 * 构造函数私有化，不允许外部类直接实例化该类
	 */
	private PropertyFilterList() {
		this.attribute(ATTR_KEY_JOINTYPE_PREFIX + ATTR_KEY_JOINTYPE_PATH_DEFAULT, JoinType.inner);
	}

	/**
	 * 通过查询条件字段、字段值及匹配方式构造本类实例并返回
	 * 
	 * @param name
	 *            查询字段名称
	 * @param value
	 *            查询字段值
	 * @param matchType
	 *            查询匹配方式
	 * @return 通过查询条件字段、字段值及匹配方式构造的本类实例
	 */
	public static PropertyFilterList instance(String name, Object value,
			MatchType matchType) {
		return PropertyFilterList.instance().add(name, value, matchType);
	}

	/**
	 * 通过查询条件字段、字段值及匹配方式为EQ(equals)构造本类实例并返回
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件字段值
	 * @return 通过查询条件字段、字段值及匹配方式为EQ(equals)构造的本类实例
	 */
	public static PropertyFilterList instance(String name, Object value) {
		return PropertyFilterList.instance().add(name, value);
	}

	/**
	 * 遍历map，将map的key做查询条件字段，map的value作为查询条件字段值，将EQ(equals)作为匹配方式构造本类实例对象并返回
	 * 
	 * @param map
	 *            构造查询条件的map
	 * @return 
	 *         遍历map，将map的key做查询条件字段，map的value作为查询条件字段值，将EQ(equals)作为匹配方式构造的本类实例对象
	 */
	public static PropertyFilterList instance(Map<String, Object> map) {
		PropertyFilterList instance = new PropertyFilterList();
		for (Map.Entry<String, Object> entry : map.entrySet())
			instance.add(entry.getKey(), entry.getValue());
		return instance;
	}

	/**
	 * 构造一个空实例对象并返回
	 * 
	 * @return 空实例对象
	 */
	public static PropertyFilterList instance() {
		return new PropertyFilterList();
	}
	
	public static PropertyFilterList orInstance(){
		PropertyFilterList result =  new PropertyFilterList();
		result.logicType = LogicType.or;
		return result;
	}

	/**
	 * 添加给定查询条件字段名称、字段值及匹配方式的查询条件
	 * 
	 * @param name
	 *            查询条件按字段
	 * @param value
	 *            查询条件按字段值
	 * @param matchType
	 *            查询条件匹配方式
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList add(String name, Object value, MatchType matchType) {
		return this.add(name, null, value, matchType);
	}

	/**
	 * 添加匹配方式为EQ(equals)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList add(String name, Object value) {
		return this.add(name, null, value, MatchType.EQ);
	}

	/**
	 * 添加给定查询条件字段名称、查询条件字段原名称、字段值及匹配方式的查询条件
	 * 
	 * @param name
	 *            查询条件字段名称
	 * @param originalName
	 *            查询条件字段原名称
	 * @param value
	 *            查询条件字段值
	 * @param matchType
	 *            查询条件匹配方式
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList add(String name, String originalName,
			Object value, MatchType matchType) {
		this.filters.add(new PropertyFilter(name, originalName, value,
				matchType));
		return this;
	}

	public PropertyFilterList addAll(PropertyFilterList propertyFilterList) {
		for (PropertyFilter filter : propertyFilterList.list())
			this.filters.add(filter);

		if (propertyFilterList.attributes != null) {
			if (this.attributes == null)
				this.attributes = new HashMap<String, Object>();

			this.attributes.putAll(propertyFilterList.attributes);
		}
		return this;
	}

	public List<PropertyFilter> list() {
		return this.filters;
	}
	
	public Projections getProjections(){
		return this.projections;
	}

	public void addAttribute(String name, Object value) {
		if (this.attributes == null)
			this.attributes = new HashMap<String, Object>();

		this.attributes.put(name, value);
	}
	
	public PropertyFilterList attribute(String name, Object value) {
		this.addAttribute(name, value);
		return this;
	}
	
	public PropertyFilterList forClass(Class<?> entityClass){
		return this.attribute(ATTR_KEY_CLASS, entityClass);
	}
	
	public PropertyFilterList alias(String alias){
		return this.attribute(ATTR_KEY_ALIAS, alias);
	}

	public Object getAttribute(String name) {
		return this.attributes == null ? null : this.attributes.get(name);
	}
	
	public Map<String,Object> getAttributes(){
		return this.attributes;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		Object paramValue = null;
		String paramName;

		for (PropertyFilter propertyFilter : this.filters) {
			paramName = propertyFilter.getOriginalName();

			if (StringUtils.isEmpty(paramName))
				paramName = propertyFilter.getPropertyName();

			if (paramName.endsWith("_" + MatchType.LIKE.name().toLowerCase())){
//				if(SystemConfig.Config.getBoolean("mome.core.auto_escape_like",true))
				//TODO
				if(false){
					paramValue = "%" + DatabaseEscapeUtils.escape((String)propertyFilter.getPropertyValue()) + "%";
				} else {
					paramValue = "%" + propertyFilter.getPropertyValue() + "%";
				}
			} else if(propertyFilter.getPropertyValue() instanceof PropertyFilterList){
				paramValue = ((PropertyFilterList)propertyFilter.getPropertyValue()).toMap();
			} else
				paramValue = propertyFilter.getPropertyValue();
			
			map.put(paramName, paramValue);
		}
		return map;
	}

	/**
	 * 添加匹配方式为EQ(equals)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList eq(String name, Object value) {
		return this.add(name, value);
	}

	/**
	 * 添加匹配方式为NE(不匹配)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList ne(String name, Object value) {
		return this.add(name, value, MatchType.NE);
	}

	/**
	 * 添加匹配方式为IEQ(忽略大小写匹配)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList ieq(String name, Object value) {
		return this.add(name, value, MatchType.IEQ);
	}

	/**
	 * 添加匹配方式为LIKE(like)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList like(String name, Object value) {
		return this.add(name, value, MatchType.LIKE);
	}

	/**
	 * 添加匹配方式为 LLIKE(left like)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList llike(String name, Object value) {
		return this.add(name, value, MatchType.LLIKE);
	}

	/**
	 * 添加匹配方式为RLIKE(right like)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList rlike(String name, Object value) {
		return this.add(name, value, MatchType.RLIKE);
	}

	/**
	 * 添加匹配方式为ILIKE(ignore case like)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList ilike(String name, Object value) {
		return this.add(name, value, MatchType.ILIKE);
	}

	/**
	 * 添加匹配方式为LT(less than)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList lt(String name, Object value) {
		return this.add(name, value, MatchType.LT);
	}

	/**
	 * 添加匹配方式为GT(greater then)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList gt(String name, Object value) {
		return this.add(name, value, MatchType.GT);
	}

	/**
	 * 添加匹配方式为LE(小于等于)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList le(String name, Object value) {
		return this.add(name, value, MatchType.LE);
	}

	/**
	 * 添加匹配方式为GE(大于等于)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList ge(String name, Object value) {
		return this.add(name, value, MatchType.GE);
	}

	/**
	 * 添加匹配方式为GE(大于等于)的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值,为Object[2]数组
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList between(String name, Object value) {
		return this.add(name, value, MatchType.BETWEEN);
	}

	/**
	 * 添加匹配方式为IN的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList in(String name, Object[] value) {
		return this.add(name, value, MatchType.IN);
	}

	/**
	 * 添加匹配方式为IN的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList in(String name, Collection<?> value) {
		return this.add(name, value, MatchType.IN);
	}

	/**
	 * 添加匹配方式为NOT_IN的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList notIn(String name, Object[] value) {
		return this.add(name, value, MatchType.NOT_IN);
	}

	/**
	 * 添加匹配方式为NOT_IN的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
	public PropertyFilterList notIn(String name, Collection<?> value) {
		return this.add(name, value, MatchType.NOT_IN);
	}

	/**
	 * 添加匹配方式为HAS的查询条件
	 * 
	 * @param name
	 *            查询条件字段
	 * @param value
	 *            查询条件值
	 * @return 调用该方法的本类实例对象自身，为的是支持连续操作
	 */
//	public PropertyFilterList has(String name, PropertyFilterList value) {
//		return this.add(name, value, MatchType.HAS);
//	}
	
	public PropertyFilterList sizeEq(String name, Integer size){
		return this.add(name, size, MatchType.SIZE_EQ);
	}
	
	public PropertyFilterList sizeGe(String name, Integer size){
		return this.add(name, size, MatchType.SIZE_GE);
	}
	
	public PropertyFilterList sizeGt(String name, Integer size){
		return this.add(name, size, MatchType.SIZE_GT);
	}
	
	public PropertyFilterList sizeLe(String name, Integer size){
		return this.add(name, size, MatchType.SIZE_LE);
	}
	
	public PropertyFilterList sizeLt(String name, Integer size){
		return this.add(name, size, MatchType.SIZE_LT);
	}

	public PropertyFilterList eqProperty(String name,String otherPropertyName){
		return this.add(name, otherPropertyName, MatchType.EQ_PROPERTY);
	}
	
	public PropertyFilterList exists(String name,PropertyFilterList propertyFilterList){
		return this.add(name, propertyFilterList, MatchType.EXISTS);
	}
	
	public PropertyFilterList seeLogicalRemoved(){
		this.addAttribute(ATTR_KEY_SEE_LOGICAL_REMOVED, true);
		return this;
	}
	
	public LogicType getLogicType(){
		return logicType;
	}
	
	/**
	 * 可扩展的查询条件
	 */
	public PropertyFilterList ex(String name,PropertyFilterList propertyFilterList, 
			PropertyFilterProcessor processor){
		Assert.notNull(name, "可扩展的查询条件必须有PropertyFilterProcessor");
		PropertyFilter filter = new PropertyFilter(name, propertyFilterList,MatchType.EX);
		filter.setProcessor(processor);
		this.filters.add(filter);
		return this;
	}
	
	public PropertyFilterList not(String name,Object value,MatchType matchType){
		this.filters.add(new PropertyFilter("not_" + name,
				new PropertyFilter(name,value,matchType),
				MatchType.NOT));
		return this;
	}
	
	/**
	 * 查询条件片段，相当于括号括起来的一个片段
	 */
	public PropertyFilterList fragment(String name,PropertyFilterList propertyFilterList){
		return this.add(name, propertyFilterList, MatchType.FRAGMENT);
	}
	
	public PropertyFilterList remove(String name){
		Iterator<PropertyFilter> iterator = this.filters.iterator();
		PropertyFilter filter;
		while(iterator.hasNext()){
			filter = iterator.next();
			if(filter.getPropertyName().equals(name))
				iterator.remove();
		}
		return this;
	}
	
	public PropertyFilterList addProcessor(String name, PropertyFilterProcessor processor){
		boolean has = false;
		for(PropertyFilter filter : this.filters){
			if(filter.getPropertyName().equals(name)){
				filter.setProcessor(processor);
				has = true;
				break;
			}
		}
		
		if(!has)
			throw new RuntimeException("找不到属性" + name + "的过滤器，无法加入处理器");
			
		return this;
	}
	
	public PropertyFilterList projections(Projections projections){
		this.projections = projections;
		return this;
	}
	
	public PropertyFilterList leftJoin(String path){
		return this.attribute(ATTR_KEY_JOINTYPE_PREFIX + path, JoinType.left);
	}
	
	public PropertyFilterList innerJoin(String path){
		return this.attribute(ATTR_KEY_JOINTYPE_PREFIX + path, JoinType.inner);
	}
	
	public PropertyFilterList cacheable(){
		return this.attribute(ATTR_KEY_CACHEABLE, true);
	}
}
