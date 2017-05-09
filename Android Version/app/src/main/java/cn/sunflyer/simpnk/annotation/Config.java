package cn.sunflyer.simpnk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Config {
	public static final String CONFIG_STRING = "String";
	public static final String CONFIG_NUMBER_INT = "Integer";
	public static final String CONFIG_NUMBER_DOUBLE = "Double";
	public static final String CONFIG_NUMBER_LONG = "Long";
	public static final String CONFIG_CHAR = "Character";
	public static final String CONFIG_BOOLEAN = "Boolean";
	public static final String CONFIG_CHARSEQUENCE = "CharSequence";
	
	public String configName();
	public String configType()  default  CONFIG_STRING;
	public boolean base64() default false;
}
