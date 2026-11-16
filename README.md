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
| 3 | Upload JSON data | Done |
| 4 | Create unit tests | Done (branch `test/unit-tests`) |

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
  {"employee_id":"001","first_name":"Ana","last_name":"Silva","email":"ana@empresa.com","title":"Developer"},
  {"employee_id":"002","first_name":"Bruno","last_name":"Costa","email":"bruno@empresa.com","title":"Designer"},
  {"employee_id":"003","first_name":"Carla","last_name":"Souza","email":"carla@empresa.com","title":"Product Manager"},
  {"employee_id":"004","first_name":"Diego","last_name":"Lima","email":"diego@empresa.com","title":"QA Analyst"}
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

### Task 3: Upload JSON data

Developed on the `feat/post-employees` branch and merged into `main` through a pull request.

**Goal:** let clients add a new employee by sending JSON with `POST /employees`, and fetch a single employee by ID.

**Step 1. Prepare the `Employee` model.** Spring needs a no-argument constructor and setters to build an object from JSON. The `@JsonProperty` annotation maps the snake_case JSON keys (`employee_id`, `first_name`, `last_name`) to the camelCase Java fields.

```java
@JsonProperty("employee_id")
private String employeeId;
```

**Step 2. Add the POST endpoint.** In `EmployeeController`, `@PostMapping` handles `POST /employees` and `@RequestBody` converts the JSON into an `Employee`. The endpoint answers `201 Created` with a `Location` header pointing to the new resource, which is the REST convention for creation. `consumes` makes it reject any content type other than JSON with `415`.

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
    employeeManager.getEmployees().addEmployee(employee);

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(employee.getEmployeeId())
            .toUri();

    return ResponseEntity.created(location).body(employee);
}
```

**Step 3. Add `GET /employees/{id}`.** The `Location` header needs a URL that works, so this endpoint returns one employee, or `404` when the ID does not exist. The lookup lives in `Employees.findById`, which returns an `Optional<Employee>`.

```java
@GetMapping("/{id}")
public ResponseEntity<Employee> getEmployee(@PathVariable String id) {
    return employeeManager.getEmployees().findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

**Step 4. Build, run and test.**

```bash
curl -i -X POST http://localhost:8080/employees \
  -H "Content-Type: application/json" \
  -d '{"employee_id":"005","first_name":"Eva","last_name":"Reis","email":"eva@empresa.com","title":"DevOps"}'

curl http://localhost:8080/employees
curl http://localhost:8080/employees/005
```

| Request | Result |
|---------|--------|
| `POST /employees` with valid JSON | `201 Created` and `Location: http://localhost:8080/employees/005` |
| `GET /employees/005` | `200` with the employee |
| `GET /employees/999` | `404 Not Found` |
| `POST /employees` with a non-JSON body | `415 Unsupported Media Type` |
| `POST /employees` with an existing `employee_id` | `409 Conflict` |
| `POST /employees` without `employee_id` | `400 Bad Request` |

Data is kept in memory, so it is lost when the app restarts.

### Task 4: Unit tests

Developed on the `test/unit-tests` branch. The suite has **15 tests** and runs in a few seconds.

```bash
./gradlew cleanTest test
```

`cleanTest` forces a fresh run. Without it, Gradle skips the tests when nothing changed. The HTML report is in `build/reports/tests/test/index.html`.

**Step 1. Choose a strategy by layer.**

| Layer | Class | Approach |
|-------|-------|----------|
| Model | `EmployeesTest`, `EmployeeManagerTest` | Plain JUnit 5, no Spring, so they run in milliseconds |
| Web | `EmployeeControllerTest` | `@WebMvcTest` with `MockMvc`, no real server |

**Step 2. Write the model tests.** Each test checks one behavior and follows Arrange, Act, Assert. `@BeforeEach` builds fresh objects so tests never depend on each other or on execution order.

**Step 3. Isolate the controller.** `@WebMvcTest` loads only the web layer, so `EmployeeManager` is replaced by a mock with `@MockitoBean`. Each test gets a fresh list of employees, which avoids flaky tests caused by shared state in a singleton.

```java
@MockitoBean
private EmployeeManager employeeManager;

@BeforeEach
void setUp() {
    employees = new Employees();
    employees.addEmployee(new Employee("001", "Ana", "Silva", "ana@empresa.com", "Developer"));
    when(employeeManager.getEmployees()).thenReturn(employees);
}
```

> Spring Boot 4 note: many tutorials still use Spring Boot 2. In Spring Boot 4, `@MockBean` became `@MockitoBean` and `@WebMvcTest` moved to `org.springframework.boot.webmvc.test.autoconfigure`.

**Step 4. Hunt for bugs with TDD.** Asking "what can a client send that I did not plan for?" revealed two bugs in `POST /employees`: a duplicate `employee_id` was accepted, and a missing one produced `Location: /employees/null` and later a `NullPointerException` in the lookup.

1. **Red:** write tests that demand the correct behavior (`409` and `400`) and watch them fail.
2. **Green:** fix the controller, validating the input first (`400`), then the business rule (`409`), and only then saving.
3. The commit history shows both steps: `test: add failing tests for duplicate and missing employee id`, then `fix: reject duplicate or missing employee_id on POST`.

**What is covered**

| Behavior | Test |
|----------|------|
| List all employees | `getAllReturnsAllEmployees` |
| Get one employee | `getByIdReturnsTheEmployee` |
| Unknown id returns `404` | `getByIdReturns404WhenMissing` |
| Create returns `201` and `Location` | `postCreatesEmployeeAndReturnsLocation` |
| Non-JSON body returns `415` | `postRejectsNonJsonContent` |
| Duplicate id returns `409` | `postRejectsDuplicateEmployeeId` |
| Missing id returns `400` | `postRejectsMissingEmployeeId` |

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

src/test/java/com/example/employee_service
├── EmployeeServiceApplicationTests.java
├── controller
│   └── EmployeeControllerTest.java
└── model
    ├── EmployeeManagerTest.java
    └── EmployeesTest.java
```

## Design notes

- **Constructor injection** over field `@Autowired`: dependencies can be `final` and the class is easier to test.
- **In-memory storage** keeps the example focused on REST concepts. A real service would use a database (for example Spring Data JPA).

## License

Released for educational purposes.
