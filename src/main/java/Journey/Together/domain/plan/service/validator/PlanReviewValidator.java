package Journey.Together.domain.plan.service.validator;

import Journey.Together.domain.plan.entity.Plan;
import Journey.Together.domain.plan.repository.PlanReviewRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanReviewValidator {
    private final PlanReviewRepository planReviewRepository;
    public void validateExists(Plan plan) {
        if (planReviewRepository.existsAllByPlanAndDeletedAtIsNull(plan)) {
            throw new ApplicationException(ErrorCode.ALREADY_EXIST_EXCEPTION);
        }
    }
}
