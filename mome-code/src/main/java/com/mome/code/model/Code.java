package com.mome.code.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.mome.core.model.AbstractModel;

@Entity
@Table(name = "SYS_CODE")
public class Code extends AbstractModel{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String value;
	private boolean enabled = true;
	private CodeType type;
	private Integer index = 0;
}
