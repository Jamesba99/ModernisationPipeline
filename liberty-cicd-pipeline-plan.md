# Replatforming WAS to WebSphere Liberty - GitHub Actions CI/CD Pipeline Plan

## Top-Level Overview

This plan defines the architecture, design, and step-by-step setup for a robust DevSecOps CI/CD pipeline using **GitHub Actions** tailored for modernizing and replatforming WebSphere Application Server (WAS) workloads to **IBM WebSphere Liberty / Open Liberty**.

The pipeline automates:
1. Java compilation & packaging (Maven).
2. Automated testing (Unit & Liberty Integration Tests using Liberty Maven Plugin / MicroShed / Testcontainers).
3. Multi-layer Vulnerability & Security Testing:
   - **Static Application Security Testing (SAST)**: GitHub CodeQL for code-level vulnerabilities and CWE patterns.
   - **Software Composition Analysis (SCA) / Dependency Vulnerability Scanning**: OWASP Dependency-Check / Mend / GitHub Dependency Review to detect known CVEs in third-party libraries.
   - **Secret Scanning**: Gitleaks / TruffleHog to prevent committed credentials/tokens.
   - **Container Vulnerability Scanning**: Aqua Trivy / Grype scanner checking OS packages and Liberty runtime image layers with automated SARIF report uploads to GitHub Security tab.
   - **Dynamic / Runtime API Security Testing (DAST)** (Optional Gate): OWASP ZAP baseline scan against the running Liberty instance.
4. Containerization using Red Hat UBI / IBM WebSphere Liberty base images complying with security policies.
5. Container registry publishing with vulnerability gate enforcement.

---

## Sub-Tasks

### Sub-Task 1: Repository Structure & Maven Configuration Baseline
- **Intent**: Establish the standard Maven project layout with Liberty Maven Plugin (`liberty-maven-plugin`) configuration, test harness, and security plugins.
- **Expected Outcomes**:
  - Valid `pom.xml` configured with `liberty-maven-plugin` and test dependencies (JUnit 5, MicroProfile/Jakarta EE APIs).
  - OWASP Dependency-Check Maven plugin integrated for local/build-time vulnerability verification.
  - Standard Liberty server configuration directory (`src/main/liberty/config/server.xml`).
  - `.gitignore` configured to exclude build targets, local server instances, and secrets.
- **Todo List**:
  1. Configure `pom.xml` with Liberty Maven plugin goals (`create`, `install-apps`, `test-start`, `test-stop`).
  2. Add `dependency-check-maven` plugin profile configured with CVSS failure thresholds (e.g., fail on CVSS >= 7.0).
  3. Define `src/main/liberty/config/server.xml` template with necessary Liberty features (e.g., `microProfile-6.0` or `jakartaee-10.0`).
  4. Ensure `.gitignore` ignores sensitive files and build artifacts (`target/`, `wlp/`, `.env`).
- **Relevant Context**: [`pom.xml`](pom.xml), [`src/main/liberty/config/server.xml`](src/main/liberty/config/server.xml), [`.gitignore`](.gitignore).
- **Status**: `[ ] pending`

---

### Sub-Task 2: Build & Automated Testing Workflow
- **Intent**: Automate the continuous integration step to compile code, run unit tests, and execute integration tests against a running Liberty instance.
- **Expected Outcomes**:
  - GitHub Actions workflow file `.github/workflows/ci.yml` triggered on push and pull requests.
  - Maven caching implemented to accelerate build times.
  - Automated execution of unit tests (`mvn test`) and integration tests (`mvn verify`).
  - Test reports and artifacts published in GitHub Actions run summary.
- **Todo List**:
  1. Create `.github/workflows/ci.yml` with matrix/step execution for Java (Temurin/IBM Semeru 17/21).
  2. Implement dependency caching (`actions/setup-java` built-in cache).
  3. Configure step for `mvn clean verify` executing Liberty integration tests.
  4. Add step using `actions/upload-artifact` to archive surefire/failsafe test reports.
- **Relevant Context**: [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
- **Status**: `[ ] pending`

---

### Sub-Task 3: Comprehensive Vulnerability & Security Testing Suite (DevSecOps)
- **Intent**: Implement multi-tier automated vulnerability testing gates covering source code, dependencies, secrets, and running endpoints.
- **Expected Outcomes**:
  - SAST workflow (`.github/workflows/codeql.yml`) scanning Java source code on PR and schedule.
  - SCA / Dependency vulnerability scanning running `dependency-check` or `dependency-review-action` with failure on CVSS >= 7.0.
  - Secret scanning step (`gitleaks-action`) blocking commits with exposed credentials or API keys.
  - Dynamic Application Security Testing (DAST) baseline job using OWASP ZAP scanning the application endpoints on the test Liberty server.
  - Security findings uploaded as SARIF reports to GitHub Security Alerts tab.
- **Todo List**:
  1. Create `.github/workflows/codeql.yml` for Java CodeQL analysis.
  2. Add dependency vulnerability scanning job with SARIF reporting.
  3. Add Gitleaks step in `.github/workflows/ci.yml` for credential leakage prevention.
  4. Configure OWASP ZAP baseline scan job triggered against the running Liberty instance.
- **Relevant Context**: [`.github/workflows/codeql.yml`](.github/workflows/codeql.yml), [`.github/workflows/security-scan.yml`](.github/workflows/security-scan.yml), [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
- **Status**: `[ ] pending`

---

### Sub-Task 4: Containerization & Container Vulnerability Scanning
- **Intent**: Create a production-ready, secure Dockerfile and execute container vulnerability scans on the packaged image.
- **Expected Outcomes**:
  - Hardened `Dockerfile` using compliant minimal Liberty base image (Red Hat UBI / IBM WebSphere Liberty minimal).
  - Non-root user execution (`USER 1001`) with read-only root filesystem compatibility.
  - Trivy container vulnerability scan step executing in CI, failing on CRITICAL/HIGH vulnerabilities, and uploading SARIF results.
- **Todo List**:
  1. Create `Dockerfile` with multi-stage build, non-root user (`USER 1001`), and `RUN configure.sh`.
  2. Create container build job in `.github/workflows/container-build.yml`.
  3. Add `aquasecurity/trivy-action` step to scan image layers for CVEs and misconfigurations.
  4. Configure upload of Trivy SARIF report to GitHub Security dashboard (`github/codeql-action/upload-sarif`).
- **Relevant Context**: [`Dockerfile`](Dockerfile), [`.github/workflows/container-build.yml`](.github/workflows/container-build.yml).
- **Status**: `[ ] pending`

---

### Sub-Task 5: Artifact & Image Registry Publishing (CD)
- **Intent**: Publish verified, scanned container images to a secure container registry (GHCR or IBM Cloud Container Registry).
- **Expected Outcomes**:
  - Image publish blocked if any vulnerability scan fails the threshold.
  - Automated container tagging (commit SHA, semver release tags).
  - Secure authentication using repository secrets / GitHub token without hardcoded credentials.
- **Todo List**:
  1. Add registry login step (`docker/login-action`) using `GITHUB_TOKEN` or configured secrets.
  2. Add build and push step (`docker/build-push-action`) dependent on successful test and vulnerability scanning jobs.
  3. Output release metadata / deployment summary.
- **Relevant Context**: [`.github/workflows/container-build.yml`](.github/workflows/container-build.yml).
- **Status**: `[ ] pending`
