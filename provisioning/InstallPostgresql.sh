#!/usr/bin/env bash

PG_VERSION=9.3

echo "*** Installing Postgresql, version $PG_VERSION ***"

apt-get update
apt-get install -y postgresql-${PG_VERSION}

su - postgres <<EOF

createdb signup
psql --quiet signup <<EOSQL
  create user signup4 password 's7p2+';
  grant all privileges on database signup to signup4;
EOSQL

EOF

apt-get install -y augeas-tools

augtool <<EOF

ins listen_addresses after /files/etc/postgresql/${PG_VERSION}/main/postgresql.conf/#comment[38]
set /files/etc/postgresql/${PG_VERSION}/main/postgresql.conf/listen_addresses '*'

set /files/etc/postgresql/${PG_VERSION}/main/pg_hba.conf/3/address all

save
quit

EOF

sudo /etc/init.d/postgresql restart

#dropdb signup
#drop owned by signup4;
#drop user signup4;
