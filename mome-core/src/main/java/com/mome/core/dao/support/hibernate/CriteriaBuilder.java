package com.mome.core.dao.support.hibernate;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.Criteria;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.Session;
import org.hibernate.annotations.Type;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.mome.core.dao.support.JoinType;
import com.mome.core.dao.support.LogicType;
import com.mome.core.dao.support.MatchType;
import com.mome.core.dao.support.ProjectionType;
import com.mome.core.dao.support.ProjectiveResultType;
import com.mome.core.dao.support.PropertyFilter;
import com.mome.core.dao.support.PropertyFilterList;
import com.mome.core.dao.support.SortProperties;
import com.mome.core.dao.support.SortProperties.SortProperty;
import com.mome.core.dao.support.SortProperties.SortType;
import com.mome.core.dao.support.hibernate.processor.BetweenProcesser;
import com.mome.core.dao.support.hibernate.processor.EqProcesser;
import com.mome.core.dao.support.hibernate.processor.EqPropertyProcessor;
import com.mome.core.dao.support.hibernate.processor.ExistsProcessor;
import com.mome.core.dao.support.hibernate.processor.FragmentProcessor;
import com.mome.core.dao.support.hibernate.processor.GeProcessor;
import com.mome.core.dao.support.hibernate.processor.GtProcessor;
import com.mome.core.dao.support.hibernate.processor.IEqProcessor;
import com.mome.core.dao.support.hibernate.processor.ILikeProcessor;
import com.mome.core.dao.support.hibernate.processor.InProcesser;
import com.mome.core.dao.support.hibernate.processor.LLikeProcessor;
import com.mome.core.dao.support.hibernate.processor.LeProcessor;
import com.mome.core.dao.support.hibernate.processor.LikeProcessor;
import com.mome.core.dao.support.hibernate.processor.LtProcessor;
import com.mome.core.dao.support.hibernate.processor.NeProcessor;
import com.mome.core.dao.support.hibernate.processor.NotInProcessor;
import com.mome.core.dao.support.hibernate.processor.NotProcessor;
import com.mome.core.dao.support.hibernate.processor.RLikeProcessor;
import com.mome.core.dao.support.hibernate.processor.SizeEqProcessor;
import com.mome.core.dao.support.hibernate.processor.SizeGeProcessor;
import com.mome.core.dao.support.hibernate.processor.SizeGtProcessor;
import com.mome.core.dao.support.hibernate.processor.SizeLeProcessor;
import com.mome.core.dao.support.hibernate.processor.SizeLtProcessor;
import com.mome.core.utils.GenericsUtils;
import com.mome.core.utils.ReflectionUtils;

/**
 * Title:hibernate org.hibernate.Criteria类的工厂类<br>
 * Description:传入初始化参数session及实体类，然后通过session创建Criteria并进行一定的处理<br>
 * Company: 亚信联创集团股份有限公司<br>
 * 
 * @author author
 * @see
 * @CreateDate 2011-8-30 上午09:40:08
 * 
 */
public class CriteriaBuilder {

	/** 通过参数传进来的hibernate的session对象 */
	private Session session;
	
	public static final String ROOT_CRITERIA_ALIAS = "root";

	private int aliasIndex = 0;

	private String autoAlias = null;

	/** hibernate的Criteria对象 */
	private Criteria criteria;
	
	private Map<String, String> aliasMap = new HashMap<String, String>();

	/** 操纵的实体bean */
	private Class<?> entityClass;

	private static Map<String, Boolean> propertyRelationMap = new HashMap<String, Boolean>();

	private Map<String,JoinType> joinTypes = new HashMap<String,JoinType>();
	
	private static final Map<MatchType,PropertyFilterProcessor> processors;
	
	private Logger logger = LoggerFactory.getLogger(CriteriaBuilder.class);
	
