#!/usr/bin/env bash


export APPLICATION_BASE_URL=http://localhost:19001

export LANG=sv_SE

sbt -Dwebdriver.chrome.driver=/usr/local/bin/chromedriver 'testOnly se.crisp.signup4.unit.* se.crisp.signup4.integration.* se.crisp.signup4.acceptance.*'
