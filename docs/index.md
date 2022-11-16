# Hand's on

## :running: Warm up

### :computer: Infrastructure
:warning: The required infrastructure is available by running [Docker containers](https://www.docker.com/resources/what-container/).

You can start the whole by running [Docker compose](https://docs.docker.com/compose/).

```bash
cd infrastructure
docker compose up -d
```

You can then check the running containers by running this command:

```jshelllanguage
docker compose ps
```

```bash
NAME                      COMMAND                  SERVICE             STATUS              PORTS
books-database            "docker-entrypoint.s…"   database            running             0.0.0.0:5432->5432/tcp
books-monitoring          "/bin/prometheus --c…"   monitoring          running             0.0.0.0:9090->9090/tcp
infrastructure-jaeger-1   "/go/bin/all-in-one-…"   jaeger              running             5775/udp, 5778/tcp, 14250/tcp, 6832/udp, 14268/tcp, 0.0.0.0:6831->6831/udp, 0.0.0.0:16686->16686/tcp
```

## :information_desk_person: Spring services to be started before

:warning: You **MUST** also start the [config-server](../config-server) service before.

Start a new shell and run the following command at the root of the project (i.e., ``rest-apis-versioning-workshop``)

```
./gradlew bootRun -p config-server
```

You should have the following output indicating the service is ready:

```
 [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8888 (http) with context path ''
 [  restartedMain] i.t.a.c.ConfigServerApplication          : Started ConfigServerApplication in 2.986 seconds (JVM running for 3.328)
```

To check it is effectively ready, you can reach the API by running this command:

```jshelllanguage
http http://localhost:8888/rest-number/default 
```

You should have such an output:

<details>
<summary>Click to expand</summary>

```json
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/json
Date: Mon, 07 Nov 2022 16:44:35 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
"label": null,
"name": "rest-number",
"profiles": [
"default"
],
"propertySources": [
{
"name": "classpath:/config/rest-number.yml",
"source": {
"management.auditevents.enabled": true,
"management.endpoint.health.enabled": true,
"management.endpoint.health.probes.enabled": true,
"management.endpoint.health.show-details": "always",
"management.endpoint.metrics.enabled": true,
"management.endpoint.prometheus.enabled": true,
"management.endpoint.shutdown.enabled": true,
"management.endpoints.enabled-by-default": true,
"management.endpoints.jmx.exposure.include": "*",
"management.endpoints.web.exposure.include": "*",
"management.health.livenessstate.enabled": true,
"management.health.readinessstate.enabled": true,
"management.metrics.web.client.request.autotime.enabled": true,
"opentracing.jaeger.enabled": true,
"opentracing.jaeger.udp-sender.host": "localhost",
"opentracing.jaeger.udp-sender.port": 6831,
"resilience4j.bulkhead.configs.default.maxConcurrentCalls": 100,
"resilience4j.bulkhead.instances.book-numbers.maxConcurrentCalls": 10,
"resilience4j.thread-pool-bulkhead.configs.default.coreThreadPoolSize": 2,
"resilience4j.thread-pool-bulkhead.configs.default.maxThreadPoolSize": 4,
"resilience4j.thread-pool-bulkhead.configs.default.queueCapacity": 2,
"resilience4j.thread-pool-bulkhead.instances.book-numbers.baseConfig": "default",
"resilience4j.timelimiter.configs.default.cancelRunningFuture": false,
"resilience4j.timelimiter.configs.default.timeoutDuration": "2s",
"resilience4j.timelimiter.instances.book-numbers.baseConfig": "default",
"server.port": 8081,
"spring.application.name": "rest-number",
"spring.cloud.circuitbreaker.resilience4j.enabled": true,
"time.to.sleep": 1000
}
}
],
"state": null,
"version": null
}

```


</details>

## :sparkles: Ready? Let's deep dive into versioning!

Here are the chapters covered by this workshop:

1. [Dealing with updates without versioning](./01-without_versioning.md)
2. [Our first version](./02-first_version.md)
3. [Adding new customers and a new functionalities](./03-second-version.md)
4. [Configuration management](./04-scm.md)
5. [Dealing with conflicts](./05-conflicts.md)
6. [Authorization issues](./06-authorization.md)