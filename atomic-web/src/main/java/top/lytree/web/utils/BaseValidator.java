package top.lytree.web.utils;

import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author Y
 */
public class BaseValidator {
    private static final Logger log = LoggerFactory.getLogger(BaseValidator.class);


    // 线程安全的，直接构建也可以，这里使用静态代码块一样的效果
    private static Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static <T> void validate(T t) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
        if (constraintViolations.size() > 0) {
            StringBuilder validateError = new StringBuilder();
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                validateError.append(constraintViolation.getMessage()).append(";");
            }
            throw new ValidationException(validateError.toString());
        }
    }
}
