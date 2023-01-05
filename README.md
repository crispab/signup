SignUp
======
[![Build Status](https://travis-ci.org/crispab/signup.svg?branch=master)](https://travis-ci.org/crispab/signup)
General
-------

This is the fourth version of the SignUp Service.

- It is written in Scala and is based on the Play Framework.
- Heroku is used for deployment

### Play Framework ###
- Play 2.6.12 - http://www.playframework.org/
- Scala 2.12.4 - http://www.scala-lang.org/
- Anorm 2.6.1 - https://www.playframework.com/documentation/2.6.x/Anorm

### Presentation ###
- Bootstrap 3.3.4 - http://getbootstrap.com/
- jQuery 2.1.4 - http://jquery.com
- bootstrap3-wysiwyg 0.3.3 - https://github.com/bootstrap-wysiwyg/bootstrap3-wysiwyg
- AddThisEvent 1.6.0 - http://addthisevent.com
- Apache Poi 3.10-FINAL - http://poi.apache.org

### Play 3rd party modules ###
- Silhouette 5.0.7 authentication library - https://www.silhouette.rocks/s
- Emailer Plugin 6.0.1 - https://github.com/playframework/play-mailer

### Run-time environment ###
- Heroku (general app server environment) - http://heroku.com
    * PostgreSQL add-on (SQL database)
    * Cloudinary add-on (profile image storage) - http://cloudinary.com
    * Papertrail add-on (log monitoring) - http://papertrailapp.com
    * Locale build pack (for i18n on Heroku)
    * An external SMTP server for sending email remiders (e.g. smtp.gmail.com)
- Gravatar.com (default profile image)

### Test environment ###
Libraries:
- ScalaTest + Play (unit test framework) - http://www.scalatest.org/plus/play
- Selenium Web Driver (included with ScalaTest + Play)
- Chromedriver 2.34 (actually a binary) - https://sites.google.com/a/chromium.org/chromedriver/home

Services:
- Postgression (test database on demand) - http://www.postgression.com
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
sbt 'testOnly unit.*'
```

#### Integration tests ####
These tests mainly do database related tests.

To execute the integration tests:
```
sbt 'testOnly integration.*'
```

#### Acceptance tests ####
The acceptance tests arr made BDD style in ScalaTst using Selenium and headless Chrome. They require the binaries 
Chrome and ChromeDriver to be installed in the test environemnt.

To execute the acceptance tests:
```
sbt 'testOnly acceptance.*'
```

#### Continuous integration ####
The CI server builds, run all tests and deploy to a staging environment, https://signup-ci-test.herokuapp.com

Service:
- Travis CI - https://travis-ci.org/crispab/signup

License, credits and stuff
--------------------------
Some clipart comes from http://openclipart.org and is Public Domain, see http://openclipart.org/share

The libraries, services and tools listed under General above is copyright and licensed by the respective creators and owners.

The rest of the code is Copyright 2012, 2013, 2014, 2015, 2016, 2017, 2018 by Mats Strandberg and Jan Grape and
licensed under the Apache License v2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Setting up a development environment using Docker for the database
----------------------------------
The PostgreSQL development database runs in a separate Docker container. The rest of the application can
be launched in your normal host operating system where you also have your source code.

This makes the Play Framework able to detect source code changes and 
initiate re-compilation and reloading.

### Get the source code  ###
The source code is stored on GitHub and managed by the version control system Git. Follow the instructions on
https://help.github.com/articles/set-up-git to get going with Git and GitHub.

Get a copy of the source code for SignUp by typing on your command line:

    $ git clone https://github.com/crispab/signup.git

This will give you the latest version of the source code.

### Set up Play Framework ###
The Play Framework is installed and run in your normal host operating system. Download and install 
Play from https://www.playframework.com

SignUp is created with Play Framework version 2.4 so it's recommended you get that version. Later versions might work, 
but Play is known to introduce breaking changes between minor versions so there is no guarantee that version 2.5 or 
later will work.

### Set up Docker ###
If you already have Docker on your local system, you just need to:

    $ cd signup
    $ ./docker_run_dev_db.sh

To install Docker, follow the instructions in https://www.docker.com/. Although Docker is based on Linux 
there are official solutions for running Docker on Mac OS X and in Windows as well.

### Run SignUp ###
Once you have the development database running in a Docker container you can launch Play using sbt:

    $ cd signup
    $ sbt run

And then point your browser on your local computer to [http://localhost:9000](http://localhost:9000)


Deploy in production
------
Read [Setting up Heroku](SettingUpHeroku.md) to learn how to deploy a SignUp instance on Heroku.
