#!/bin/sh
heroku config:set \
    APPLICATION_BASE_URL="http://<your application>.herokuapp.com" \
    ADDTHISEVENT_LICENSE="<your addthisevent.com license>" \
    CLOUDINARY_FOLDER="<your folder name>" \
    PASSWORD_SALT="<your password salt>" \
    SMTP_MOCK=false \
    SMTP_HOST=smtp.sendgrid.net \
    SMTP_PORT=587 \
    SMTP_SSL=true \
    SMTP_TLS=true \
    NEW_RELIC_LOG=stdout \
    TZ="Europe/Stockholm" \
    LC_ALL="sv_SE.UTF-8" \
    DATABASE_DRIVER=org.postgresql.Driver \
    JAVA_TOOL_OPTIONS=-Djava.net.preferIPv4Stack=true \
    JAVA_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dnewrelic.bootstrap_classpath=true -Dnewrelic.environment=production -javaagent:target/universal/stage/lib/com.newrelic.agent.java.newrelic-agent-3.8.2.jar" \
    $*
