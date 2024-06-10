package Journey.Together.domain.bookbark.entity;

import Journey.Together.domain.bookbark.dto.PlaceBookmarkRes;
import Journey.Together.domain.dairy.entity.Plan;
import Journey.Together.global.util.S3Client;
import jakarta.annotation.Nullable;

public record PlanBookmarkRes(
        Long planId,
        String name,
        String profileImg,
        String title,
        @Nullable
        String image
) {

    public static PlanBookmarkRes of(Plan plan, String profileImg, String image){
        return new PlanBookmarkRes(plan.getPlanId(), plan.getMember().getNickname(), profileImg, plan.getTitle(), image);
    }
}
