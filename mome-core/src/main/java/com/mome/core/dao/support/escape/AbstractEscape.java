package com.mome.core.dao.support.escape;

public abstract class AbstractEscape implements DatabaseEscape{

	@Override
	public String escape(String value) {
		return value;
	}

	protected abstract String getEscape();

	@Override
	public String encode(String value) {
		return value.replace("%", getEscape() + "%").replace("_", getEscape() + "_");
	}
}
