package com.mome.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mome.core.dao.support.PropertyFilterList;
import com.mome.core.dao.support.SortProperties;
import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.utils.GenericsUtils;

/**
 * 基础的HibernateDao
 * 
 * 使用sessionFactory的currentSession，无需自己管理session，也不用调用spring的template代码
 * 
 * @author allan
 *
 * @param <T> 实体类型
 * @param <PK> 主键类型
 */
@Component
@Scope("prototype")
public class GenericHibernateDao<T> extends HibernateDao{
	
	@SuppressWarnings("unchecked")
	protected Class<T> entityClass = GenericsUtils
			.getSuperClassGenricType(this.getClass());;

	public GenericHibernateDao() {
	}

	public GenericHibernateDao(java.lang.Class<T> persistClass) {
		this.entityClass = persistClass;
	}

	/**
	 * @return 当前管理的实体类型
	 */
	public Class<T> getEntityClass() {
		return this.entityClass;
	}
	
	void setEntityClass(Class<T> entityClass){
		this.entityClass = entityClass;
	}

	/****************************  以下是工具性方法      *******************************/
	protected Criteria createCriteria() {
		return this.getSession().createCriteria(this.entityClass);
	}

	protected Criteria createCriteria(PropertyFilterList propertyFilterList) {
		return new CriteriaBuilder(this.getSession(), this.entityClass)
			.build(propertyFilterList);
	}
	/****************************  以上是工具性方法      *******************************/
	
	
	/****************************  以下是业务方法      *******************************/
	public boolean exists(Serializable id) {
		return this.createCriteria()
				.add(Restrictions.eq("id", id))
				.setProjection(Projections.rowCount())
				.list()
				.get(0).equals(1L);
	}

	@SuppressWarnings("unchecked")
	public T get(Serializable id) {
		Object entity = this.getSession().get(entityClass, id);
		return entity == null ? null : (T) entity;
	}

	@SuppressWarnings("unchecked")
	public T load(Serializable id) {
		return (T) this.getSession().load(entityClass, id);
	}

	public List<T> getAll() {
		return super.findAllList(this.entityClass);
	}

	public void removeById(Serializable id) {
		super.removeById(this.entityClass, id);
	}

	public Long count(PropertyFilterList propertyFilterList) {
		return countByCriteria(this.createCriteria(propertyFilterList));
	}

	@SuppressWarnings("unchecked")
	public T findUnique(String name, Object value) {
		return (T) new CriteriaBuilder(this.getSession(),this.entityClass)
			.build(name, value)
			.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public T findUnique(PropertyFilterList propertyFilterList) {
		return (T) this.createCriteria(propertyFilterList).uniqueResult();
	}
	

	@SuppressWarnings("unchecked")
	protected List<T> find(String hql, Object... values) {
		return (List<T>) super.findList(hql, values);
	}

	@SuppressWarnings("unchecked")
	public List<T> find(PropertyFilterList propertyFilterList,
			SortProperties sortProperties, int start, int maxResults) {

		Criteria criteria = new CriteriaBuilder(this.getSession(), entityClass)
			.build(propertyFilterList, sortProperties);
		
		criteria.setFirstResult(start);
		
		if(maxResults > 0)
			criteria.setMaxResults(maxResults);
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> find(String name,Object value){
		return new CriteriaBuilder(this.getSession(),this.entityClass)
			.build(name, value)
			.list();
	}

	@SuppressWarnings("unchecked")
	protected List<T> find(String hql, int start, int maxResults,
			Object... parameters) {
		return (List<T>) super.findList(hql, start, maxResults, parameters);
	}
	
}
