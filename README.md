# WordPress UI Automation Framework

Automated UI testing framework for [WordPress](https://wordpress.org/) built with Java and Selenium WebDriver.

The project focuses on automating core WordPress functionality, including authentication, content management, media uploads, comments moderation, and user role permissions.

## Tech Stack

- **Java** — primary programming language
- **Selenium WebDriver** — browser automation
- **TestNG** — test execution, grouping, and parallel execution
- **Maven** — build and dependency management
- **Allure** — test reporting
- **Log4j2** — logging
- **WebDriverManager** — local WebDriver management
- **Docker Compose** — Selenium Grid infrastructure
- **Selenium Grid** — remote browser execution
- **Jenkins** — CI/CD pipeline

## Features

- Page Object Model (POM) architecture
- Reusable page objects and common test utilities
- Explicit waits for reliable UI interactions
- ThreadLocal WebDriver management for parallel test execution
- Local and remote browser execution
- Chrome and Firefox support
- Headless browser mode
- TestNG test groups
- Test data cleanup after test execution
- Logging with Log4j2
- Allure test reports
- Jenkins pipeline integration

## Automated Test Coverage

The framework covers the following WordPress functionality:

| Test Area | Description |
|---|---|
| Authentication | Administrator login |
| Comments | Guest comments, moderation, approval, spam, and trash |
| Pages | Page creation, editing, publishing, draft status, and deletion |
| Posts | Post creation, editing, publishing, draft status, and deletion |
| Media | Media library access, file upload, and deletion |
| Menu Access | Menu visibility based on user roles and permissions |

## Test Groups

Tests are organized into functional TestNG groups:

| Group | Description |
|---|---|
| `smoke` | Basic application availability and login |
| `comments` | Comments functionality |
| `pages` | Pages management |
| `posts` | Posts management |
| `media` | Media library functionality |
| `menu` | Role-based menu access |

Test groups are configured in `src/test/resources/group-testng.xml`.

Parallel test execution is configured separately in `src/test/resources/parallel-testng.xml`.

## Project Structure

```text
WordPress-Framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/lysenka/framework/
│   │   │       ├── pages/          # Page Object classes
│   │   │       ├── util/           # Configuration utilities
│   │   │       └── webdriver/      # Browser management and factory
│   │   └── resources/
│   │       ├── log4j2.xml
│   │       └── project.properties
│   │
│   └── test/
│       ├── java/
│       │   └── com/lysenka/framework/tests/
│       │       ├── BaseTest.java
│       │       ├── LoginTest.java
│       │       ├── CommentsTest.java
│       │       ├── PagesTest.java
│       │       ├── PostsTest.java
│       │       ├── MediaUploadTest.java
│       │       └── MenuAccessTest.java
│       │
│       └── resources/
│           ├── test-data/
│           ├── group-testng.xml
│           └── parallel-testng.xml
│
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
└── README.md
```

## Selenium Grid

The project supports remote browser execution using Selenium Grid and Docker Compose.

The Grid configuration includes:

- Selenium Hub
- Chrome Node
- Firefox Node

Start the Selenium Grid infrastructure:

```bash
docker compose up -d
```

Check running containers:

```bash
docker compose ps
```

Open Selenium Grid UI:

```text
http://localhost:4444
```

Stop the infrastructure:

```bash
docker compose down
```

## Configuration

The framework uses `src/main/resources/project.properties` for configuration.

Main configuration parameters:

| Property | Description |
|---|---|
| `browserType` | Browser selection (`chrome` or `firefox`) |
| `executionMode` | Execution mode (`local` or `remote`) |
| `headless` | Enable or disable headless mode |
| `gridUrl` | Selenium Grid URL |

Browser configuration can be overridden using JVM system properties.

For example:

```bash
-DbrowserType=FIREFOX
```

Do not commit real credentials or other sensitive configuration values to the repository.

## Running Tests

Run the default test suite:

```bash
mvn clean test
```

Run tests in Chrome:

```bash
mvn clean test -DbrowserType=CHROME
```

Run tests in Firefox:

```bash
mvn clean test -DbrowserType=FIREFOX
```

For remote execution, make sure Selenium Grid is running and `executionMode` is set to `remote`.

## Jenkins Integration

The project includes a `Jenkinsfile` for automated test execution.

The pipeline supports:

- Maven-based test execution
- Browser selection through build parameters
- Selenium Grid integration
- Test result publishing
- Allure report generation

The browser can be selected when starting a parameterized Jenkins build.

## Test Reports

Test results are generated in the Maven `target` directory.

Allure results can be used to generate an HTML report:

```bash
mvn allure:report
```

The generated report is available in:

```text
target/site/allure-maven-plugin/
```

## Author

**Konstantin Lysenka**

Java / Automation QA Engineer

GitHub: [@eternalwrr](https://github.com/eternalwrr)