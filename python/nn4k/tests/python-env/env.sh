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
