package net.wolftail.api.lifecycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation served only as a shortcut for writing java doc likes 'Call in
 * logic XX thread during OO section'. And there may isn't a check mechanism in
 * the annotated method(or constructor)'s implement.
 * 
 * <p>
 * When applied to a method or constructor, this annotation indicates during the
 * method(or constructor)'s calling the game section and the current thread must
 * satisfy some constrains(see {@link #section()} and {@link #thread()}).
 * </p>
 * 
 * <p>
 * Applying this annotation to a class is identical to applying to the class's
 * all declared methods and constructors. If a declared method(or constructor)
 * already has a {@code SideWith} annotation, the method(or constructor)'s goes
 * first.
 * </p>
 * 
 * @see GameSection
 * @see LogicType
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
public @interface SideWith {
	
	/**
	 * Require one of the game sections to be in {@link SectionState#ACTIVE ACTIVE}.
	 * Empty simply indicates no requirement.
	 * 
	 * @return the game sections
	 */
	GameSection[] section() default {};
	
	/**
	 * Require one of the logic types to be the same as the current thread's logic
	 * type. Empty indicates no requirement.
	 * 
	 * @return the logic types
	 */
	LogicType[] thread() default {};
}
