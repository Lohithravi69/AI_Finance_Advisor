from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List, Optional
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split
from transformers import pipeline
import joblib
import os

app = FastAPI(title="AI Finance Advisor - AI Service", version="0.1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load or train ML models
MODEL_DIR = "models"
os.makedirs(MODEL_DIR, exist_ok=True)

# Sample training data for categorization
sample_data = [
    ("grocery store purchase", "grocery"),
    ("rent payment", "housing"),
    ("electric bill", "utilities"),
    ("gas station", "transport"),
    ("restaurant dinner", "dining"),
    ("amazon purchase", "shopping"),
    ("starbucks coffee", "dining"),
    ("uber ride", "transport"),
    ("internet bill", "utilities"),
    ("supermarket", "grocery"),
]

# Categorization model
if os.path.exists(f"{MODEL_DIR}/categorizer.pkl"):
    categorizer = joblib.load(f"{MODEL_DIR}/categorizer.pkl")
else:
    df = pd.DataFrame(sample_data, columns=["text", "category"])
    X_train, X_test, y_train, y_test = train_test_split(df["text"], df["category"], test_size=0.2, random_state=42)
    categorizer = Pipeline([
        ('tfidf', TfidfVectorizer()),
        ('clf', MultinomialNB())
    ])
    categorizer.fit(X_train, y_train)
    joblib.dump(categorizer, f"{MODEL_DIR}/categorizer.pkl")

# Chat model (basic transformer)
chat_model = pipeline("text2text-generation", model="google/flan-t5-small", device=-1)  # CPU


class Transaction(BaseModel):
    id: Optional[str] = None
    description: str = Field(..., min_length=1)
    amount: float
    date: str
    merchant: Optional[str] = None


class CategorizeRequest(BaseModel):
    transactions: List[Transaction]


class CategorizedItem(BaseModel):
    id: Optional[str] = None
    description: str
    amount: float
    category: str


class CategorizeResponse(BaseModel):
    items: List[CategorizedItem]


class PredictRequest(BaseModel):
    past_monthly_spend: List[float] = Field(..., description="Last N months spend")


class PredictResponse(BaseModel):
    next_month_spend: float


class AdviseRequest(BaseModel):
    income: float
    fixed_expenses: float
    variable_expenses: float
    savings_goal: Optional[float] = None


class AdviseResponse(BaseModel):
    suggested_savings: float
    note: str


class ChatRequest(BaseModel):
    message: str


class ChatResponse(BaseModel):
    reply: str


@app.get("/health")
def health():
    return {"status": "ok"}


# Very naive rule-based categorizer stub
_KEYWORDS = {
    "grocery": ["grocery", "supermarket", "whole foods", "kroger", "aldi", "costco"],
    "rent": ["rent", "landlord"],
    "utilities": ["utility", "electric", "water", "gas", "internet", "wifi"],
    "transport": ["uber", "lyft", "gas station", "bus", "train", "metro"],
    "dining": ["restaurant", "cafe", "dining", "bar", "mcdonald", "starbucks"],
    "shopping": ["amazon", "mall", "store", "clothes", "electronics"],
}


def _match_category(text: str) -> str:
    t = text.lower()
    for cat, keys in _KEYWORDS.items():
        if any(k in t for k in keys):
            return cat
    return "other"


@app.post("/categorize", response_model=CategorizeResponse)
def categorize(req: CategorizeRequest):
    items = []
    for tx in req.transactions:
        text = (tx.description or "") + " " + (tx.merchant or "")
        try:
            cat = categorizer.predict([text])[0]
        except:
            cat = _match_category(text)  # fallback
        items.append(CategorizedItem(id=tx.id, description=tx.description, amount=tx.amount, category=cat))
    return CategorizeResponse(items=items)


@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    data = np.array(req.past_monthly_spend[-6:] or [0.0])
    trend = (data[-1] - data[0]) / max(len(data) - 1, 1)
    next_val = float(max(0.0, data[-1] + trend))
    return PredictResponse(next_month_spend=round(next_val, 2))


@app.post("/advise", response_model=AdviseResponse)
def advise(req: AdviseRequest):
    disposable = max(0.0, req.income - req.fixed_expenses - req.variable_expenses)
    suggested = round(disposable * 0.2, 2)  # 20% starting heuristic
    note = "Aim for 20% savings of disposable income; adjust to reach goals."
    if req.savings_goal and req.savings_goal > 0:
        note += " Consider earmarking part of monthly savings toward your goal."
    return AdviseResponse(suggested_savings=suggested, note=note)


@app.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest):
    # Placeholder response; replace with LLM-backed service later
    message = req.message.strip().lower()
    if "budget" in message:
        reply = "A simple rule is 50/30/20 for needs/wants/savings."
    elif "save" in message:
        reply = "Automate transfers on payday to hit your savings goals."
    else:
        reply = "I can help with budgeting, savings, and expense insights."
    return ChatResponse(reply=reply)
