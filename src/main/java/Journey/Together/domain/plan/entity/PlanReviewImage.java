package Journey.Together.domain.plan.entity;

import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "palnReviewImage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanReviewImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planReviewImage_id")
    private Long planReviewImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planReview_id", nullable = false, columnDefinition = "bigint")
    private PlanReview planReview;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Builder
    public PlanReviewImage(String imageUrl,PlanReview planReview){
        this.imageUrl=imageUrl;
        this.planReview= planReview;
    }
}
