# Project Implementation Plan: JobRunr Weather Job Scheduler

## Project Overview

**Goal:** Learn and understand JobRunr by building a small Spring-based application that uses JobRunr to enqueue and schedule background jobs which call the OpenWeather API to fetch weather data.

**Tech Stack:**
- Java 21
- Spring 4
- Maven
- JobRunr
- H2 Database
- OpenWeather API

**Core Features:**
1. An API endpoint to enqueue a one-time background job that immediately fetches current weather data.
2. An API endpoint to create a recurring scheduled job that fetches weather data at a fixed interval.
3. Persist job/weather data using H2.

---

## Phase 1: Project Setup & Environment Preparation

**Objective:**
Set up the base Spring + Maven project skeleton with all necessary dependencies and verify the application boots successfully.

**Output:**
- A runnable Spring Boot/Spring project with Maven build configured (`pom.xml` with Spring, JobRunr, H2, and HTTP client dependencies).
- Application configuration files (`application.yml`/`application.properties`) with placeholders for OpenWeather API key and H2 settings.
- Project successfully starts with no business logic implemented.
- Basic smoke test confirming the Spring application context loads.

---

## Phase 2: JobRunr & H2 Integration

**Objective:**
Integrate JobRunr into the Spring application and connect it to H2 for job storage, and verify JobRunr's dashboard/backend is operational.

**Output:**
- JobRunr configured to use H2 as its storage provider.
- JobRunr dashboard accessible and showing an empty job queue.
- Confirmation (via manual test job or dashboard) that JobRunr can store and process jobs against H2.

---

## Phase 3: OpenWeather API Client

**Objective:**
Build a simple internal service/client responsible for calling the OpenWeather API and mapping the response into an internal model.

**Output:**
- A weather client component capable of calling OpenWeather and returning parsed weather data.
- Configuration for API key and base URL externalized properly.
- Unit tests covering the client logic (using mocked HTTP responses), verifying correct parsing and error handling for failed/invalid responses.

---

## Phase 4: On-Demand Job Enqueue API

**Objective:**
Implement the first REST API that allows a client to trigger an immediate, one-time background job via JobRunr to fetch current weather data.

**Output:**
- REST endpoint that enqueues a JobRunr job on request.
- JobRunr job implementation that calls the weather client and persists/logs the result.
- Job execution visible and traceable in the JobRunr dashboard.
- Integration test verifying that calling the endpoint results in a job being enqueued and executed successfully.

---

## Phase 5: Recurring Scheduled Job API

**Objective:**
Implement the second REST API that allows a client to register a recurring JobRunr schedule, which automatically enqueues weather-fetching jobs at a fixed interval.

**Output:**
- REST endpoint to create/register a recurring job (with configurable interval, e.g. cron or duration-based).
- Recurring job correctly appears in JobRunr dashboard and fires automatically at the configured interval.
- Ability to view/verify job execution history over multiple cycles.
- Integration test verifying that a recurring job is registered and triggers at least once within a test-controlled time window.

---

## Phase 6: Persistence of Weather Results

**Objective:**
Store fetched weather data results (from both enqueue and scheduled jobs) into H2 for later retrieval, so results aren't only visible via logs/dashboard.

**Output:**
- A data model/entity and repository for weather results.
- Both job types (on-demand and scheduled) persist their results after successful execution.
- A simple read endpoint (optional) to list/query stored weather results.
- Unit/integration tests confirming weather data is correctly persisted after job execution.

---

## Phase 7: Testing & Quality Hardening

**Objective:**
Consolidate and strengthen test coverage across the application, including failure scenarios (e.g., OpenWeather API errors, job retries).

**Output:**
- Unit tests covering services, clients, and job classes.
- Integration tests covering both API endpoints end-to-end (enqueue flow and schedule flow).
- Test cases for failure/retry behavior (e.g., simulate OpenWeather API failure and confirm JobRunr retry behavior).
- A documented test report or summary of coverage achieved.

---

## Phase 8: Documentation & Wrap-Up

**Objective:**
Document the project setup, architecture, and key JobRunr learnings for future reference.

**Output:**
- `README.md` with setup instructions, how to run the app, and how to use both APIs.
- A short "Lessons Learned" section summarizing JobRunr concepts explored (enqueue vs. recurring jobs, dashboard, storage providers, retry mechanism).
- Final working project ready for demonstration.
