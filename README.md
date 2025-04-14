# photogram

Instagram을 모티브로 한 MSA 기반의 SNS 프로젝트입니다.  
대규모 트래픽 처리와 이벤트 기반 아키텍처 설계를 목표로 구현되었습니다.

---

## 프로젝트 목표

- MSA 구조 설계 및 구현
- Kafka 기반 이벤트 처리 경험
- ElasticSearch를 활용한 태그 기반 검색
- 대규모 시스템 설계 및 트래픽 처리 구조 학습

---

## 시스템 아키텍처
<img width="630" alt="스크린샷 2025-04-14 오후 2 43 50" src="https://github.com/user-attachments/assets/977574e6-2a84-47cd-a34b-0c1ba7ffec1b" />

- **Gateway**: 진입점, JWT 인증 및 라우팅
- **Discovery**: 마이크로서비스 등록 및 위치 탐색
- **Config**: 설정 중앙관리
- **User-service**: 회원가입, 로그인, 유저/팔로우 관리
- **Post-service**: 게시글, 이미지 업로드, 좋아요, 댓글
- **Feed-service**: 팔로우 기반 피드 구성
- **Post-Search-service**: 태그 기반 게시글 검색

---

## 기술 스택

| 분류             | 기술                                                         |
|------------------|--------------------------------------------------------------|
| Backend          | Spring Boot (Kotlin), Spring Cloud                           |
| DB               | MySQL, Redis                                                 |
| 메시징           | Kafka                                                        |
| 검색             | ElasticSearch                                                |
| 스토리지         | AWS S3                                                       |
| 이메일           | AWS SES                                                      |
| 인프라           | Config Server, Discovery(Eureka), Gateway                    |
| 모니터링         | Micrometer, Grafana, Prometheus                              |
| 분산 추적        | Sleuth, Zipkin                                               |
| 장애 대응        | Resilience4j                                                 |

---

## 실행 방법
```bash
./gradlew build
docker-compose up
```

---

## [인증 시스템](https://dev-setung.tistory.com/55) (JWT + Gateway 인증 필터)
<img width="656" alt="스크린샷 2025-04-14 오후 2 56 01" src="https://github.com/user-attachments/assets/5295d268-7fe9-4982-8f9f-a9bf48ec48f5" />

- 로그인 시 `user-service`에서 JWT를 생성하고 클라이언트에 전달
- 모든 요청은 `Gateway`에서 JWT를 검증 후 `user-id`를 헤더로 추가해 마이크로서비스로 전달
- 각 서비스에서는 `user-id`를 통해 인증된 사용자 정보를 활용
- 익명 사용자 요청도 지원 (JWT 미포함 시 `user-id: -1`로 간주)
- 인증이 필요한 API와 아닌 API를 구분할 수 있는 설정 제공 (`allowAnonymous`)

---

## [태그 기반 게시글 검색](https://dev-setung.tistory.com/56) (ElasticSearch + Kafka)
<img width="580" alt="스크린샷 2025-04-14 오후 2 56 32" src="https://github.com/user-attachments/assets/fe1e21b4-e185-42a7-b6d5-58dc90ed978f" />

- 게시글 저장 시 Kafka를 통해 게시글 정보를 `post-search-service`로 전송
- `post-search-service`는 해당 데이터를 ElasticSearch에 인덱싱
- 검색 요청 시 ES에서 `multi_match` 쿼리로 태그 기반 게시글 검색 수행
- 태그 자동 완성 또는 관련성 높은 검색 기능 확장 가능


---


