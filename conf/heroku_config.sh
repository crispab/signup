#!/bin/sh
heroku config:set \
    APPLICATION_NAME="<The display name for the application>" \
    APPLICATION_THEME="crisp" \
    APPLICATION_BASE_URL="http://<your application>.herokuapp.com" \
    ADDTHISEVENT_LICENSE="<your addthisevent.com license>" \
    CLOUDINARY_FOLDER="<your folder name>" \
    PASSWORD_SALT="<your password salt>" \
    SLACK_CHANNEL_URL="<if you use chat via slack.com and want notifications there, otherwise remove this line>" \
    GOOGLE_CLIENT_ID="<to enable Google login - get from Google Dev Console>" \
    GOOGLE_CLIENT_SECRET="<to enable Google login - get from Google Dev Console>" \
    FACEBOOK_CLIENT_ID="<to enable Facebook login - get from Facebook's dev site>" \
    FACEBOOK_CLIENT_SECRET="<to enable Facebook login - get from Facebook's dev site>" \
    SMTP_MOCK=false \
    SMTP_HOST=smtp.sendgrid.net \
    SMTP_PORT=587 \
    SMTP_SSL=false \
    SMTP_TLS=true \
    NEW_RELIC_LOG=stdout \
    NEW_RELIC_APP_NAME="<The name displayed in Newrelic's console>" \
    TZ="Europe/Stockholm" \
    LC_ALL="sv_SE.UTF-8" \
    DATABASE_DRIVER=org.postgresql.Driver \
    JAVA_TOOL_OPTIONS=-Djava.net.preferIPv4Stack=true \
    JAVA_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dnewrelic.bootstrap_classpath=true -Dnewrelic.environment=production -javaagent:target/universal/stage/lib/com.newrelic.agent.java.newrelic-agent-3.16.1.jar" \
    $*
