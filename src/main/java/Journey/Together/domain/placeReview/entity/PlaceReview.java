package Journey.Together.domain.placeReview.entity;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.place.entity.Place;
import Journey.Together.global.common.BaseTimeEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import lombok.*;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlaceReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_review_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    Place place;

    Float grade;

    String content;

    LocalDate date;


    @Builder
    public PlaceReview(Member member, Place place, Float grade, String content, LocalDate date){
        this.member = member;
        this.place = place;
        this.date =date;
        this.grade =grade;
        this.content=content;
    }

}
