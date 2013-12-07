package com.mome.core.utils;

import org.hibernate.criterion.MatchMode;

public class MomeExpressions {
	
	public static EscapedLikeExpression like(String propertyName,String value, MatchMode matchMode){
		return new EscapedLikeExpression(propertyName,matchMode.toMatchString(value));
	}
	
	public static EscapedLikeExpression ilike(String propertyName,String value, MatchMode matchMode){
		return new EscapedLikeExpression(propertyName,matchMode.toMatchString(value),true);
	}
}
