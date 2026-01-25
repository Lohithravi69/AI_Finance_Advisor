from typing import Optional, Dict, List, Any
from dataclasses import dataclass, asdict
from datetime import datetime
from enum import Enum


class InsightType(str, Enum):
    """Insight type enumeration - matches API contract"""
    OVERSPENDING = "overspending"
    UNDERSPENDING = "underspending"
    TREND = "trend"
    ANOMALY = "anomaly"
    OPPORTUNITY = "opportunity"
    RISK = "risk"


class InsightSeverity(str, Enum):
    """Insight severity level - matches API contract"""
    INFO = "info"
    WARNING = "warning"
    ALERT = "alert"
    CRITICAL = "critical"


class TrendDirection(str, Enum):
    """Trend direction enumeration"""
    INCREASING = "increasing"
    DECREASING = "decreasing"
    STABLE = "stable"
    VOLATILE = "volatile"


class AnalysisType(str, Enum):
    """Analysis type enumeration - matches API contract"""
    SPENDING_PATTERN = "spending_pattern"
    BUDGET_HEALTH = "budget_health"
    SAVINGS_POTENTIAL = "savings_potential"
    RISK_ASSESSMENT = "risk_assessment"
    GOAL_PROGRESS = "goal_progress"


class AdviceType(str, Enum):
    """Advice type enumeration - matches API contract"""
    SAVINGS_OPTIMIZATION = "savings_optimization"
    DEBT_MANAGEMENT = "debt_management"
    INVESTMENT_STRATEGY = "investment_strategy"
    BUDGET_CREATION = "budget_creation"
    EXPENSE_REDUCTION = "expense_reduction"
    INCOME_GROWTH = "income_growth"


class RecommendationPriority(str, Enum):
    """Recommendation priority enumeration"""
    CRITICAL = "critical"
    HIGH = "high"
    MEDIUM = "medium"
    LOW = "low"


@dataclass
class InsightDTO:
    """Insight Data Transfer Object - matches API contract"""
    type: str  # InsightType
    severity: str  # InsightSeverity
    category: str
    message: str
    current_amount: Optional[float] = None
    average_amount: Optional[float] = None
    percentage_change: Optional[float] = None
    trend: Optional[str] = None  # TrendDirection
    metadata: Optional[Dict[str, Any]] = None

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return asdict(self)


@dataclass
class AnalysisRequestDTO:
    """Analysis Request DTO - matches API contract"""
    analysis_type: str  # AnalysisType
    period: Optional[str] = None
    include_categories: Optional[List[str]] = None
    custom_parameters: Optional[Dict[str, Any]] = None

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return asdict(self)


@dataclass
class AnalysisResponseDTO:
    """Analysis Response DTO - matches API contract"""
    analysis_id: str
    timestamp: str
    analysis_type: str
    insights: List[Dict[str, Any]]
    summary: str
    confidence: float

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return {
            'analysisId': self.analysis_id,
            'timestamp': self.timestamp,
            'analysisType': self.analysis_type,
            'insights': self.insights,
            'summary': self.summary,
            'confidence': self.confidence
        }


@dataclass
class RecommendationDTO:
    """Recommendation DTO - matches API contract"""
    priority: str  # RecommendationPriority
    category: str
    title: str
    description: str
    current_status: str
    target_amount: Optional[float] = None
    current_amount: Optional[float] = None
    action_items: Optional[List[str]] = None
    timeframe: Optional[str] = None

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return asdict(self)


@dataclass
class AdviceRequestDTO:
    """Advice Request DTO - matches API contract"""
    advice_type: str  # AdviceType
    context: Optional[Dict[str, Any]] = None

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return asdict(self)


@dataclass
class AdviceResponseDTO:
    """Advice Response DTO - matches API contract"""
    advice_id: str
    timestamp: str
    advice_type: str
    recommendations: List[Dict[str, Any]]
    estimated_impact: Dict[str, Any]
    confidence: float

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return {
            'adviceId': self.advice_id,
            'timestamp': self.timestamp,
            'adviceType': self.advice_type,
            'recommendations': self.recommendations,
            'estimatedImpact': self.estimated_impact,
            'confidence': self.confidence
        }


@dataclass
class ErrorDTO:
    """Error Response DTO - matches API contract"""
    success: bool = False
    error: Dict[str, Any] = None

    def to_dict(self) -> Dict[str, Any]:
        """Convert to dictionary for JSON serialization"""
        return {
            'success': self.success,
            'error': self.error or {}
        }

    @staticmethod
    def create(code: str, message: str, details: Optional[List[Dict]] = None) -> 'ErrorDTO':
        """Factory method to create error response"""
        error = {
            'code': code,
            'message': message,
            'timestamp': datetime.now().isoformat() + 'Z',
        }
        if details:
            error['details'] = details

        return ErrorDTO(success=False, error=error)
