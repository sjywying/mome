package com.mome.core.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

import com.mome.core.model.Model;

@Component
public class DaoLocator implements BeanFactoryAware{
	
	private static Map<Class<?>,GenericHibernateDao<?>> registry = new HashMap<Class<?>,GenericHibernateDao<?>>();
	private static BeanFactory beanFactory;
	
	@SuppressWarnings("unchecked")
	public static <T extends Model<? extends Serializable>> GenericHibernateDao<T> getDao(Class<T> entityClass, Class<?> defaultDaoClass){
		String beanName = StringUtils.uncapitalize(entityClass.getSimpleName()) + "Dao";
		GenericHibernateDao<T> dao = null;
		try {
			dao = beanFactory.getBean(beanName, GenericHibernateDao.class);
		} catch (NoSuchBeanDefinitionException e) {
			
		}
		if(dao != null)
			return dao;
		else if(registry.containsKey(entityClass))
			return (GenericHibernateDao<T>) registry.get(entityClass);
		else{
			beanName = StringUtils.uncapitalize(defaultDaoClass.getSimpleName());
			dao = (GenericHibernateDao<T>)beanFactory.getBean("scopedTarget." + beanName,defaultDaoClass);
			dao.setEntityClass(entityClass);
			registry.put(entityClass, dao);
			return dao;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
}
