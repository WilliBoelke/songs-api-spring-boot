package htwb.ai;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Annotation available during Runtime
@Retention(RetentionPolicy.RUNTIME)
// Annotation only on Methods
@Target(ElementType.METHOD)
public @interface RunMe {

}