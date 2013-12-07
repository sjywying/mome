package com.mome.core.dao.support.escape;


public class OracleEscape implements DatabaseEscape{

	public static final char escape = 7;
	
	@Override
	public String escape(String value) {
		return value.replaceAll("([^" + escape + "]/)", "$1/")
			.replaceAll("(" + escape + "/)", "/");
	}
	
	@Override
	public String encode(String value) {
		return value.replace("%", escape + "/%").replace("_", escape + "/_");
	}

}
