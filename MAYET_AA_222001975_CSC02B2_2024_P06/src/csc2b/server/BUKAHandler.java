package csc2b.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class BUKAHandler implements Runnable
{
	
	//Login
	// save pdf, save id
	// port 2018
	/* AUTH <Name> <Password>:
			validate
			show error if wrong
		LIST:
			list of pdfs from txt file
		PDFRET <ID>:
			return file
			validate id, error msg if wrong id
			confirmation msg and file size
			then file
		LOGOUT:
			log client off			
	*/
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
    public BUKAHandler(Socket newConnectionToClient)
    {
    	//Bind streams
    	try
    	{
    		socket = newConnectionToClient;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			dataIn = new DataInputStream(socket.getInputStream());
			dataOut = new DataOutputStream(socket.getOutputStream());
			
		}catch (IOException e)
    	{
			e.printStackTrace();
		}

    }
    
    public void run()
    {
    	//Process commands from client
    	boolean running = true;
    	while (running)
    	{
    		try
    		{
				String messageString = in.readLine();
				StringTokenizer tokens = new StringTokenizer(messageString);
				
				String commmandString = tokens.nextToken().toUpperCase();
				
				if (commmandString.equals("AUTH"))
				{
					String usernameString = tokens.nextToken();
					String passwordString = tokens.nextToken();
					boolean isUser = matchUser(usernameString, passwordString);
					
					if (isUser)
					{
						out.println("HELLO " + usernameString);
					}else
					{
						out.println("Incorrect username or password, please try again.");
					}
					out.flush();
				}else if (commmandString.equals("LIST"))
				{
					ArrayList<String> fileList = getFileList();
					String sendString = "";
					for (int i=0; i<fileList.size(); i++)
					{
						sendString += fileList.get(i) + " ";
					}
					out.println(sendString);
					out.flush();
				}else if (commmandString.equals("PDFRET"))
				{
					String fileID = tokens.nextToken();
					String fileName = idToFile(fileID);
					
					File file = new File("./data/server/" + fileName);
					if (file.exists() && file.isFile())
					{
						System.out.println("File " + fileName + " with ID: " + fileID + " confirmed for being sent. Size: " + file.length());
						out.println(fileName);
						out.flush();
						
						out.println(fileID);
						out.flush();
						
						out.println(file.length());
						out.flush();
						
						FileInputStream fileInputStream = new FileInputStream(file);
						byte[] buffer = new byte[2048];
						int n=0;
						while((n=fileInputStream.read(buffer)) > 0)
						{
							dataOut.write(buffer, 0,n);
							dataOut.flush();
						}
						fileInputStream.close();
						System.out.println("File: " + fileName + " sucessfully sent to client.");
					}else
					{
						out.println("File ID " + fileID + " was invalid.");
						out.flush();
					}
				}else if (commmandString.equals("LOGOUT"))
				{
					out.println("GOODBYE :)");
					out.flush();
					
					in.close();
					out.close();
					dataIn.close();
					dataOut.close();
					socket.close();
					
					running = false;
				}
			} catch (IOException e)
    		{
				e.printStackTrace();
			}
    	}
    }
    
    private boolean matchUser(String username,String password)
    {
    	boolean found = false;
    	File userFile = new File("./data/server/users.txt");
    	try
    	{
    		//Code to search users.txt file for match with username and password
    		Scanner scan = new Scanner(userFile);
    		String line = "";
    		
    		while(scan.hasNextLine() && !found)
    		{
    			line = scan.nextLine();
    			String lineSec[] = line.split("\\s");
    			
    			if (username.equals(lineSec[0]) && password.equals(lineSec[1]))
    			{
					found = true;
				}
    		}
    		scan.close();
    	}catch(IOException ex)
    	{
    		ex.printStackTrace();
    	}
    	return found;
    }
    
    private ArrayList<String> getFileList()
    {
    	ArrayList<String> result = new ArrayList<String>();
    	
		try
		{
			//Code to add list text file contents to the arraylist.
			File lstFile = new File("./data/server/PdfList.txt");
			
			Scanner scan = new Scanner(lstFile);
			String line = "";
			
			//Read each line of the file and add to the arraylist
			while(scan.hasNextLine())
    		{
    			line = scan.nextLine();
    			String lineSec[] = line.split("\\s");
    			
    			result.add(lineSec[1]);
    		}
			scan.close();
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return result;
    }
    
    private String idToFile(String ID)
    {
    	String result = "";
    	
    	try
    	{
    		//Code to find the file name that matches strID
        	File lstFile = new File("./data/server/PdfList.txt");
        	
    		Scanner scan = new Scanner(lstFile);
    		String line = "";
    		
    		//Read filename from file and search for filename based on ID
    		while(scan.hasNextLine())
    		{
    			line = scan.nextLine();
    			String lineSec[] = line.split("\\s");
    			
    			if(lineSec[0].equals(ID))
    			{
    				result = lineSec[1];
    			}
    		}
    		scan.close();
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace();
    	}
    	return result;
    }
}














