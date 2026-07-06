.PHONY: help up down restart build logs backend-logs frontend-logs test frontend-build clean

help:
	@echo "Available commands:"
	@echo "  make up              - Build and start all Docker containers"
	@echo "  make down            - Stop and remove Docker containers"
	@echo "  make restart         - Restart all Docker containers"
	@echo "  make build           - Build Docker images"
	@echo "  make logs            - Show logs for all services"
	@echo "  make backend-logs    - Show backend logs"
	@echo "  make frontend-logs   - Show frontend logs"
	@echo "  make test            - Run backend tests"
	@echo "  make frontend-build  - Build frontend"
	@echo "  make clean           - Stop containers and remove volumes"

up:
	docker compose up -d --build

down:
	docker compose down

restart:
	docker compose down
	docker compose up -d --build

build:
	docker compose build

logs:
	docker compose logs -f

backend-logs:
	docker logs -f rabam-backend

frontend-logs:
	docker logs -f rabam-frontend

test:
	mvn test

frontend-build:
	cd frontend && npm run build

clean:
	docker compose down -v