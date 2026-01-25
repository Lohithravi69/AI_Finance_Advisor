"""
API Routes for AI Service
Implements endpoints defined in API contract (docs/api-spec.yaml)
"""
import uuid
import logging
from datetime import datetime
from flask import Blueprint, request, jsonify
from app.analyzer import FinancialAnalyzer
from app.recommender import RecommendationEngine
from app.dto import (
    AnalysisType,
    AdviceType,
    ErrorDTO,
    AnalysisResponseDTO,
    AdviceResponseDTO
)

logger = logging.getLogger(__name__)

# Create blueprints
ai_bp = Blueprint('ai', __name__)
health_bp = Blueprint('health', __name__)

# Initialize analyzers
analyzer = FinancialAnalyzer()
recommender = RecommendationEngine()


# ============================================================
# AI ENDPOINTS
# ============================================================

@ai_bp.route('/analyze', methods=['POST'])
def analyze():
    """
    POST /ai/analyze - Perform financial analysis
    Implements API contract endpoint
    """
    try:
        data = request.get_json()
        
        if not data:
            return jsonify(ErrorDTO.create('BAD_REQUEST', 'Request body is required').to_dict()), 400

        # Validate request
        analysis_type = data.get('analysisType')
        if not analysis_type:
            return jsonify(ErrorDTO.create('VALIDATION_ERROR', 'analysisType is required').to_dict()), 422

        # Mock transaction data (in real scenario, fetch from Finance Service)
        transactions = _get_mock_transactions(analysis_type)

        # Perform analysis
        if analysis_type == AnalysisType.SPENDING_PATTERN.value:
            insights, summary, confidence = analyzer.analyze_spending_patterns(
                transactions,
                period=data.get('period', 'month'),
                include_categories=data.get('includeCategories')
            )
        elif analysis_type == AnalysisType.BUDGET_HEALTH.value:
            insights, summary, confidence = analyzer.analyze_budget_health(
                transactions,
                monthly_income=data.get('context', {}).get('monthlyIncome')
            )
        elif analysis_type == AnalysisType.SAVINGS_POTENTIAL.value:
            insights, summary, confidence = analyzer.identify_savings_potential(transactions)
        else:
            return jsonify(ErrorDTO.create('BAD_REQUEST', f'Unknown analysis type: {analysis_type}').to_dict()), 400

        # Format response
        response = AnalysisResponseDTO(
            analysis_id=f"ana_{uuid.uuid4().hex[:6]}",
            timestamp=datetime.now().isoformat() + 'Z',
            analysis_type=analysis_type,
            insights=[insight.to_dict() for insight in insights],
            summary=summary,
            confidence=confidence
        )

        logger.info(f"Analysis completed: {response.analysis_id}")
        return jsonify(response.to_dict()), 200

    except Exception as e:
        logger.error(f"Analysis error: {str(e)}", exc_info=True)
        return jsonify(ErrorDTO.create('INTERNAL_SERVER_ERROR', str(e)).to_dict()), 500


@ai_bp.route('/advice', methods=['POST'])
def advice():
    """
    POST /ai/advice - Generate financial advice
    Implements API contract endpoint
    """
    try:
        data = request.get_json()
        
        if not data:
            return jsonify(ErrorDTO.create('BAD_REQUEST', 'Request body is required').to_dict()), 400

        # Validate request
        advice_type = data.get('adviceType')
        if not advice_type:
            return jsonify(ErrorDTO.create('VALIDATION_ERROR', 'adviceType is required').to_dict()), 422

        context = data.get('context', {})

        # Generate recommendations
        if advice_type == AdviceType.SAVINGS_OPTIMIZATION.value:
            recommendations, estimated_impact, confidence = recommender.generate_savings_optimization(context)
        elif advice_type == AdviceType.DEBT_MANAGEMENT.value:
            recommendations, estimated_impact, confidence = recommender.generate_debt_management(context)
        elif advice_type == AdviceType.EXPENSE_REDUCTION.value:
            recommendations, estimated_impact, confidence = recommender.generate_expense_reduction(context)
        else:
            return jsonify(ErrorDTO.create('BAD_REQUEST', f'Unknown advice type: {advice_type}').to_dict()), 400

        # Format response
        response = AdviceResponseDTO(
            advice_id=f"adv_{uuid.uuid4().hex[:6]}",
            timestamp=datetime.now().isoformat() + 'Z',
            advice_type=advice_type,
            recommendations=recommendations,
            estimated_impact=estimated_impact,
            confidence=confidence
        )

        logger.info(f"Advice generated: {response.advice_id}")
        return jsonify(response.to_dict()), 200

    except Exception as e:
        logger.error(f"Advice error: {str(e)}", exc_info=True)
        return jsonify(ErrorDTO.create('INTERNAL_SERVER_ERROR', str(e)).to_dict()), 500


