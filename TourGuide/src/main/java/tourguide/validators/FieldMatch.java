package tourguide.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Nico
 *
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Documented
public @interface FieldMatch {
	
	/**
	 * @return a sTRING OF ERROR
	 */
	String message() default "The higher price must be superior to the lower";

    /**
     * @return Class of object
     */
    Class<?>[] groups() default {};

    /**
     * @return Class of payload
     */
    Class<? extends Payload>[] payload() default {};

}
