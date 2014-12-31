Using Vagrant
=============

Prerequisites
-------------

You need to install (no configuration, just install)

- Vagrant - https://www.vagrantup.com
- VirtualBox - https://www.virtualbox.org

Basic Usage
-----------

The idea is to run your IDE on your host machine, and everything else in a guest machine/VirtualBox.

1. Open a terminal and do `cd <SignUp-git-repo-directory>`.
1. Then you do `vagrant up` and wait while vagrant configure VirtualBox and download, install and configure everything else.
1. Now you can ssh into the guest machine by doing `vagrant ssh`
1. Go to the shared project/SignUp directory with `cd /vagrant`
1. Start the application with `activator run`

Note: When doing `activator run` I sometimes get an error the first time. Don't know why. I just do `activator run` again and it works.

To later stop the VirtualBox, you can do either of (from the host machine):

- `vagrant suspend` to save the state of everything in the VirtualBox and stop it
- `vagrant halt` to shutdown Ubuntu (and all processes) on the guest machine
- `vagrant destroy` to permanently remove the guest machine

What is installed by Vagrant on the VirtualBox?
-----------------------------------------------

### Ubuntu ###

- Ubuntu is installed
- Memory size is configured to 2 GB
- Port forward, SignUp application/web port 9000 is forwarded so that your can access SignUp with a browser from the host machine
- Port forward, PostgreSQL port 5432 is forwarded so that access is possible from the host machine

### PostgreSQL ###

- PostgreSQL is installed
- It's set up with database signup, user signup4 and password 's7p2+'
- It's configured to listen to all interfaces
- It's configured to accept connections from all hosts

### Java ###

- Java 7 is installed

### Play Framework ###

- The Play Framework (actually Activator) is installed. Application dependencies are downloaded on demand, for example when doing `activator run`.

The Play install is a little bit special. Libraries downloaded by Activator will be needed from the IDE. So Activator (with Play) is installed
in \<SignUp-git-repo-directory\>/opt/activator-*. As \<SignUp-git-repo-directory\> on the host machine is the same as /vagrant
on the guest machine, the play installation can be shared. The opt directory is in .gitignore so it will not be committed
to git.

