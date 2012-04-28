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

In order to set up the development environment you need to:

 - Clone this project from GitHub
 - Install Play 2.0
 - Install Heroku Toolkit - https://toolbelt.heroku.com/ (Optional, only needed to deploy to Heroku)
 - ```play eclipsify``` or ```play idea```
 
 
Run SignUp
----------

Once you have the development environment set up you should be able to do

```play run```

And the direct your browser to 
[http://localhost:9000](http://localhost:9000)

Deploy
------

Deployment is done to Heroku. 

* Install Heroku toolbelt. 
* Do a 'heroku login'.
* cd into the root of this application
* git remote add heroku git@heroku.com:signup4.git
* git push heroku master

I (Mats) had to do a 'heroku keys:add ~/.ssh/id_rsa.pub' on one of my
machines as I first got 'Permission denied (publickey).' when trying
to 'git push heroku master'