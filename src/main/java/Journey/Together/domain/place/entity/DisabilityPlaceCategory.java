package Journey.Together.domain.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisabilityPlaceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subCategory_id")
    private DisabilitySubCategory subCategory;

    @Builder
    public DisabilityPlaceCategory(Place place, DisabilitySubCategory subCategory){
        this.place=place;
        this.subCategory=subCategory;
    }
}
