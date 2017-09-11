#!/usr/bin/env bash

export WEB_DRIVER_TYPE=phantomjs
export PHANTOMJS_BINARRY_PATH=/usr/local/bin/phantomjs
export APPLICATION_BASE_URL=http://localhost:3333

export LANG=sv_SE

activator 'test-only unit.* integration.* acceptance.*'
#activator 'test-only unit.* integration.*'
#activator 'test-only acceptance.*'
