package Journey.Together.domain.report.entity;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.report.enumerate.ReviewType;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long review_id;

    private String reason;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    private Boolean approval;

    @Builder
    public Report(Long review_id, String reason, String description, ReviewType reviewType){
        this.review_id=review_id;
        this.reason=reason;
        this.description=description;
        this.reviewType=reviewType;
    }

}
