from fastapi import APIRouter, Header, HTTPException
from datetime import datetime
import uuid

from ..models import AdviceRequest, AdviceResponse, Recommendation

router = APIRouter()


def _require_auth(authorization: str | None):
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="Missing or invalid Authorization header")


@router.post("/advice", response_model=AdviceResponse)
def advice(request: AdviceRequest, authorization: str | None = Header(default=None)):
    _require_auth(authorization)

    recs: list[Recommendation] = []
    if request.adviceType == "savings_optimization":
        recs.append(
            Recommendation(
                priority="high",
                category="emergency_fund",
                title="Build Emergency Fund",
                description="Save 3–6 months of expenses in liquid savings",
                currentStatus="In Progress",
                targetAmount=12000.0,
                currentAmount=4500.0,
                actionItems=[
                    "Automate $300/month transfer",
                    "Reduce dining by 20%",
                    "Use high-yield savings account",
                ],
                timeframe="6 months",
            )
        )

    response = AdviceResponse(
        adviceId=f"adv_{uuid.uuid4().hex[:6]}",
        timestamp=datetime.utcnow(),
        adviceType=request.adviceType,
        recommendations=recs,
        estimatedImpact={
            "monthlySavingsIncrease": 300.0,
            "annualSavingsIncrease": 3600.0,
            "projectedNetWorth10Years": 110000.0,
        },
        confidence=0.9,
    )
    return response
