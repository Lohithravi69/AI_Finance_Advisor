"""
Flask application factory and initialization
"""
import logging
from flask import Flask
from flask_cors import CORS
from config import get_config

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


def create_app(config=None):
    """
    Application factory function
    Creates and configures Flask app
    """
    app = Flask(__name__)

    # Load configuration
    if config is None:
        config = get_config()
    app.config.from_object(config)

    # Configure CORS
    CORS(app, resources={
        r"/api/v1/*": {
            "origins": ["http://localhost:3000", "http://localhost:8080"],
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Content-Type", "Authorization"]
        }
    })

    # Register blueprints
    from app.routes import ai_bp, health_bp
    app.register_blueprint(ai_bp, url_prefix='/api/v1/ai')
    app.register_blueprint(health_bp, url_prefix='/api/v1/health')

    # Error handlers
    @app.errorhandler(400)
    def bad_request(error):
        return {'success': False, 'error': {'code': 'BAD_REQUEST', 'message': str(error)}}, 400

    @app.errorhandler(401)
    def unauthorized(error):
        return {'success': False, 'error': {'code': 'UNAUTHORIZED', 'message': str(error)}}, 401

    @app.errorhandler(404)
    def not_found(error):
        return {'success': False, 'error': {'code': 'NOT_FOUND', 'message': 'Resource not found'}}, 404

    @app.errorhandler(500)
    def internal_error(error):
        logger.error(f"Internal server error: {str(error)}")
        return {'success': False, 'error': {'code': 'INTERNAL_SERVER_ERROR', 'message': 'An unexpected error occurred'}}, 500

    logger.info(f"AI Service initialized with config: {config.__class__.__name__}")

    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=8082, debug=True)
