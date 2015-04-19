package cn.sunflyer.simpnk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {
	public static final String CONFIG_STRING = "String";
	public static final String CONFIG_NUMBER_INT = "Integer";
	public static final String CONFIG_NUMBER_DOUBLE = "Double";
	
	String configName() default "New Config";
	String configVal() default "Null";
	String configType() default "String";
}
