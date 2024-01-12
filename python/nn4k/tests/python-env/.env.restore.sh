#!/bin/bash

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

if [ -f ${_SCRIPT_DIR_PATH}/.env/requirements.txt ]
then
    exit
fi

set -e
rm -rf ${_SCRIPT_DIR_PATH}/.env
python3 -m venv ${_SCRIPT_DIR_PATH}/.env
source ${_SCRIPT_DIR_PATH}/.env/bin/activate
python -m pip install --upgrade pip
python -m pip freeze > ${_SCRIPT_DIR_PATH}/.env/requirements.txt
