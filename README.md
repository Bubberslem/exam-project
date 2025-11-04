# Candidate Manager API

Simple REST API to manage candidates and skills. Includes enrichment of candidate skills from an external Skill Stats provider.

## Quick overview

- Endpoints (base path `/api`):
    - GET  `/candidate` — list candidates
    - POST `/candidate` — create candidate
    - GET  `/candidate/{id}` — read candidate (returns skills; skills are enriched with market data when available)
    - PUT  `/candidate/{id}` — update candidate
    - DELETE `/candidate/{id}` — delete candidate
    - PUT  `/candidate/{candidateId}/skill/{skillId}` — link existing skill to a candidate
    - GET  `/skill` — list skills
    - POST `/skill` — create skill
    - GET  `/skill/{id}` — read skill
    - PUT  `/skill/{id}` — update skill
    - DELETE `/skill/{id}` — delete skill


## Technology used

- **Java 17**
- **Hibernate/JPA**
- **Jakarta Persistence**
- **Lombok**
- **Junit**
- **PostgreSQL**  (local or remote) — used by the application
- **Docker**

## HOW TO USE

1. **Clone repository**
- Clone from provided Git repository to your local machine.

2. **Add config.properties**
    - Create a config.properties file in the src/main/resources folder with the following content:
    - DB_NAME=candidate_management
    - DB_USERNAME=postgres
    - DB_PASSWORD=postgres
    - DB_HOST=localhost
    - SECRET_KEY=YOUR_SECRET_KEY (needs to be  32 characters long)
    - ISSUER=YOUR_ISSUER
    - TOKEN_EXPIRE_TIME=1800000

3. **Set up the database**
- Ensure you have a PostgreSQL database running (locally or remotely).
- Create a database named `candidate_management` (or the name you set in config.properties).

Notes:
- If you run tests with Testcontainers you don't need to configure a local DB for tests; Docker must be running.
- Default app port used in examples: 7070.

4. **Build and run the application**
- Run the main method in src/main/java/dat/Main.java
- The application will start on port 7070 by default.
- Either use dev.http or see the endpoints above to call the API endpoints.


## Skill enrichment (external API) Couldn't Get this to work properly but would have had these features

When retrieving a candidate by id the application will call the external Skill Stats API once (passing multiple slugs) to enrich each skill with:
- popularityScore
- averageSalary



## Testing

Run unit + integration tests:
- Unit tests are provided in the src/test/java folder for specific methods


Notes:
- Integration tests use Testcontainers; Docker must be running on the machine.
- Tests may start the app on port 7070 (see tests for the exact port). Adjust config if port conflicts occur.

## Troubleshooting

- If tests fail due to database issues, ensure Docker is running (for Testcontainers) or the local database is reachable and config.properties is correct.



## Author

- Project: Candidate Manager
- Author: Marcus Rasmusen - Cphbusiness Lyngby


## Testing

- Unit tests are provided in the src/test/java folder for specific methods

