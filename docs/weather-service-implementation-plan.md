# Implementation Plan: Weather Data Background Service

## Project Overview
A small-scale Java application that fetches weather data from the OpenWeather API and stores it in an H2 database. When a request is triggered, the system does **not** call the external API synchronously — instead, it enqueues the task using **JobRunr**, which processes it asynchronously in the background.

**Architecture layers:** Controller → Service → External Service → Model (Entity + Repository)

---

## Phase 1: Project Setup & Base Architecture

**Goal:**
Establish the base Java/Spring Boot project structure, dependencies, and configuration needed for all subsequent phases (JobRunr, H2, HTTP client for OpenWeather).

**Output:**
- A runnable, empty Spring Boot project with the required dependencies (Spring Web, Spring Data JPA, H2, JobRunr) configured.
- Base package structure created for `controller`, `service`, `external`, `model` (with `entity` and `repository` sub-packages).
- Application starts successfully with JobRunr dashboard and H2 console accessible.

---

## Phase 2: Data Model Layer (Entity & Repository)

**Goal:**
Define how weather data is structured and persisted.

**Output:**
- A `WeatherRecord` entity representing the weather data fields to be stored (e.g., city, temperature, humidity, description, timestamp).
- A Spring Data JPA repository interface for basic CRUD operations on `WeatherRecord`.
- H2 schema auto-generated and verified via H2 console.

---

## Phase 3: External Service Integration (OpenWeather Client)

**Goal:**
Enable the application to retrieve raw weather data from the OpenWeather API.

**Output:**
- An external service component capable of calling the OpenWeather API for a given city/location and returning parsed weather data.
- Verified successful API call with a sample response (manually tested, not yet connected to the job queue).

---

## Phase 4: Background Job Processing (JobRunr Integration)

**Goal:**
Move the OpenWeather API call out of the request path by defining it as an asynchronous background job.

**Output:**
- A JobRunr job definition that, when enqueued, calls the external service and saves the result via the repository.
- Confirmed via JobRunr dashboard that a manually enqueued job executes successfully end-to-end (fetch → save to H2).

---

## Phase 5: Service Layer

**Goal:**
Provide the business logic layer that receives requests and enqueues jobs (instead of calling the external service directly).

**Output:**
- A service component that, when invoked, enqueues the background job via JobRunr and returns immediately (no blocking call to OpenWeather).
- Verified that invoking the service does not block and the job appears in the JobRunr queue.

---

## Phase 6: Controller Layer

**Goal:**
Expose an API endpoint that allows external requests to trigger the weather-fetch process.

**Output:**
- A REST endpoint (e.g., `POST /weather/fetch`) that accepts a request and delegates to the service layer.
- Endpoint returns an immediate acknowledgment response while the actual data fetch happens in the background.

---

## Phase 7: End-to-End Testing & Verification

**Goal:**
Confirm the full flow works correctly from API request to stored data.

**Output:**
- A verified end-to-end test: calling the API endpoint → job enqueued → job executed in background → data correctly persisted in H2.
- Basic error-case check (e.g., invalid city name / API failure) confirmed not to crash the background worker.

---

## Phase 8: Finalization

**Goal:**
Wrap up the project into a clean, deliverable state.

**Output:**
- Final working application with all layers integrated (Controller → Service → External Service → Model).
- Basic README documenting how to run the application and trigger the endpoint.
