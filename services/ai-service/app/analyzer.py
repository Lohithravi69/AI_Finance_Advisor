"""
AI Analysis Engine
Provides intelligent financial analysis and pattern recognition
"""
import logging
from typing import Dict, List, Any, Tuple, Optional
from datetime import datetime, timedelta
from decimal import Decimal
import numpy as np
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
class TransactionData:
    """Transaction data from Finance Service"""
    transaction_id: str
    type: str  # "income" or "expense"
    category: str
    amount: float
    currency: str
    date: str
    description: Optional[str] = None


class FinancialAnalyzer:
    """
    Core financial analysis engine
    Provides spending pattern analysis, anomaly detection, and financial health assessment
    """

    # Spending thresholds
    OVERSPENDING_THRESHOLD = 0.25  # 25% above average
    UNDERSPENDING_THRESHOLD = 0.25  # 25% below average
    ANOMALY_THRESHOLD = 2.0  # 2 standard deviations

    def __init__(self):
        self.logger = logging.getLogger(__name__)

    def analyze_spending_patterns(
            self,
            transactions: List[Dict[str, Any]],
            period: str = "month",
            include_categories: Optional[List[str]] = None
    ) -> Tuple[List[InsightDTO], str, float]:
        """
        Analyze spending patterns for anomalies, trends, and overspending
        
        Returns:
            Tuple of (insights, summary, confidence_score)
        """
        insights = []
        
        if not transactions:
            return insights, "No transaction data available for analysis", 0.5

        # Filter by categories if specified
        if include_categories:
            transactions = [t for t in transactions 
                          if t.get('category', '').lower() in include_categories]

        # Group transactions by category
        category_data = self._group_by_category(transactions)
        
        # Analyze each category
        for category, cat_transactions in category_data.items():
            if cat_transactions:
                insights.extend(
                    self._analyze_category(category, cat_transactions)
                )

        # Generate overall summary
        summary = self._generate_summary(insights)
        
        # Calculate confidence based on data volume
        confidence = min(0.95, 0.5 + (len(transactions) / 100))

        return insights, summary, confidence

    def analyze_budget_health(
            self,
            transactions: List[Dict[str, Any]],
            monthly_income: Optional[float] = None
    ) -> Tuple[List[InsightDTO], str, float]:
        """
        Analyze overall budget health and savings rate
        
        Returns:
            Tuple of (insights, summary, confidence_score)
        """
        insights = []

        if not transactions:
            return insights, "Insufficient data for budget analysis", 0.5

        # Calculate totals
        total_income = sum(t['amount'] for t in transactions if t['type'] == 'income')
        total_expenses = sum(t['amount'] for t in transactions if t['type'] == 'expense')
        
        # Use provided income if available, otherwise use calculated
        income = monthly_income or total_income
        
        if income > 0:
            savings_rate = ((income - total_expenses) / income) * 100
            
            # Add insights based on savings rate
            if savings_rate < 10:
                insights.append(InsightDTO(
                    type=InsightType.ALERT.value,
                    severity=InsightSeverity.CRITICAL.value,
                    category="savings",
                    message=f"Savings rate is critically low at {savings_rate:.1f}%. Target: 20%+",
                    current_amount=savings_rate,
                    average_amount=20.0,
                    percentage_change=savings_rate - 20.0
                ))
            elif savings_rate < 20:
                insights.append(InsightDTO(
                    type=InsightType.WARNING.value,
                    severity=InsightSeverity.WARNING.value,
                    category="savings",
                    message=f"Savings rate is below target at {savings_rate:.1f}%. Aim for 20%+",
                    current_amount=savings_rate,
                    average_amount=20.0,
                    percentage_change=savings_rate - 20.0
                ))
            else:
                insights.append(InsightDTO(
                    type=InsightType.OPPORTUNITY.value,
                    severity=InsightSeverity.INFO.value,
                    category="savings",
                    message=f"Good savings rate at {savings_rate:.1f}%. Keep it up!",
                    current_amount=savings_rate,
                    average_amount=20.0,
                    percentage_change=savings_rate - 20.0
                ))

        summary = self._generate_summary(insights)
        confidence = 0.85

        return insights, summary, confidence

    def identify_savings_potential(
            self,
            transactions: List[Dict[str, Any]]
    ) -> Tuple[List[InsightDTO], str, float]:
        """
        Identify areas where user can save money
        
        Returns:
            Tuple of (insights, summary, confidence_score)
        """
        insights = []

        if not transactions:
            return insights, "No transaction data available", 0.5

        # Focus on expense categories
        expenses = [t for t in transactions if t['type'] == 'expense']
        category_data = self._group_by_category(expenses)

        # Identify high-spending categories
        sorted_categories = sorted(
            category_data.items(),
            key=lambda x: sum(t['amount'] for t in x[1]),
            reverse=True
        )

        # Recommend reducing top 3 categories
        for category, cat_transactions in sorted_categories[:3]:
            total = sum(t['amount'] for t in cat_transactions)
            avg = total / len(cat_transactions) if cat_transactions else 0
            
            # Suggest 15% reduction
            potential_savings = total * 0.15
            
            insights.append(InsightDTO(
                type=InsightType.OPPORTUNITY.value,
                severity=InsightSeverity.INFO.value,
                category=category,
                message=f"Potential to save ${potential_savings:.2f}/month in {category}. Try 15% reduction.",
                current_amount=total,
                percentage_change=-15.0
            ))

        summary = self._generate_summary(insights)
        confidence = 0.80

        return insights, summary, confidence

    # Private helper methods

    def _group_by_category(
            self,
            transactions: List[Dict[str, Any]]
    ) -> Dict[str, List[Dict[str, Any]]]:
        """Group transactions by category"""
        grouped = {}
        for transaction in transactions:
            category = transaction.get('category', 'uncategorized').lower()
            if category not in grouped:
                grouped[category] = []
            grouped[category].append(transaction)
        return grouped

    def _analyze_category(
            self,
            category: str,
            transactions: List[Dict[str, Any]]
    ) -> List[InsightDTO]:
        """Analyze spending in a specific category"""
        insights = []
        amounts = [float(t.get('amount', 0)) for t in transactions]

        if not amounts:
            return insights

        # Calculate statistics
        mean = np.mean(amounts)
        std = np.std(amounts)
        current_total = sum(amounts)
        
        # Detect anomalies (2 std dev from mean)
        if std > 0:
            anomalies = [a for a in amounts if abs(a - mean) > self.ANOMALY_THRESHOLD * std]
            if anomalies:
                insights.append(InsightDTO(
                    type=InsightType.ANOMALY.value,
                    severity=InsightSeverity.ALERT.value,
                    category=category,
                    message=f"Unusual spike detected in {category}. "
                            f"Current: ${max(anomalies):.2f}, "
                            f"Typical: ${mean:.2f}",
                    current_amount=max(anomalies),
                    average_amount=mean,
                    percentage_change=((max(anomalies) - mean) / mean * 100) if mean > 0 else 0
                ))

        # Detect trends (increasing/decreasing)
        if len(transactions) >= 3:
            dates = [t.get('date', '') for t in transactions]
            sorted_txns = sorted(zip(dates, amounts), key=lambda x: x[0])
            recent_amounts = [a for _, a in sorted_txns[-3:]]
            
            if len(recent_amounts) >= 3:
                trend = self._calculate_trend(recent_amounts)
                if trend == TrendDirection.INCREASING.value:
                    percent_change = ((recent_amounts[-1] - recent_amounts[0]) / recent_amounts[0] * 100) \
                        if recent_amounts[0] > 0 else 0
                    
                    if percent_change > 20:  # 20% increase
                        insights.append(InsightDTO(
                            type=InsightType.TREND.value,
                            severity=InsightSeverity.WARNING.value,
                            category=category,
                            message=f"{category.capitalize()} spending is trending upward ({percent_change:.1f}%)",
                            trend=TrendDirection.INCREASING.value,
                            percentage_change=percent_change
                        ))

        return insights

    def _calculate_trend(self, values: List[float]) -> str:
        """Calculate trend direction for a series of values"""
        if len(values) < 2:
            return TrendDirection.STABLE.value

        changes = [values[i+1] - values[i] for i in range(len(values)-1)]
        avg_change = np.mean(changes)
        volatility = np.std(changes)

        if volatility > abs(avg_change):
            return TrendDirection.VOLATILE.value
        elif avg_change > 0.01:
            return TrendDirection.INCREASING.value
        elif avg_change < -0.01:
            return TrendDirection.DECREASING.value
        else:
            return TrendDirection.STABLE.value

    def _generate_summary(self, insights: List[InsightDTO]) -> str:
        """Generate natural language summary from insights"""
        if not insights:
            return "No significant financial patterns detected in your spending."

        # Count insights by severity
        critical = sum(1 for i in insights if i.severity == InsightSeverity.CRITICAL.value)
        alerts = sum(1 for i in insights if i.severity == InsightSeverity.ALERT.value)
        warnings = sum(1 for i in insights if i.severity == InsightSeverity.WARNING.value)

        if critical > 0:
            return f"⚠️ Critical: {critical} critical issue(s) found. Review immediately."
        elif alerts > 0:
            return f"⚠️ Alert: {alerts} alert(s) detected. Review spending in these areas."
        elif warnings > 0:
            return f"📊 {warnings} area(s) need attention. Consider reducing discretionary spending."
        else:
            return "✅ Your spending patterns look healthy. Keep monitoring for changes."
