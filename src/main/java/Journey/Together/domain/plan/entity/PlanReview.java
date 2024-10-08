package Journey.Together.domain.plan.entity;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    Member member;

    @Column(name = "grade")
    private float grade;

    @Column(name = "content",columnDefinition = "varchar(300)")
    private String content;

    @OneToOne
    @JoinColumn(name = "plan_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plan plan;

    private Boolean report = false;


    @Builder
    public PlanReview(Member member,float grade, String content,Plan plan){
        this.member=member;
        this.grade = grade;
        this.content=content;
        this.plan=plan;
    }

    public void setReport(Boolean report) {
        this.report = report;
    }
}