@ai_bp.route('/chat', methods=['POST'])
def chat():
    """
    POST /ai/chat - Interactive chat with AI advisor
    Implements API contract endpoint
    """
    try:
        data = request.get_json()
        
        if not data:
            return jsonify(ErrorDTO.create('BAD_REQUEST', 'Request body is required').to_dict()), 400

        message = data.get('message')
        if not message:
            return jsonify(ErrorDTO.create('VALIDATION_ERROR', 'message is required').to_dict()), 422

        conversation_id = data.get('conversationId', f"conv_{uuid.uuid4().hex[:6]}")

        # Generate AI response (simplified)
        response = {
            'conversationId': conversation_id,
            'response': _generate_chat_response(message),
            'suggestedActions': ['Review dining expenses', 'Increase savings rate', 'Create budget'],
            'timestamp': datetime.now().isoformat() + 'Z'
        }

        logger.info(f"Chat response generated for conversation: {conversation_id}")
        return jsonify(response), 200

    except Exception as e:
        logger.error(f"Chat error: {str(e)}", exc_info=True)
        return jsonify(ErrorDTO.create('INTERNAL_SERVER_ERROR', str(e)).to_dict()), 500


# ============================================================
# HEALTH CHECK ENDPOINTS
# ============================================================

@health_bp.route('', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'UP',
        'service': 'ai-service',
        'version': '1.0.0',
        'timestamp': datetime.now().isoformat() + 'Z'
    }), 200


@health_bp.route('/ready', methods=['GET'])
def readiness():
    """Readiness probe"""
    return jsonify({'status': 'READY'}), 200


@health_bp.route('/live', methods=['GET'])
def liveness():
    """Liveness probe"""
    return jsonify({'status': 'ALIVE'}), 200


# ============================================================
# HELPER FUNCTIONS
# ============================================================

def _get_mock_transactions(analysis_type: str) -> list:
    """
    Get mock transaction data
    In production, this would fetch from Finance Service
    """
    import random
    from datetime import datetime, timedelta

    transactions = []
    
    # Generate sample transactions for last 30 days
    for i in range(30):
        date = (datetime.now() - timedelta(days=i)).isoformat()
        
        if analysis_type == AnalysisType.SPENDING_PATTERN.value:
            categories = ['groceries', 'dining', 'entertainment', 'shopping', 'utilities']
            for _ in range(random.randint(1, 3)):
                transactions.append({
                    'id': f'txn_{uuid.uuid4().hex[:6]}',
                    'type': 'expense',
                    'category': random.choice(categories),
                    'amount': round(random.uniform(10, 200), 2),
                    'date': date,
                    'currency': 'USD'
                })
        
        # Add income
        if i % 30 == 0:  # Monthly income
            transactions.append({
                'id': f'txn_{uuid.uuid4().hex[:6]}',
                'type': 'income',
                'category': 'salary',
                'amount': 5000,
                'date': date,
                'currency': 'USD'
            })

    return transactions


def _generate_chat_response(message: str) -> str:
    """
    Generate AI chat response
    In production, this would use actual NLP model
    """
    message_lower = message.lower()
    
    if 'save' in message_lower or 'savings' in message_lower:
        return "Based on your financial profile, I'd recommend increasing your savings rate to 20% of your income. " \
               "Start by setting up automatic transfers to a high-yield savings account."
    elif 'spend' in message_lower or 'expense' in message_lower:
        return "Your spending has been trending upward in dining and entertainment. " \
               "Consider reducing discretionary expenses by 15% to reach your savings goals."
    elif 'debt' in message_lower:
        return "Let's focus on paying down your debt strategically. " \
               "I recommend the avalanche method - pay minimums on all debts, " \
               "then put extra money toward the highest interest rate debt first."
    elif 'budget' in message_lower:
        return "Here's my recommendation: Allocate 50% of income to needs, 30% to wants, and 20% to savings. " \
               "This can be adjusted based on your specific goals and situation."
    else:
        return "I'm here to help you optimize your finances. " \
               "Ask me about saving strategies, expense reduction, debt management, or budgeting tips!"
