import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.*;

class SocketClient {
	private Socket s = null;
	private InputStream inStream = null;
	private OutputStream outStream = null;
	private int port;
	private String ip;
	private String fileName;
	private volatile boolean is_sending = false;

	public SocketClient(String ip, int port, String fileName) {
		this.port = port;
		this.ip = ip;
		this.fileName = fileName;
	}
	public void createSocket() {
		try {
			s = new Socket(ip, port);
			System.out.println("Client Connected");
			System.out.println(s + " is created");
			inStream = s.getInputStream();
			outStream = s.getOutputStream();
			createReadThread();
			createWriteThread();
		} catch (UnknownHostException u) {
			u.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public void createReadThread() {
		Thread readThread = new Thread() {
			public void run() {
				while (s.isConnected()) {
					try {
						synchronized(s){
							if(!is_sending)
								s.wait();
							byte[] readBuffer = new byte[200];
							int num = inStream.read(readBuffer);

							if (num > 0) {
								byte[] arrayBytes = new byte[num];
								System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
								String recvedMessage = new String(arrayBytes, "UTF-8");
								if(recvedMessage.equals("Succeeded")){
									is_sending = false;
									System.out.println("Data sent successfully");
									s.notify();
								}

							}else {
								break;
							}
						}

					}catch (SocketException se){
						System.exit(0);

					} catch (IOException i) {
						i.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		};
		readThread.start();
	}

	public void createWriteThread() {
		Thread writeThread = new Thread() {
			public void run() {
				while (s.isConnected()) {

					try {
						//BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

						//String typedMessage = inputReader.readLine();
						File file = new File(fileName);
						Scanner scanner = new Scanner(file);
						System.out.println("Read text file using Scanner");
						//read line by line
						synchronized(s){
							while(scanner.hasNextLine()){
								//process each line
								if(!is_sending){
									String line = scanner.nextLine();
									outStream.write(line.getBytes());
									System.out.println(line + " is sending by thread " + Thread.currentThread().getId());
									is_sending = true;
									s.notify();
									s.wait();
								}
							}
						}
						scanner.close();
						s.close();
						break;
						//System.exit(0);
					} catch (IOException i) {
						i.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 

				}
			}
		};
		writeThread.start();
	}	


}


public class Client {
	public static void main(String[] args) throws IOException {
		String ip = "localhost";
		String fileName = "/Users/David/Desktop/Computer Network/Assignment 1/gps";

		SocketClient client1 = new SocketClient(ip,12345, fileName.concat(Integer.toString(0) + ".txt"));
		client1.createSocket();
		SocketClient client2 = new SocketClient(ip,12345, fileName.concat(Integer.toString(1) + ".txt"));
		client2.createSocket();
		SocketClient client3 = new SocketClient(ip,12345, fileName.concat(Integer.toString(2) + ".txt"));
		client3.createSocket();
		SocketClient client4 = new SocketClient(ip,12345, fileName.concat(Integer.toString(3) + ".txt"));
		client4.createSocket();
		SocketClient client5 = new SocketClient(ip,12345, fileName.concat(Integer.toString(4) + ".txt"));
		client5.createSocket();
		SocketClient client6 = new SocketClient(ip,12345, fileName.concat(Integer.toString(5) + ".txt"));
		client6.createSocket();
		SocketClient client7 = new SocketClient(ip,12345, fileName.concat(Integer.toString(6) + ".txt"));
		client7.createSocket();
		SocketClient client8 = new SocketClient(ip,12345, fileName.concat(Integer.toString(7) + ".txt"));
		client8.createSocket();

	}

}
