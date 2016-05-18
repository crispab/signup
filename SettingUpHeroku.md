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

    $ git clone https://github.com/crispab/signup.git


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

    $ cd signup

Login to Heroku from the command line client (you only have to do this once) and create an application instance on
Heroku. Choose an application name like "signup-\<your name>".

    $ heroku login
    $ heroku apps:create signup-<your name> --region eu

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

    $ heroku addons:create heroku-postgresql
    $ heroku addons:create sendgrid
    $ heroku addons:create cloudinary
    $ heroku addons:create newrelic
    $ heroku addons:create papertrail

The above will select the cost free plans for each add-on. If you need better service level or more capacity, pick a paid
plan for each add-on.

Connect your local Git repository to Heroku's Git repository
------

Your local Git repository (the one created for you when you cloned the source code) needs to know about the repository on Heroku
so you can push the source code to Heroku for build and deploy.

First, find out the Git URL to your application's repository on Heroku:

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

Use the Git URL printed above to create a remote called "heroku" in your local Git repository:

    $ git remote add heroku git@heroku.com:signup-<your name>.git

Optional: Enable Google login
------

It's possible to allow users to login via Google (OAuth2) instead of providing email and SignUp password. This only
works for users who are already created in SignUp and have their Google (GMail or Google Apps) email address in their
user profile.

To enable this you have to create an API project in the Google Developers Console. Go to
https://console.developers.google.com and choose to create a new project. Name it something useful like "signup-login".
This step may take a moment.

Once the project is created you'll be presented the project's dashboard. Choose `APIs & auth -> Create new Client ID -> Web application`.
Remove anything under "Authorized Javascipt Origins" and store the path
"http://signup-\<your name>.herokuapp.com/google/callback" in "Authorized Redirect URI". Choose `Create Client ID`.

If you want to you can configure the Consent Screen to suit your needs.

Finally, go to `APIs & auth -> APIs` and enable the Google+ API.


Configure environment variables for your Heroku application
------

SignUp's default configuration is for a development environment, but it can be overridden by setting environment
variables in the execution environment.

Copy the script [conf/heroku_config_template.sh](conf/heroku_config_template.sh) on your local machine.

    $ cp heroku_config_template.sh heroku_config_current.sh
    
Edit [conf/heroku_config_current.sh](conf/heroku_config_current.sh) and set the values that apply to your SignUp4 instance.

Finally run `heroku_config_current.sh` (it contains an Heroku toolbelt command to set up your applications environment).


| Environment variable | Description |
| --------------------:| ----------- |
| APPLICATION_BASE_URL | The public web URL to the application on Heroku. It's used when generating links in mail reminders. Get the value from the `heroku apps:info` command above. |
| ADDTHISEVENT_LICENSE | You can do without a license key for this library, but the menu presented to add calendar events to your on-line calendar will contain a message from the provider. |
| CLOUDINARY_FOLDER    | The folder in the Cloudinary media library where production user profile images should be stored. It will be automatically created on Cloudinary. |
| PASSWORD_SALT | A password salt helps encrypt the user's passwords more safely in the database. Set it to a random string of characters. |
| SLACK_CHANNEL_URL | If you use http://slack.com for group chat and want notifications on a chat channel, create an incoming WebHooks integration on slack.com and set this variable to your unique WebHooks URL. If you don't use Slack, don't set this variable at all. |
| GOOGLE_CLIENT_ID | To enable Google login - get from Google Developers Console, https://console.developers.google.com |
| GOOGLE_CLIENT_SECRET | To enable Google login - get from Google Developers Console, https://console.developers.google.com |
| FACEBOOK_CLIENT_ID | To enable Facebook login - get from Facebook's developer site, https://developers.facebook.com/ |
| FACEBOOK_CLIENT_SECRET | To enable Facebook login - get from Facebook's developer site, https://developers.facebook.com/ |
| EVENT_REMINDER_FIRST_DAYS | Number of days before the event that the first reminder is sent. Defaults to 7 (Note 1) |
| EVENT_REMINDER_SEND_TIME | The time of day that automatic reminders are sent. Defaults to 01:00 (Note 1) |

Note 1) Default values mentioned here are "snapshot values" valid when this documentation was last updated. 
Checking `application.conf` is always the best source of information for default values.

Push the source code to Heroku and whitness the automatic deploy
------

Push the source code (master branch) to Heroku for build and deploy:

    $ git push heroku master

If you get an error message about "Permission denied (publickey)" you might first have to do:

    $ heroku keys:add ~/.ssh/id_rsa.pub

The above tells Heroku that it's OK for your Git client to push changes to your application's Git repository on Heroku.

Set password for admin user in the application
------
By setting the environment variable PASSWORD_SALT the passwords in the database will be encrypted more safely. 
However, since you just changed the salt, this means that the existing default "admin" user has a password encrypted the wrong way.
So we have to update it manually.

Open a shell on your Heroku server:
    
    $ heroku run bash
    Running bash on signup-<your name>... up, run.7816
    ~ $ 

Start the PostgreSQL interpreter
   
    ~ $ psql --set "salt='$PASSWORD_SALT'" $DATABASE_URL
    psql (9.5.1, server 9.3.9)
    SSL connection (protocol: TLSv1.2, cipher: DHE-RSA-AES256-GCM-SHA384, bits: 256, compression: off)
    Type "help" for help.
    
    d94mbv2v0vv2dc=>

Update the password for the "admin" user, quit psql and exit the shell

    d94mbv2v0vv2dc=> update users set pwd=md5('mypassword' || :salt) where email='admin@crisp.se';
    d94mbv2v0vv2dc=> \q
    ~ $ exit
    $

Once the system is up & running (next section) you can login using:
- Email: admin@crisp.se
- Password: mypassword (or whatever you set above)
    
Check that the application is running
------

Point your browser to `http://signup-<your name>.herokuapp.com/` and wait for the index page to load. Right after a
deploy it may take some time for the Heroku servers to respond.

If you get nothing or an error page, access the logs via the Papertrail plugin on the Heroku Dashboard and find out what
went wrong: Open the application page in the Herou dashboard and click on the "Papertrail add-on".

Alternatively, check the logs from your local machine using the Heroku toolbelt:

    $ heroku logs
