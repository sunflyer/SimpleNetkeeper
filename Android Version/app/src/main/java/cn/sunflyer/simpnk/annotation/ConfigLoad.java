package cn.sunflyer.simpnk.annotation;

/**
 * Created by 陈耀璇 on 2015/5/13.
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigLoad {

    /**
     * 注解请求字段
     * */
    public String req();

    /**
     * 请求字段的类型，默认为String
     * */
    public String type() default Config.CONFIG_STRING;

    /**
     * 请求来源类，默认StatusController
     * */
    public String sourceClass() default "StatusController";

    /**
     * 本地方法名称
     * */
    public String methodName() default "setText";

    /**
     * 本地方法需求类型
     * */
    public String methodType() default Config.CONFIG_CHARSEQUENCE;

    /**
     * 如果本地方法需求布尔型参数，需要在这里给定真值
     * */
    public String reqVal() default "";

    public static final String sourcePrefix = "cn.sunflyer.simpnk.control.";
}
