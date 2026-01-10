from fastapi import APIRouter, Header, HTTPException
from datetime import datetime
import uuid

from ..models import AnalysisRequest, AnalysisResponse, Insight

router = APIRouter()


def _require_auth(authorization: str | None):
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="Missing or invalid Authorization header")


@router.post("/analyze", response_model=AnalysisResponse)
def analyze(request: AnalysisRequest, authorization: str | None = Header(default=None)):
    _require_auth(authorization)

    # Simple heuristic demo — replace with real analytics later
    insights: list[Insight] = []
    if request.analysisType == "spending_pattern":
        if request.includeCategories:
            insights.append(
                Insight(
                    type="trend",
                    severity="info",
                    category=request.includeCategories[0],
                    message=f"Spending trend observed in {request.includeCategories[0]}",
                    trend="increasing",
                )
            )
        insights.append(
            Insight(
                type="overspending",
                severity="warning",
                category="dining",
                message="Dining expenses 30% above typical month",
                currentAmount=560.0,
                averageAmount=430.0,
                percentageChange=30.0,
            )
        )

    summary = "Preliminary analysis completed. Trends and anomalies identified."
    response = AnalysisResponse(
        analysisId=f"ana_{uuid.uuid4().hex[:6]}",
        timestamp=datetime.utcnow(),
        analysisType=request.analysisType,
        insights=insights,
        summary=summary,
        confidence=0.85,
    )
    return response
