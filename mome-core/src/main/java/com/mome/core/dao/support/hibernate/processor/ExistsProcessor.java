package com.mome.core.dao.support.hibernate.processor;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Subqueries;

import com.mome.core.dao.support.PropertyFilterList;
import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

public class ExistsProcessor implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias, String name, Object value,
			CriteriaBuilder context) {
		if (value instanceof PropertyFilterList) {
			PropertyFilterList propertyFilterList = (PropertyFilterList) value;

			Class<?> targetClass = (Class<?>) propertyFilterList
					.getAttribute(PropertyFilterList.ATTR_KEY_CLASS);

			if (targetClass == null)
				throw new RuntimeException("Exists查询需要给子查询设置entityClass");

			DetachedCriteria dc = new CriteriaBuilder(context.getSession(),
					targetClass).buildDetachedCriteria(propertyFilterList);

			dc.setProjection(Projections.id());

			return  Subqueries.exists(dc);
		} else {
			throw new RuntimeException("Exists查询只允许PropertyFilterList值");
		}
	}

}
