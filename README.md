## 프로젝트 소개
실제 주식 투자에는 자산 손실에 대한 리스크가 있어, 처음 시작하기 어렵고 쉽게 경험을 쌓기 힘듭니다.  
이에 따라, 가볍게 시작하면서도 자신만의 투자 가치를 키워갈 수 있는 **모의 주식 투자 플랫폼**을 기획하게 되었습니다.

이 서비스는 투자 경험이 부족한 사용자들이 **리스크 없이 전략을 실험하고**,  
**거래를 기록하고 복기**하면서 투자 감각을 키울 수 있도록 설계되었습니다.

또한 **한국투자증권 OpenAPI**를 연동하여 **실시간 시세 반영**, **가상 주문 처리**, **잔고 및 거래 내역 기록** 등의 기능을 제공합니다.


## Tech Stack
- **Language & Frameworks**: Java 21, Spring Boot 3.3.6
- **Database**: MySQL, H2 (for testing)
- **ORM & Persistence**: Spring Data JPA, MyBatis
- **API & Network**: RESTful API, WebSocket (STOMP)
- **API Docs**: Swagger (SpringDoc OpenAPI 2.6.0)
- **Security**: Spring Security, JWT
- **Build Tool**: Gradle

## 주요 기능
- 사용자 인증/인가 (JWT 기반)
- 계좌 등록 및 잔고 확인
- 주식 매수 / 매도 / 주문 취소 처리
- 실시간 WebSocket 기반 시세 전송
- Redis를 통한 실시간 데이터 브로드캐스팅
- 포트폴리오 및 거래 내역 조회

## ERD
![Mock_Stock (3).png](./images/erd.png)
원본: https://www.erdcloud.com/d/TiBLG8nW8FK5pMGSv


- **User**: 회원 가입 정보 및 인증에 사용
- **Account**: 각 사용자의 모의 투자 계좌
- **Order**: 주문 내역 (시장가 / 지정가, 매수 / 매도)
- **OrderTransaction**: 체결된 거래 내역
- **AccountTransaction**: 입출금, 이체 내역 관리
- **Portfolio**: 보유 주식 평균 단가 및 수량 관리
- **Watchlist**: 관심 종목 등록 및 조회 기능
- **Stock**: 주식 종목 정보 및 시세
- **SearchHistory**: 종목 검색 기록
- **SocialAccount**: 소셜 로그인 연동 정보(현재 카카오만 가능)



