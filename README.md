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

