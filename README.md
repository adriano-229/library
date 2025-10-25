# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.7/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.7/maven-plugin/build-image.html)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

---

## First-time Database Setup (MySQL)

This project uses MySQL with placeholders configured in `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/____libraryDB____?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=false
```

You have two simple options to get the schema and sample data:

### Option A: Import SQL manually (fastest to see data)

1) Create the database:

```sql
CREATE
DATABASE ____libraryDB____ CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2) Import the full schema and data:

```bash
mysql -u root -p ____libraryDB____ < database_schema.sql
```

3) Start the app:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Login with:

- admin@library.com / password123 (ADMIN)
- user1@example.com / password123 (USER)

### Option B: Use Flyway migrations (recommended for development)

1) Ensure migrations are in `src/main/resources/db/migration/`.

2) Switch to dev profile and enable Flyway:

```properties
# src/main/resources/application.properties (or use application-dev.properties)
spring.profiles.active=dev
spring.flyway.enabled=true
spring.flyway.clean-disabled=false
spring.flyway.baseline-on-migrate=false
```

3) (Optional) Clean + migrate automatically in dev only:

- `src/main/java/com/adriano/library/config/FlywayConfig.java` can define a dev-only clean+migrate strategy.

4) Run the app:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Notes:

- Keep `spring.jpa.hibernate.ddl-auto=none` so schema is Flyway-managed.
- In prod, keep `spring.flyway.enabled=true` and `spring.flyway.clean-disabled=true` to prevent data loss.

### Troubleshooting

- If you see `Schema ... is up to date. No migration necessary`, ensure your migration files are inside
  `src/main/resources/db/migration` and named like `V1__desc.sql`.
- If manual import fails, verify MySQL is running and credentials match (root/root by default).
- To reset the DB in dev, drop and recreate the database, or use Flyway clean (dev only).
