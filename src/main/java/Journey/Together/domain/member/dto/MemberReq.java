package Journey.Together.domain.member.dto;

import Journey.Together.domain.member.enumerate.RelationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberReq(
        @Null
        String name,
        @Null
        String phone,
        @Null
        String profileUrl,
        @Null
        String bloodType,
        @Null
        String birth,
        @Null
        String disease,
        @Null
        String allergy,
        @Null
        String medication,
        @Null
        RelationType part1_rel,
        @Null
        String part1_phone,
        @Null
        RelationType part2_rel,
        @Null
        String part2_phone
){

}