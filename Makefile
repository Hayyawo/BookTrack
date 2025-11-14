# Makefile
.PHONY: build up down logs clean test

build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f app

clean:
	docker-compose down -v --rmi all

restart: down up

test:
	mvn clean test

health:
	curl http://localhost:8080/actuator/health

ps:
	docker-compose ps