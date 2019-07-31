# STM32 - Android - server GPS info collector

The aim of this project is to use STM32 to collect device's location information, send it to an Android phone via bluetooth, after compressing calculation of Android phone, the result will return to STM32 and be uploaded to a remote server to store in a database.

This is a project with three parts.

- STM32 with GPS, Bluetooth and 4G Module. 

- Android Phone. In this case, I use a Huawei P10 with the latest system update.

- A remote server with a public IP.

## Deploy

### STM32

With Keil, just build the project and flash it into the board.

### Android

With Android Studio, build and install the app  to the connected android phone.

### Server

1. Change to the backend branch and edit the mysql server’s information like host, user, password ans etc.

2. upload the backend branch to the server, install the pip requirements.

3. Open the port `10086` in the server’s firewall setting.

4. run the command `nohup gunicorn -c gun.py app:app > app.log 2>&1 &`, It will be running in background.
