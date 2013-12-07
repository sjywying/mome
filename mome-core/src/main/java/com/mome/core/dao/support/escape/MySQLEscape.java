package com.mome.core.dao.support.escape;


public class MySQLEscape extends AbstractEscape {

	@Override
	protected String getEscape() {
		return "\\";
	}

}
