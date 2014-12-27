#!/usr/bin/env bash

PLAY_VERSION=2.2.6
PLAY_ZIP=play-${PLAY_VERSION}.zip
INSTALL_DIR=/vagrant/opt
BIN_DIR=/home/vagrant/bin

echo "*** Installing Play, version $PLAY_VERSION ***"

apt-get install -y unzip

su - vagrant <<EOF

rm -r ${INSTALL_DIR}/play-*
mkdir ${INSTALL_DIR}
cd ${INSTALL_DIR}

wget --no-verbose http://downloads.typesafe.com/play/${PLAY_VERSION}/${PLAY_ZIP}
unzip ${PLAY_ZIP}
rm ${PLAY_ZIP}

mkdir ${BIN_DIR}
ln -s ${INSTALL_DIR}/play-${PLAY_VERSION}/play ${BIN_DIR}/play

EOF
