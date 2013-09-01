/**
 * Model.java
 * 
 * Created in 2008-1-17
 */
package com.mome.core.model;

import java.io.Serializable;

/**
 * <p>
 * 数据层顶级接口,所有与数据库交互的model对象必须继承此接口<br>
 * 数据层顶级接口，提供了主键生成方法，及version记录等
 * </p>
 * 
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-8-6 下午9:29:21
 * @version V1.0
 */
public interface Model<K extends Serializable> {

	/**
	 * 获取主键
	 * 
	 * @return 主键
	 */
	K getId();

	/**
	 * 设置主键
	 * 
	 * @param id
	 *            主键
	 */
	void setId(K id);

	/**
	 * 获取version
	 * 
	 * @return
	 */
	Integer getVersion();

	/**
	 * 设置version
	 * 
	 * @param version
	 */
	void setVersion(Integer version);
}
