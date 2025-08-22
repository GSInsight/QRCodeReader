# QR Code Reader

간단하고 빠른 안드로이드 QR 코드 스캐너 앱입니다. 앱을 실행하면 즉시 카메라가 활성화되어 QR 코드를 스캔하고, 인식된 내용을 상세히 보여줍니다.

## 📱 주요 기능

### 🎯 **핵심 기능**
- **즉시 스캔**: 앱 실행 시 바로 카메라 활성화
- **실시간 인식**: Google ML Kit을 사용한 빠르고 정확한 QR 코드 감지
- **스마트 결과 표시**: QR 코드 타입에 따른 맞춤형 액션 제공
- **플래시 지원**: 어두운 환경에서도 스캔 가능

### 🔗 **지원하는 QR 코드 타입**
- **URL**: 웹사이트 바로 열기
- **전화번호**: 다이얼 앱으로 연결
- **이메일**: 이메일 앱으로 연결
- **SMS**: 메시지 앱으로 연결
- **WiFi**: WiFi 정보 표시
- **위치**: 지도 앱으로 연결
- **텍스트**: 일반 텍스트 표시

### ⚡ **편리한 액션**
- **복사**: 클립보드에 내용 복사
- **공유**: 다른 앱으로 내용 공유
- **바로 열기**: URL, 전화, 이메일 등 즉시 실행
- **다시 스캔**: 연속 스캔 지원

## 🛠️ 기술 스택

- **언어**: Java
- **최소 SDK**: Android 7.0 (API 24)
- **타겟 SDK**: Android 14 (API 34)
- **카메라**: CameraX
- **ML**: Google ML Kit Barcode Scanning
- **UI**: Material Design 3
- **아키텍처**: ViewBinding

## 📋 요구사항

### **시스템 요구사항**
- Android 7.0 (API 24) 이상
- 카메라 하드웨어 필수
- 최소 RAM: 2GB 권장

### **권한**
- `CAMERA`: QR 코드 스캔을 위한 카메라 접근

## 🚀 설치 및 빌드

### **1. 프로젝트 클론**
```bash
git clone <repository-url>
cd qr-code-reader
```

### **2. Android Studio에서 열기**
1. Android Studio 실행
2. "Open an existing project" 선택
3. 프로젝트 폴더 선택

### **3. 의존성 동기화**
```bash
./gradlew sync
```

### **4. 빌드 및 실행**
```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease
```

## 📁 프로젝트 구조

```
app/
├── src/main/
│   ├── java/com/gsinsight/qrcodereader/
│   │   ├── MainActivity.java          # 메인 스캔 화면
│   │   └── ResultActivity.java        # 결과 표시 화면
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml      # 메인 화면 레이아웃
│   │   │   └── activity_result.xml    # 결과 화면 레이아웃
│   │   ├── drawable/                  # 아이콘 및 그래픽 리소스
│   │   ├── values/
│   │   │   ├── colors.xml            # 컬러 팔레트
│   │   │   ├── strings.xml           # 문자열 리소스
│   │   │   └── themes.xml            # 앱 테마
│   │   └── xml/
│   └── AndroidManifest.xml           # 앱 설정 및 권한
└── build.gradle                      # 빌드 설정
```

## 🎨 UI/UX 특징

### **메인 스캔 화면**
- **풀스크린 카메라 프리뷰**: 최대한의 스캔 영역 제공
- **스캔 가이드라인**: 점선 테두리로 스캔 영역 표시
- **실시간 상태 표시**: 스캔 상태를 하단에 실시간 표시
- **플래시 버튼**: 우상단 FAB로 쉬운 접근

### **결과 화면**
- **Material Design 3**: 최신 디자인 가이드라인 적용
- **카드 기반 레이아웃**: 깔끔한 정보 표시
- **컨텍스트 액션**: QR 타입에 따른 스마트 버튼
- **복사 가능 텍스트**: 긴 텍스트도 쉽게 선택 가능

## 🔧 사용법

### **기본 스캔**
1. 앱 실행
2. 카메라 권한 허용
3. QR 코드를 화면 중앙의 가이드라인에 맞춤
4. 자동으로 인식되면 결과 화면으로 이동

### **어두운 환경에서 스캔**
1. 우상단 플래시 버튼 터치
2. 플래시가 켜진 상태에서 스캔

### **결과 활용**
- **복사**: 내용을 클립보드에 복사
- **공유**: 다른 앱으로 내용 공유
- **바로 열기**: URL, 전화번호 등 즉시 실행
- **다시 스캔**: 메인 화면으로 돌아가 연속 스캔

## 🔒 보안 및 개인정보

- **로컬 처리**: 모든 QR 코드 처리는 기기 내에서 수행
- **데이터 수집 없음**: 스캔한 내용을 외부로 전송하지 않음
- **권한 최소화**: 카메라 권한만 사용
- **ML Kit**: Google의 검증된 온디바이스 ML 라이브러리 사용

## 🐛 문제 해결

### **카메라가 작동하지 않는 경우**
1. 앱 권한에서 카메라 권한 확인
2. 다른 카메라 앱이 실행 중인지 확인
3. 기기 재시작 후 재시도

### **QR 코드가 인식되지 않는 경우**
1. QR 코드가 화면 가이드라인 안에 있는지 확인
2. 카메라와 QR 코드 간 적절한 거리 유지 (10-30cm)
3. 조명이 충분한지 확인 또는 플래시 사용
4. QR 코드가 손상되지 않았는지 확인

### **앱이 느린 경우**
1. 백그라운드 앱 정리
2. 기기 저장공간 확인
3. 앱 캐시 삭제 후 재시도

## 📈 버전 히스토리

### **v1.0.0** (2025-01-01)
- 초기 릴리즈
- 기본 QR 코드 스캔 기능
- Material Design 3 UI
- 다양한 QR 타입 지원
- 플래시 기능

## 🤝 기여하기

1. 이슈 리포트: 버그나 개선사항을 GitHub Issues에 등록
2. 풀 리퀘스트: 코드 기여는 항상 환영합니다
3. 피드백: 사용 후기나 제안사항을 공유해 주세요

## 📞 문의

- **개발사**: GS Insight
- **패키지명**: com.gsinsight.qrcodereader
- **라이선스**: MIT License

## 📄 라이선스

```
MIT License

Copyright (c) 2025 GS Insight

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

**QR Code Reader** - 빠르고 간편한 QR 코드 스캐너 📱✨