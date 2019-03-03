package nl.cerios.reactive.chocolate.factory.verticle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface TestAsync {
  int numInputMessages() default 1;

  int numResultMessages() default 1;

  long maxDurationMillis() default 1_000L;
}