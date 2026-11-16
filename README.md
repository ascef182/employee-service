# Employee Service

A small RESTful web service built with **Java** and **Spring Boot** that manages a list of employees. It is a step-by-step solution to a hands-on software engineering challenge (design, build, extend and test a REST API), written to be easy to follow for junior developers and easy to review for recruiters.

## Tech stack

- Java 25
- Spring Boot 4 (Spring Web MVC)
- Gradle (wrapper included)
- JUnit 5

## Challenge roadmap

| # | Task | Status |
|---|------|--------|
| 1 | Write a proposal for a RESTful web service | Done |
| 2 | Build a basic RESTful web service | Done |
| 3 | Upload JSON data | Planned |
| 4 | Create unit tests | Planned |

## Getting started

```bash
git clone <your-fork-or-clone-url>
cd employee-service
./gradlew bootJar
java -jar build/libs/employee-service-0.0.1-SNAPSHOT.jar
```

Then call the API:

```bash
curl http://localhost:8080/employees
```

Expected response:

```json
[
  {"employeeId":"001","firstName":"Ana","lastName":"Silva","email":"ana@empresa.com","title":"Developer"},
  {"employeeId":"002","firstName":"Bruno","lastName":"Costa","email":"bruno@empresa.com","title":"Designer"},
  {"employeeId":"003","firstName":"Carla","lastName":"Souza","email":"carla@empresa.com","title":"Product Manager"},
  {"employeeId":"004","firstName":"Diego","lastName":"Lima","email":"diego@empresa.com","title":"QA Analyst"}
]
```

## Step-by-step walkthrough

### Task 1: Proposal

Before writing code, the design is planned:

- **Framework:** Spring Boot, using a layered design (controller, manager, model).
- **Hosting:** HPE GreenLake cloud platform for scalable infrastructure.
- **Endpoints** (REST conventions and status codes):
  - `GET /employees` returns all employees (`200`)
  - `GET /employees/{id}` returns one employee (`200` / `404`)
  - `POST /employees` creates an employee (`201` / `400`)
  - `PUT /employees/{id}` updates an employee (`200` / `404`)
  - `DELETE /employees/{id}` deletes an employee (`204` / `404`)
- **Quality:** input validation, HTTPS, centralized error handling, JUnit and Mockito tests.

> Note: the HTTP verb for updating a resource is `PUT`, not `PUSH`.

### Task 2: Build a basic RESTful web service

**Step 1. Generate the project.** Create a Spring Boot project with the *Spring Web* dependency (for example with [start.spring.io](https://start.spring.io)) using Gradle and Java.

**Step 2. Create the `Employee` model.** It holds the data of one employee in `private` fields, set through the constructor and exposed with getters. Keeping fields private means access is controlled by methods.

```java
public class Employee {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String title;

    public Employee(String employeeId, String firstName, String lastName,
                    String email, String title) { /* assign fields */ }

    // getters for every field
}
```

**Step 3. Create the `Employees` collection.** It wraps a `List<Employee>` and exposes a getter, a setter and `addEmployee`.

**Step 4. Create the `EmployeeManager`.** Annotated with `@Component`, it owns an `Employees` instance and seeds it with four hard-coded employees in its constructor. This stands in for a database for now.

**Step 5. Create the `EmployeeController`.** Annotated with `@RestController` and `@RequestMapping("/employees")`, it receives the manager through constructor injection and returns the list on `GET /employees`. Spring converts the returned objects to JSON automatically.

**Step 6. Build and run.**

```bash
./gradlew bootJar
java -jar build/libs/employee-service-0.0.1-SNAPSHOT.jar
```

**Step 7. Test.**

```bash
curl http://localhost:8080/employees
```

You should see the four seeded employees as JSON.

### Task 3: Upload JSON data (planned)

Add an endpoint that accepts a JSON payload and adds employees to the list.

### Task 4: Unit tests (planned)

Cover the model, manager and controller with JUnit 5 and Spring's MockMvc.

## Project structure

```
src/main/java/com/example/employee_service
├── EmployeeServiceApplication.java
├── controller
│   └── EmployeeController.java
└── model
    ├── Employee.java
    ├── EmployeeManager.java
    └── Employees.java
```

## Design notes

- **Constructor injection** over field `@Autowired`: dependencies can be `final` and the class is easier to test.
- **In-memory storage** keeps the example focused on REST concepts. A real service would use a database (for example Spring Data JPA).

## License

Released for educational purposes.
