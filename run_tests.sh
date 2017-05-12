#!/usr/bin/env bash

export WEB_DRIVER_TYPE=phantomjs
export PHANTOMJS_BINARRY_PATH=/usr/local/bin/phantomjs
export APPLICATION_BASE_URL=http://localhost:3333

activator 'test-only acceptance.*'
