package vn.thuephonghoc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import vn.thuephonghoc.aspect.LimitType;

//Used to describe the method
@Target(ElementType.METHOD)
//The annotation is not only saved in the class file, but still exists after the jvm loads the class file
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

     // Resource name, used to describe the interface function
     String name() default "";

     // resource key
     String key() default "";

     // key prefix
     String prefix() default "";

     // time, in seconds
     int period();

     // Limit the number of visits
     int count();

     // restriction type
     LimitType limitType() default LimitType.CUSTOMER;

}
