from pydantic import BaseModel, Field, EmailStr
from typing import List, Optional, Literal
from datetime import datetime

# Shared AI/Finance models per OpenAPI

class Insight(BaseModel):
    type: Literal["overspending", "underspending", "trend", "anomaly", "opportunity", "risk"]
    severity: Literal["info", "warning", "alert", "critical"]
    category: Optional[str] = None
    message: str
    currentAmount: Optional[float] = None
    averageAmount: Optional[float] = None
    percentageChange: Optional[float] = None
    trend: Optional[Literal["increasing", "decreasing", "stable", "volatile"]] = None
    metadata: Optional[dict] = None

class AnalysisRequest(BaseModel):
    analysisType: Literal[
        "spending_pattern",
        "budget_health",
        "savings_potential",
        "risk_assessment",
        "goal_progress",
    ]
    period: Optional[Literal["week", "month", "quarter", "year"]] = "month"
    includeCategories: Optional[List[str]] = None
    customParameters: Optional[dict] = None

class AnalysisResponse(BaseModel):
    analysisId: str
    timestamp: datetime
    analysisType: str
    insights: List[Insight] = []
    summary: Optional[str] = None
    confidence: float = Field(ge=0, le=1, default=0.8)

class Recommendation(BaseModel):
    priority: Literal["critical", "high", "medium", "low"]
    category: Optional[str] = None
    title: str
    description: Optional[str] = None
    currentStatus: Optional[str] = None
    targetAmount: Optional[float] = None
    currentAmount: Optional[float] = None
    actionItems: List[str] = []
    timeframe: Optional[str] = None

class AdviceRequest(BaseModel):
    adviceType: Literal[
        "savings_optimization",
        "debt_management",
        "investment_strategy",
        "budget_creation",
        "expense_reduction",
        "income_growth",
    ]
    context: Optional[dict] = None

class AdviceResponse(BaseModel):
    adviceId: str
    timestamp: datetime
    adviceType: str
    recommendations: List[Recommendation] = []
    estimatedImpact: Optional[dict] = None
    confidence: float = Field(ge=0, le=1, default=0.9)

class ChatRequest(BaseModel):
    message: str
    conversationId: Optional[str] = None

class ChatResponse(BaseModel):
    conversationId: Optional[str] = None
    response: str
    suggestedActions: List[str] = []
    timestamp: datetime

class LoginRequest(BaseModel):
    email: EmailStr
    password: str
    rememberMe: Optional[bool] = False

class LoginResponseUser(BaseModel):
    id: str
    email: EmailStr
    name: str
    role: Literal["user", "admin"]
    createdAt: Optional[datetime] = None
    lastLoginAt: Optional[datetime] = None

class LoginResponse(BaseModel):
    success: bool = True
    token: str
    refreshToken: Optional[str] = None
    expiresIn: int
    user: LoginResponseUser
