package com.mome.core.dao.support;

public enum JoinType {

	inner(0),left(1),right(2),full(4);
	
	int hibernateJoinType;
	
	JoinType(int hibernateJoinType){
		this.hibernateJoinType = hibernateJoinType;
	}
	
	/**
	 * @return the hibernateJoinType
	 */
	public int hibernateJoinType() {
		return hibernateJoinType;
	}
}
