#!/usr/bin/env python
"""
AI Service Entry Point
Starts the Flask application
"""
import os
from app import create_app

if __name__ == '__main__':
    # Set environment
    os.environ.setdefault('FLASK_ENV', 'development')
    os.environ.setdefault('FLASK_APP', 'wsgi.py')
    port = int(os.environ.get('AI_SERVICE_PORT', '8001'))

    # Create and run app
    app = create_app()
    app.run(
        host='0.0.0.0',
        port=port,
        debug=os.environ.get('FLASK_ENV') == 'development'
    )
