package com.mome.core.dao.support.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.BasicTransformerAdapter;

import com.mome.core.dao.support.Projection;

public class ProjectionMapResultTransformer extends BasicTransformerAdapter
		implements Serializable {

	private static final long serialVersionUID = 3958989021950599679L;
	private List<Projection> projections;
	
	ProjectionMapResultTransformer(List<Projection> projections){
		this.projections = projections;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Map result = new HashMap(tuple.length);
		for ( int i=0; i<tuple.length; i++ ) {
			String alias = projections.get(i).getAlias();
			if ( alias!=null ) {
				result.put( alias, tuple[i] );
			}
		}
		return result;
	}
}
