#!/usr/bin/env bash

#export WEB_DRIVER_TYPE=phantomjs
#export PHANTOMJS_BINARRY_PATH=/usr/local/bin/phantomjs
export APPLICATION_BASE_URL=http://localhost:19001

export LANG=sv_SE

#sbt 'test-only se.crisp.signup4.unit.* se.crisp.signup4.integration.* se.crisp.signup4.acceptance.*'
sbt 'test-only se.crisp.signup4.web.*'
