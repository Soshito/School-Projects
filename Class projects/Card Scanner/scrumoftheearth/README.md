# ScrumOfTheEarth
## Attendance App
* Gavin Papenthien
* Sam Miller
* John Trachte
* Dylan Brodie

## API
Backend RESTful API \
Built with Java 21 and Spring Boot 3.2.2 \
Build Tool - Apache Maven

Authoritative system for all business logic & resource CRUD. \
Fully implemented stateless JWT authentication & User role-based authorization.

**Dependancies:**
* Apache Maven 3.9.6 https://maven.apache.org/download.cgi
* All other dependancies listed in `pom.xml` will be automatically installed by Maven.
* Java 21

**Build Instructions:**
* Open IntelliJ IDE
* Select `file --> open`
* Navigate to API source download
* Select `pom.xml`
* Choose "Open as project"
* Run `ApiApplication` file

**Test Reproduction:**
* Open in IntelliJ IDE with the same method as Build Instructions.
* Navigate to `src/test/java/attendanceapp.api` in the Project Explorer
* Right click the `attendanceapp.api` package
* Hover "More Run/Debug" on the dropdown
* Select "Run 'tests in "attendanceapp.api'" with Coverage"
* If prompted in bottom right corner of IDE, accept "Enable Lombok Generation"
* When tests are complete running, a report should appear on the right side of the IDE.
* If prompted with popup, select "Replace Active Suites"

**Warning:** If tests are ran at 11:50pm or later, they will crash. The tests are reliant on automatically generated data using SQL queries that add ten minutes to the current local time and does not wrap into the next day, causing failure.

## Database

**Dependancies:**
* pgAdmin4 install
* Latest PostgreSQL version
* libnfc-bin
* libnfc-examples
* Ubuntu device


**Build Instructions:**
* Open pgAdmin4
* Connect to server or create one
* Write queries to create needed tables for database
* Write `SELECT` queries to check if columns created successfully
* Add things to table using the `INSERT INTO` command

## Scanner
**Ubuntu Setup**
* Run scanner/install.sh as root
* Create relevant roomData.txt in /usr directory
* Run script `main` in `src/scanner/scanner/src/main/dist`

**Dependancies:**
* Python
* PySerial

**Build Instructions:**
* Open Pycharm IDE
* Select `file --> open`
* Navigate to Scanner source download
* Open in Pycharm
* Connect CP2102 USB to UART (NFC reader module)
* Connect microcontroller
* Run `main.py`

**Test Reproduction**
* Open in Pycharm IDE with the same method as Build Instructions
* Open system terminal in Pycharm
* Run command `pytest --cov=src --cov-report term-missing`
**Notes:** 
Tests on the functionality of the microcontroller will require a pass with a jumper between pin 14 and 3v3 and a pass with no jumper. 
Tests on the functionality of the API will require that a student with an ID corresponding to the NFC card is enrolled within the system in a class occuring during time of test.
Tests on the functionality of the NFC scanner module will require a pass with a short card ID, a pass with a long card ID, and a pass with the NFC scanner module unplugged.
Function `run()` in `start_manager.py` lacks test functions; `run()` consists of an infinite loop, and is impossible to test by code.

## Frontend 

**Dependancies:**
* Node js
* React
* IDE capable of runnin java script

**Build Instructions:**
* (recommended) Install newest VS Code
* Install newest nodejs LTS version for your operating system
* open ternminal in VS Code and run this line: npm install npm-scripts
* then run this line: npm install universal-cookie