#!/bin/sh
# This is a config template for the SignUp4 config.
# Make a copy of this file and update it with your instance-specific config.
# It is not run as part of pushing to Heroku - it's a command you run with Heroku toolbelt.
# Your copy of this file will contain sensitive information, so you do NOT want to check it in to Git.
# If you call your copy heroku_config_current.sh it will be git ignored.
# E.g. cp heroku_config_template.sh heroku_config_current.sh
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
    PLAY_MAILER_MOCK=false \
    PLAY_MAILER_HOST=smtp.gmail.com \
    PLAY_MAILER_PORT=587 \
    PLAY_MAILER_SSL=false \
    PLAY_MAILER_TLS=true \
    PLAY_MAILER_USER="<your_user>@gmail.com" \
    PLAY_MAILER_PASSWORD="<your app password in gmail>" \
    TZ="Europe/Stockholm" \
    DATABASE_DRIVER=org.postgresql.Driver \
    JAVA_TOOL_OPTIONS=-Djava.net.preferIPv4Stack=true \
    JAVA_OPTS="-Xmx384m -Xss512k -XX:+UseCompressedOops -Dfile.encoding=UTF-8 -javaagent:/app/target/universal/stage/lib/io.codekvast.codekvast-agent-0.22.1.jar" \
    SBT_OPTS="-Dsbt.jse.engineType=Node" \
    SBT_CLEAN="true" \
    HOST_ALLOWED_1="<your application>.herokuapp.com" \
    $*

heroku buildpacks:clear $*
heroku buildpacks:add https://github.com/heroku/heroku-buildpack-locale $*
heroku buildpacks:add heroku/nodejs $*
heroku buildpacks:add heroku/scala $*
