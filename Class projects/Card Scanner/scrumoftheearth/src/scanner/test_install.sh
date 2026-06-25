# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Test installation of all dependencies for NFC scanner functionality.
# ----------------------------------------------------------------------------------------------

# Requirements
echo "Script requires:"
echo "----- install.sh has been run"
echo ""

echo "====== BEGIN SCRIPT ======"
echo ""
echo ""

# Test libnfc-bin installed (specifically nfc-list functionality)
cd /bin
NFC_LIST=$(ls | grep 'nfc-list')
[[ $NFC_LIST == "nfc-list" ]] && echo "PASS- libnfc installed successfully" || echo "FAIL- libnfc not installed successfully"

# Test libnfc-examples installed (specifically nfc-poll functionality)
NFC_POLL=$(ls | grep 'nfc-poll')
[[ $NFC_POLL == "nfc-poll" ]] && echo "PASS- libnfc-examples installed successfully" || echo "FAIL-libnfc-examples not installed successfully"

# Test if user has access to /dev/ttyUSB0 
OWNERSHIP="$(ls -l /dev/ttyUSB0 | awk '{print $1}')" 
[[ $OWNERSHIP = "crw-rw----" ]] && echo "PASS- User has access to /dev/ttyUSB0" || echo "FAIL- User does not have access to /dev/ttyUSB0"

# Test if last line of /etc/nfc/libnfc.conf is uncommented
COMMENT=$(tail -1 /etc/nfc/libnfc.conf | grep '#')
[[ $COMMENT == '' ]] && echo "PASS- Device connects to /dev/ttyUSB0" || echo "FAIL- Device does not connect to /dev/ttyUSB0"