	static{
		processors = new HashMap<MatchType,PropertyFilterProcessor>(); 
		processors.put(MatchType.BETWEEN, new BetweenProcesser());
		processors.put(MatchType.EQ,new EqProcesser());
		processors.put(MatchType.EQ_PROPERTY,new EqPropertyProcessor());
		processors.put(MatchType.EXISTS,new ExistsProcessor());
		processors.put(MatchType.GE,new GeProcessor());
		processors.put(MatchType.GT,new GtProcessor());
		processors.put(MatchType.IEQ,new IEqProcessor());
		processors.put(MatchType.ILIKE,new ILikeProcessor());
		processors.put(MatchType.IN,new InProcesser());
		processors.put(MatchType.LE,new LeProcessor());
		processors.put(MatchType.LIKE,new LikeProcessor());
		processors.put(MatchType.LLIKE,new LLikeProcessor());
		processors.put(MatchType.LT,new LtProcessor());
		processors.put(MatchType.NE,new NeProcessor());
		processors.put(MatchType.NOT_IN,new NotInProcessor());
		processors.put(MatchType.RLIKE,new RLikeProcessor());
		processors.put(MatchType.FRAGMENT, new FragmentProcessor());
		processors.put(MatchType.SIZE_EQ, new SizeEqProcessor());
		processors.put(MatchType.SIZE_GE, new SizeGeProcessor());
		processors.put(MatchType.SIZE_GT, new SizeGtProcessor());
		processors.put(MatchType.SIZE_LE, new SizeLeProcessor());
		processors.put(MatchType.SIZE_LT, new SizeLtProcessor());
		processors.put(MatchType.NOT, new NotProcessor());
	}

	/**
	 * 通过传入hibernate session对象和实体bean Class进行构造
	 * 
	 * @param session
	 *            hibernate的session
	 * @param entityClass
	 *            需要操作的实体bean Class
	 */
	public CriteriaBuilder(Session session, Class<?> entityClass) {
		this.session = session;
		this.entityClass = entityClass;
	}

	/**
	 * 初始化方法，初始化本类的criterionBuilder等属性
	 */
	private void init() {
		this.criteria = this.session.createCriteria(this.entityClass,ROOT_CRITERIA_ALIAS);
		//this.criterionBuilder = new CriterionBuilder(this.criteria,
		//		this.session, this.entityClass);
		this.aliasIndex = 0;
		this.autoAlias = null;
		this.aliasMap.clear();
		this.joinTypes.clear();
	}

	public Criteria build(String name, Object value) {
		this.init();
		String alias = name.indexOf('.') > -1 ? this.createAlias(name) : name;
		this.criteria.add(processors.get(MatchType.EQ)
				.buildCriterion(alias, name, value, this));
		return this.criteria;
	}

	public Criteria build(PropertyFilterList propertyFilterList) {
		this.init();
		this.initJoinTypes(propertyFilterList);
		this.processPropertyFilterList(this.criteria, propertyFilterList);
		this.processProjections(this.criteria,propertyFilterList);
		this.processCacheable(this.criteria,propertyFilterList);
		return this.criteria;
	}

	public Criteria build(PropertyFilterList propertyFilterList,
			SortProperties sortProperties) {
		this.init();
		this.initJoinTypes(propertyFilterList);
		this.processPropertyFilterList(this.criteria, propertyFilterList);
		this.processProjections(this.criteria,propertyFilterList);
		this.processOrders(sortProperties);
		this.processCacheable(this.criteria,propertyFilterList);
		return this.criteria;
	}

	public Criteria build(PropertyFilterList propertyFilterList,
			SortProperties sortProperties, String projections) {
		this.init();
		this.initJoinTypes(propertyFilterList);
		this.processPropertyFilterList(this.criteria, propertyFilterList);
		this.processProjections(this.criteria,propertyFilterList);
		this.processOrders(sortProperties);
		this.processProjections(projections);
		this.processCacheable(this.criteria,propertyFilterList);
		return this.criteria;
	}

	public DetachedCriteria buildDetachedCriteria(PropertyFilterList propertyFilterList) {
		
		this.init();
		
		this.initJoinTypes(propertyFilterList);
		
		String alias = (String)propertyFilterList.getAttribute(PropertyFilterList.ATTR_KEY_ALIAS);
		
		DetachedCriteria dc;
		
		if(alias != null)
			dc = DetachedCriteria.forClass(this.entityClass,alias);
		else
			dc = DetachedCriteria.forClass(this.entityClass);
		
		if (propertyFilterList != null){
			List<PropertyFilter> list = propertyFilterList.list();
			Criterion criterion;
	
			Disjunction disjunction = null;
			
			if(LogicType.or == propertyFilterList.getLogicType())
				disjunction = Restrictions.disjunction();
			
			for (PropertyFilter propertyFilter : list) {
				criterion = this.createCriterion(propertyFilter);

				if (criterion != null){
					if(LogicType.or == propertyFilterList.getLogicType())
						disjunction.add(criterion);
					else
						dc.add(criterion);
				}
			}
			
			if(LogicType.or == propertyFilterList.getLogicType())
				dc.add(disjunction);
			
		}
		
		return dc;
	}
	

