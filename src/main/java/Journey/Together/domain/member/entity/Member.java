package Journey.Together.domain.member.entity;

import Journey.Together.domain.member.enumerate.BloodType;
import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, columnDefinition = "bigint")
    private Long memberId;

    // 이메일은 최대 255자 + 1자(@) + 69자해서 최대 320글자이므로, varchar(320) 사용
    @Column(name = "email", nullable = false, columnDefinition = "varchar(320)")
    private String email;

    @Column(name = "password", columnDefinition = "varchar(255)")
    private String password;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(50)")
    private String name;

    @Column(name = "phone", columnDefinition = "varchar(15)")
    private String phone;

    @Column(name = "profile_url", columnDefinition = "text")
    private String profileUrl;

    @Column(name = "login_type", nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "blood_type", nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column(name = "member_type", nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Column(name = "birth", columnDefinition = "varchar(255)")
    private String birth;

    @Column(name = "disease", columnDefinition = "varchar(255)")
    private String disease;

    @Column(name = "allergy", columnDefinition = "varchar(255)")
    private String allergy;

    @Column(name = "medication", columnDefinition = "varchar(255)")
    private String medication;

    @Column(name = "refreshToken", columnDefinition = "varchar(255)")
    private String refreshToken;

    @Builder
    public Member(String email, String name, String phone, String profileUrl, String loginType,String bloodType,String memberType, String birth, String allergy, String medication, String refreshToken) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.profileUrl = profileUrl;
        this.loginType = LoginType.valueOf(loginType);
        this.bloodType = BloodType.valueOf(bloodType);
        this.memberType = MemberType.valueOf(bloodType);
        this.birth = birth;
        this.allergy = allergy;
        this.medication=medication;
        this.refreshToken=refreshToken;
    }
}
