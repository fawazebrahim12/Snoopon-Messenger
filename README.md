# SNOOP ON README #

There are two things that needs to be done before getting started

1.Setting up the server and 2.installing app in the device

### For setting up the server ###

* You need a database(to store users’ details)
* And Node.js (which is your server)

### Database set up ###

I've used mongodb

Install MongoDB. Execute the following command lines to create a database and get it ready for accepting connections . Mongodb data will be stored in the folder data.

$ mkdir data
$ echo 'mongod --bind_ip=$IP --dbpath=data --nojournal --rest "$@"' > mongod
$ chmod a+x mongodn

You can start mongodb by running the mongod script on your project root:

$ ./mongod

If everything goes fine then your database is set up and is waiting for connections.

### Server (node.js) set up ###

Download the node.js folder. and execute the following lines in your terminal

* cd node-chat
* node app (runs node app)

You will get something like

"Server is running at 127.0.0.1:8080" #You can change the port and ip in node.js file.

That's it Server is ready.............

### Installing apk in your device ###

* Download the Androidchat2 app
* find the .apk file in \AndroidChat2\app\build\outputs\apk\app-debug.apk"
* Install the file in your device

You need google services pre-installed in your device.