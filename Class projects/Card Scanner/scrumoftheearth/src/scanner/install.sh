# ----------------------------------------------------------------------------------------------
# Name: Johnathan Trachte
# Project: Attendance App - This is a full stack attendance tracking and management software.
# Purpose: Install all dependencies for NFC scanner functionality.
# ----------------------------------------------------------------------------------------------

# User warning
echo "Sudo privelages required for:"
echo "----- libnfc-bin installation"
echo "----- change ownership of /dev/ttyUSB0"
echo "----- editing /etc/nfc/libnfc.conf"
echo ""

# Requirements
echo "Script requires:"
echo "----- Run as root"
echo "----- NFC scanner connected"
echo ""

echo "====== BEGIN SCRIPT ======"
echo ""
echo ""

# Install libnfc-bin
sudo apt install libnfc-bin
sudo apt install libnfc-examples
echo ""

# Allow user direct access to /dev/ttyUSB0
sudo chown $USER /dev/ttyUSB0

# Change libnfc to connect to /dev/ttyUSB0
cd /etc/nfc
echo "device.connstring = \"pn532_uart:/dev/ttyUSB0\"" >> libnfc.conf

# End script
echo "Finished!"
exit
