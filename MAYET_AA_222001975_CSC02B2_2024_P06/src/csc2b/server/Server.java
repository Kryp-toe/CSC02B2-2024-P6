package csc2b.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	public Server(int port)
	{
		try
		{
    		ServerSocket serverSocket = new ServerSocket(port);
    		
    		boolean isActive = true;
    		System.out.println("Connection established");
    		while (isActive)
    		{
    			Socket socket = serverSocket.accept();
    			System.out.println("Server accepted");
    			Thread thread = new Thread(new BUKAHandler(socket));
        		thread.start();
			}
    		
    		serverSocket.close();
    		
		} catch (IOException e)
    	{
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args)
    {
    	Server server = new Server(3333);
    }
}
