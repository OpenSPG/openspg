# Copyright 2023 OpenSPG Authors
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software distributed under the License
# is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied.

_SCRIPT_FILE_PATH="${BASH_SOURCE[0]}"
while [ -h ${_SCRIPT_FILE_PATH} ]
do
    _SCRIPT_DIR_PATH=$(cd -P "$(dirname ${_SCRIPT_FILE_PATH})" && pwd)
    _SCRIPT_FILE_PATH=$(readlink ${_SCRIPT_FILE_PATH})
    case ${_SCRIPT_FILE_PATH} in
        /*) ;;
        *) _SCRIPT_FILE_PATH=${_SCRIPT_DIR_PATH}/${_SCRIPT_FILE_PATH} ;;
    esac
done
_SCRIPT_DIR_PATH=$(cd -P "$(dirname ${_SCRIPT_FILE_PATH})" && pwd)

${_SCRIPT_DIR_PATH}/.env.restore.sh
source ${_SCRIPT_DIR_PATH}/.env/bin/activate
