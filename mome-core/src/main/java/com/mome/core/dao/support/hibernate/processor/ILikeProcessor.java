package com.mome.core.dao.support.hibernate.processor;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;

import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;
import com.mome.core.utils.MomeExpressions;

public class ILikeProcessor implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias, String name, Object value,
			CriteriaBuilder context) {
		return MomeExpressions.ilike(alias, (String) value,
				MatchMode.ANYWHERE);
	}

}