	private void processCacheable(Criteria criteria,
			PropertyFilterList propertyFilterList) {
		if (propertyFilterList == null)
			return;
		
		Boolean cacheable = (Boolean) propertyFilterList.getAttribute(PropertyFilterList.ATTR_KEY_CACHEABLE);
		if(cacheable != null && cacheable)
			criteria.setCacheable(true);
	}

	/**
	 * 初始化关联方式
	 * @param propertyFilterList
	 */
	private void initJoinTypes(PropertyFilterList propertyFilterList) {
		if(propertyFilterList == null)
			return;
		
		for(Map.Entry<String, Object> attr : propertyFilterList.getAttributes().entrySet()){
			if(attr.getKey().startsWith(PropertyFilterList.ATTR_KEY_JOINTYPE_PREFIX))
				this.joinTypes.put(attr.getKey(), (JoinType)attr.getValue());
		}
	}

	private void processProjections(String projections) {
		Assert.hasText(projections);

		ProjectionList projectionList = Projections.projectionList();

		String[] projectionArray = projections.split(",");

		for (String projection : projectionArray)
			projectionList.add(Projections.property(projection));

		this.criteria.setProjection(projectionList);
	}

	private void processOrders(SortProperties sortProperties) {
		if (sortProperties == null)
			return;

		List<SortProperty> list = sortProperties.list();
		for (SortProperty sp : list) {
			if (sp.getSortType() == SortType.ASC)
				this.criteria.addOrder(Order.asc(this.createAlias(sp.getPropertyName())));
			else if (sp.getSortType() == SortType.DESC)
				this.criteria.addOrder(Order.desc(this.createAlias(sp.getPropertyName())));
		}
	}

	private void processPropertyFilterList(Criteria criteria,
			PropertyFilterList propertyFilterList) {
		if (propertyFilterList == null)
			return;

		StringBuilder log = new StringBuilder("查询参数：");
		
		List<PropertyFilter> list = propertyFilterList.list();
		Criterion criterion;
		
		Disjunction disjunction = null;
		
		if(LogicType.or == propertyFilterList.getLogicType())
			disjunction = Restrictions.disjunction();
		
		for (PropertyFilter propertyFilter : list) {
			criterion = this.createCriterion(propertyFilter);
			
			log.append(propertyFilter.getPropertyName())
				.append('=')
				.append(propertyFilter.getPropertyValue())
				.append(',');
			
			if (criterion != null){
				if(LogicType.or == propertyFilterList.getLogicType())
					disjunction.add(criterion);
				else
					criteria.add(criterion);
			}
		}
		
		if(list.size() > 0)
			logger.debug(log.deleteCharAt(log.length() - 1).toString());
		
		if(LogicType.or == propertyFilterList.getLogicType())
			criteria.add(disjunction);
	}
	

