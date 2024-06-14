package Journey.Together.domain.plan.entity;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Plan extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id",columnDefinition = "bigint")
    private Long planId;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title",columnDefinition = "varchar(255)" )
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Builder
    public Plan( Member member, String title, LocalDate startDate, LocalDate endDate, Boolean isPublic){
        this.member = member;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPublic = isPublic;
    }
}
