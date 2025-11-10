def call() {
    echo "Running unit tests"
    sh """
        if [ -f gradlew ]; then
            ./gradlew test
        elif [ -f pom.xml ]; then
            mvn test
        else
            echo "No build tool found, skipping tests"
        fi
    """
}

