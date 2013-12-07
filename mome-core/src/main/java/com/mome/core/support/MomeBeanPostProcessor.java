package com.mome.core.support;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.mome.core.utils.SystemConfig;

@Component
public class MomeBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(bean instanceof SessionFactoryImplementor){
			SystemConfig.DIALECT_HIBERNATE = ((SessionFactoryImplementor)bean).getDialect().getClass().getName();
		}
		return bean;
	}

}
