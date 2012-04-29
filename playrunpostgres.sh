#!/bin/sh
# Start play overriding database config with config for Postgres (rather than H2)

play -Dconfig.file=conf/postgres.conf run

