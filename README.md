# Vulnerable Spring Boot App

This is an intentionally vulnerable Java Spring Boot application for local SonarQube/SonarScanner testing.

Do not deploy this application to a real environment.

## Vulnerabilities Included

- SQL injection through string-concatenated SQL in `/login`
- Hardcoded credentials and API keys
- Sensitive data written to logs
- Path traversal in `/file`
- OS command injection in `/ping`
- Weak MD5 hashing in `/hash`
- AES ECB mode in `/encrypt`
- Predictable OTP generation with `java.util.Random`
- H2 console exposed through application configuration
- Stack traces exposed in error responses

## Run the App

```bash
mvn spring-boot:run
```

## Build for SonarQube

```bash
mvn clean verify
```

## Run SonarQube Scan

Start SonarQube locally on `http://localhost:9000`, then run:

```bash
mvn sonar:sonar -Dsonar.token=YOUR_TOKEN
```

You can also use the standalone scanner:

```bash
mvn clean verify
sonar-scanner -Dsonar.token=YOUR_TOKEN
```

## Example Requests

```bash
curl "http://localhost:8080/login?username=alice&password=password123"
curl "http://localhost:8080/file?name=../pom.xml"
curl "http://localhost:8080/ping?host=127.0.0.1"
curl -X POST "http://localhost:8080/hash" -H "Content-Type: application/json" -d '{"value":"hello"}'
curl -X POST "http://localhost:8080/encrypt" -H "Content-Type: text/plain" -d "secret"
curl "http://localhost:8080/otp"
```
# vulerablecode
