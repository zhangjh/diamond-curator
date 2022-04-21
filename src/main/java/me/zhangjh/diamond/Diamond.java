package me.zhangjh.diamond;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangjh
 * @date 2022/04/21
 * 优先选用注解方式，对业务无侵入，低版本spring项目可选用抽象类 {@link AbstractDiamondListener}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Diamond {

    /** diamond groupId */
    String groupId();

    /** diamond dataId */
    String dataId();

    /** diamond内容格式：默认为'text'，普通文本，可以传'json'表示json */
    String format() default "text";

    /** 当内容为text普通文本时，key和value的自定义分隔符，默认为':' */
    String split() default ":";
}
