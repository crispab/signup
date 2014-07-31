#!/bin/sh
heroku config:set \
    DATABASE_DRIVER=org.postgresql.Driver \
    SMTP_MOCK=false \
    SMTP_HOST=smtp.sendgrid.net \
    SMTP_PORT=587 \
    SMTP_SSL=true \
    SMTP_TLS=true \
    SMTP_USER=crisp \
    SMTP_PASSWORD=pwd \
    PASSWORD_SALT=salt \
    NEW_RELIC_LOG=stdout \
    TZ="Europe/Stockholm" \
    LC_ALL="sv_SE.UTF-8" \
    ADDTHISEVENT_LICENSE="" \
    CLOUDINARY_CLOUD_NAME="" \
    CLOUDINARY_API_KEY="" \
    CLOUDINARY_API_SECRET="" \
    CLOUDINARY_FOLDER=signup \
    JAVA_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dnewrelic.bootstrap_classpath=true -Dnewrelic.environment=production -javaagent:target/staged/newrelic-agent-3.8.2.jar" \
    $*
