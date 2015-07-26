#!/usr/bin/env bash

# Got this from https://www.digitalocean.com/community/tutorials/how-to-install-java-on-ubuntu-with-apt-get
# and http://stackoverflow.com/questions/19275856/auto-yes-to-the-license-agreement-on-sudo-apt-get-y-install-oracle-java7-instal

echo "*** Installing Java ***"

apt-get install -y python-software-properties
add-apt-repository ppa:webupd8team/java
apt-get update
echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections
apt-get install -y oracle-java8-installer
