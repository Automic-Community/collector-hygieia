package com.capitalone.dashboard.collector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
	String name();
	
	public boolean isWildCard() default false;
}

