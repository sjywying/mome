package com.mome.core.dao.support.hibernate.processor;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import com.mome.core.dao.support.LogicType;
import com.mome.core.dao.support.PropertyFilter;
import com.mome.core.dao.support.PropertyFilterList;
import com.mome.core.dao.support.hibernate.CriteriaBuilder;
import com.mome.core.dao.support.hibernate.PropertyFilterProcessor;

public class FragmentProcessor implements PropertyFilterProcessor {

	@Override
	public Criterion buildCriterion(String alias, String name, Object value,
			CriteriaBuilder context) {
		PropertyFilterList propertyFilterList = (PropertyFilterList)value;
		
		List<PropertyFilter> list = propertyFilterList.list();
		
		Criterion criterion;
		
		if(list.size() == 1){
			PropertyFilter propertyFilter = list.get(0);
			return context.createCriterion(propertyFilter);
		}else{
			Junction junction;
			
			if(LogicType.or == propertyFilterList.getLogicType())
				junction = Restrictions.disjunction();
			else
				junction = Restrictions.conjunction();
			
			for (PropertyFilter propertyFilter : list) {
				criterion = context.createCriterion(propertyFilter);
				junction.add(criterion);
			}
			
			return junction;
		}
	}

}
