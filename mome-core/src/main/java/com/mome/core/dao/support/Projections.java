package com.mome.core.dao.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.Assert;

/**
 * <p>
 * 投影
 * </p>
 *
 * @author Crazy/Y </br>
 * @Email sjywying105@gmail.com
 * @date 2013-9-11 下午6:50:10
 * @version V1.0
 */
public class Projections {

	private List<Projection> values = new ArrayList<Projection>();
	private ProjectiveResultType resultType = ProjectiveResultType.array;

	public static Projections instance(){
		return new Projections();
	}
	
	public static Projections mapInstance(){
		Projections result = new Projections();
		result.resultType = ProjectiveResultType.map;
		return result;
	}
	
	public Projections add(Projection projection){		
		this.values.add(projection);
		return this;
	}
	
	public Projections property(String property){
		return this.add(new Projection(ProjectionType.property, property));
	}
	
	public Projections property(String property, String alias){
		return this.add(new Projection(ProjectionType.property, property, alias));
	}
	
	public Projections sum(String property){
		return this.add(new Projection(ProjectionType.sum, property));
	}
	
	public Projections sum(String property, String alias){
		return this.add(new Projection(ProjectionType.sum, property,alias));
	}
	
	public Projections rowCount(){
		return this.add(new Projection(ProjectionType.rowCount, null));
	}
	
	public Projections rowCount(String alias){
		return this.add(new Projection(ProjectionType.rowCount, null, alias));
	}
	
	public Projections count(String property){
		return this.add(new Projection(ProjectionType.count, property));
	}
	
	public Projections count(String property, String alias){
		return this.add(new Projection(ProjectionType.count, property, alias));
	}
	
	public Projections distinct(String property){
		Assert.isTrue(this.resultType != ProjectiveResultType.map, "结果为Map的查询不支持distinct投影");
		return this.add(new Projection(ProjectionType.distinct, property));
	}
	
	public Projections countDistinct(String property){
		return this.add(new Projection(ProjectionType.countDistinct, property));
	}
	
	public Projections avg(String property){
		return this.add(new Projection(ProjectionType.avg, property));
	}
	
	public Projections avg(String property, String alias){
		return this.add(new Projection(ProjectionType.avg, property, alias));
	}
	
	public Projections max(String property){
		return this.add(new Projection(ProjectionType.max, property));
	}
	
	public Projections max(String property, String alias){
		return this.add(new Projection(ProjectionType.max, property, alias));
	}
	
	public Projections min(String property){
		return this.add(new Projection(ProjectionType.min, property));
	}
	
	public Projections min(String property, String alias){
		return this.add(new Projection(ProjectionType.min, property, alias));
	}
	
	public Projections group(String property){
		return this.add(new Projection(ProjectionType.group, property));
	}
	
	public Projections group(String property, String alias){
		return this.add(new Projection(ProjectionType.group, property, alias));
	}
	
	public Projections id(String property){
		return this.add(new Projection(ProjectionType.id, property));
	}
	
	public Projections id(String property, String alias){
		return this.add(new Projection(ProjectionType.id, property, alias));
	}
	
	public List<Projection> list(){
		return this.values;
	}
	
	public Projections remove(String name){
		Iterator<Projection> iterator = this.values.iterator();
		Projection projection;
		while(iterator.hasNext()){
			projection = iterator.next();
			if(projection.getProperty().equals(name))
				iterator.remove();
		}
		return this;
	}

	public ProjectiveResultType getResultType() {
		return resultType;
	}
}
