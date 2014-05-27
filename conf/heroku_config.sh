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
    ADDTHISEVENT_LICENSE="" \
    $*
