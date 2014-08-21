#!/bin/sh
heroku config:set \
    APPLICATION_BASE_URL="http://<your application>.herokuapp.com" \
    DATABASE_DRIVER=org.postgresql.Driver \
    SMTP_MOCK=false \
    SMTP_HOST=smtp.sendgrid.net \
    SMTP_PORT=587 \
    SMTP_SSL=true \
    SMTP_TLS=true \
    PASSWORD_SALT="<your password salt>" \
    NEW_RELIC_LOG=stdout \
    TZ="Europe/Stockholm" \
    LC_ALL="sv_SE.UTF-8" \
    ADDTHISEVENT_LICENSE="<your addthisevent.com license>" \
    CLOUDINARY_CLOUD_NAME="<your cloud name>" \
    CLOUDINARY_API_KEY="<your api key>" \
    CLOUDINARY_API_SECRET="<your api secret>" \
    CLOUDINARY_FOLDER="signup" \
    JAVA_TOOL_OPTIONS=-Djava.net.preferIPv4Stack=true \
    JAVA_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dnewrelic.bootstrap_classpath=true -Dnewrelic.environment=production -javaagent:target/universal/stage/lib/com.newrelic.agent.java.newrelic-agent-3.8.2.jar" \
    $*
