package net.wolftail.api.lifecycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When applied to a method or constructor, this annotation indicates during
 * the method(or constructor)'s calling the game section or the thread calling
 * must satisfy some constrains(see {@link #section()} and {@link #thread()}).
 * 
 * <p>
 * Applying this annotation to a class is identical to applying it to every
 * member methods and constructors of target class.
 * </p>
 * 
 * @see GameSection
 * @see LogicType
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
public @interface SideWith {
	
	/**
	 * Require one of the game sections to be in {@link SectionState#ACTIVE ACTIVE}.
	 * 
	 * @return the game sections
	 */
	GameSection[] section() default {};
	
	/**
	 * Require one of the logic types to be the same as the current thread's logic type.
	 * 
	 * @return the logic types
	 */
	LogicType[] thread() default {};
}
