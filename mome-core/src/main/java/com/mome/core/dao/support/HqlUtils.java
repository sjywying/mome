package com.mome.core.dao.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title:HQL工具类<br>
 * Description:HQL工具类。提供了两个方法，一个为将传入的SQL删除从from之前的部分去掉，一个为将order by部分去掉<br>
 * Company: 亚信联创集团股份有限公司<br>
 * 
 * @author author
 * @see
 * @CreateDate 2011-8-29 下午06:12:39
 * 
 */
public class HqlUtils {

	/**
	 * 将HQL中，删除掉第一个from及from之前的部分
	 * 
	 * @param hql
	 *            需要操作的HQL
	 * @return 去掉from及之前部分的HQL
	 * @waring 注:本方法不适用于union及union all的SQL
	 */
	public static String removeSelect(String hql) {
		int beginPos = hql.toLowerCase().indexOf("from");
		return hql.substring(beginPos);
	}

	/**
	 * 将HQL中，删除掉order by及之后的部分
	 * 
	 * @param hql
	 *            需要操作的HQL
	 * @return 删除掉order by及之后的部分的HQL
	 */
	public static String removeOrders(String hql) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

}
