package Journey.Together.domain.plan.entity;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "day")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Day extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id",columnDefinition = "bigint")
    private Long dayId;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(targetEntity = Plan.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(targetEntity = Place.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Place place;

    @Column(name = "date")
    private LocalDate date;

    @Builder
    public Day (Member member,Plan plan,Place place,LocalDate date){
        this.member = member;
        this.plan = plan;
        this.place = place;
        this.date = date;
    }

}
