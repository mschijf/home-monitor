# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Home monitoring application that collects and stores data from various home devices and services, exposes metrics to Prometheus, and visualizes them via Grafana dashboards. Runs on a Raspberry Pi.

**Data sources integrated:**
- **HomeWizard** – electricity and water meters (local HTTP API)
- **Tado** – thermostat/temperature (OAuth2 REST API)
- **Eneco (heath)** – gas/heating consumption (Selenium-scraped via browser)
- **Shelly** – thermometer (cloud API)
- **Tuya** – smart plugs (cloud API with HMAC-SHA256 auth)
- **WeatherAPI** – outdoor weather
- **System** – Raspberry Pi CPU/GPU temperature, backup status, Dropbox free space

## Commands

### Build
```bash
./mvnw clean package          # build jar (skip deploy)
./mvnw clean package -DskipTests
```

### Run locally (dev)
```bash
docker compose up -d          # start local PostgreSQL on port 5432
./mvnw spring-boot:run        # start app with dev profile (schedulers disabled via "-" cron)
```

### Build and deploy to Raspberry Pi
```bash
./mvnw clean deploy           # builds jar, SCP to pi, and SSH-triggers run.sh on the pi
```

### Check for dependency updates
```bash
./mvnw versions:display-dependency-updates
```

### Tests
```bash
./mvnw test                   # run all tests (currently only a stub test exists)
```

## Architecture

### Package layout (`src/main/kotlin/ms/homemonitor/`)

Each data source is a self-contained module under its own package:

```
<domain>/
  repository/       JPA entities + Spring Data repositories
  restclient/       HTTP or CLI client + response models
  service/          business logic, called by Scheduler
```

Shared infrastructure lives in `shared/`:
- `scheduler/Scheduler.kt` – single Spring `@Scheduled` bean wiring all services; cron expressions come from `application.yml`
- `shared/scheduler/AbstractBaseScheduler.kt` – `runSafely {}` wrapper used by every scheduled method
- `shared/summary/service/SummaryService.kt` – computes year-to-date summaries and prognoses for electricity, water, and heath; cached in-memory, cleared every 5 min
- `shared/tools/TimedCache.kt` – simple TTL cache used by individual services (e.g., OAuth tokens)
- `shared/admin/repository/` – stores last-processed timestamps per data source in the DB
- `shared/tools/micrometer/MicroMeterMeasurement.kt` – thin wrapper around Micrometer for publishing gauges and counters to Prometheus

### Configuration profiles

| Profile | Purpose |
|---|---|
| *(default / dev)* | All schedulers set to `"-"` (disabled). Local PostgreSQL at `localhost:5432`. Mock CLI commands from `./mock/`. Secrets set to `not_for_dev`. |
| `prod` | Real cron schedules. PostgreSQL at `192.168.2.39:5432`. Real CLI paths on the Pi. Secrets loaded from `/home/martinschijf/application/config/secrets.yml`. |

Activate prod profile with `--spring.profiles.active=prod`.

### Database

PostgreSQL, managed by Flyway. Migrations in `src/main/resources/db/migration/`. Dev connection: `jdbc:postgresql://localhost:5432/home-monitor` (user: `home-monitor`, password: `home-monitor`).

### Observability

- Spring Actuator + Micrometer with Prometheus registry exposed at `/actuator/prometheus`
- Grafana dashboards in `grafana/` (import the JSON files into Grafana)
- Prometheus + Grafana run as Docker containers on the Pi (see `docker/compose.yml`)

### API / Swagger

springdoc-openapi at `/swagger-ui.html`. API groups:
- **summary** – year summaries for electricity, water, heath (`/**/summary`)
- **admin** – verify live data from each device client (`/admin/**`)
- **log** – tail application log (`/log/**`)
- **test** – scratch endpoints (`/test/**`)

### Deployment pipeline

`mvn deploy` uses `exec-maven-plugin` to SSH into `pi` and invoke `~/repository/repository-run-scripts/run.sh`, which stops the old instance and starts the new jar. The jar is uploaded via `wagon-ssh-external` SCP.

### Adding a new data source

Follow the pattern of any existing domain package:
1. `repository/` – JPA entity with `@Entity` + Spring Data `JpaRepository`
2. `restclient/` – REST/CLI client + data model classes
3. `service/` – service class with a `processMeasurement()` method; publish metrics via `MicroMeterMeasurement`
4. Wire a new `@Scheduled` method in `Scheduler.kt` with a configurable cron expression
5. Add the cron property to both `application.yml` (set to `"-"`) and `application-prod.yml`
6. Add a Flyway migration SQL in `db/migration/`
