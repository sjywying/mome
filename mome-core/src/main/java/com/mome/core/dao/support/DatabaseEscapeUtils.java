package com.mome.core.dao.support;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.springframework.util.StringUtils;

import com.mome.core.dao.support.escape.DatabaseEscape;
import com.mome.core.dao.support.escape.HSQLEscape;
import com.mome.core.dao.support.escape.MySQLEscape;
import com.mome.core.dao.support.escape.OracleEscape;
import com.mome.core.utils.SystemConfig;

public class DatabaseEscapeUtils {

	public static final char escape = 7;
	private static Map<String,DatabaseEscape> map = new HashMap<String,DatabaseEscape>();

	static{
		map.put(Oracle10gDialect.class.getName(), new OracleEscape());
		map.put(HSQLDialect.class.getName(), new HSQLEscape());
		map.put(MySQL5InnoDBDialect.class.getName(), new MySQLEscape());
		//TODO
//		map.put(PostgreSQL82Dialect.class.getName(), new MySQLEscape());
	}
	
	public static String escape(String value){
		if(!StringUtils.isEmpty(value)){
			return map.get(SystemConfig.DIALECT_HIBERNATE).escape(value);
		}
		
		return value;
	}
	
	public static String encode(String value){
		if(!StringUtils.isEmpty(value)){
			return map.get(SystemConfig.DIALECT_HIBERNATE).encode(value);
		}
		
		return value;
	}
}
