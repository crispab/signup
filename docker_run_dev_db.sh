#!/bin/sh -x

# As an alternative to run everything in VirtualBox using Vagrant it's possible
# to just run the PostgreSQL database in a Docker container and Play Framework
# in your host operating system.

# This launches a PostgreSQL database with the correct user, password and database
# name as a detached Docker container.
# Check with 'docker ps'
# Stop with 'docker stop postgres'
# Delete with 'docker rm postgres'
docker run --name postgres -e POSTGRES_USER=signup4 -e POSTGRES_PASSWORD=s7p2+ -e POSTGRES_DB=signup -p 5432:5432 -d postgres
