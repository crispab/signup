# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

SignUp (v4) is an event sign-up web app for groups: users belong to groups, events belong to groups, and members register participation status per event. It is a Scala 2.12 / Play 2.6 app using Anorm over PostgreSQL, Silhouette for auth, and is deployed on Heroku.

## Toolchain constraints

- **Java 8 (or 9) is required.** `build.sbt` has an `initialize` check that aborts the build on any other JVM version.
- sbt 1.1.1, Play sbt-plugin 2.6.20, Scala 2.12.4. Play has breaking changes between minor versions — don't bump versions casually.
- `lib/` holds unmanaged Cloudinary jars (the managed `cloudinary-scala-play` dependency is commented out in `build.sbt`).

## Common commands

```bash
./docker_run_dev_db.sh   # start local Postgres 9 in Docker (db=signup, user=signup4) on :5432
sbt run                  # dev server with hot reload at http://localhost:9000
sbt compile
sbt stage                # builds target/universal/stage (used by Procfile on Heroku)
```

### Tests

Test packages are `se.crisp.signup4.{unit,integration,acceptance}` (the README's `unit.*` shorthand is outdated — use the full package).

```bash
sbt 'testOnly se.crisp.signup4.unit.*'
sbt 'testOnly se.crisp.signup4.integration.*'      # needs the local Postgres running
sbt 'testOnly se.crisp.signup4.unit.util.HtmlHelperTest'   # single test class
./run_tests.sh           # all three suites, as CI does
```

- Integration tests use `GuiceOneAppPerSuite` against the configured DB (`db.default.url`, default local Docker Postgres). Evolutions auto-apply.
- Acceptance tests are ScalaTest/Selenium with headless Chrome, aggregated in `acceptance/MasterSuite` (`GuiceOneServerPerSuite` on port 19001). They need Chrome + ChromeDriver; pass `-Dwebdriver.chrome.driver=/path/to/chromedriver`, and set `APPLICATION_BASE_URL=http://localhost:19001` and `LANG=sv_SE` (see `run_tests.sh` / `.travis.yml`).

## Architecture

Source lives under `app/se/crisp/signup4/`:

- **controllers/** — Play `InjectedController`s; routes in `conf/routes`. Actions are secured with Silhouette (`silhouette.SecuredAction(WithPermission(Administrator))` etc.).
- **models/** — plain case classes (`Event`, `Group`, `User`, `Participation`, `Membership`, `Reminder`, `LogEntry`). **models/dao/** — `@Singleton` DAOs injected with `play.api.db.Database`, using hand-written Anorm SQL and `RowParser`s. There is no ORM; schema changes go in a new numbered file in `conf/evolutions/default/` (next after the highest existing number), which is auto-applied on startup.
- **silhouette/** + **modules/SilhouetteModule.scala** — auth wiring: cookie authenticator, credentials (legacy `MD5PasswordHasher` with `password.salt`) plus Google/Facebook OAuth2 (`controllers/SocialLogin`). Authorization is a two-value `Permission` (`Administrator` / `NormalUser`) checked via `WithPermission`. Silhouette's default secured/unsecured error handler modules are disabled in favour of `silhouette/ErrorHandler`.
- **services/** — `EventReminderActor` (Akka, bound in `modules/SignupModule` as `"event-reminder-actor"`) sends reminders via `MailReminder` (play-mailer) and `SlackReminder`. `Application` controller's eager startup schedules a daily `CheckEvents` at `event.reminder.send.time`; `Events`/`Participations` controllers also message the actor for ad-hoc reminders/cancellations.
- **util/** — injectable helpers used from controllers and Twirl views (`ThemeHelper`, `LocaleHelper`, `ExcelHelper` (Apache POI export), `AuthHelper`, etc.).
- **views/** — Twirl templates; `page.scala.html` is the layout.

### Theming

Two themes exist: `crisp` (default) and `b73`, selected by `APPLICATION_THEME`. Theme-specific assets live in `app/assets/themes/<theme>/` (each with a `<theme>-bootstrap.less`; only `*-bootstrap.less` files are compiled by sbt-less). `ThemeHelper.forTheme` substitutes `[THEME]` in asset paths, and some email templates are duplicated per theme under `views/events/<theme>/`. Changes to theme-specific emails/styles usually need to be made for both themes.

### Configuration & i18n

`conf/application.conf` sets local-dev defaults and overrides almost everything from env vars (`DATABASE_URL`, `PLAY_MAILER_*`, `CLOUDINARY_URL`, `SLACK_CHANNEL_URL`, `HOST_ALLOWED_n`, `APPLICATION_BASE_URL`, `LANG`, `TZ`, ...). Mail is mocked locally (`play.mailer.mock=true`). Allowed hosts filter is explicit — add new hostnames via `HOST_ALLOWED_n`. Supported languages are `sv` and `en` (`conf/messages.sv`, `conf/messages.en`); the app forces the JVM default locale/timezone from config at startup (default `sv_SE`, `Europe/Stockholm`). Add new message keys to both files.

## Deployment

Heroku, Java 1.8 via `system.properties`, start command in `Procfile`. `conf/heroku_config_template.sh` lists the env vars to set; full instructions are in `SettingUpHeroku.md`. Travis CI (`.travis.yml`) runs all tests on `master` and deploys to the `signup-ci-test` Heroku app.
