SignUp
======

General
-------

This is the fourth version of the SignUp Service.

- It is written in Scala and based on the Play Framework.
- Anorm is used for SQL Database access
- Heroku is used for deployment


### Play Framework ###

- Play 2.2.2 - http://www.playframework.org/
- Scala 2.10.3 (part of Play) - http://www.scala-lang.org/
- Anorm (part of Play) - http://www.playframework.com/documentation/2.2.x/ScalaAnorm

### Presentation ###

- Twitter Bootstrap 2.2.1 - http://bootstrapdocs.com/v2.2.1/docs/
- jQuery 1.9.0 (used by Twitter Bootstrap) - http://jquery.com
- bootstrap-wysihtml5 0.0.2 - http://jhollingworth.github.com/bootstrap-wysihtml5
- wysihtml5 0.3.0 (used by bootstrap-wysihtml5) - http://github.com/xing/wysihtml5
- AddThisEvent 1.5.1 - http://addthisevent.com
- Apache Poi 3.10-FINAL - http://poi.apache.org

### Play plugins ###

- Play2.x module for Authentication and Authorization 0.11 - https://github.com/t2v/play2-auth/blob/release0.11.0/README.md
- Emailer Plugin 2.2.0 - http://github.com/typesafehub/play-plugins/tree/master/mailer

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

The Twitter Bootstrap LESS files that are included in the source code
are Copyright 2012 Twitter, Inc and licensed under the Apache
License v2.0.
Modifications have been made by us to create a project specific look & feel.

Some clipart comes from http://openclipart.org and is Public Domain, see http://openclipart.org/share 

The rest of the code is Copyright 2012, 2013, 2014 by Mats Strandberg and Jan Grape and
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
    vagrant@vagrant-ubuntu-trusty-64:~$ play run

And then point your browser on your local computer to [http://localhost:9000](http://localhost:9000)


Deploy in production
------

Read [Setting up Heroku](SettingUpHeroku.md) to learn how to deploy a SignUp instance on Heroku.
