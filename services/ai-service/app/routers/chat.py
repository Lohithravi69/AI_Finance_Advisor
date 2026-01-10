from fastapi import APIRouter, Header, HTTPException
from datetime import datetime

from ..models import ChatRequest, ChatResponse

router = APIRouter()


def _require_auth(authorization: str | None):
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(status_code=401, detail="Missing or invalid Authorization header")


@router.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest, authorization: str | None = Header(default=None)):
    _require_auth(authorization)

    bot_reply = (
        "Good question! Start by reviewing discretionary spending (dining, "
        "entertainment). Negotiate bills, cancel unused subscriptions, and "
        "automate a fixed monthly savings transfer."
    )

    return ChatResponse(
        conversationId=request.conversationId,
        response=bot_reply,
        suggestedActions=[
            "Review dining expenses",
            "Compare insurance rates",
            "Cancel unused subscriptions",
        ],
        timestamp=datetime.utcnow(),
    )
