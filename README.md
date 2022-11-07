# 선착순 쿠폰 발급 API
개요
 - 수강 신청, 선착순 쿠폰 이벤트, 티켓 예매, 예약 시스템 등 특정 시간에 트래픽이 몰리는 상황과 동시성을 고려한 서비스 개발이 궁금해서 시작한 프로젝트 

<br>

내용

 - 판매자 권한을 가진 유저가 쿠폰 정보를 등록하면 일반 유저는 한정된 개수를 가진 쿠폰에 대해 다운로드 요청을 해서 쿠폰을 발급받는 시나리오를 가정하여 개발
 - Redisson 분산 락을 활용하여 쿠폰 개수 동시성 이슈 처리

- 선착순 쿠폰 이벤트 특성상 순간 몰리는 트래픽에 대응하여 서버가 Auto Scaling 되기 전에 이벤트가 끝날 수도 있으므로 서버의 대수를 미리 늘리고 감당할 수 있는 트래픽을 측정하는 것이 중요하다고 생각해 nGrinder를 학습해 성능테스트 진행

<br>

## 프로젝트 구조

![architecture](./etc/architecture.png)

## DB

![db](./etc/db.png)

## 기술 스택

- Kotlin, Kotest, Mockk
- Spring Boot, Spring Security, Spring Data JPA
- MySQL, Redis(redisson), nGrinder, GCP
- Jenkins, JaCoCo, SonarQube

<br>

## 성능 테스트

API 서버 2대로 부하 분산 중인 GCP 로드 밸런서로 요청하여 쿠폰 다운로드 기능 성능 테스트 

- 테스트를 위한 더미 데이터(유저 50만, 유저가 보유한 쿠폰 정보 500만, 쿠폰 정보 1만)를 미리 넣어두고 테스트

- 선착순 이벤트 특성상 유저가 이미 로그인이 완료된 상태에서 쿠폰 다운로드 요청을 보낼 것이라 생각하고 테스트 시나리오를 가정하여 미리 Access Token 5만 개를 세팅하고 쿠폰 개수 5000으로 설정, vuser 320과 1600으로 테스트 진행

### 서버 사양

- API 서버 2대 - 4vCPU RAM 8GB 

- MySQL 서버 1대 - 2vCPU Ram 8GB

- Redis 서버 1대 - 2vCPU Ram 8GB

- Ngrinder Controller 1대 - 2vCPU RAM 4GB

- Ngrinder Agent 4대 - 2vCPU RAM 4GB

<br>

1. vuser = 320, coupon count = 5000

![nr1](./etc/nr1.jpg)

2. vuser = 1600, coupon count = 5000

![nr2](./etc/nr2.jpg)

- 남은 쿠폰이 존재해서 쿠폰 다운 로드 요청이 처리될 때 TPS 60 ~ 120, 쿠폰이 모두 소진된 후 들어오는 요청 처리 시 TPS 140 ~ 180 정도 측정됨
- 생각보다 TPS가 적게 나왔다고 생각, 분산 락을 활용하는 방법이 아닌 Redis가 싱글 스레드 기반으로 커맨드를 처리하는 점을 활용하여 쿠폰 개수 감소를 처리해서 다운로드 로직을 수정하는 것이 더 나은 방법일 수도 있다고 생각됨
- 쿠폰을 다운로드할 수 있는 조건에 부합해서 DB(MySQL)에 실제 쿠폰을 지급하는 Insert Query가 선착순 쿠폰 다운로드 API의 비즈니스 로직 내에 포함되는데 쿠폰 지급 로직은 따로 Queue에 쌓아서 Consumer에서 처리하는 것이 성능과 안전성 면에서 더 좋지 않을까 생각됨

