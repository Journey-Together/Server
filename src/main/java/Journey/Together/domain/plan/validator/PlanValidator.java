package Journey.Together.domain.plan.validator;

import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class PlanValidator {
    public void validateExists(Plan plan) {
        if (plan == null) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }
}
