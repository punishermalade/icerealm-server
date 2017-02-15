icerealm-server project!
---------------------------

How to install it on your computer:
- Checkout the entire project into a folder on your computer
- In Eclipse, create a new project with the source that you downloaded.
- In Eclipse, export the project as a 'Runnable jar'
- Create a new folder and move the generated jar in it
- Run this command: java -jar icerealmserver.jar
- with any browser, go to: http://localhost
- If you see a web page, you successfully installed the server.


How to run a third party plugin:
- Create a new Java project in Eclipse
- Add the 'icerealm-server' project (or jar) as a librairies.
- Create a new class that implements RequestHandler
- Add a new entry in the icerealm.config.xml file. Follow the "WebServer" plugin as an example.
- Start/Restart the server with the command: java -jar icerealmserver.jar
- In the console, this line should be visible: Application "yourapp" started and listening on port 80
- If the plugin fails to start, check the console for the exception stack trace.


How to create a web application:
- Create a new Java project in Eclipse
- Add the 'icerealm-server' project (or jar) as a librairies.
- Create a new class that extends ChainedHTTPMethodHandler
- Add a <handler> entry in the config file.
- Start/Restart the server.
- In the console, this line should visible: "yourhandlername" handler has been instanciated
- All the handlers are managed by the WebServerHandler class.


Little bit of history
-------------------------
People might wonder, why did you write a new web server while there is already a lot of them available? Reason was simple, I was checking for a way to code something using the WebSocket protocol. However, I found it very hard and difficult to get one framework that would let me write a plugin easily. 

One night, after couple days of trying multiple open-source projects, I found a website that had a very simple WebSocket implementation. I was curious to see how I could just use this Java class. This is how the icerealm-server was born. Actually, it was reborn because I did another project before this one that was also a web server. But the code was ugly and did not support the WebSocket protocol.

I hope you'll enjoy progamming some plugin for this server and let me know if you'd like a feature implemented.



