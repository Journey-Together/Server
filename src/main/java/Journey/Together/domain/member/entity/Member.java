package Journey.Together.domain.member.entity;

import Journey.Together.domain.member.enumerate.LoginType;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.domain.member.enumerate.RelationType;
import Journey.Together.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", columnDefinition = "bigint")
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
    private String profileUuid;

    @Column(name = "login_type", nullable = false, columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "blood_type", columnDefinition = "varchar(255)")
    private String bloodType;

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

    @Column(name = "part1_rel", columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private RelationType part1Rel;

    @Column(name = "part1_phone", columnDefinition = "varchar(255)")
    private String part1Phone;

    @Column(name = "part2_rel", columnDefinition = "varchar(255)")
    @Enumerated(EnumType.STRING)
    private RelationType part2Rel;

    @Column(name = "part2_phone", columnDefinition = "varchar(255)")
    private String part2Phone;

    @Column(name = "refreshToken", columnDefinition = "varchar(255)")
    private String refreshToken;

    @Builder
    public Member(String email, String name, String phone, String profileUuid, LoginType loginType,String bloodType,MemberType memberType, String birth, String allergy, String medication,RelationType part1Rel,String part1Phone,RelationType part2Rel,String part2Phone, String refreshToken) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.profileUuid = profileUuid;
        this.loginType = loginType;
        this.bloodType = bloodType;
        this.memberType = memberType;
        this.birth = birth;
        this.allergy = allergy;
        this.medication=medication;
        this.part1Rel=part1Rel;
        this.part1Phone=part1Phone;
        this.part2Rel=part2Rel;
        this.part2Phone=part2Phone;
        this.refreshToken=refreshToken;
    }
}
