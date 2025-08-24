package Journey.Together.domain.place.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "place")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    private Long id;

    private String name;

    private String address;
    @Column(columnDefinition = "TEXT")
    private String firstImg;
    private String category;
    private Double mapX;
    private Double mapY;
    private String createdAt;
    @Column(columnDefinition = "TEXT")
    private String overview;

    private String areaCode;

    private String sigunguCode;

    private String tel;

    @Column(columnDefinition = "TEXT")
    private String homepage;

    private boolean isActive=true;


    @OneToMany(mappedBy = "place",fetch = FetchType.LAZY)
    private Set<DisabilityPlaceCategory> placeDisabilityCategories = new HashSet<>();

    @Builder
    public Place(Long id, String name, String address, String firstImg, String category, Double mapX, Double mapY, String createdAt, String areaCode, String sigunguCode, String tel){
        this.id =id;
        this.name=name;
        this.address=address;
        this.firstImg=firstImg;
        this.category=category;
        this.mapX=mapX;
        this.mapY=mapY;
        this.createdAt=createdAt;
        this.areaCode = areaCode;
        this.sigunguCode = sigunguCode;
        this.tel = tel;
    }
    public Long getId() {
        return id;
    }

    public void setDetailData(String homepage, String overview){
        this.homepage = homepage;
        this.overview = overview;
    }

    public void setDisabilityCategories(Set<DisabilityPlaceCategory> placeDisabilityCategories){
        this.placeDisabilityCategories = placeDisabilityCategories;
    }

    public void setIsActive() {this.isActive = !isActive;}
}
