package com.mome.core.dao.support;

/**
 * <p>
 * 投影类型
 * </p>
 *
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-9-11 下午6:54:30
 * @version V1.0
 */
public enum ProjectionType {

	group,
	max,
	min,
	rowCount,
	count,
	id,
	avg,
	distinct,
	countDistinct,
	property,
	sum;
	
	public String getAlias(String property){
		switch(this){
			case property:
				return property + "_";
			default:
				return property + "_" + this.name();
		}
	}
}
