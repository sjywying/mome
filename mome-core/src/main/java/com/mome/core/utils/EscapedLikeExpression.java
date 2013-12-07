package com.mome.core.utils;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleExpression;

import com.mome.core.dao.support.DatabaseEscapeUtils;

public class EscapedLikeExpression extends SimpleExpression {

	private static final long serialVersionUID = -8277235093414167691L;

	private static boolean escape = false;
	private static boolean appendEscape = false;
	
//	static{
//		appendEscape = SystemConfig.Config.getBoolean("mome.core.auto_escape_like",true)
//			&& (SystemConfig.DIALECT_HIBERNATE.indexOf("Oracle") > -1
//			|| SystemConfig.DIALECT_HIBERNATE.indexOf("HSQL") > -1);
//		
//		escape = SystemConfig.Config.getBoolean("mome.core.auto_escape_like",true);
//	}
	
	public EscapedLikeExpression(String propertyName, Object value) {
		super(propertyName, escape ? DatabaseEscapeUtils.escape((String)value) : value, " like ");
	}
	
	public EscapedLikeExpression(String propertyName, Object value, boolean ignoreCase) {
		super(propertyName, escape ? DatabaseEscapeUtils.escape((String)value) : value, " like ", true);
	}

	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
		throws HibernateException {
		String result = super.toSqlString(criteria, criteriaQuery);
		if(appendEscape)
			result = result + " escape '/'";
		return result;
	}

}
