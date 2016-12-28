SignUp
======
[![Build Status](https://travis-ci.org/crispab/signup.svg?branch=master)](https://travis-ci.org/crispab/signup)
General
-------

This is the fourth version of the SignUp Service.

- It is written in Scala and is based on the Play Framework.
- Heroku is used for deployment

### Play Framework ###
- Activator 1.3.5 (that's how you get the Play Framework these days) - http://www.playframework.org/
- Play 2.3.7 (comes with Activator) - http://www.playframework.org/
- Scala 2.11.4 - http://www.scala-lang.org/
- Anorm (part of Play) - http://www.playframework.com/documentation/2.3.x/ScalaAnorm

### Presentation ###
- Bootstrap 3.3.4 - http://getbootstrap.com/
- jQuery 2.1.4 - http://jquery.com
- bootstrap3-wysiwyg 0.3.3 - https://github.com/bootstrap-wysiwyg/bootstrap3-wysiwyg
- AddThisEvent 1.6.0 - http://addthisevent.com
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

### Test environment ###
Libraries:
- ScalaTest + Play (unit test framework) - http://www.scalatest.org/plus/play
- JUnit 4.12 (unit test framework in Java)
- Cucumber Java 1.2 (BDD style acceptance test framework)
- Selenium Web Driver 2.45 (web bowser driven tests)
- PhantomJS (WebKit based headless web browser) - http://phantomjs.org
- phantomjsdriver 1.2.1 (glue between Selenium Web Driver and PhantomJS)

Services:
- Postgression (test database on demand) - http://www.postgression.com
- Relish (documentaion) - http://www.relishapp.com/crisp/signup/docs
- Mailinator (temporary mail boxes on demand) - https://mailinator.com
- Travis CI (CI server) - https://travis-ci.org/crispab/signup

### Testing ###
SignUp is far from a good example when it comes to automated testing.

This incarnation of SignUp (the fourth) was conceived with the intention of trying out the Play 2 framework and its 
highly interactive way of developing; write some code and immediately experience the changes in the browser, write 
some more code... 

While this truly is awesome in many ways, one discovery we have made is that we got caught up in the 
edit-(manual)test-cycle to the degree that very few automated tests were written as the application evolved. 
A lesson learned.

The test cases that do exist are divided into three categories: unit, integration and acceptance.

#### Unit tests ####
To execute the unit tests:
```
activator 'test-only unit.*'
```

#### Integration tests ####
These tests mainly do database related tests.

To execute the integration tests:
```
activator 'test-only integration.*'
```

#### Acceptance tests ####
The acceptance tests (step functions) are actually written in Java (and not Scala).

To execute the acceptance tests:
```
activator 'test-only acceptance.*'
```

#### Continuous integration ####
The CI server builds, run all tests and deploy to a staging environment, https://signup-ci-test.herokuapp.com

Service:
- Travis CI - https://travis-ci.org/crispab/signup

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


Alternative 1: Setting up a development environment using Vagrant
----------------------------------
In the SignUp development environment Vagrant is used to create a local deployment environment in a virtual machine
(VirtualBox) with a Java run-time, Play Framework (including Scala) and a PostgreSQL database pre-installed.

The source code tree is shared between your host computer (where you do your editing) and the virtual machine
(where Play is run).

### Get the source code ###
The source code is stored on GitHub and managed by the version control system Git. Follow the instructions on
https://help.github.com/articles/set-up-git to get going with Git and GitHub.

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


Alternative 2: Setting up a development environment using Docker
----------------------------------
Instead of running the whole development environment in a VirtualBox using Vagrant, it's really enough
to just run the PostgreSQL development database in separate container. The rest of the application can
be launched in your normal host operating system where you also have your source code.

This is a bit snappier and also makes the Play Framework better at detecting source code changes and 
initiate re-compilation and reloading.

### Get the source code (same as for the Vagrant option) ###
The source code is stored on GitHub and managed by the version control system Git. Follow the instructions on
https://help.github.com/articles/set-up-git to get going with Git and GitHub.

Get a copy of the source code for SignUp by typing on your command line:

    $ git clone https://github.com/crispab/signup.git

This will give you the latest version of the source code.

### Set up Play Framework ###
In this solution, Play Framework is installed and run in you normal host operating system. Download and install 
Play from https://www.playframework.com

SignUp is created with Play Framework version 2.3 so it's recommended you get that version. Later versions might work, 
but Play is known to introduce breaking changes between minor versions so there is no guarantee that version 2.4 or 
later will work.

### Set up Docker ###
If you already have Docker on your local system, you just need to:

    $ cd signup
    $ ./docker_run_dev_db.sh

To install Docker, follow the instructions in https://www.docker.com/. Although Docker is based on Linux 
there are official solutions for running Docker on Mac OS X and in Windows as well.

### Run SignUp ###
Once you have the development database running in a Docker container you can launch Play using activator:

    $ cd signup
    $ activator run

And then point your browser on your local computer to [http://localhost:9000](http://localhost:9000)


Deploy in production
------
Read [Setting up Heroku](SettingUpHeroku.md) to learn how to deploy a SignUp instance on Heroku.
