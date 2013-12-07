package com.mome.core.dao.support.hibernate.processor;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

public class SizeGtProcessor implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias, String name, Object value,
			CriteriaBuilder context) {
		return Restrictions.sizeGt(alias, (Integer)value);
	}

}
