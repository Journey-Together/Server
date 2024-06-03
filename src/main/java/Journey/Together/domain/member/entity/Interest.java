package Journey.Together.domain.member.entity;

import Journey.Together.domain.member.dto.InterestDto;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Entity
@Table(name = "interest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Interest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id", columnDefinition = "bigint")
    private Long interestId;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "pysical")
    private Boolean isPysical;

    @Column(name = "hear")
    private Boolean isHear;

    @Column(name = "visual")
    private Boolean isVisual;

    @Column(name = "child")
    private Boolean isChild;

    @Column(name = "elderly")
    private Boolean isElderly;

    public void update(InterestDto interestDto) {
        this.isPysical=interestDto.isPysical();
        this.isHear=interestDto.isHear();
        this.isVisual=interestDto.isVisual();
        this.isChild=interestDto.isChild();
        this.isElderly = interestDto.isElderly();
    }

    @Builder
    public Interest(Member member, Boolean isPysical, Boolean isHear, Boolean isVisual, Boolean isElderly, Boolean isChild){
        this.member = member;
        this.isPysical=isPysical;
        this.isHear=isHear;
        this.isVisual=isVisual;
        this.isChild=isChild;
        this.isElderly = isElderly;
    }
}
