"""
Advanced Predictive Analytics Engine
Provides sophisticated financial forecasting and risk assessment
"""
import logging
from typing import Dict, List, Any, Tuple, Optional
from datetime import datetime, timedelta
from decimal import Decimal
import numpy as np
import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import StandardScaler
from dataclasses import dataclass

from app.dto import (
    InsightDTO,
    InsightType,
    InsightSeverity,
    TrendDirection,
    AnalysisType
)

logger = logging.getLogger(__name__)


@dataclass
class CashFlowPrediction:
    """Cash flow prediction result"""
    date: str
    predicted_balance: float
    confidence: float
    risk_level: str
    factors: List[str]


@dataclass
class RiskAssessment:
    """Financial risk assessment"""
    overall_risk_score: float
    risk_factors: List[str]
    mitigation_strategies: List[str]
    confidence_level: float


class PredictiveAnalyzer:
    """
    Advanced predictive analytics engine for financial forecasting
    Uses machine learning models for cash flow prediction and risk assessment
    """

    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.scaler = StandardScaler()
        self.cash_flow_model = RandomForestRegressor(
            n_estimators=100,
            random_state=42,
            max_depth=10
        )

    def predict_cash_flow(
        self,
        transactions: List[Dict[str, Any]],
        months_ahead: int = 6
    ) -> Tuple[List[CashFlowPrediction], Dict[str, Any], float]:
        """
        Predict future cash flow using historical patterns and ML

        Returns:
            Tuple of (predictions, metadata, confidence)
        """
        predictions = []

        if len(transactions) < 10:
            # Not enough data for reliable prediction
            return predictions, {"error": "Insufficient transaction history"}, 0.3

        try:
            # Prepare data for ML model
            df = self._prepare_transaction_data(transactions)

            if df.empty:
                return predictions, {"error": "No valid transaction data"}, 0.3

            # Train model on historical data
            self._train_cash_flow_model(df)

            # Generate predictions
            current_balance = self._calculate_current_balance(transactions)
            predictions = self._generate_predictions(df, current_balance, months_ahead)

            metadata = {
                "model_accuracy": 0.85,  # Placeholder - would be calculated from validation
                "prediction_horizon": months_ahead,
                "data_points_used": len(df),
                "last_transaction_date": df['date'].max().strftime('%Y-%m-%d')
            }

            confidence = min(0.9, 0.5 + (len(transactions) / 200))  # Scale with data volume

        except Exception as e:
            self.logger.error(f"Cash flow prediction failed: {e}")
            return predictions, {"error": str(e)}, 0.1

        return predictions, metadata, confidence

    def assess_financial_risk(
        self,
        user_profile: Dict[str, Any],
        transactions: List[Dict[str, Any]]
    ) -> Tuple[RiskAssessment, List[InsightDTO], float]:
        """
        Comprehensive financial risk assessment

        Returns:
            Tuple of (risk_assessment, insights, confidence)
        """
        insights = []

        try:
            # Calculate various risk factors
            liquidity_risk = self._assess_liquidity_risk(transactions)
            spending_risk = self._assess_spending_risk(transactions)
            income_risk = self._assess_income_risk(user_profile, transactions)
            debt_risk = self._assess_debt_risk(user_profile)

            # Overall risk score (weighted average)
            overall_risk = (
                liquidity_risk * 0.3 +
                spending_risk * 0.25 +
                income_risk * 0.25 +
                debt_risk * 0.2
            )

            # Generate risk factors list
            risk_factors = []
            if liquidity_risk > 0.7:
                risk_factors.append("Low emergency fund coverage")
            if spending_risk > 0.7:
                risk_factors.append("High discretionary spending")
            if income_risk > 0.7:
                risk_factors.append("Unstable income sources")
            if debt_risk > 0.7:
                risk_factors.append("High debt-to-income ratio")

            # Generate mitigation strategies
            mitigation_strategies = self._generate_mitigation_strategies(risk_factors)

            # Create insights for high-risk areas
            if overall_risk > 0.6:
                insights.append(InsightDTO(
                    type=InsightType.ALERT.value,
                    severity=InsightSeverity.CRITICAL.value,
                    category="risk_assessment",
                    message=f"High financial risk detected (score: {overall_risk:.1f}/1.0). "
                           f"Immediate attention required.",
                    current_amount=overall_risk * 100,
                    percentage_change=0
                ))

            risk_assessment = RiskAssessment(
                overall_risk_score=overall_risk,
                risk_factors=risk_factors,
                mitigation_strategies=mitigation_strategies,
                confidence_level=0.82
            )

            confidence = 0.82

        except Exception as e:
            self.logger.error(f"Risk assessment failed: {e}")
            risk_assessment = RiskAssessment(
                overall_risk_score=0.5,
                risk_factors=["Unable to assess - data error"],
                mitigation_strategies=["Review financial records"],
                confidence_level=0.1
            )
            confidence = 0.1

        return risk_assessment, insights, confidence

    def forecast_savings_goals(
        self,
        current_savings: float,
        monthly_contribution: float,
        target_amount: float,
        expected_return_rate: float = 0.07
    ) -> Tuple[Dict[str, Any], List[InsightDTO], float]:
        """
        Forecast time to reach savings goals with compound interest

        Returns:
            Tuple of (forecast_data, insights, confidence)
        """
        insights = []
        forecast_data = {}

        try:
            if monthly_contribution <= 0:
                return {"error": "Invalid monthly contribution"}, insights, 0.1

            # Calculate months needed
            months_needed = self._calculate_compound_interest_time(
                current_savings, monthly_contribution, target_amount, expected_return_rate
            )

            # Generate monthly projections
            projections = []
            balance = current_savings
            monthly_rate = expected_return_rate / 12

            for month in range(min(int(months_needed) + 12, 120)):  # Max 10 years
                balance = balance * (1 + monthly_rate) + monthly_contribution
                projections.append({
                    "month": month + 1,
                    "balance": round(balance, 2),
                    "contributions": monthly_contribution * (month + 1),
                    "interest_earned": round(balance - current_savings - monthly_contribution * (month + 1), 2)
                })

            forecast_data = {
                "months_to_goal": round(months_needed, 1),
                "years_to_goal": round(months_needed / 12, 1),
                "total_contributions": round(monthly_contribution * months_needed, 2),
                "total_interest": round(target_amount - current_savings - monthly_contribution * months_needed, 2),
                "monthly_projections": projections[:24],  # First 2 years
                "feasibility_score": self._calculate_feasibility_score(months_needed, target_amount)
            }

            # Generate insights
            if months_needed > 60:  # More than 5 years
                insights.append(InsightDTO(
                    type=InsightType.WARNING.value,
                    severity=InsightSeverity.WARNING.value,
                    category="goal_forecast",
                    message=f"Goal may take {forecast_data['years_to_goal']} years. Consider increasing contributions.",
                    current_amount=months_needed,
                    target_amount=60,
                    percentage_change=((months_needed - 60) / 60 * 100)
                ))

            confidence = 0.88

        except Exception as e:
            self.logger.error(f"Goal forecasting failed: {e}")
            forecast_data = {"error": str(e)}
            confidence = 0.1

        return forecast_data, insights, confidence

    # Private helper methods

    def _prepare_transaction_data(self, transactions: List[Dict[str, Any]]) -> pd.DataFrame:
        """Prepare transaction data for ML analysis"""
        df = pd.DataFrame(transactions)

        if df.empty:
            return df

        # Convert date strings to datetime
        df['date'] = pd.to_datetime(df['date'], errors='coerce')
        df = df.dropna(subset=['date'])

        # Sort by date
        df = df.sort_values('date')

        # Add derived features
        df['amount'] = pd.to_numeric(df['amount'], errors='coerce')
        df['is_income'] = df['type'] == 'income'
        df['is_expense'] = df['type'] == 'expense'

        # Group by month for aggregation
        df['month'] = df['date'].dt.to_period('M')
        monthly_data = df.groupby('month').agg({
            'amount': ['sum', 'mean', 'count'],
            'is_income': 'sum',
            'is_expense': 'sum'
        }).reset_index()

        # Flatten column names
        monthly_data.columns = ['month', 'total_amount', 'avg_amount', 'transaction_count',
                               'income_count', 'expense_count']

        return monthly_data

    def _train_cash_flow_model(self, df: pd.DataFrame) -> None:
        """Train the cash flow prediction model"""
        if len(df) < 3:
            return

        # Prepare features
        df['month_num'] = range(len(df))
        features = ['month_num', 'total_amount', 'avg_amount', 'transaction_count']

        X = df[features].values
        y = df['total_amount'].shift(-1).fillna(df['total_amount'].mean()).values

        # Scale features
        X_scaled = self.scaler.fit_transform(X)

        # Train model
        self.cash_flow_model.fit(X_scaled[:-1], y[:-1])  # Use all but last for training

    def _generate_predictions(
        self,
        df: pd.DataFrame,
        current_balance: float,
        months_ahead: int
    ) -> List[CashFlowPrediction]:
        """Generate cash flow predictions"""
        predictions = []
        last_month_num = len(df)

        for month in range(1, months_ahead + 1):
            # Prepare features for prediction
            features = np.array([[
                last_month_num + month - 1,
                df['total_amount'].iloc[-1] if len(df) > 0 else 0,
                df['avg_amount'].iloc[-1] if len(df) > 0 else 0,
                df['transaction_count'].iloc[-1] if len(df) > 0 else 0
            ]])

            # Scale features
            features_scaled = self.scaler.transform(features)

            # Predict amount
            predicted_amount = self.cash_flow_model.predict(features_scaled)[0]

            # Update balance
            current_balance += predicted_amount

            # Assess risk
            risk_level = self._assess_balance_risk(current_balance, predicted_amount)

            # Generate factors
            factors = self._generate_balance_factors(current_balance, predicted_amount)

            prediction = CashFlowPrediction(
                date=(datetime.now() + timedelta(days=month*30)).strftime('%Y-%m-%d'),
                predicted_balance=round(current_balance, 2),
                confidence=0.75,  # Base confidence
                risk_level=risk_level,
                factors=factors
            )

            predictions.append(prediction)

        return predictions

    def _calculate_current_balance(self, transactions: List[Dict[str, Any]]) -> float:
        """Calculate current account balance from transactions"""
        balance = 0
        for transaction in transactions:
            amount = float(transaction.get('amount', 0))
            if transaction.get('type') == 'income':
                balance += amount
            elif transaction.get('type') == 'expense':
                balance -= amount
        return balance

    def _assess_balance_risk(self, balance: float, monthly_flow: float) -> str:
        """Assess risk level based on balance and cash flow"""
        if balance < 0:
            return "critical"
        elif balance < monthly_flow * 2:
            return "high"
        elif balance < monthly_flow * 6:
            return "medium"
        else:
            return "low"

    def _generate_balance_factors(self, balance: float, monthly_flow: float) -> List[str]:
        """Generate factors affecting balance prediction"""
        factors = []

        if balance < monthly_flow * 3:
            factors.append("Low emergency fund")
        if monthly_flow < 0:
            factors.append("Negative cash flow")
        if balance > monthly_flow * 12:
            factors.append("Strong savings position")

        return factors if factors else ["Stable financial position"]

    def _assess_liquidity_risk(self, transactions: List[Dict[str, Any]]) -> float:
        """Assess liquidity risk (0-1 scale)"""
        # Calculate average monthly expenses
        expenses = [t for t in transactions if t.get('type') == 'expense']
        if not expenses:
            return 0.5

        monthly_expenses = sum(float(t.get('amount', 0)) for t in expenses) / max(1, len(set(
            t.get('date', '')[:7] for t in expenses  # Group by month
        )))

        # Assume emergency fund should cover 6 months
        recommended_fund = monthly_expenses * 6
        current_balance = self._calculate_current_balance(transactions)

        coverage_ratio = current_balance / recommended_fund if recommended_fund > 0 else 0
        return max(0, min(1, 1 - coverage_ratio))  # Convert to risk score

    def _assess_spending_risk(self, transactions: List[Dict[str, Any]]) -> float:
        """Assess spending risk based on discretionary spending"""
        expenses = [t for t in transactions if t.get('type') == 'expense']
        if not expenses:
            return 0.5

        total_expenses = sum(float(t.get('amount', 0)) for t in expenses)

        # Define discretionary categories (high risk)
        discretionary_categories = ['entertainment', 'dining', 'shopping', 'subscriptions']
        discretionary_spending = sum(
            float(t.get('amount', 0)) for t in expenses
            if t.get('category', '').lower() in discretionary_categories
        )

        discretionary_ratio = discretionary_spending / total_expenses if total_expenses > 0 else 0
        return min(1, discretionary_ratio * 2)  # Scale to 0-1

    def _assess_income_risk(self, user_profile: Dict[str, Any], transactions: List[Dict[str, Any]]) -> float:
        """Assess income stability risk"""
        income_transactions = [t for t in transactions if t.get('type') == 'income']

        if len(income_transactions) < 3:
            return 0.8  # High risk with limited income history

        # Calculate income variability
        amounts = [float(t.get('amount', 0)) for t in income_transactions]
        if len(amounts) < 2:
            return 0.5

        mean_income = np.mean(amounts)
        std_income = np.std(amounts)
        cv = std_income / mean_income if mean_income > 0 else 1  # Coefficient of variation

        return min(1, cv)  # Convert to 0-1 risk score

    def _assess_debt_risk(self, user_profile: Dict[str, Any]) -> float:
        """Assess debt risk based on debt-to-income ratio"""
        # This would require debt information from user profile
        # Placeholder implementation
        debt_to_income = user_profile.get('debt_to_income_ratio', 0.3)
        return min(1, debt_to_income * 2)  # Scale to 0-1

    def _generate_mitigation_strategies(self, risk_factors: List[str]) -> List[str]:
        """Generate mitigation strategies for identified risks"""
        strategies = []

        for factor in risk_factors:
            if "emergency fund" in factor.lower():
                strategies.extend([
                    "Build 3-6 months of expenses in emergency fund",
                    "Automate monthly transfers to savings",
                    "Cut discretionary spending by 20%"
                ])
            elif "spending" in factor.lower():
                strategies.extend([
                    "Create category budgets and stick to them",
                    "Use cash-only for discretionary purchases",
                    "Track every expense for one month"
                ])
            elif "income" in factor.lower():
                strategies.extend([
                    "Develop multiple income streams",
                    "Build skills for higher-paying opportunities",
                    "Create a side hustle or freelance work"
                ])
            elif "debt" in factor.lower():
                strategies.extend([
                    "Focus on high-interest debt first (avalanche method)",
                    "Consider debt consolidation",
                    "Negotiate lower interest rates with creditors"
                ])

        return list(set(strategies))  # Remove duplicates

    def _calculate_compound_interest_time(
        self,
        current: float,
        monthly: float,
        target: float,
        rate: float
    ) -> float:
        """Calculate months needed to reach target with compound interest"""
        if monthly <= 0:
            return float('inf')

        remaining = target - current
        if remaining <= 0:
            return 0

        monthly_rate = rate / 12

        # Use compound interest formula
        # Future Value = P * (1 + r)^n + PMT * (((1 + r)^n - 1) / r)
        # Solving for n (number of months)

        # Simplified calculation for monthly contributions
        months = 0
        balance = current

        while balance < target and months < 1200:  # Max 100 years
            balance = balance * (1 + monthly_rate) + monthly
            months += 1

        return months

    def _calculate_feasibility_score(self, months_needed: float, target_amount: float) -> float:
        """Calculate how feasible a goal is (0-1 scale)"""
        if months_needed <= 12:  # 1 year
            return 0.9
        elif months_needed <= 60:  # 5 years
            return 0.7
        elif months_needed <= 120:  # 10 years
            return 0.5
        else:
            return 0.3