	private void processProjections(Criteria criteria,
			PropertyFilterList propertyFilterList) {
		if(propertyFilterList == null 
				|| propertyFilterList.getProjections() == null 
				|| CollectionUtils.isEmpty(propertyFilterList.getProjections().list()))
			return;
		
		ProjectionList hibernateProjection = Projections.projectionList();
		String property = null;
		String alias;
		for(com.mome.core.dao.support.Projection projection 
				: propertyFilterList.getProjections().list()){
			
			property = projection.getProperty();

			if(property != null && property.indexOf('.') > -1)
				property = this.createAlias(property);
			
			alias = projection.getAlias();
			
			if(projection.getType() == ProjectionType.property)
					hibernateProjection.add(Projections.property(property).as(alias));
			else if(projection.getType() == ProjectionType.count)
				hibernateProjection.add(Projections.count(property).as(alias));
			else if(projection.getType() == ProjectionType.rowCount)
				hibernateProjection.add(((SimpleProjection)Projections.rowCount()).as(alias));
			else if(projection.getType() == ProjectionType.sum)
				hibernateProjection.add(Projections.sum(property).as(alias));
			else if(projection.getType() == ProjectionType.group)
				hibernateProjection.add(Projections.groupProperty(property).as(alias));
//			else if(projection.getType() == ProjectionType.datePart){
//				DatePartProjection dateProjection = (DatePartProjection)projection;
//				hibernateProjection.add(new HibernateDatePartProjection(property, dateProjection.isGrouped(), dateProjection.getPart()).as(alias));
//			} else if(projection.getType() == ProjectionType.dateString){
//				DateStringProjection dateProjection = (DateStringProjection)projection;
//				hibernateProjection.add(new HibernateDateStringProjection(property, dateProjection.isGrouped(), dateProjection.getPattern()).as(alias));
			else if(projection.getType() == ProjectionType.avg)
				hibernateProjection.add(Projections.avg(property).as(alias));
			else if(projection.getType() == ProjectionType.countDistinct)
				hibernateProjection.add(Projections.countDistinct(property).as(alias));
			else if(projection.getType() == ProjectionType.distinct)
				hibernateProjection.add(Projections.distinct(Projections.property(property)));
			else if(projection.getType() == ProjectionType.max)
				hibernateProjection.add(Projections.max(property).as(alias));
			else if(projection.getType() == ProjectionType.min)
				hibernateProjection.add(Projections.min(property).as(alias));
			else if(projection.getType() == ProjectionType.id)
				hibernateProjection.add(Projections.id().as(alias));
//			else if(projection.getType() == ProjectionType.rowsConcat){
//				RowsConcatProjection rowsConcatProjection = (RowsConcatProjection)projection;
//				hibernateProjection.add(new HibernateRowsConcatProjection(rowsConcatProjection.getProperty(), ",", rowsConcatProjection.isGrouped()).as(alias));
//			}
		}
		
		criteria.setProjection(hibernateProjection);
		
		if(propertyFilterList.getProjections().getResultType() == ProjectiveResultType.map)
			criteria.setResultTransformer(new ProjectionMapResultTransformer(propertyFilterList.getProjections().list()));
	}

	/**
	 * Hibernate的Criteria本身不支持直接加入跨表的条件，需要创建alias
	 * 这里根据元数据检测是否需要创建alias等等，最终支持加入跨表的条件
	 */
	public Criterion createCriterion(PropertyFilter propertyFilter){
		
		String fieldName = propertyFilter.getPropertyName();
		MatchType matchType = propertyFilter.getMatchType();

		String alias = fieldName.indexOf('.') > -1 ? this.createAlias(fieldName) : fieldName;
		//TODO  HAS
		
		if(MatchType.EX != matchType)
			return processors.get(propertyFilter.getMatchType()).buildCriterion(alias, 
					fieldName, propertyFilter.getPropertyValue(), this);
		else
			return propertyFilter.getProcessor().buildCriterion(alias, 
					fieldName, propertyFilter.getPropertyValue(), this);
		
	}

	private String[] splitField(String path) {
		String[] result = new String[(org.apache.commons.lang3.StringUtils.countMatches(path, ".") + 1) * 2];
		if (path.indexOf(".") == -1
				|| (org.apache.commons.lang3.StringUtils.countMatches(path, ".") == 1 && (path
						.endsWith(".id") || isCustomType(path)))) {
			result[0] = path;
			result[1] = "";
		} else {
			String[] names = path.split("\\.");
			int pos = path.indexOf(".");
			for (int i = 0; i < names.length; i++) {
				String s = path.substring(0, pos);
				if (i != names.length - 1 && this.isRelationProperty(s)) {
					result[i * 2] = names[i];
					result[i * 2 + 1] = "true";
				} else {
					result[i * 2] = names[i];
					result[i * 2 + 1] = "";
				}
				if (i < names.length - 1)
					pos += names[i + 1].length() + 1;
			}
		}

		return result;
	}

	/**
	 * 判断传入的属性字段是否是实体bean的属性
	 * 
	 * @param path
	 *            属性字段
	 * @return 是返回true
	 */
	private boolean isCustomType(String path) {
		String[] terms = path.split("\\.");
		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(this.entityClass, terms[0]);
		if(pd == null)
			throw new PropertyNotFoundException(this.entityClass + "." + terms[0]);
		else
			return pd.getReadMethod().isAnnotationPresent(Type.class);
	}

