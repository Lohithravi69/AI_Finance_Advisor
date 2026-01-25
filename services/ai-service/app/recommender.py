"""
AI Recommendation Engine
Generates personalized financial recommendations
"""
import logging
from typing import Dict, List, Any, Tuple
from datetime import datetime
from app.dto import (
    RecommendationDTO,
    RecommendationPriority,
    AdviceType
)

logger = logging.getLogger(__name__)


class RecommendationEngine:
    """
    Recommendation engine for personalized financial advice
    Generates actionable recommendations based on user's financial profile
    """

    def __init__(self):
        self.logger = logging.getLogger(__name__)

    def generate_savings_optimization(
            self,
            context: Dict[str, Any]
    ) -> Tuple[List[Dict[str, Any]], Dict[str, Any], float]:
        """
        Generate savings optimization recommendations
        
        Context should contain:
        - monthly_income: float
        - current_savings_rate: float
        - financial_goals: List[str]
        
        Returns:
            Tuple of (recommendations, estimated_impact, confidence)
        """
        recommendations = []
        
        monthly_income = context.get('monthly_income', 0)
        current_savings_rate = context.get('current_savings_rate', 0)
        financial_goals = context.get('financial_goals', [])

        # Recommendation 1: Emergency Fund
        if 'emergency_fund' in financial_goals:
            emergency_fund_target = monthly_income * 4  # 4 months of expenses
            
            rec = RecommendationDTO(
                priority=RecommendationPriority.HIGH.value,
                category="emergency_fund",
                title="Build Emergency Fund",
                description=f"Establish an emergency fund of ${emergency_fund_target:.2f} "
                           f"(4 months of expenses) to handle unexpected costs",
                current_status="In Progress",
                target_amount=emergency_fund_target,
                current_amount=emergency_fund_target * 0.4,  # Assume 40% achieved
                action_items=[
                    "Set up automatic transfer of $300/month to savings",
                    "Use high-yield savings account (4.5% APY)",
                    "Keep funds liquid and accessible"
                ],
                timeframe="6 months"
            )
            recommendations.append(rec.to_dict())

        # Recommendation 2: Retirement
        if 'retirement' in financial_goals:
            current_retirement_contribution = monthly_income * 0.06  # Assume 6% baseline
            recommended_contribution = monthly_income * 0.15  # Recommend 15%
            additional_monthly = recommended_contribution - current_retirement_contribution
            
            rec = RecommendationDTO(
                priority=RecommendationPriority.HIGH.value,
                category="retirement",
                title="Increase Retirement Contributions",
                description="Increase retirement savings to ensure comfortable retirement",
                current_status="In Progress",
                target_amount=recommended_contribution * 12,  # Annual
                current_amount=current_retirement_contribution * 12,
                action_items=[
                    f"Increase 401(k) from 6% to 15% (+${additional_monthly:.2f}/month)",
                    "Max out employer match immediately",
                    "Consider Roth IRA for additional tax benefits"
                ],
                timeframe="3 months"
            )
            recommendations.append(rec.to_dict())

        # Recommendation 3: Debt Reduction (if needed)
        rec = RecommendationDTO(
            priority=RecommendationPriority.MEDIUM.value,
            category="debt_reduction",
            title="Create Debt Payoff Plan",
            description="Develop a strategic plan to eliminate high-interest debt",
            current_status="Not Started",
            action_items=[
                "List all debts with interest rates",
                "Use debt snowball or avalanche method",
                "Consider balance transfer for credit card debt"
            ],
            timeframe="12 months"
        )
        recommendations.append(rec.to_dict())

        # Calculate impact
        annual_impact = additional_monthly * 12 if 'retirement' in financial_goals else 0
        estimated_impact = {
            'monthlySavingsIncrease': additional_monthly,
            'annualSavingsIncrease': annual_impact,
            'projectedNetWorth10Years': (monthly_income * 0.20 * 120)  # 20% savings * 10 years
        }

        confidence = 0.88

        return recommendations, estimated_impact, confidence

    def generate_debt_management(
            self,
            context: Dict[str, Any]
    ) -> Tuple[List[Dict[str, Any]], Dict[str, Any], float]:
        """
        Generate debt management recommendations
        
        Context should contain:
        - total_debt: float
        - monthly_payment: float
        - interest_rate: float
        """
        recommendations = []
        
        total_debt = context.get('total_debt', 0)
        monthly_payment = context.get('monthly_payment', 0)
        interest_rate = context.get('interest_rate', 0)

        # Calculate payoff timeline
        if monthly_payment > 0 and total_debt > 0:
            # Simplified payoff calculation
            months_to_payoff = total_debt / (monthly_payment - (total_debt * interest_rate / 100 / 12))
            months_to_payoff = max(1, months_to_payoff)  # Avoid division errors
        else:
            months_to_payoff = 0

        # Recommendation 1: Accelerated payoff
        rec1 = RecommendationDTO(
            priority=RecommendationPriority.CRITICAL.value,
            category="debt_payoff",
            title="Accelerate Debt Payoff",
            description=f"At current rate, payoff in {months_to_payoff:.0f} months. "
                       f"Consider increasing payments to reduce interest.",
            current_status="In Progress",
            target_amount=0,  # Full payoff
            current_amount=total_debt,
            action_items=[
                f"Increase monthly payment from ${monthly_payment:.2f} to ${monthly_payment * 1.5:.2f}",
                f"This saves ${interest_rate * 100:.2f} in interest",
                "Redirect bonuses/tax refunds to debt"
            ],
            timeframe=f"{max(6, int(months_to_payoff/2))} months"
        )
        recommendations.append(rec1.to_dict())

        # Recommendation 2: Balance transfer (for credit cards)
        if interest_rate > 15:
            rec2 = RecommendationDTO(
                priority=RecommendationPriority.HIGH.value,
                category="balance_transfer",
                title="Consider Balance Transfer",
                description="Move debt to 0% APR balance transfer card",
                current_status="Not Started",
                action_items=[
                    "Search for 0% APR balance transfer offers (12-21 months)",
                    "Compare transfer fees vs interest savings",
                    "Apply for card and transfer balance immediately"
                ],
                timeframe="1 month"
            )
            recommendations.append(rec2.to_dict())

        estimated_impact = {
            'interestSavings': total_debt * (interest_rate / 100) * (months_to_payoff / 12),
            'payoffTimeReduction': f"{int(months_to_payoff/2)} months",
            'creditScoreImprovement': "50-100 points"
        }

        confidence = 0.82

        return recommendations, estimated_impact, confidence

    def generate_expense_reduction(
            self,
            context: Dict[str, Any]
    ) -> Tuple[List[Dict[str, Any]], Dict[str, Any], float]:
        """
        Generate expense reduction recommendations
        
        Context should contain:
        - category_spending: Dict[str, float]
        - monthly_income: float
        """
        recommendations = []
        
        category_spending = context.get('category_spending', {})
        monthly_income = context.get('monthly_income', 1000)

        # Analyze each category for reduction opportunities
        priority_order = [
            ('subscriptions', 'Review Subscriptions'),
            ('dining', 'Reduce Dining Out'),
            ('entertainment', 'Entertainment Budget'),
            ('shopping', 'Smart Shopping')
        ]

        for category, title in priority_order:
            amount = category_spending.get(category, 0)
            if amount > 0:
                # Recommend 20% reduction
                savings = amount * 0.20
                
                rec = RecommendationDTO(
                    priority=RecommendationPriority.MEDIUM.value,
                    category=category,
                    title=title,
                    description=f"Reduce {category} spending by 20% (save ${savings:.2f}/month)",
                    current_status="Not Started",
                    current_amount=amount,
                    target_amount=amount * 0.80,
                    action_items=[
                        f"Cancel unused subscriptions" if category == 'subscriptions' else 
                        f"Cook at home 2x/week instead of dining out",
                        "Set monthly budget for this category",
                        "Track spending weekly"
                    ],
                    timeframe="1 month"
                )
                recommendations.append(rec.to_dict())

        total_potential_savings = sum(
            category_spending.get(cat, 0) * 0.20 for cat, _ in priority_order
        )

        estimated_impact = {
            'monthlySavingsIncrease': total_potential_savings,
            'annualSavingsIncrease': total_potential_savings * 12,
            'savingsAsPercentOfIncome': (total_potential_savings / monthly_income * 100) if monthly_income > 0 else 0
        }

        confidence = 0.79

        return recommendations, estimated_impact, confidence
