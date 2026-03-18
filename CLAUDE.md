# BlinkAir Backend

## Overview
BlinkAir Telegram Mini App 社交匹配后端。Spring Boot 3.2.2 + Java 17。

## Database
- 同库 `social_fantasy_bot`，新表前缀 `ba_`
- 复用现有 `users` / `user_profiles` 表（只读）
- RDS: `blink-db.cdiu4sokw18e.ap-southeast-2.rds.amazonaws.com`
- SSH 跳板: `ssh -i kiss-me-server.pem ubuntu@3.107.95.119`

## Build & Run
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Port
- 8081 (避免与 spark_be 8080 冲突)

## API Prefix
- `/api/v1/`

## Key Decisions
- WebSocket STOMP for IM
- JWT auth (copied from spark_be)
- 2s interval match scheduler
