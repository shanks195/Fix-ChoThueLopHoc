package vn.thuephonghoc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
	// The attribute name of the basic object
    String propName() default "";
    // inquiry mode
    Type type() default Type.EQUAL;

    // The attribute name of the connection query, such as dept in the User class
    String joinName() default "";

    // Default left connection
    Join join() default Join.LEFT;

    String blurry() default "";

    enum Type {
        // equal/
        EQUAL
        //greater or equal to
        , GREATER_THAN
        // less than or equal to
        , LESS_THAN
        // Medium fuzzy query
        , INNER_LIKE
        // Left fuzzy query
        , LEFT_LIKE
        // Right fuzzy query
        , RIGHT_LIKE
        // less than
        , LESS_THAN_NQ
        // contains
        , IN
        // not equal to
        ,NOT_EQUAL
        // between
        ,BETWEEN
        // not null
        ,NOT_NULL
    }

    // Suitable for simple connection query, please customize the annotation for complex, or use sql query
    enum Join {
        /**Left and right connection*/ 
        LEFT, RIGHT
    }
}
