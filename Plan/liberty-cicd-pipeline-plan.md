# Replatforming WAS to WebSphere Liberty - GitHub Actions CI/CD Pipeline Plan

## Top-Level Overview

This plan defines the end-to-end architecture, migration approach, and step-by-step setup for replatforming a WebSphere Application Server (WAS Traditional) workload to **IBM WebSphere Liberty / Open Liberty** with a comprehensive **DevSecOps CI/CD pipeline in GitHub Actions**.

### Migration & Replatforming Sequence
1. **Source Ingestion First**: Import the WAS application code first to ground feature discovery, inspect proprietary dependencies, and generate accurate Liberty configuration.
2. **Code & Dependency Assessment**: Modernize WAS-specific APIs (`com.ibm.websphere.*`), outdated `web.xml` / `ejb-jar.xml` descriptors, and build dependencies to Jakarta EE / MicroProfile standards.
3. **Liberty Configuration Baseline**: Configure `pom.xml` with `liberty-maven-plugin` and generate minimal `server.xml`.
4. **DevSecOps CI/CD Pipeline**:
   - Automated compilation and unit testing (`mvn test`).
   - Liberty integration testing against live local Liberty instances (`mvn verify`).
   - Multi-tier vulnerability scanning: SAST (CodeQL), SCA (OWASP Dependency-Check / Mend), Secrets (Gitleaks), DAST (OWASP ZAP), and Container Scanning (Trivy).
   - Hardened container build using minimal Red Hat UBI / Liberty base images running as non-root (`USER 1001`).
   - Secure container publishing to image registry (GHCR / ICR) guarded by vulnerability thresholds.

---

## Sub-Tasks

### Sub-Task 1: WAS Application Ingestion & Migration Assessment
- **Intent**: Bring existing WAS application source into the repository, scan for proprietary WAS APIs, and assess needed Liberty features and dependencies.
- **Expected Outcomes**:
  - Application source files, deployment descriptors, and build scripts imported into the workspace.
  - Identification of proprietary WAS packages, JNDI lookups, datasources, and runtime libraries.
  - Generated inventory of target Liberty features required for runtime execution.
- **Todo List**:
  1. Place/clone WAS source code into project structure.
  2. Inspect existing build file / dependencies for WAS runtime jars.
  3. Run static assessment to catalogue Java EE / Jakarta EE APIs and proprietary IBM imports.
  4. Document target Liberty feature list (e.g. `servlet-6.0`, `cdi-4.0`, `jdbc-4.3`).
- **Relevant Context**: Workspace root, application source directory, `web.xml`, `pom.xml`.
- **Status**: `[ ] pending`

---

### Sub-Task 2: Repository Structure & Liberty Maven Baseline
- **Intent**: Configure Maven build with `liberty-maven-plugin`, Liberty runtime configuration, and security scanning plugins.
- **Expected Outcomes**:
  - Valid `pom.xml` configured with `liberty-maven-plugin` goals (`create`, `install-apps`, `test-start`, `test-stop`).
  - Standard Liberty server configuration directory (`src/main/liberty/config/server.xml`) tailored to assessed features.
  - `dependency-check-maven` plugin profile configured with CVSS failure threshold (CVSS >= 7.0).
  - `.gitignore` configured to exclude build targets, Liberty server runtime files (`wlp/`), and secrets.
- **Todo List**:
  1. Configure `pom.xml` with modern Jakarta EE / MicroProfile coordinates and `liberty-maven-plugin`.
  2. Add `dependency-check-maven` profile for local build vulnerability scanning.
  3. Create `src/main/liberty/config/server.xml` with discovered features, HTTP endpoints, and datasource templates.
  4. Ensure `.gitignore` protects against committing build outputs and credentials.
- **Relevant Context**: [`pom.xml`](pom.xml), [`src/main/liberty/config/server.xml`](src/main/liberty/config/server.xml), [`.gitignore`](.gitignore).
- **Status**: `[ ] pending`

---

