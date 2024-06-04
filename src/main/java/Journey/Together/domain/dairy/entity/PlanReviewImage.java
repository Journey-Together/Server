package Journey.Together.domain.dairy.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "palnReviewImage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planReviewImage_id")
    private Long planReviewImageId;

    @Column(name = "imageUrl")
    private String imageUrl;
}
