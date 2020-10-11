package me.sombrero;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // TYPE: Interface, Class, Enum 세가지에 붙일 수 있는 애노테이션.
@Retention(RetentionPolicy.SOURCE)
public @interface Magic {
}
