# Fake OAuth2 Server

This project simulates issuing RS256-signed jwt tokens in a loop with 10_000 iterations to demonstrate a relevant performance loss encountered in native compilation.

- GIthub issue https://github.com/oracle/graal/issues/5697

## GraalVm 22.0

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

##  GraalVM 17.0.7+8.1

#### JIT

Result:

```
RUN 1 - 6993ms
RUN 2 - 6945ms
RUN 3 - 6938ms
```

#### Native SerialGC

Result:

```
RUN 1 - 18214ms
RUN 2 - 18318ms
RUN 3 - 18288ms
```

#### Native PGO SerialGC

```
RUN 1 - 18068ms
RUN 2 - 17991ms
RUN 3 - 18098ms
```

#### Native PGO G1GC

```
RUN 1 - 18290ms
RUN 2 - 18119ms
RUN 3 - 18186ms
```

Perf record

```
# To display the perf.data header info, please use --header/--header-only options.
#
#
# Total Lost Samples: 0
#
# Samples: 74K of event 'cycles'
# Event count (approx.): 97891020906
#
# Overhead  Command          Shared Object        Symbol
# ........  ...............  ...................  .............................................
#
    10.81%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051edd5
     9.20%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051edce
     8.75%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051eda6
     8.40%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051ed9f
     7.67%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051edc7
     7.19%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051ed86
     4.57%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051f129
     3.05%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x00000000008d9663
     3.02%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051f130
     2.75%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051f116
     1.74%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x00000000008d9619
     1.63%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051ed8f
     1.44%  fake-oauth-jwt-  fake-oauth-jwt-loop  [.] 0x000000000051eddc
```

Perf annotate

```
 Percent |      Source code & Disassembly of fake-oauth-jwt-loop for cycles (78 samples, percent: local period)
---------------------------------------------------------------------------------------------------------------
         :
         :
         :
         : 3      Disassembly of section .text:
         :
         : 5      0000000000108000 <__svm_code_section>:
         : 6      __svm_code_section():
    0.00 :   108000: lea    -0x48(%rsp),%rbx
    0.00 :   108005: cmp    0x8(%r15),%rbx
    0.00 :   108009: jbe    418100 <graal_vm_locator_symbol+0x49ba0>
    0.00 :   10800f: mov    %rbx,%rsp
    0.00 :   108012: cmp    %r14,%rsi
    0.00 :   108015: je     108347 <__svm_code_section+0x347>
    0.00 :   10801b: mov    %rdi,0x38(%rsp)
```

### PC Settings

- CPU: Ryzen 9 7950X
- Mem: 16GB 6000MHZ (x2)

--pgo-instrument vvsantos.fake.oauth.loop.MainLoopJwtTest
--pgo=default.iprof vvsantos.fake.oauth.loop.MainLoopJwtTest