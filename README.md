# 스파이더 카드놀이 — Android

갤럭시 S25에서 혼자 즐기는 가로형 오프라인 스파이더 카드놀이입니다.

## 기능

- 스페이드 104장, 10열, 추가 배분 5회
- 드래그 또는 카드 선택 후 목적지 탭으로 이동
- K부터 A까지 한 묶음 완성, 8묶음 완성 시 승리
- 되돌리기, 힌트, 일시정지, 새 게임
- 기기 내부 게임 상태 및 시간 자동 저장
- 카드 집기, 내려놓기, 묶음 완성 효과음
- 순서가 맞지 않아 집을 수 없는 앞면 카드는 회색으로 표시
- 로그인, 광고, 인터넷 연결 불필요

## 빌드 및 설치

Android Studio에서 이 저장소를 열고 Android SDK 35와 Gradle JDK 21을 사용합니다.
USB 디버깅을 활성화한 안드로이드폰을 연결하고 Run을 실행하면 설치됩니다.

Windows에서 APK 빌드:

```powershell
.\gradlew.bat assembleDebug
```

APK 경로: `app/build/outputs/apk/debug/app-debug.apk`

기존 설치본과 서명이 같으면 업데이트 설치로 저장된 게임을 유지할 수 있습니다.
다른 PC의 디버그 서명으로 빌드하면 기존 설치본과 서명이 다를 수 있습니다.

## 게임 규칙 테스트

JDK의 `javac`와 `java`를 사용할 수 있는 환경에서 실행합니다.

```text
javac -d build/rule-tests app/src/main/java/com/solo/starlightdrift/SpiderGame.java tests/SpiderGameTest.java
java -cp build/rule-tests SpiderGameTest
```

자세한 규칙은 [SPIDER.md](SPIDER.md)를 참고하세요.

현재 버전: 2.2. APK 빌드 및 규칙 테스트를 통과했습니다. 최신 변경 사항의 실제 휴대폰 화면·음향 검증은 별도로 필요합니다.
