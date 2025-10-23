# 다온길-서버
<img width="741" alt="스크린샷 2024-07-04 오전 1 14 11" src="https://github.com/Journey-Together/Server/assets/92644651/89ace427-8a1f-4ed8-98c1-c23d478d3e4c">

>  취약 계층에게 여행에 필요한 정보를 효과적으로 제공하는 어플리케이션

<br>

## 👩🏻‍💻 Server Developer

| [미혜](https://github.com/mmihye) | [서연](https://github.com/sycuuui)
| :--: | :--: |
| <img width="300" alt="미혜" src="https://avatars.githubusercontent.com/u/92644651?v=4"> | <img width="300" alt="서연" src="https://avatars.githubusercontent.com/u/102959791?v=4"> | 


<br>


## 📦 ERD
<img width="614" height="657" alt="스크린샷 2025-10-23 오후 6 48 20" src="https://github.com/user-attachments/assets/feaa3911-0099-4b3f-8542-5784fa84da55" />


<br><br>

## 🧬 Architecture
<img width="700" alt="스크린샷 2024-07-04 오전 1 41 22" src="https://github.com/Journey-Together/Server/assets/92644651/a544f467-45cf-4bf4-bae2-22110d4bb5d8">

<br><br>

## 📂 폴더 구조도
```
├── main
│   ├── java
│   │   └── Journey
│   │       └── Together
│   │           ├──🗂 domain
│   │           │   ├──🗂 bookbark
│   │           │   │   ├──🗂 controller
│   │           │   │   ├──🗂 dto
│   │           │   │   ├──🗂 entity
│   │           │   │   ├──🗂 enumerate
│   │           │   │   ├──🗂 repository
│   │           │   │   └──🗂 service
│   │           │   ├──🗂 member
│   │           │   │   ├──🗂 controller
│   │           │   │   ├──🗂 dto
│   │           │   │   ├──🗂 entity
│   │           │   │   ├──🗂 enumerate
│   │           │   │   ├──🗂 repository
│   │           │   │   └──🗂 service
│   │           │   ├──🗂 place
│   │           │   │   ├──🗂 controller
│   │           │   │   ├──🗂 dto
│   │           │   │   │   ├──🗂request
│   │           │   │   │   └──🗂 response
│   │           │   │   ├──🗂 entity
│   │           │   │   ├──🗂 enumerate
│   │           │   │   ├──🗂 repository
│   │           │   │   └──🗂 service
│   │           │   └──🗂 plan
│   │           │       ├──🗂 controller
│   │           │       ├──🗂 dto
│   │           │       ├──🗂 entity
│   │           │       ├──🗂 repository
│   │           │       └──🗂 service
│   │           └──🗂 global
│   │               ├──🗂 common
│   │               ├──🗂 config
│   │               ├──🗂 exception
│   │               ├──🗂 security
│   │               │   ├──🗂 jwt
│   │               │   │   └──🗂 dto
│   │               │   ├──🗂 kakao
│   │               │   │   └──🗂 dto
│   │               │   └──🗂 naver
│   │               │       └──🗂 dto
│   │               └──🗂 util
│   └──🗂 resources
│       └── static
└── test
    └── java
        └── Journey
            └── Together
```
<br><br>


## Branch

Branch 전략은 Git-flow를 준수합니다.

[우린 Git-flow를 사용하고 있어요 | 우아한형제들 기술블로그](https://techblog.woowahan.com/2553/)

branch 이름: 관련브랜치 분류/#[Issue tracker]
 ex) feature/#1
 

### Commit
| 커밋 구분 | 설명 |
| --- | --- |
| Feat | (Feature) 개선 또는 기능 추가 |
| Fix | (Bug Fix) 버그 수정 |
| Doc | (Documentation) 문서 작업 |
| Test | (Test) 테스트 추가/수정 |
| Build | (Build) 빌드 프로세스 관련 수정(yml) |
| Performance | (Performance) 속도 개선 |
| Refactor | (Cleanup) 코드 정리/리팩토링 |

- 이슈번호와 함께 커밋 내용을 적는다.
- 예시 : [#1] Feat : ~

