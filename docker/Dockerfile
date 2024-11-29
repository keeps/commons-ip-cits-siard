FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="admin@keep.pt" vendor="KEEP SOLUTIONS"

WORKDIR /KEEPS

RUN mkdir -p /KEEPS/bin && \
  mkdir -p /KEEPS/lib

COPY ./target/*.jar /KEEPS/lib/commons-ip-cits-siard.jar
COPY ./bin /KEEPS/bin
RUN chmod +x /KEEPS/bin/commons-ip-cits-siard

ENV PATH="/KEEPS/bin:$PATH"

ENTRYPOINT [ "/bin/sh", "/KEEPS/bin/commons-ip-cits-siard"]
