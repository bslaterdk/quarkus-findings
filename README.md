# openshift-s2i-issue project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running and testing the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

To test the service is running and responding, use the following curl command:
```shell script
curl --location --request POST 'localhost:8080/input' --header 'Content-Type: application/json' --data-raw '{ "field1": "Storebrand", "field2": "SOS" }'
```

## Packaging the application as docker image

The application can be packaged using the Openshift extension, with the following command:
```shell script
./mvnw -Dquarkus.container-image.build=true -Dquarkus.container-image.tag=1.0.0 -Dproject.version=1.0.0 -Dquarkus.kubernetes-client.trust-certs=true -Dquarkus.profile=prod -Dquarkus.package.type=fast-jar clean package
```

## Running the application as docker image
The application is now runnable using following command:
```shell script
docker run <URL for registry>/<project/namespace>/openshift-s2i-issue:1.0.0
```

This will how ever fail with the following error:

```shell script
sudo docker runt docker-registry-default.apps.ocpt1.xxx.eu/sandbox-dev/openshift-s2i-issue:1.0.0
/usr/local/s2i/run: line 15: /opt/jboss/container/maven/default//scl-enable-maven: No such file or directory
Starting the Java application using /opt/jboss/container/java/run/run-java.sh ...
ERROR Neither $JAVA_MAIN_CLASS nor $JAVA_APP_JAR is set and 0 JARs found in /deployments (1 expected)
INFO exec  java -javaagent:/usr/share/java/jolokia-jvm-agent/jolokia-jvm.jar=config=/opt/jboss/container/jolokia/etc/jolokia.properties -XX:+UseParallelOldGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MaxMetaspaceSize=100m -XX:+ExitOnOutOfMemoryError -cp "." -jar
Error: -jar requires jar file specification
Usage: java [options] <mainclass> [args...]
           (to execute a class)
   or  java [options] -jar <jarfile> [args...]
           (to execute a jar file)
   or  java [options] -m <module>[/<mainclass>] [args...]
       java [options] --module <module>[/<mainclass>] [args...]
           (to execute the main class in a module)
   or  java [options] <sourcefile> [args]
           (to execute a single source-file program)

 Arguments following the main class, source file, -jar <jarfile>,
 -m or --module <module>/<mainclass> are passed as the arguments to
 main class.

 where options include:.
 .
 .
 .
```

It looks like the docker image run.java application can't find the jar file to run. And it according to /opt/jboss/container/java/run/run-java.sh it looks in /deployments folder.
When doing a listing of /deployments we can see there is no jar file, at all. But there is a target folder, which contains the jar file and the rest of the dependencies for the jar file.
In this case it's a fast-jar build. The same has been seen when creatign normal jar application.

```
sh-4.4$ ls -alF /deployments
total 16
drwxrwxr-x 1 jboss root 4096 Jan 18 11:35 ./
drwxr-xr-x 1 root  root 4096 Jan 18 12:03 ../
drwxrwxr-x 2 jboss root 4096 Dec 16 04:40 data/
drwxrwxr-x 5 jboss root 4096 Jan 18 11:35 target/
sh-4.4$

sh-4.4$ ls -alF /deployments/target/
total 24
drwxrwxr-x 5 jboss root 4096 Jan 18 11:35 ./
drwxrwxr-x 1 jboss root 4096 Jan 18 11:35 ../
drwxrwxr-x 2 jboss root 4096 Jan 18 11:35 app/
drwxrwxr-x 4 jboss root 4096 Jan 18 11:35 lib/
drwxrwxr-x 2 jboss root 4096 Jan 18 11:35 quarkus/
-rw-rw-r-- 1 jboss root  670 Jan 18 11:35 quarkus-run.jar
sh-4.4$
```

If we run the quarkus-run.jar file manually, the application then starts as expected.
```
sh-4.4$ java -jar /deployments/target/quarkus-run.jar
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2021-01-18 11:44:20,884 INFO  [org.apa.cam.qua.cor.CamelBootstrapRecorder] (main) bootstrap runtime: org.apache.camel.quarkus.core.CamelContextRuntime
2021-01-18 11:44:21,004 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Apache Camel 3.6.0 (camel-1) is starting
2021-01-18 11:44:21,007 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
2021-01-18 11:44:21,036 INFO  [org.apa.cam.imp.eng.InternalRouteStartupManager] (main) Route: example_route started and consuming from: direct://pipeline
2021-01-18 11:44:21,056 INFO  [org.apa.cam.imp.eng.InternalRouteStartupManager] (main) Route: route1 started and consuming from: platform-http:///input
2021-01-18 11:44:21,061 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Total 2 routes, of which 2 are started
2021-01-18 11:44:21,061 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Apache Camel 3.6.0 (camel-1) started in 0.056 seconds
2021-01-18 11:44:21,224 INFO  [io.quarkus] (main) openshift-s2i-issue 1.0.0 on JVM (powered by Quarkus 1.10.5.Final) started in 1.822s. Listening on: http://0.0.0.0:8080
2021-01-18 11:44:21,226 INFO  [io.quarkus] (main) Profile prod activated.
2021-01-18 11:44:21,227 INFO  [io.quarkus] (main) Installed features: [camel-attachments, camel-bean, camel-core, camel-direct, camel-http, camel-log, camel-platform-http, camel-rest, camel-support-common, camel-support-commons-logging, camel-support-httpclient, cdi, kubernetes, mutiny, smallrye-context-propagation, smallrye-health, smallrye-openapi, vertx, vertx-web]
```

