# Gatling-gRPC demo in Gradle

A demo project showcasing a Gatling test
living in the same project as the gRPC server.

## Run

Run the server: `./gradlew run`\
Run the load test: `./gradlew gatlingRun`

## Dynamic Payload

This project also demonstrates the integration between
Java Protobuf messages with Gatling Expressions.

```scala
val message: Expression[Ping] =
  Ping.getDefaultInstance
    // dynamic payload!
    .update(_.setData)($("data"))
```

`_.setData` is a method reference of `Ping.Builder`; and\
`data` is a [session attribute](https://gatling.io/docs/current/session/session_api/)
containing a number.

See the runtime-generated payload in the `TRACE` logging!

```
=========================
gRPC request:
headers=
grpc-accept-encoding: gzip
payload=
data: 18

=========================
gRPC response:
status=
OK
body=
data: 18

<<<<<<<<<<<<<<<<<<<<<<<<<
```

## Extension

Some magic is used to aid type inference in
[Gatling-JavaPB](https://github.com/phiSgr/gatling-grpc/tree/master/java-pb).
For that to work in IntelliJ,
enable the extension in the "Event Log" in the bottom right corner.

![enable extension](intellij_event_log.png)

Otherwise, IntelliJ cannot recognize the method references like `setData`,
and they will not be auto-suggested or syntax-highlighted.

If you do not get the notification,
try forcing the download of the extension by adding\
`gatling 'com.github.phisgr:gatling-javapb-ijext:1.2.0'`\
in `dependencies {` in [build.gradle](./build.gradle).

After that you can remove that line of dependency.
