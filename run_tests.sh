#!/usr/bin/env bash

export WEB_DRIVER_TYPE=phantomjs
export PHANTOMJS_BINARRY_PATH=/usr/local/bin/phantomjs
export APPLICATION_BASE_URL=http://localhost:3333

export LANG=sv_SE

sbt 'test-only se.crisp.signup4.unit.* se.crisp.signup4.integration.* se.crisp.signup4.acceptance.*'
#activator 'test-only se.crisp.signup4.unit.* se.crisp.signup4.integration.*'
#activator 'test-only se.crisp.signup4.acceptance.*'
