package com.mome.core.dao.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Title:数据库查询SQL排序方式集合<br>
 * 
 * Description:封装了数据库查询SQL排序字段及排序方式的集合，支持根据多列排序<br>
 * 
 * Company: 亚信联创集团股份有限公司<br>
 * 
 * @author allan
 * @see
 * @CreateDate 2011-8-29 上午09:31:07
 * 
 */
public class SortProperties {

	/** 记录排序字段及排序方式的list */
	private List<SortProperty> children = new ArrayList<SortProperty>();

	/**
	 * 构造器私有，不允许外部直接创建实例
	 */
	private SortProperties() {
	}

	/**
	 * Title:排序方式<br>
	 * 
	 * Description:升序排列和降序排列两种方式<br>
	 * 
	 * Company: 亚信联创集团股份有限公司<br>
	 * 
	 * @author allan
	 * @see
	 * @CreateDate 2011-8-29 上午09:28:30
	 * 
	 */
	public static enum SortType {
		ASC, DESC
	}

	/**
	 * 带参数的构造器。调用了无参构造及add方法
	 * 
	 * @param propertyName
	 *            排序字段
	 * @param sortType
	 *            排序方式
	 * @return SortProperties实例
	 */
	public static SortProperties instance(String propertyName, SortType sortType) {
		return SortProperties.instance().add(propertyName, sortType);
	}

	/**
	 * 无属性构造方法，内部直接调用了new SortProperties()
	 * 
	 * @return 新生成的SortProperties实例
	 */
	public static SortProperties instance() {
		return new SortProperties();
	}

	/**
	 * 将参数封装成SortProperty对象后添加到list中
	 * 
	 * @param propertyName
	 *            排序字段
	 * @param sortType
	 *            排序方式
	 * @return 调用此方法的SortProperties实例自身
	 */
	public SortProperties add(String propertyName, SortType sortType) {
		this.children.add(new SortProperty(propertyName, sortType));
		return this;
	}

	/**
	 * 将排序字段按asc升序排序方式添加到当前对象中
	 * 
	 * @param propertyName
	 *            排序字段
	 * @return 调用本方法的SortProperties对象自身
	 */
	public SortProperties asc(String propertyName) {
		return this.add(propertyName, SortType.ASC);
	}

	/**
	 * 将排序字段按desc降序排序方式添加到当前对象中
	 * 
	 * @param propertyName
	 *            排序字段
	 * @return 调用本方法的SortProperties对象自身
	 */
	public SortProperties desc(String propertyName) {
		return this.add(propertyName, SortType.DESC);
	}

	/**
	 * 循环遍历sortProperties对象，将sortProperties对象所有排序方式添加到当前对象中<br>
	 * 此方法存在如下可能：当前对象已经存在按name asc排序，而传进来的sortProperties含有按name desc排序<br>
	 * 因此如果调用此方法，应慎重
	 * 
	 * @param sortProperties
	 * @return
	 */
	public SortProperties addAll(SortProperties sortProperties) {
		for (SortProperty sp : sortProperties.list())
			this.children.add(sp);

		return this;
	}

	public List<SortProperty> list() {
		return this.children;
	}

	/**
	 * Title:记录数据库查询SQL排序信息的类<br>
	 * 
	 * Description: 记录数据库查询语句排序字段和排序方式<br>
	 * 
	 * Company: 亚信联创集团股份有限公司<br>
	 * 
	 * @author allan
	 * @see
	 * @CreateDate 2011-8-29 上午09:24:21
	 * 
	 */
	public static class SortProperty {

		/** 排序字段 */
		private String propertyName;

		/** 排序方式，有asc和desc两种排序方式 */
		private SortType sortType;

		/**
		 * 构造器
		 * 
		 * @param propertyName
		 *            排序字段
		 * @param sortType
		 *            排序方式
		 */
		public SortProperty(String propertyName, SortType sortType) {
			this.propertyName = propertyName;
			this.sortType = sortType;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public SortType getSortType() {
			return sortType;
		}

		public String toString() {
			return "{propertyName:" + propertyName + ", sortType:"
					+ sortType.name() + "}";
		}
	}

}
