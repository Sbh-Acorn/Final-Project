## Gradle 빌드 이미지
#FROM gradle:8.4.0-jdk17 AS build
#WORKDIR /app
#COPY . .
#RUN gradle clean build -x test --no-daemon
#
## 애플리케이션 실행 이미지
#FROM openjdk:17-jdk-slim
#WORKDIR /app
#COPY --from=build /app/build/libs/Caltizm-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Gradle 빌드 이미지
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test --no-daemon

# 애플리케이션 실행 이미지
FROM openjdk:17-jdk-slim

# 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    gnupg \
    libgconf-2-4 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxrandr2 \
    libxi6 \
    libxcursor1 \
    libxdamage1 \
    libxtst6 \
    libpango1.0-0 \
    fonts-liberation \
    libappindicator3-1 \
    xdg-utils \
    libfontconfig1 \
    libatk1.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Google Chrome 설치
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# ChromeDriver 설치 (명시적 버전)
RUN wget -O /tmp/chromedriver.zip https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/131.0.6778.204/linux64/chromedriver-linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    chmod +x /usr/local/bin/chromedriver && \
    rm -rf /tmp/chromedriver.zip /usr/local/bin/chromedriver-linux64

# 업로드 디렉토리 생성
RUN mkdir -p /app/upload/image && chmod -R 777 /app/upload

WORKDIR /app

# 빌드 결과 복사
COPY --from=build /app/build/libs/Caltizm-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
