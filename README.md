
# Smart-City-Resource-Management-System

**Smart-City-Resource-Management-System** — a JavaFX GUI–driven smart city resource-management prototype with role-based dashboards, simulated live operations (energy, transport) via non-blocking threads, and a MongoDB-backed `CityRepository<T>` for persistent heterogeneous resource handling. ([GitHub][1])

---

## Table of contents

* [Overview](#overview)
* [Features](#features)
* [Tech stack](#tech-stack)
* [Prerequisites](#prerequisites)
* [Build & run](#build--run)
* [Configuration (MongoDB)](#configuration-mongodb)
* [Project structure](#project-structure)
* [UML & design docs](#uml--design-docs)
* [Contributing](#contributing)
* [License & contact](#license--contact)

---

## Overview

This repository contains a prototype application intended to demonstrate a smart-city resource-management system using Java desktop UI (JavaFX), concurrency to simulate live systems, and MongoDB for persistence. It includes source under `src/`, Maven build configuration, and UML/design artifacts. ([GitHub][1])

---

## Features

* JavaFX GUI with role-based dashboards and views. ([GitHub][1])
* `CityRepository<T>` abstraction that persists heterogeneous resources to MongoDB. ([GitHub][1])
* Non-blocking threads/tasks simulating live energy consumption and transport operations. ([GitHub][1])
* Maven-based Java project for easy build and dependency management. ([GitHub][2])

---

## Tech stack

* Java (JavaFX for GUI) — desktop application. ([GitHub][1])
* MongoDB (for data persistence). ([GitHub][1])
* Maven (project build / dependencies). ([GitHub][2])

---

## Prerequisites

* Java JDK 11+ (or the version declared in `pom.xml`). ([GitHub][2])
* Maven 3.x
* A running MongoDB instance (local or remote).
* (Optional) An IDE that supports JavaFX (IntelliJ IDEA, Eclipse with e(fx)clipse, or Visual Studio Code with Java extensions).

---

## Build & run

1. **Clone the repo**

```bash
git clone https://github.com/abdulrahman-software/Smart-City-Resource-Management-System.git
cd Smart-City-Resource-Management-System
```

2. **Build with Maven**

```bash
mvn clean package
```

The `pom.xml` controls dependencies and packaging — inspect it for plugins or extra run tasks. ([GitHub][2])

3. **Run from Maven (if a javafx plugin is configured)**

```bash
mvn javafx:run
```

> If `javafx:run` is not configured in the `pom.xml`, run the generated JAR:

```bash
java -jar target/<artifactId>-<version>.jar
```

(Replace `<artifactId>-<version>.jar` with the actual artifact found in `target/` after `mvn package`.) ([GitHub][2])

4. **Run from your IDE**
   Import the project as a Maven project into your IDE and run the main class (look in `src/main` for the application entrypoint). ([GitHub][3])

---

## Configuration (MongoDB)

The application expects a MongoDB connection — check the code or `application.properties`/`config` files (if present) for the connection string key. Typical approaches:

* **Env var** (recommended):

```bash
export MONGO_URI="mongodb://localhost:27017/smartcity"
```

* **System property**:

```bash
java -DMONGO_URI="mongodb://localhost:27017/smartcity" -jar target/...
```

If the repo contains a configuration file or a constants class that defines the MongoDB URI, update that file before running. (Search for `MongoClient`, `mongodb`, or `connectionString` in the `src/` tree.) ([GitHub][3])

---

## Project structure (high level)

```
Smart-City-Resource-Management-System/
├─ .vscode/                 # editor settings (optional)
├─ src/
│  └─ main/                 # Java source (controllers, models, services, UI)
├─ target/                  # build output (after mvn package)
├─ pom.xml                  # Maven config & dependencies
├─ UML diagram.pdf          # design/architecture diagrams
├─ Members.txt              # contributors / team notes
```

See the `src` and `pom.xml` in the repo for exact package names and main class. ([GitHub][3])

---

## UML & design docs

A UML diagram is included in the repository (`UML diagram.pdf`) which describes the high-level class and module layout — useful if you plan to extend the system or integrate new resource types. ([GitHub][4])

---

## Contributing

Contributions, bug reports, and feature requests are welcome.

Suggested flow:

1. Fork the repository.
2. Create a branch: `git checkout -b feature/<short-name>`.
3. Commit code & tests.
4. Push and open a PR against `main`.

See `Members.txt` for project authors / contacts. ([GitHub][5])

---
