#!/usr/bin/env bash

ACTIVATOR_VERSION=1.2.12
ACTIVATOR_ZIP=typesafe-activator-${ACTIVATOR_VERSION}-minimal.zip
INSTALL_DIR=/vagrant/opt
BIN_DIR=/home/vagrant/bin

echo "*** Installing Activator, version $ACTIVATOR_VERSION ***"

apt-get install -y unzip

su - vagrant <<EOF

rm -r ${INSTALL_DIR}
mkdir ${INSTALL_DIR}
cd ${INSTALL_DIR}

wget --no-verbose http://downloads.typesafe.com/typesafe-activator/${ACTIVATOR_VERSION}/${ACTIVATOR_ZIP}
unzip ${ACTIVATOR_ZIP}
rm ${ACTIVATOR_ZIP}

mkdir ${BIN_DIR}
ln -s ${INSTALL_DIR}/activator-${ACTIVATOR_VERSION}-minimal/activator ${BIN_DIR}/activator

EOF

