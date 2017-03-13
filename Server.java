import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class RWThread{
	private byte [] arrBytes;
	private Socket s;
	private InputStream inStream;
	private OutputStream outStream;
	private volatile boolean has_data = false;
	
	RWThread(Socket s) throws IOException{
		this.s = s;
		inStream = s.getInputStream();
		outStream = s.getOutputStream();
	}
	
	public void createReadThread(){
		Thread readthread = new Thread(){
			public void run(){
				while(s.isConnected()){
					byte[] buffer = new byte[200];

					try {
						synchronized(s){
							if(!has_data){
								System.out.println("Thread " + Thread.currentThread().getId() + " is ready to send data to the cloud");
								//Wait until the client send data and store it in the buffer
								int num = inStream.read(buffer);
								//num > 0 means the data has been received successfully
								if(num > 0){
									arrBytes = new byte [num];
									System.arraycopy(buffer, 0, arrBytes, 0, num);
									/*Just for tesing in some cases:
									String msg = new String(arrBytes, "UTF-8");
									System.out.println(name + Thread.currentThread().getId() + " : " + msg;*/
									has_data = true;
									s.notify();
									System.out.println("Thread " + Thread.currentThread().getId() + " is waiting to receive data from client");
									s.wait();
								}
								else{
									s.close();
								}
							}
						}


					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}



			}
		};
		readthread.start();

	}

	public void createWriteThread() {
		Thread writethread = new Thread() {
			public void run(){
				//This portion of code is to setup the connection between server and MQTT cloud
				String topic        = "GPS";
				int qos             = 1;
				String broker       = "tcp://m10.cloudmqtt.com:14062";

				//MQTT client id to use for the device. "" will generate a client id automatically
				String clientId     = "";
				MemoryPersistence persistence = new MemoryPersistence();
				try {
					MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
					mqttClient.setCallback(new MqttCallback() {
						public void messageArrived(String topic, MqttMessage msg)
								throws Exception {
							System.out.println("Recived:" + topic);
							System.out.println("Recived:" + new String(msg.getPayload()));
						}

						public void deliveryComplete(IMqttDeliveryToken arg0) {
							System.out.println("Delivary complete");
						}

						public void connectionLost(Throwable arg0) {
							// TODO Auto-generated method stub
						}
					});

					MqttConnectOptions connOpts = new MqttConnectOptions();
					connOpts.setCleanSession(true);
					connOpts.setUserName("cat");
					connOpts.setPassword(new char[]{'c', 'a', 't'});
					mqttClient.connect(connOpts);
					//This portion of code is to setup the connection between server and MQTT cloud
					
					while(s.isConnected()){
						try {
							synchronized(s){
								if(!has_data){
									System.out.println("Thread " + Thread.currentThread().getId() + " is waiting to send data");
									s.wait();
								}
								else{
									System.out.println("Thread " + Thread.currentThread().getId() + " is sending data to cloud");
									MqttMessage message = new MqttMessage(arrBytes);
									message.setQos(qos); 
									System.out.println("Publish message: " + message);
									//Send the data to cloud server
									mqttClient.publish(topic, message);
									String str = "Succeeded";
									//Send the notification to client
									outStream.write(str.getBytes());
									has_data = false;
									s.notify();
								}
							}
						}

						catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mqttClient.disconnect();
				} catch(MqttException me) {
					System.out.println("reason "+me.getReasonCode());
					System.out.println("msg "+me.getMessage());
					System.out.println("loc "+me.getLocalizedMessage());
					System.out.println("cause "+me.getCause());
					System.out.println("excep "+me);
					me.printStackTrace();
				}
			}
		};
		writethread.start();
	}
}

class SocketThread{
	private Socket s;
	private int port;
	
	SocketThread(int port){
		this.port = port;
	}
	public void createConn() throws IOException{
		ServerSocket ss = new ServerSocket(port);
		System.out.println("Server is created!");
		System.out.println("Waiting for connection");
		while(true){
			s = ss.accept();
			System.out.println(s + " is created");
			RWThread thread = new RWThread(s);
			thread.createReadThread();
			thread.createWriteThread();	
		}
	}
}



public class Server {
	public static String name;
	public final static int port = 12345;
	public static void main(String[] args) throws IOException {
		SocketThread server = new SocketThread (port);
		server.createConn();

	}
}