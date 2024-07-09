package Journey.Together.domain.plan.entity;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "planReview")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanReview extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planReview_id",columnDefinition = "bigint")
    private Long planReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @Column(name = "grade")
    private float grade;

    @Column(name = "content",columnDefinition = "varchar(300)")
    private String content;

    @OneToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "planReview", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlanReviewImage> planReviewImages = new ArrayList<>();

    @Builder
    public PlanReview(Member member,float grade, String content,Plan plan,List<PlanReviewImage> planReviewImages){
        this.member=member;
        this.grade = grade;
        this.content=content;
        this.plan=plan;
        this.planReviewImages=planReviewImages;
    }
}