### Sub-Task 3: Build & Automated Testing Workflow
- **Intent**: Automate continuous integration to compile code, run unit tests, and execute Liberty integration tests in GitHub Actions.
- **Expected Outcomes**:
  - GitHub Actions workflow `.github/workflows/ci.yml` triggered on push and pull requests.
  - Maven dependency caching configured for fast CI runs.
  - Automated unit test execution (`mvn test`) and integration tests (`mvn verify`) running against Liberty test server.
  - Test reports and logs published in GitHub Actions run artifacts.
- **Todo List**:
  1. Create `.github/workflows/ci.yml` with Java 17/21 runtime setup.
  2. Implement Maven caching via `actions/setup-java`.
  3. Add step for `mvn clean verify` executing Liberty integration tests.
  4. Add `actions/upload-artifact` step to archive surefire and failsafe test reports.
- **Relevant Context**: [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
- **Status**: `[ ] pending`

---

### Sub-Task 4: Comprehensive Multi-Tier Vulnerability & Security Testing Suite
- **Intent**: Implement automated DevSecOps gates across static code, third-party dependencies, secrets, and live application endpoints.
- **Expected Outcomes**:
  - SAST workflow (`.github/workflows/codeql.yml`) scanning Java source code for vulnerabilities and CWE flaws.
  - Dependency vulnerability scanning (OWASP Dependency-Check / GitHub Dependency Review) failing on CVSS >= 7.0.
  - Secret scanning step (`gitleaks-action`) blocking accidental credential commits.
  - DAST baseline scan job (OWASP ZAP) executing against the active Liberty server endpoint.
  - Security reports uploaded to GitHub Security tab via SARIF.
- **Todo List**:
  1. Create `.github/workflows/codeql.yml` for Java CodeQL SAST scanning.
  2. Add dependency vulnerability scanning job to CI with SARIF report export.
  3. Add Gitleaks secret leak detection step in CI pipeline.
  4. Configure OWASP ZAP baseline scan step running against the live test Liberty instance.
- **Relevant Context**: [`.github/workflows/codeql.yml`](.github/workflows/codeql.yml), [`.github/workflows/security-scan.yml`](.github/workflows/security-scan.yml), [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
- **Status**: `[ ] pending`

---

### Sub-Task 5: Hardened Containerization & Container Image Vulnerability Scanning
- **Intent**: Package the Liberty application into a secure container image and verify layer security with container vulnerability scanning.
- **Expected Outcomes**:
  - Production `Dockerfile` using minimal Red Hat UBI / IBM WebSphere Liberty base image.
  - Non-root user execution (`USER 1001`), read-only root filesystem support, and `RUN configure.sh` optimization.
  - Container vulnerability scan (Aqua Trivy) executing in CI, blocking on CRITICAL/HIGH vulnerabilities, and uploading SARIF results.
- **Todo List**:
  1. Create hardened multi-stage `Dockerfile` with non-root user (`USER 1001`).
  2. Create container build job in `.github/workflows/container-build.yml`.
  3. Add `aquasecurity/trivy-action` step to scan image layers for CVEs and misconfigurations.
  4. Configure SARIF upload to GitHub Security dashboard.
- **Relevant Context**: [`Dockerfile`](Dockerfile), [`.github/workflows/container-build.yml`](.github/workflows/container-build.yml).
- **Status**: `[ ] pending`

---

### Sub-Task 6: Image Registry Publishing & Release Gates (CD)
- **Intent**: Publish verified and scanned container images to a secure container registry (GHCR / ICR).
- **Expected Outcomes**:
  - Image publication gated on passing all build, test, and vulnerability scanning checks.
  - Automated container tagging (commit SHA, semver release tags).
  - Secure authentication using repository secrets / GitHub token without hardcoded credentials.
- **Todo List**:
  1. Add registry login step (`docker/login-action`) using `GITHUB_TOKEN` or configured secrets.
  2. Add `docker/build-push-action` dependent on successful security and test jobs.
  3. Generate release metadata and deployment summary.
- **Relevant Context**: [`.github/workflows/container-build.yml`](.github/workflows/container-build.yml).
- **Status**: `[ ] pending`
