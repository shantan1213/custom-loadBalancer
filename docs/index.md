---
layout: home
title: Home
---

# Building a Custom Spring Boot Load Balancer

This is the development journal for a small Java project: one Spring Boot
application that accepts client requests and forwards them to separate backend
services. The goal is to understand the request path end to end, from an HTTP
call at the load balancer to a record saved by a backend.

> **Project status:** The proxy currently forwards requests to backend 1 only.
> Backend 2 has its own persistence and API, but automatic distribution or
> failover between the two backends is not implemented yet.

## The project

The system consists of three independently runnable Spring Boot applications:

| Application | Local address | Responsibility |
| --- | --- | --- |
| Custom Load Balancer | `http://localhost:8080` | Receives client requests and proxies them to backend 1 |
| Backend 1 | `http://localhost:11111` | Handles user create/update operations and stores users in H2 |
| Backend 2 | `http://localhost:22222` | Provides a second user backend with its own H2 database |

Each backend uses an in-memory H2 database. The databases are separate: a user
created in one backend is not automatically available in the other. Data is
cleared when the corresponding application stops.

## How a request travels

For a create request, the client sends JSON to the load balancer. Its
`ProxyController` accepts the request, `ProxyService` delegates it, and
`RequestForwarder` sends it to backend 1's `/create` endpoint. The backend
validates and persists the user, then the proxy returns the backend response to
the client.

```text
Client
  -> POST /proxy/create
  -> Load balancer (localhost:8080)
  -> POST /create on backend 1 (localhost:11111)
  -> H2 in-memory database
  <- saved user JSON, including generated ID
```

Updates follow the same path using `PUT /proxy/update/{id}` and backend 1's
`PUT /update/{id}` endpoint. An unknown ID produces a `404 Not Found`.

## Run the applications

Install a JDK compatible with each module and use the Maven wrapper included
with each project. Backend 1 and the load balancer require Java 25 in their
current Maven configuration; backend 2 requires Java 21.

Start each application in a separate terminal:

```powershell
# Backend 1
cd C:\Projects\backend\serverone\backend
.\mvnw.cmd spring-boot:run

# Backend 2
cd C:\Projects\backend\servertwo\backend2
.\mvnw.cmd spring-boot:run

# Custom load balancer
cd C:\Projects\Custom-LoadBalancer
.\mvnw.cmd spring-boot:run
```

The load balancer's current targets are configured in
`src/main/java/com/platformdaemon/customloadbalancer/requestproxy/BackendServerConstant.java`.
The defaults are backend 1 at port `11111` and backend 2 at port `22222`, but
the current create, update, and ping proxy methods target backend 1.

## Try the API

### Check backend 1 through the proxy

```powershell
curl.exe http://localhost:8080/proxy/ping
```

Expected response:

```text
pong
```

### Create a user

```powershell
curl.exe -X POST "http://localhost:8080/proxy/create" `
  -H "Content-Type: application/json" `
  -d "{\"firstName\":\"Alex\",\"lastName\":\"Kim\"}"
```

The response contains the saved user's generated `id`. Keep that value for the
update request.

### Update a user by ID

Replace `1` with the ID returned by the create request:

```powershell
curl.exe -X PUT "http://localhost:8080/proxy/update/1" `
  -H "Content-Type: application/json" `
  -d "{\"firstName\":\"Jamie\",\"lastName\":\"Park\"}"
```

Both names are required and must not be blank. Invalid JSON or invalid names
produce a `400 Bad Request`; an ID that does not exist produces a `404 Not
Found`.

## Inspect the H2 databases

With a backend running, open its console:

- Backend 1: `http://localhost:11111/h2-console`
- Backend 2: `http://localhost:22222/h2-console`

Use the database URL `jdbc:h2:mem:testdb`, username `sa`, and an empty password.
Connect to the backend whose console you opened; each process has its own
in-memory database.

## Error handling

The backends return consistent `ProblemDetail` responses for common failures,
including invalid input, malformed request bodies, unsupported methods or
media types, missing resources, and database conflicts. Unexpected failures
are logged server-side and returned as a generic `500 Internal Server Error`
without exposing stack traces to clients.

The load balancer returns `503 Service Unavailable` if it cannot connect to the
configured backend. It also uses problem responses for malformed requests,
unknown proxy paths, unsupported methods, and unexpected errors.

## What I learned

This project has been a practical way to connect several Spring concepts:

- Spring MVC controllers receive HTTP requests and bind JSON request bodies.
- A service and a request-forwarding component keep proxy responsibilities
  separate.
- `RestClient` can forward JSON and preserve the backend's response status,
  headers, and body.
- Spring Data JPA repositories persist entities, while H2 provides a quick
  local database for development.
- Bean Validation rejects blank user names before they reach persistence.
- A `@RestControllerAdvice` can map expected failures to useful HTTP problem
  responses.

## Next steps

The next milestone is to implement actual distribution between backend 1 and
backend 2. That will require a clear server-selection policy, handling of
unavailable instances, and tests that verify requests are sent to the intended
backend. Until that is implemented, this project should be described as a
request proxy with two backend services—not as an active round-robin load
balancer.

## Source repositories

- [Custom load balancer](https://github.com/shantan1213/custom-loadBalancer)
- [Backend 1](https://github.com/shantan1213/backend)
- [Backend 2](https://github.com/shantan1213/backend2)
