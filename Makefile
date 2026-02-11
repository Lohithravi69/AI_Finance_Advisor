.PHONY: help build up down restart logs clean ps health

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build all Docker images
	docker-compose build

build-no-cache: ## Build all images without cache
	docker-compose build --no-cache

up: ## Start all services
	docker-compose up -d

up-build: ## Build and start all services
	docker-compose up -d --build

down: ## Stop and remove all containers
	docker-compose down

down-volumes: ## Stop and remove all containers and volumes (WARNING: deletes data)
	docker-compose down -v

restart: ## Restart all services
	docker-compose restart

restart-service: ## Restart a specific service (usage: make restart-service SERVICE=finance-service)
	docker-compose restart $(SERVICE)

logs: ## View logs from all services
	docker-compose logs -f

logs-service: ## View logs from a specific service (usage: make logs-service SERVICE=finance-service)
	docker-compose logs -f $(SERVICE)

ps: ## List all running containers
	docker-compose ps

health: ## Check health of all services
	@echo "Checking service health..."
	@docker-compose ps
	@echo "\nFinance Service Health:"
	@curl -s http://localhost:8081/actuator/health || echo "Finance service not responding"
	@echo "\nAI Service Health:"
	@curl -s http://localhost:8000/health || echo "AI service not responding"
	@echo "\nGateway Health:"
	@curl -s http://localhost:8080/actuator/health || echo "Gateway not responding"
	@echo "\nWeb Application:"
	@curl -s http://localhost/health || echo "Web application not responding"

clean: ## Remove all containers, images, and volumes
	docker-compose down -v --rmi all

start-infra: ## Start only infrastructure services (postgres, keycloak, mongo, redis)
	docker-compose up -d postgres keycloak mongo redis

start-apps: ## Start only application services
	docker-compose up -d finance-service ai-service gateway web

stop: ## Stop all services without removing containers
	docker-compose stop

exec-finance: ## Open shell in finance-service container
	docker-compose exec finance-service sh

exec-ai: ## Open shell in ai-service container
	docker-compose exec ai-service sh

exec-db: ## Open PostgreSQL CLI
	docker-compose exec postgres psql -U postgres -d aifinance

exec-mongo: ## Open MongoDB CLI
	docker-compose exec mongo mongosh aifinance

exec-redis: ## Open Redis CLI
	docker-compose exec redis redis-cli

backup-db: ## Backup PostgreSQL database
	docker-compose exec postgres pg_dump -U postgres aifinance > backup_$(shell date +%Y%m%d_%H%M%S).sql

rebuild: down build up ## Rebuild and restart all services

dev: ## Start services for development (infrastructure only)
	docker-compose up -d postgres mongo redis
	@echo "Infrastructure services started. Run your application services locally."

prod: build up health ## Build, start, and check health of all services

.DEFAULT_GOAL := help
