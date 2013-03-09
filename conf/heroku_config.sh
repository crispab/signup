heroku config:set \
    DATABASE_DRIVER=org.postgresql.Driver \
    SMTP_MOCK=false \
    SMTP_HOST=[host] \
    SMTP_PORT=25 \
    SMTP_SSL=true \
    SMTP_TLS=true \
    SMTP_USER=[user] \
    SMTP_PASSWORD=[password] \
    $*