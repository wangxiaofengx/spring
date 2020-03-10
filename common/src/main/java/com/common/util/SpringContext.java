package com.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		context = arg0;
	}

	public static ApplicationContext getSpringContext() {
		return context;
	}

	public static Object getBean(String beanId) {
		return context.getBean(beanId);
	}

	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}

	public static Object getBean(String beanId, Object... args) {
		System.out.println("环境：" + context);
		return context.getBean(beanId, args);
	}

	public static <T> T getBean(Class<T> clazz, Object... args) {
		return context.getBean(clazz, args);
	}
}
