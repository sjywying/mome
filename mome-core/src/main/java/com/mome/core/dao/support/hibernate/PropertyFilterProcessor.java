package com.mome.core.dao.support.hibernate;


import org.hibernate.criterion.Criterion;

/**
 * <p>
 * 把PropertyFilter转化为Criterion的过程
 * </p>
 *
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-9-11 下午4:47:09
 * @version V1.0
 */
public interface PropertyFilterProcessor {

	public Criterion buildCriterion(
			String alias, 
			String name, 
			Object value, 
			CriteriaBuilder context);
	
}
