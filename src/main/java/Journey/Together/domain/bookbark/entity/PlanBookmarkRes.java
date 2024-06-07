package Journey.Together.domain.bookbark.entity;

import Journey.Together.domain.bookbark.dto.PlaceBookmarkRes;
import Journey.Together.domain.dairy.entity.Plan;
import jakarta.annotation.Nullable;

public record PlanBookmarkRes(
        Long planId,
        String name,
        String profileImg,
        String title,
        @Nullable
        String image
) {
    public static PlanBookmarkRes of(Plan plan, String image){
        return new PlanBookmarkRes(plan.getPlanId(), plan.getMember().getName(), plan.getMember().getProfileUuid(), plan.getTitle(), image);
    }
}
