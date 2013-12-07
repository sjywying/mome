package com.mome.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

import com.mome.core.dao.support.HqlUtils;
import com.mome.core.dao.support.PropertyFilterList;
import com.mome.core.dao.support.SortProperties;
import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.utils.ReflectionUtils;

public class HibernateDao {

	private static final Log log = LogFactory.getLog(HibernateDao.class);
	protected SessionFactory sessionFactory;
	
	/****************************  以下是工具性方法      *******************************/
	/**
	 * 使用sessionFactory.getCurrentSession()获取session，
	 * 无需自己管理session。实际上session的打开、关闭都是在
	 * transactionManager里面做的。
	 * 
	 * @return Hibernate Session 实例
	 */
	protected Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected Query createQuery(String hql, Object... values) {
		Assert.hasText(hql);
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}
	
	@SuppressWarnings("unchecked")
	protected Long countByCriteria(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(impl,
					"orderEntries");
			ReflectionUtils
					.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			log.error("反射异常", e);
		}

		Long totalCount = (Long) c.setProjection(Projections.rowCount())
				.uniqueResult();

		totalCount = totalCount == null ? 0 : totalCount;

		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			log.error("反射异常", e);
		}

		return totalCount;
	}
	
	/****************************  以上是工具性方法      *******************************/
	public void save(Object object) {
		getSession().saveOrUpdate(object);
	}
	
	/**
	 * 批量保存
	 * @param objects 要保存的对象集合
	 */
	public void saveAll(Collection<?> objects){
		for(Object o : objects)
			this.save(o);
	}

	public void merge(Object object) {
		getSession().merge(object);
	}

	public void evict(Object object) {
		getSession().evict(object);
	}
	
	public void remove(Object o) {
		this.getSession().delete(o);
	}

	@SuppressWarnings("unchecked")
	protected Long count(String hql, Object... values) {
		// Count查询
		String countQueryString = " select count (*) "
				+ HqlUtils.removeSelect(HqlUtils.removeOrders(hql));

		Query query = this.getSession().createQuery(countQueryString);
		for (int i = 0; i < values.length; i++)
			query.setParameter(i, values[i]);

		List<Long> countlist = query.list();

		return (Long) countlist.get(0);
	}
	
	public void refresh(Object o) {
		this.getSession().refresh(o);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T get(Class<T> entityClass,Serializable id) {
		T entity = (T)this.getSession().get(entityClass, id);
		return entity == null ? null : (T) entity;
	}

	@SuppressWarnings("unchecked")
	protected <T> T load(Class<T> entityClass,Serializable id) {
		return (T) this.getSession().load(entityClass, id);
	}

	protected void removeById(Class<?> entityClass,Serializable id) {
		this.getSession().delete(this.load(entityClass,id));
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> findAllList(Class<T> entityClass) {
		return this.getSession().createCriteria(entityClass).list();
	}
	
	protected List<?> findList(String hql, Object... values) {
		Query query = this.getSession().createQuery(hql);
		for (int i = 0; i < values.length; i++)
			query.setParameter(i, values[i]);
		return query.list();
	}

	protected List<?> findList(String hql, int start, int maxResults,
			Object... parameters) {
		Query query = createQuery(hql, parameters).setFirstResult(start);
		
		if(maxResults > 0)
			query.setMaxResults(maxResults).list();
		
		return query.list();
	}
	
	public List<?> findList(PropertyFilterList propertyFilterList,Class<?> entityClass,	
			SortProperties sortProperties, int start, int maxResults){
		Criteria criteria = new CriteriaBuilder(this.getSession(), entityClass)
			.build(propertyFilterList,sortProperties);
	
		criteria.setFirstResult(start);
		if(maxResults > 0)
			criteria.setMaxResults(maxResults);
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	protected List<Map<String,Object>> findMapList(PropertyFilterList propertyFilterList, Class<?> entityClass, String projections, 
			SortProperties sortProperties, int start, int maxResults) {
		
		Criteria criteria = new CriteriaBuilder(this.getSession(), entityClass)
			.build(propertyFilterList,sortProperties,projections);
		
		criteria.setFirstResult(start);
		if(maxResults > 0)
			criteria.setMaxResults(maxResults);
		
		//这个地方做的比较恶心
		List<Object> list = criteria.list();
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> map = null;
		String[] projectionArray = projections.split(",");
		for(Object o : list){
			map = new HashMap<String,Object>();
			if(o instanceof Object[]){
				Object[] values = (Object[])o;
				for(int i=0;i<projectionArray.length;i++)
					map.put(projectionArray[i], values[i]);
			}else
				map.put(projections, o);
			
			result.add(map);
		}
				
		return result;
	}
	
	public void flush(){
		this.getSession().flush();
	}
}
