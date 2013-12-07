package com.mome.core.dao.support.hibernate.processor;

import java.util.Collection;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

public class NotInProcessor implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias, String name, Object value,
			CriteriaBuilder context) {
		Criterion criterion = null;
		if (value instanceof Object[])
			criterion = Restrictions.in(alias, (Object[]) value);
		else if (value instanceof Collection)
			criterion = Restrictions.in(alias, (Collection<?>) value);
		else
			throw new RuntimeException("错误的参数类型");

		criterion = Restrictions.not(criterion);
		
		return criterion;
	}

}
