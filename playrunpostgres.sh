#!/bin/sh
# Start play overriding database config with config for PostgreSQL (rather than H2)

# export SMTP_MOCK=false
# export SMTP_HOST=<host>
# export SMTP_PORT=25
# export SMTP_SSL=true
# export SMTP_TLS=true
# export SMTP_USER=<user>
# export SMTP_PASSWORD=<password>

play debug -Dconfig.file=conf/postgres.conf run

