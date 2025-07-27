package Journey.Together.domain.member.validator;

import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public void validateExistsAndActive(String email) {
        memberRepository.findMemberByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
    }
}
