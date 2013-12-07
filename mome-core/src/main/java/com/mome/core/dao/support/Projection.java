package com.mome.core.dao.support;

public class Projection {

	private ProjectionType type;
	private String property;
	protected String alias;
	
	public Projection(ProjectionType type, String property) {
		this.type = type;
		this.property = property;
	}
	
	public Projection(ProjectionType type, String property, String alias) {
		this(type,property);
		this.alias = alias;
	}

	public ProjectionType getType() {
		return type;
	}
	
	public String getProperty() {
		return property;
	}
	
	public String getAlias(){
		return this.alias == null ? this.type.getAlias(this.property) : this.alias;
	}
}
