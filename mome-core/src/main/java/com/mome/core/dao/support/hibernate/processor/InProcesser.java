package com.mome.core.dao.support.hibernate.processor;

import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

public class InProcesser implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias,String name, Object value, CriteriaBuilder context) {
		Criterion criterion = null;
		
		if (value instanceof Object[]) {
			Object[] valueArray = (Object[]) value;

			if (valueArray.length == 0)
				throw new RuntimeException("IN子句要求值不为空集合或空数组");

			if (ArrayUtils.contains(valueArray, null)) {
				if (valueArray.length == 1)
					criterion = Restrictions.isNull(alias);
				else {
					Disjunction disjunction = Restrictions.disjunction();
					for (Object item : valueArray) {
						if (item == null)
							disjunction.add(Restrictions.isNull(alias));
						else
							disjunction.add(Restrictions.eq(alias, item));
					}
					criterion = disjunction;
				}
			} else
				criterion = Restrictions.in(alias, (Object[]) value);
		} else if (value instanceof Collection) {
			Collection<?> valueCollection = (Collection<?>) value;

			if (valueCollection.size() == 0)
				throw new RuntimeException("IN子句要求值不为空集合或空数组");

			if (valueCollection.contains(null)) {
				if (valueCollection.size() == 1)
					criterion = Restrictions.isNull(alias);
				else {
					Disjunction disjunction = Restrictions.disjunction();
					for (Object item : valueCollection) {
						if (item == null)
							disjunction.add(Restrictions.isNull(alias));
						else
							disjunction.add(Restrictions.eq(alias, item));
					}

					criterion = disjunction;
				}
			} else
				criterion = Restrictions.in(alias, valueCollection);
		} else {
			// add by qinfj 当传入的值不为数组或者Collection时抛出异常,便于发现由于疏忽导致的错误
			throw new RuntimeException("IN子句查询字段值必须为数组或者Collection");
		}

		return criterion;
	}

}
