def call() {
    echo "Running unit tests..."
    sh '''
        if [ -f package.json ]; then
            echo "Running npm tests..."
            npm test
        elif [ -f pom.xml ]; then
            echo "Running Maven tests..."
            mvn test
        elif [ -f requirements.txt ]; then
            echo "Running Python tests..."
            python -m pytest
        else
            echo "No test configuration found, skipping tests"
        fi
    '''
}
