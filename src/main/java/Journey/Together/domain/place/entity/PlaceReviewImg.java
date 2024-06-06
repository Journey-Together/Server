package Journey.Together.domain.place.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlaceReviewImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_review_id")
    PlaceReview placeReview;

    String imgUrl;

    @Builder
    public PlaceReviewImg(PlaceReview placeReview, String imgUrl){
        this.imgUrl = imgUrl;
        this.placeReview =placeReview;
    }
}
