Setting up a SignUp instance on Heroku
======

General
------

SignUp runs on Heroku. Actually the service can run in any environment that supports the Play Framework and Postgres, 
but since Heroku was used from start it's a lot easier to deploy in this environment.


Get access to the source code
------

Deployment on Heroku is done by submitting (pushing) source code to Heroku's Git repository. The source is 
automatically picked up, compiled and deployed on the Heroku servers.

The source code is managed by the version control system Git. If you don't have Git on your local computer already, 
download and install it from http://git-scm.com/downloads

Get a copy of the source code for SignUp by typing on your command line:

```
$ git clone https://github.com/crispab/signup.git
```

This will give you the latest version of the source code. 

Become a Heroku user
------

Heroku is the cloud service where SignUp is deployed. To use it, you have to have a Heroku account, so unless you 
already have one, go to https://www.heroku.com, choose "Sign up for free" (not referring to our application) and follow
the instructions.

Install the Heroku Toolbelt on your local computer
------

Although Heroku can be operated entirely via its web user interface (the dashboard) it can also be operated via a 
command line client - the Heroku Toolbelt. Sometimes this is more efficient.

Go to https://toolbelt.heroku.com, download the client installation package and run the installation.

Create an application instance on Heroku
------

Open up a new command/terminal window on your computer. If the installation of the Heroku Toolbelt was successful you
should now have a new command "heroku" available.

Change directory to the top level directory of the SignUp source code you retrieved earlier.

```
$ cd signup
``` 

Login to Heroku from the command line client (you only have to do this once) and create an application instance on
Heroku. Choose an application name like "signup-\<your name>".

```
$ heroku login
$ heroku apps:create signup-<your name> --region eu
```

Get add-ons for your new Heroku application
------

SignUp requires a number of Heroku add-ons to run, and some are just nice to have:

- Heroku Postgres add-on (SQL database)
- PG Backups add-on (database backup)
- SendGrid add-on (bulk email) - http://sendgrid.com
- Cloudinary add-on (profile image storage) - http://cloudinary.com
- NewRelic add-on (app monitoring) - http://newrelic.com
- Papertrail add-on (log monitoring) - http://papertrailapp.com

Get the add-ons via the command line interface:

```
$ heroku addons:add heroku-postgresql
$ heroku addons:add pgbackups:auto-month
$ heroku addons:add sendgrid
$ heroku addons:add cloudinary
$ heroku addons:add newrelic:stark
$ heroku addons:add papertrail
```

The above will select the cost free plans for each add-on. If you need better service level or more capacity, pick a paid 
plan for each add-on.

Configure environment variables for your Heroku application
------

SignUp's default configuration is for a development environment, but it can be overridden by setting environment
variables in the execution environment.

Edit and run the script [conf/heroku_config.sh](conf/heroku_config.sh) on your local machine with values from your 
Heroku application and add-ons.

Connect your local Git repository to Heroku's Git repository
------

Your local Git repository (the one created for you when you cloned the source code) needs to know about the repository on Heroku
so you can push the source code to Heroku for build and deploy.

First, find out the Git URL to your application's repository on Heroku:

```
$ heroku apps:info
=== signup-<your name>
Addons:        cloudinary:starter
               heroku-postgresql:hobby-dev
               newrelic:stark
               papertrail:choklad
               pgbackups:auto-month
               sendgrid:starter

Git URL:       git@heroku.com:signup-<your name>.git
Owner Email:   yourmail@yourdomain.com
Region:        eu
Repo Size:     7M
Slug Size:     194M
Stack:         cedar
Web URL:       http://signup-<your name>.herokuapp.com/
```

Use the Git URL printed above to create a remote called "heroku" in your local Git repository:

```
$ git remote add heroku git@heroku.com:signup-<your name>.git
```

Push the source code to Heroku and whitness the automatic deploy
------

Push the source code (master branch) to Heroku for build and deploy:

```
$ git push heroku master
```

If you get an error message about "Permission denied (publickey)" you might have to first do:

```
$ heroku keys:add ~/.ssh/id_rsa.pub
```

The above tells Heroku that it's OK for your Git client to push changes to your application's Git repository on Heroku.


Check that the application is running
------

Point your browser to ```http://signup-<your name>.herokuapp.com/``` and wait for the index page to load. Right after a 
deploy it may take some time for the Heroku servers to respond.

If you get nothing or an error page, access the logs via the Papertrail plugin on the Heroku Dashboard and find out what
went wrong: Open the application page in the Herou dashboard and click on the "Papertrail add-on".

Alternatively, check the logs from your local machine using the Heroku toolbelt:
```
$ heroku logs
```
