Part that accepts interacts with redis and sends email sending requests \
\
To actually run it you need to run from project directory
```
mvn package
docker build -t nets-core .
```
To fully start everything you need to build nets-core, nets-proxy, nets-sender \
After manually setup nginx that will point to docker compose exposed port. As an example you can use `front.conf` \
Then go to `serviceUp` and provide in  /certificates respective certificates to be used inside app (`example.local.crt`, `example.local.key`) \
Then in /config create sender-secret-application.properties with fields as described in sender part of project \
After all this you can run `docker-compose-up`. Your nginx that points to exposed port now can be used by you to access app. \
About https - they are used by all outgoing requests unless they are to send mail or to db. All incoming connections after sidecar gets their https converted to http. \
But there is an exception! Manually written proxy directly accesses core upstream using http and bypassing sidecar. It is kinda internal proxy so be it. Also access to it I will consider internal and let it be. \
It is possible to make this proxy able to accept https connections and send https but I didn't manage to. Spring just killed me.