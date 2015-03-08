SignUp
======

General
-------

This is the fourth version of the SignUp Service.

- It is written in Scala and based on the Play Framework.
- Anorm is used for SQL Database access
- Heroku is used for deployment


### Play Framework ###

- Activator 1.2.12 (that's how you get the Play Framework these days) - http://www.playframework.org/
- Play 2.3.7 (comes with Activator) - http://www.playframework.org/
- Scala 2.11.1 (comes with Activator) - http://www.scala-lang.org/
- Anorm (part of Play) - http://www.playframework.com/documentation/2.3.x/ScalaAnorm

### Presentation ###

- Twitter Bootstrap 3.3.2 - http://getbootstrap.com/
- jQuery 1.11.1 (used by Twitter Bootstrap) - http://jquery.com
- bootstrap-wysiwyg - http://github.com/mindmup/bootstrap-wysiwyg
- jQuery Hotkeys Plugin (used by bootstrap-wysiwyg) - https://github.com/jeresig/jquery.hotkeys
- AddThisEvent 1.5.8 - http://addthisevent.com
- Apache Poi 3.10-FINAL - http://poi.apache.org

### Play plugins ###

- Play2.x module for Authentication and Authorization 0.13 - https://github.com/t2v/play2-auth
- Emailer Plugin 2.3.x - http://github.com/typesafehub/play-plugins/tree/master/mailer

### Run-time environment ###

- Heroku (general app server environment) - http://heroku.com
    * PostgreSQL add-on (SQL database)
    * PG Backup add-on (database backup)
    * SendGrid add-on (bulk email) - http://sendgrid.com
    * Cloudinary add-on (profile image storage) - http://cloudinary.com
    * NewRelic add-on (app monitoring) - http://newrelic.com
    * Papertrail add-on (log monitoring) - http://papertrailapp.com
- Gravatar.com (default profile image)


License, credits and stuff
--------------------------

Some clipart comes from http://openclipart.org and is Public Domain, see http://openclipart.org/share

The libraries, services and tools listed under General above is copyright and licensed by the respective creators and owners.

The rest of the code is Copyright 2012, 2013, 2014, 2015 by Mats Strandberg and Jan Grape and
licensed under the Apache License v2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Setting up a development environment
----------------------------------

In the SignUp development environment Vagrant is used to create a local deployment environment in a virtual machine
(VirtualBox) with a Java run-time, Play Framework (including Scala) and a PostgreSQL database pre-installed.

The source code tree is shared between your host computer (where you do your editing) and the virtual machine
(where Play is run).

### Get the source code ###

The source code is stored on GitHub and managed by the version control system Git. Follow the instructions on
https://help.github.com/articles/set-up-git/ to get going with Git and GitHub.

Get a copy of the source code for SignUp by typing on your command line:

    $ git clone https://github.com/crispab/signup.git

This will give you the latest version of the source code.


### Set up Vagrant ###

If you already have Vagrant on your local system, you just need to:

    $ cd signup
    $ vagrant up

To install and configure Vagrant, follow the instructions in [Using Vagrant](UsingVagrant.md).

### Run SignUp ###

Once you have the development environment set up you should be able to launch
SignUp from inside the virtual machine:

    $ vagrant ssh
    vagrant@vagrant-ubuntu-trusty-64:~$ cd /vagrant
    vagrant@vagrant-ubuntu-trusty-64:~$ activator run

And then point your browser on your local computer to [http://localhost:19000](http://localhost:19000)

### Acceptance tests ###

UI acceptance tests are implemented through cucumber and webdriver.



Deploy in production
------

Read [Setting up Heroku](SettingUpHeroku.md) to learn how to deploy a SignUp instance on Heroku.
