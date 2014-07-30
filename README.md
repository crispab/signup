SignUp
======

General
-------

This is the fourth version of the SignUp Service.

- It is written in Scala and based on the Play Framework.
- Anorm is used for SQL Database access*
- Heroku is used for deployment

*) H2 when running locally, PostgreSQL on Heroku


### Play Framework ###

- Play 2.2.2 - http://www.playframework.org/
- Scala 2.10.4 (part of Play) - http://www.scala-lang.org/
- Anorm (part of Play) - http://www.playframework.com/documentation/2.2.x/ScalaAnorm

### Presentation ###

- Twitter Bootstrap 2.2.1 - http://bootstrapdocs.com/v2.2.1/docs/
- jQuery 1.9.0 (used by Twitter Bootstrap) - http://jquery.com
- bootstrap-wysihtml5 0.0.2 - http://jhollingworth.github.com/bootstrap-wysihtml5
- wysihtml5 0.3.0 (used by bootstrap-wysihtml5) - http://github.com/xing/wysihtml5
- AddThisEvent v1.5.1 - http://addthisevent.com

### Services ###
- Play2.x module for Authentication and Authorization 0.11 - https://github.com/t2v/play2-auth/blob/release0.11.0/README.md
- Emailer Plugin 2.2.0 - http://github.com/typesafehub/play-plugins/tree/master/mailer


### Run-time environment ###
- Heroku (general app server environment) - http://heroku.com
    * Heroku PostgreSQL add-on (SQL database)
    * Heroku SendGrid add-on (bulk email) - http://sendgrid.com
    * Heroku New Relic add-on (app monitoring) - http://newrelic.com


License, credits and stuff
--------------------------

The Twitter Bootstrap LESS files that are included in the source code
are Copyright 2012 Twitter, Inc and licensed under the Apache
License v2.0.
Modifications have been made by us to create a project specific look & feel.

Some clipart comes from http://clipart.org and is Public Domain, see http://openclipart.org/share 

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


Setting Up Development Environment
----------------------------------

### Basics ###

In order to set up the development environment you need to:

- Clone this project from GitHub
- Install Play 2.2.x
- Install Heroku Toolkit - https://toolbelt.heroku.com/ (Optional, only needed to deploy to Heroku)
- ```play eclipsify``` or ```play idea```

### "Persistent Database" - Postgres ###

When running SignUp as described above, the H2 database is used.
The H2 database is running in-memory, in-process. This is handy,
because of the simple setup, but each time you stop Play your database
will be gone. It would be nice to have a persistent database,
e.g. a disk based database. When running on Heroku, Postgres will be used.
For these reasons, a local installation of Postgres is good.

To install Postgres on MacOS X:

- Download Postgres from [postgres.org](http://www.postgresql.org/)
- Install a standard Postgres (You may have to restart your Mac as the installation fiddles with shared memory)
- Set the password for the DATABASE (super)user postgres when prompted
- Finish the installation
- On MacOS X: A UNIX user is created: 'PostgreSQL'
  This user seem to get some password that you cannot find out.
  Reset the password of the UNIX user 'PostgreSQL' to something you know, and something safe, as this is a real MacOS X user.
- Do some stuff from the command prompt to create a database and a database user

```
$ su - PostgreSQL
Password: <Enter the password of your UNIX user 'PostgreSQL'>
$ createdb signup
Password: <Enter the password of your DATABASE user 'postgres'>
$ psql -s signup
Password: <Enter the password of your DATABASE user 'postgres'>
signup=# create user signup4 password 's7p2+';
signup=# grant all privileges on database signup to signup4;
```

Now you can start SignUp with Postgres using

./playrunpostgres.sh

Run SignUp
----------

Once you have the development environment set up you should be able to do

```play run```

And the direct your browser to
[http://localhost:9000](http://localhost:9000)

If you have a local Postgres (installed as described above) you may start SignUp using

./playrunpostgres.sh

Deploy
------

Deployment is done to Heroku.

It runs at [http://signup4.herokuapp.com](http://signup4.herokuapp.com)

To be able to deploy to Heroku you must:

- Install Heroku toolbelt.
- Do a 'heroku login'.
- cd into the root of this application
- git remote add staging git@heroku.com:signup4.git
- git push staging master

I (Mats) had to do a 'heroku keys:add ~/.ssh/id_rsa.pub' on one of my
machines as I first got 'Permission denied (publickey).' when trying
to 'git push heroku master'

