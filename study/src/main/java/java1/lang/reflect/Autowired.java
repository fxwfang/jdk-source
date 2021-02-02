package java1.lang.reflect;

import java.lang.annotation.*;

//添加四个元注解
@Retention(RetentionPolicy.RUNTIME) //
@Target(ElementType.FIELD) // 作用域
@Inherited  //是否可以被继承
@Documented
public @interface Autowired {
    /**
     * Retention：
     * 按生命周期来划分可分为3类：
     * 1、RetentionPolicy.SOURCE：注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃；
     * 2、RetentionPolicy.CLASS：注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期；
     * 3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在；
     * 这3个生命周期分别对应于：Java源文件(.java文件) ---> .class文件 ---> 内存中的字节码。
     */

}
