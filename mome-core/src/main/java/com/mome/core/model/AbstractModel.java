/**
 * AbstractModel.java
 * 
 * Created in 2008-1-17
 */
package com.mome.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;

/**
 * <p>
 * 自定义model类的父类，提供了id的生成方式，及重写了hashcode、equals方法</br>
 * 持久化模型抽象实现，主要是确定主键生成机制，添加通用的hashcode、equals方法。
 * </p>
 *
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-8-7 下午4:03:42
 * @version V1.0
 */
@MappedSuperclass
public abstract class AbstractModel implements Model<String>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Date createTime = new Date();
	private Integer version;
	
	@Id
	@Column(name="ID", length=32)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy="uuid")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name="CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getVersion() {
		return version;
	}

	@Version
	@Column(name="VERSION")
	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof AbstractModel)) {
			return false;
		}
		AbstractModel model = (AbstractModel) o;
		return ObjectUtils.equals(id, model.getId());
	}

	public int hashCode() {
		return ObjectUtils.hashCode(id);
	}

	public String toString() {
		return this.getClass().getName() + "#" + id;
	}
}
