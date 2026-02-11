# Docker Deployment Guide

This guide explains how to build and run the AI Finance Advisor application using Docker containers.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB RAM allocated to Docker
- At least 10GB free disk space

## Architecture

The application consists of the following services:

### Infrastructure Services
- **PostgreSQL** (port 5432) - Primary database
- **Keycloak** (port 8888) - Authentication & authorization
- **MongoDB** (port 27017) - NoSQL database for AI service
- **Redis** (port 6379) - Caching layer

### Application Services
- **Finance Service** (port 8081) - Spring Boot REST API
- **AI Service** (port 8000) - Python FastAPI for AI features
- **Gateway** (port 8080) - API Gateway (Spring Cloud Gateway)
- **Web** (port 80) - React frontend served by Nginx

## Quick Start

### 1. Build and Start All Services

```bash
# Start infrastructure services first
docker-compose up -d postgres keycloak mongo redis

# Wait for services to be healthy (about 30 seconds)
docker-compose ps

# Start application services
docker-compose up -d finance-service ai-service gateway web
```

### 2. Start Everything at Once

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f finance-service
```

### 3. Access the Application

- **Web Application**: http://localhost
- **API Gateway**: http://localhost:8080
- **Finance Service**: http://localhost:8081
- **AI Service**: http://localhost:8000
- **Keycloak Admin**: http://localhost:8888 (admin/admin)
- **H2 Console** (if enabled): http://localhost:8081/h2-console

## Service Management

### Check Service Status

```bash
# View running containers
docker-compose ps

# Check service health
docker-compose exec finance-service wget -qO- http://localhost:8081/actuator/health
docker-compose exec ai-service python -c "import urllib.request; print(urllib.request.urlopen('http://localhost:8000/health').read())"
```

### Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose down -v

# Stop specific service
docker-compose stop finance-service
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart finance-service
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f finance-service

# Last 100 lines
docker-compose logs --tail=100 finance-service
```

## Development Workflow

### Rebuild After Code Changes

```bash
# Rebuild specific service
docker-compose build finance-service
docker-compose up -d finance-service

# Rebuild all services
docker-compose build
docker-compose up -d
```

### Execute Commands in Containers

```bash
# Access finance-service shell
docker-compose exec finance-service sh

# Access PostgreSQL
docker-compose exec postgres psql -U postgres -d aifinance

# Access MongoDB
docker-compose exec mongo mongosh aifinance

# Access Redis
docker-compose exec redis redis-cli
```

## Troubleshooting

### Service Won't Start

```bash
# Check logs
docker-compose logs service-name

# Check if port is already in use
netstat -ano | findstr :8080

# Remove containers and try again
docker-compose down
docker-compose up -d --force-recreate
```

### Database Connection Issues

```bash
# Verify PostgreSQL is running
docker-compose exec postgres pg_isready -U postgres

# Check database exists
docker-compose exec postgres psql -U postgres -c "\l"

# Inspect network connectivity
docker network inspect aifinanceadvisor_aifa-network
```

### Out of Memory

```bash
# View resource usage
docker stats

# Increase Docker memory allocation in Docker Desktop settings
# Recommended: 4GB minimum, 8GB for full stack
```

### Clean Start

```bash
# Stop everything and remove volumes
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Rebuild from scratch
docker-compose build --no-cache
docker-compose up -d
```

## Environment Variables

### Finance Service

- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` - Keycloak issuer URI

### AI Service

- `MONGODB_URL` - MongoDB connection string
- `REDIS_URL` - Redis connection string
- `DATABASE_NAME` - MongoDB database name

### Gateway

- `FINANCE_SERVICE_URL` - Finance service URL
- `AI_SERVICE_URL` - AI service URL

## Production Considerations

### Security

1. Change default passwords in `docker-compose.yml`
2. Use secrets management (Docker Swarm secrets or Kubernetes secrets)
3. Enable HTTPS with valid certificates
4. Restrict network access between services
5. Run containers as non-root users (already configured)

### Performance

1. Configure resource limits for each service
2. Use production-ready database with persistent volumes
3. Enable caching strategies
4. Use CDN for static assets
5. Configure connection pooling

### Monitoring

1. Enable Prometheus metrics export
2. Set up health check endpoints (already configured)
3. Configure log aggregation (ELK stack, Loki, etc.)
4. Use APM tools (New Relic, Datadog, etc.)

### Backup

```bash
# Backup PostgreSQL
docker-compose exec postgres pg_dump -U postgres aifinance > backup.sql

# Backup MongoDB
docker-compose exec mongo mongodump --db aifinance --out /tmp/backup

# Backup volumes
docker run --rm -v aifinanceadvisor_pgdata:/data -v $(pwd):/backup alpine tar czf /backup/pgdata-backup.tar.gz /data
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build and Push Docker Images

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build images
        run: docker-compose build
      
      - name: Push to registry
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker-compose push
```

## Support

For issues or questions:
- Check the logs: `docker-compose logs`
- Review the documentation in `/docs`
- Open an issue on GitHub
