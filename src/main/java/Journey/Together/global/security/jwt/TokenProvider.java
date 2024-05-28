package Journey.Together.global.security.jwt;

import Journey.Together.domain.member.entity.Member;
import Journey.Together.domain.member.enumerate.MemberType;
import Journey.Together.domain.member.repository.MemberRepository;
import Journey.Together.global.exception.ApplicationException;
import Journey.Together.global.exception.ErrorCode;
import Journey.Together.global.security.jwt.dto.TokenDto;
import Journey.Together.global.security.PrincipalDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final MemberRepository memberRepository;

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    // ATK 만료시간: 1일
    private static final long accessTokenExpirationTime = 7 * 24 * 60 * 60 * 1000L;

    // RTK 만료시간: 30일
    private static final long refreshTokenExpirationTime = 30 * 24 * 60 * 60 * 1000L;

    /**
     * 의존성 주입 후 초기화를 수행하는 메소드
     */
    @PostConstruct
    protected void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    /**
     * ATK 생성
     * @param member - 사용자 정보를 추출하여 액세스 토큰 생성
     * @return 생성된 액세스 토큰 정보 반환
     */
    private String createAccessToken(Member member) {
        Claims claims = getClaims(member);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * RTK 생성
     * @param member - 사용자 정보를 추출하여 리프레쉬 토큰 생성
     * @return 생성된 리프레쉬 토큰 정보 반환
     */
    private String createRefreshToken(Member member) {
        Claims claims = getClaims(member);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 로그인 시, 액세스 토큰과 리프레쉬 토큰 발급
     * @param member - 로그인한 사용자 정보
     * @return 액세스 토큰과 리프레쉬 토큰이 담긴 TokenDto 반환
     */
    public TokenDto createToken(Member member) {
        return TokenDto.builder()
                .accessToken(createAccessToken(member))
                .refreshToken(createRefreshToken(member))
                .build();
    }

    /**
     * 토큰 유효성 검사
     * @param token - 일반적으로 액세스 토큰 / 토큰 재발급 요청 시에는 리프레쉬 토큰이 들어옴
     * @return 유효하면 true, 유효하지 않으면 false 반환
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.WRONG_TOKEN);
        }
    }

    /**
     * 리프레쉬 토큰 기반으로 액세스 토큰 재발급 + 리프레쉬 토큰의 유효기간이 액세스 토큰의 유효기간보다 짧을 경우, 리프레쉬 토큰도 재발급
     * @param member - 재발급을 요청한 사용자 정보
     * @param refreshToken - 재발급을 요청했던 리프레쉬 토큰
     * @return 재발급된 액세스 토큰을 담은 TokenDto 객체 반환
     */
    public TokenDto reissue(Member member, String refreshToken) {
        // 액세스 토큰 재발급
        String accessToken = createAccessToken(member);

        // 리프레쉬 토큰 재발급 조건 및 로직
        if(getExpiration(refreshToken) <= getExpiration(accessToken)) {
            refreshToken = createRefreshToken(member);
        }

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰에서 정보를 추출해서 Authentication 객체를 반환
     * @param token - 액세스 토큰으로, 해당 토큰에서 정보를 추출해서 사용
     * @return 토큰 정보와 일치하는 Authentication 객체 반환
     */
    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        MemberType memberType = getType(token);
        Member member = memberRepository.findMemberByEmailAndMemberTypeAndDeletedAtIsNull(email, memberType).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_EXCEPTION));
        PrincipalDetails principalDetails = new PrincipalDetails(member);

        return new UsernamePasswordAuthenticationToken(principalDetails, "", principalDetails.getAuthorities());
    }

    /**
     * 토큰에서 email 정보 반환
     * @param token - 일반적으로 액세스 토큰 / 토큰 재발급 요청 시에는 리프레쉬 토큰이 들어옴
     * @return 사용자의 email 반환
     */
    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 토큰에서 사용자의 역할 반환
     * @param token - 일반적으로 액세스 토큰 / 토큰 재발급 요청 시에는 리프레쉬 토큰이 들어옴
     * @return 사용자의 역할 반환 (UserRole)
     */
    public MemberType getType(String token) {
        return MemberType.valueOf((String) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role"));
    }

    /**
     * 토큰의 만료기한 반환
     * @param token - 일반적으로 액세스 토큰 / 토큰 재발급 요청 시에는 리프레쉬 토큰이 들어옴
     * @return 해당 토큰의 만료정보를 반환
     */
    public Long getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().getTime();
    }

    /**
     * Claims 정보 생성
     * @param member - 사용자 정보 중 사용자를 구분할 수 있는 정보 두 개를 활용함
     * @return 사용자 구분 정보인 이메일과 역할을 저장한 Claims 객체 반환
     */
    private Claims getClaims(Member member) {
        Claims claims = Jwts.claims().setSubject(member.getEmail());
        claims.put("role", member.getMemberType());

        return claims;
    }
}
