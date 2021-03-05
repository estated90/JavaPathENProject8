package tourguide.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tourguide.dto.UserNewPreferences;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldMatch, UserNewPreferences> {

	private Logger logger = LoggerFactory.getLogger(FieldsValueMatchValidator.class);

	public void initialize(FieldMatch constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(UserNewPreferences userNewPreferences, final ConstraintValidatorContext context) {
		try {
			double highPrice = userNewPreferences.getHighPricePoint();
			double lowPrice = userNewPreferences.getLowerPricePoint();
			return highPrice>lowPrice;
		} catch (final Exception ex) {
			logger.info("Error while getting values from object", ex);
			return false;
		}

	}

}
