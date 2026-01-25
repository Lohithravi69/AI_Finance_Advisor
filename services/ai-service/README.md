# AI Service README

## Overview

AI Service provides intelligent financial analysis and personalized recommendations using machine learning and statistical analysis.

## Structure

```
ai-service/
├── app/
│   ├── __init__.py          # Flask app factory
│   ├── routes.py            # REST API endpoints
│   ├── analyzer.py          # Financial analysis engine
│   ├── recommender.py       # Recommendation engine
│   └── dto.py               # Data transfer objects
├── config.py                # Configuration management
├── wsgi.py                  # Entry point
└── requirements.txt         # Python dependencies
```

## Features

### Analysis Endpoints

- **POST /api/v1/ai/analyze**
  - Spending pattern analysis
  - Budget health assessment
  - Savings potential identification
  - Anomaly detection

- **POST /api/v1/ai/advice**
  - Savings optimization
  - Debt management strategies
  - Expense reduction tips
  - Investment recommendations

- **POST /api/v1/ai/chat**
  - Interactive financial advisor
  - Context-aware responses
  - Suggested actions

### Financial Analysis Capabilities

- **Spending Pattern Recognition**
  - Anomaly detection (2σ threshold)
  - Trend analysis
  - Category-wise breakdown
  - Month-over-month comparison

- **Budget Health**
  - Savings rate calculation
  - Income vs expense analysis
  - Goal tracking

- **Savings Potential**
  - Category-specific recommendations
  - Reduction opportunities
  - Estimated impact calculation

### Recommendation Engine

- **Savings Optimization**
  - Emergency fund planning
  - Retirement contribution strategy
  - Debt payoff plans

- **Debt Management**
  - Payoff timeline calculation
  - Interest savings analysis
  - Balance transfer recommendations

- **Expense Reduction**
  - Subscription audit
  - Dining optimization
  - Smart shopping tips

## Setup

### Installation

```bash
# Navigate to AI service
cd services/ai-service

# Create virtual environment
python -m venv venv
source venv/Scripts/activate  # Windows
# or
source venv/bin/activate  # Unix/Mac

# Install dependencies
pip install -r requirements.txt
```

### Running

```bash
# Development
python wsgi.py

# Or with Flask CLI
flask run --port 8082

# Or with Gunicorn (production)
gunicorn -w 4 -b 0.0.0.0:8082 wsgi:app
```

### Testing

```bash
# Test health check
curl http://localhost:8082/api/v1/health

# Test analysis endpoint
curl -X POST http://localhost:8082/api/v1/ai/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "analysisType": "spending_pattern",
    "period": "month"
  }'
```

## API Contract Compliance

All endpoints implement the OpenAPI 3.0 specification defined in `/docs/api-spec.yaml`

### Request/Response Examples

See `docs/api-spec.yaml` for complete specifications including:
- Request validation schemas
- Response formats
- Error handling
- Example payloads

## Architecture

### Layered Design

1. **Routes Layer** (`routes.py`)
   - HTTP request handling
   - Input validation
   - Response formatting

2. **Business Logic Layer**
   - `analyzer.py` - Financial analysis algorithms
   - `recommender.py` - Recommendation generation

3. **Data Layer** (`dto.py`)
   - Type-safe data structures
   - Schema validation
   - API contract alignment

4. **Configuration Layer** (`config.py`)
   - Environment-based settings
   - Service discovery
   - JWT configuration

## Dependencies

- **flask** - Web framework
- **flask-cors** - CORS handling
- **pydantic** - Data validation
- **pandas** - Data analysis
- **numpy** - Numerical computing
- **scikit-learn** - Machine learning
- **requests** - HTTP client (for service integration)

## Integration Points

### Finance Service Integration

- Currently uses mock data
- Ready to integrate with Finance Service at port 8081
- Will fetch real transaction data via HTTP

### Expected Finance Service Contract

```python
GET /api/v1/finance/transaction?userId=xxx&period=month
Response:
{
    "transactions": [...],
    "pagination": {...},
    "summary": {...}
}
```

## Configuration

Environment variables (via `config.py`):

```bash
FLASK_ENV=development
FINANCE_SERVICE_URL=http://localhost:8081/api/v1
JWT_SECRET=your-secret-key
```

## Security

- CORS configured for local development
- JWT-ready for gateway integration
- Input validation on all endpoints
- Error responses sanitized in production

## Future Enhancements

- [ ] Real-time Finance Service integration
- [ ] Machine learning model deployment
- [ ] Advanced anomaly detection
- [ ] Predictive financial modeling
- [ ] Multi-currency support
- [ ] Goal-based recommendations
- [ ] Benchmark comparisons
