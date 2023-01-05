## Fake OAuth2 Server

This project simulates issuing RS256-signed jwt tokens in a loop with 10_000 iterations to demonstrate a relevant performance loss encountered in native compilation.

- GIthub issue https://github.com/oracle/graal/issues/5697

### Tests

#### JIT

Execution:

```sh
mvn clean package
java -jar target/fake-oauth-jwt-loop.jar
```

Result:

```
RUN 1 - 8496ms
RUN 2 - 8490ms
RUN 3 - 8500ms
```

#### Native

Execution:

```sh
mvn clean package -Pnative
./target/fake-oauth-jwt-loop
```

Result:

```
RUN 1 - 41606ms
RUN 2 - 41718ms
RUN 3 - 42839ms
```

### PC Settings

- CPU: Ryzen 9 7950X
- Mem: 16GB 6200MHZ (x2)