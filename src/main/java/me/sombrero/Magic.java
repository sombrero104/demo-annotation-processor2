package me.sombrero;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Magic 애노테이션 만들기.
 */
@Target(ElementType.TYPE) // TYPE: Interface, Class, Enum 세가지에 붙일 수 있는 애노테이션.
@Retention(RetentionPolicy.SOURCE) // CLASS(바이트코드)까지 갈 필요 없음.
public @interface Magic {
}
