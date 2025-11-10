def call() {
    echo "Building application..."
    sh '''
        if [ -f package.json ]; then
            echo "Building Node.js application..."
            npm install
            npm run build || echo "No build script found"
        elif [ -f pom.xml ]; then
            echo "Building Maven application..."
            mvn clean package -DskipTests
        elif [ -f requirements.txt ]; then
            echo "Installing Python dependencies..."
            pip install -r requirements.txt
        elif [ -f go.mod ]; then
            echo "Building Go application..."
            go build -o app
        else
            echo "No build configuration found"
        fi
    '''
}
