SignUp
======

General
-------

This is the fourth version of the SignUp Service.

 - It is written in Scala and based on the Play Framework.
 - Anorm is used for SQL Database access*
 - Heroku is used for deployment
 
*) H2 when running locally, Postgres on Heroku


### Play Framework ###

 - Play 2.0 - http://www.playframework.org/
 - Scala 2.9.1 (part of Play) - http://www.scala-lang.org/
 - Anorm (part of play) - http://www.playframework.org/documentation/2.0/ScalaAnorm

### Presentation ###

 - Twitter Bootstrap 2.0

Setting Up Development Environment
----------------------------------

### Basics ###

In order to set up the development environment you need to:

 - Clone this project from GitHub
 - Install Play 2.0
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
signup=# grant all privileges on signup to signup4;
```

Now you can start SignUp with Postgres using

```./playrunpostgres.sh``` 
 
 
Run SignUp
----------

Once you have the development environment set up you should be able to do

```play run```

And the direct your browser to 
[http://localhost:9000](http://localhost:9000)

If you have a local Postgres (installed as described above) you may start SignUp using:
```./playrunpostgres.sh```

Deploy
------

Deployment is done to Heroku. 

It runs at [http://signup4.herokuapp.com](http://signup4.herokuapp.com)

To be able to deploy to Heroku you must:

* Install Heroku toolbelt. 
* Do a 'heroku login'.
* cd into the root of this application
* git remote add heroku git@heroku.com:signup4.git
* git push heroku master

I (Mats) had to do a 'heroku keys:add ~/.ssh/id_rsa.pub' on one of my
machines as I first got 'Permission denied (publickey).' when trying
to 'git push heroku master'