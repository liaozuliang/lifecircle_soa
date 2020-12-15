FROM 172.10.10.56/base/jar8-base:v1
ADD target/lifecircle_base.jar .
ENTRYPOINT java $JAVA_OPTS -Dpinpoint.agentId=${HOSTNAME##*-}  -Dpinpoint.applicationName=${APPS_NAME%-*} -jar lifecircle_base.jar
