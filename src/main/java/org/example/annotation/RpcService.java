package org.example.annotation;

import java.lang.annotation.*;

//由于cglib对象是继承原来的类实现的，因此使注解也能被继承
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String value() default "";
}
