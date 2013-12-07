package com.mome.core.dao.support.hibernate.processor;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.mome.core.dao.support.PropertyFilter;
import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

public class NotProcessor implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias, String name, Object value,
			CriteriaBuilder context) {
		return Restrictions.not(context.createCriterion((PropertyFilter)value));
	}

}