	private boolean isRelationProperty(String property) {
		String key = this.entityClass.getName() + "." + property;
		if (propertyRelationMap.containsKey(key))
			return propertyRelationMap.get(key);

		Class<?> clazz = this.entityClass;
		PropertyDescriptor pd = null;

		if (property.indexOf('.') == -1) {
			pd = BeanUtils.getPropertyDescriptor(this.entityClass, property);
			if(pd == null)
				return false;
			else
				clazz = pd.getPropertyType();
		} else {
			String[] terms = property.split("\\.");
			for (String term : terms) {
				pd = BeanUtils.getPropertyDescriptor(clazz, term);
				if(pd == null)
					return false;
				clazz = pd.getPropertyType();
			}
		}
		
		if(Collection.class.isAssignableFrom(clazz)){
			try {
				java.lang.reflect.Field f = ReflectionUtils.getDeclaredField(this.entityClass, property);
				java.lang.reflect.Type fieldType = f.getGenericType();
				clazz = GenericsUtils.getGenericType(fieldType, 0);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		boolean result;

		if (pd.getReadMethod().isAnnotationPresent(Type.class))
			result = false;
		else if (clazz.isAnnotationPresent(Entity.class)
				|| clazz
						.isAnnotationPresent(org.hibernate.annotations.Entity.class)
				|| clazz.isAnnotationPresent(MappedSuperclass.class))
			result = true;
		else
			result = false;

		propertyRelationMap.put(key, result);

		return result;
	}

	private String createAlias(String fieldName){
		String[] aliases = this.splitField(fieldName);
		String[] names = fieldName.split("\\.");
		// int lastAlias = 0;

		String subPath = null;
		String lastSubPath;
		String theSubPath = null;
		boolean containsSubPath = false;
		JoinType joinType;

		for (int i = 0; i < aliases.length / 2; i += 1) {
			if (aliases[i * 2 + 1] == null)
				continue;

			subPath = this.getSubPath(names, i + 1);
			containsSubPath = aliasMap.containsKey(subPath);

			if (!containsSubPath && !aliases[i * 2 + 1].equals("")) {
				lastSubPath = this.getSubPath(names, i);
				autoAlias = "auto_alias_" + aliasIndex;
				String last = aliasMap.get(lastSubPath);
				if (last == null)
					last = "";
				else
					last += ".";
				
				joinType = this.joinTypes.get(PropertyFilterList.ATTR_KEY_JOINTYPE_PREFIX + subPath);
				if(joinType == null)joinType = this.joinTypes.get(PropertyFilterList.ATTR_KEY_JOINTYPE_PREFIX + PropertyFilterList.ATTR_KEY_JOINTYPE_PATH_DEFAULT);
				if(joinType == null)joinType = JoinType.inner;
				
				criteria.createAlias(last + aliases[i * 2], autoAlias, joinType.hibernateJoinType());
				aliasMap.put(subPath, autoAlias);
				aliasIndex++;

				theSubPath = subPath;
			} else if (containsSubPath)
				theSubPath = subPath;

			// lastAlias ++;
		}

		String n = aliasMap.get(theSubPath);
		if (n == null)
			n = "";
		else
			n += ".";

		if (StringUtils.isEmpty(theSubPath))
			return fieldName;
		else
			return n + fieldName.substring(theSubPath.length() + 1);
	}
	
	private String getSubPath(String[] key, int i) {
		StringBuffer buf = new StringBuffer(key[0]);
		for (int j = 1; j < i; j++)
			buf.append(".").append(key[j]);
		return buf.toString();
	}

	public void addOrder(Criteria c, String order, boolean isAsc) {
		if (!StringUtils.isEmpty(order)) {
			if (!isAsc)
				criteria.addOrder(Order.desc(order));
			else
				criteria.addOrder(Order.asc(order));
		}
	}

	public void addLimit(Criteria c, int start, int max) {
		if (max > 0) {
			c.setFirstResult(start);
			c.setMaxResults(max);
		}
	}

	public Session getSession() {
		return session;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}
	
	public Criteria value(){
		return this.criteria;
	}
}
