# Network Application
## Overview
- This program creates connections between clients and server using TCP protocol. Clients send data to server then server read the data and send to cloud server using CloudMQTT server. Finally, our network application intendtion is to transfer the data from the cloud to an android application for analysis purposes.
- From the transport layer:
	+ To demonstrate the realistic scienarioes, there would be a tons of clients. Therefore, to handle plenty of connections, multiple threads are needed in this program. More specifically, there are two kinds of threads which are ReadThread and WriteThread are included in both Client.java and Server.java will be discussed later.
## How to use TCP application:
- Open the website CloudMQTT -> Control Panel -> Login -> Your CloudMQTT Instance(s) -> Details -> Websocket UI
- Run the file Server.java and Client.java respectively.
- You should see the data transfer from the program to CloudMQTT Console.
- Note: After running the following steps, you should restart your CloudMQTT Console in order to prevent the Connection Lost for the new program execution.
## TCP Interpretation
### Client.java
- Class: SocketClient: Which is used to create an object can be acted as a client and numerous objects are also allowed
	+ public void createSocket(): Open socket in the client to connect and create two threads for transfering the data.
	+ public void createWriteThread(): Create a thread to read data from censors (read from txt file in this case), send them to server when connected and sleep until the ReadThread notifies for the next transfer.
	+ public void createReadThread(): Create a thread to wait for response from the server after the data has been sent to the cloud and halt till the WriteThread has sent the data to Server.
### Server.java
- Class RWThread: Thread to read and write
	+ public void createReadThread(): Create thread to read data from clients and wait until the WriteThread done its job.
	+ public void createWriteThread(): Create thread to send data to cloud server and wait for the ReadThread receives the new data to continue executing.
- Class SocketThread:
	+ public void createConn(): Create a connection for each client request to connect the server.
## Android app
- Create interface include 3 function :
	+ Input bus id
	+ Button (track bus id)
	+ TextView: show all information of bus ( bus id, longtitude , attitude, timestamp )
- MainActivity.java:
	+ Use MQTT import
	+ Subscribe to mqtt by using client.subscribe() (line 83)
	+ Create Input and Output using EditText and TextView by java android 
	+ Set on click button when the button are clicked, it run client.setCallBack(new MqttCallback()) and run function If() to find correct bus id  
	+ To check the bus id:Use String[] split (line 147) 
	+ To show list of bus id:Use append(java android) 
