#!/bin/sh -x

# This launches a PostgreSQL database with the correct user, password and database
# name as a detached Docker container.
docker run --name postgres -e POSTGRES_USER=signup4 -e POSTGRES_PASSWORD=s7p2+ -e POSTGRES_DB=signup -p 5432:5432 -d postgres:9

# Some nice docker commands:
# Check with 'docker ps'
# Stop with 'docker stop postgres'
# Start with 'docker start postgres'
# Delete with 'docker rm postgres'
