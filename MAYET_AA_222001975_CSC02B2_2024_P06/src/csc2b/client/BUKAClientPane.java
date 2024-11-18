package csc2b.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BUKAClientPane extends GridPane //You may change the JavaFX pane layout
{
	private Socket serverSocket;
	private BufferedReader in;
	private PrintWriter out;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private ArrayList<String> fileList = new ArrayList<String>();
	
	private TextArea txtStatus;
	private Label lblUsername;
	private TextField txtUsername;
	private Label lblPassword;
	private TextField txtPassword;
	private Label lblList;
	private Label lblGetPDF;
	private TextField txtFileChoice;
	private Button btnConnect;
	private Button btnList;
	private Button btnGetPDF;
	private Button btnLogOut;
	
    public BUKAClientPane(String INetAddress, int port)
    {
    	setUpGUI(INetAddress, port);
    	//Create client connection
    	//Create buttons for each command
    	//Use buttons to send commands
    	
    }
    
    public void setUpGUI(String INetAddress, int port)
    {
    	this.setPadding(new Insets(10,10,10,10));
    	
    	txtStatus = new TextArea();
    	txtStatus.setText("Output Window: \n");
    	
    	lblUsername = new Label("Username: ");
    	txtUsername = new TextField();
    	lblPassword = new Label("Password: ");
    	txtPassword = new TextField();
    	
    	lblList = new Label("Get list of Files");
    	
    	lblGetPDF = new Label("Download File (enter number): ");
    	txtFileChoice = new TextField();
    	
    	btnConnect = new Button("Connect");
    	btnConnect.setOnAction(e -> connect(INetAddress, port));
    	
    	btnList = new Button("List");
    	btnList.setOnAction(e -> getFileList());
    	
    	btnGetPDF = new Button("Download PDF");
    	btnGetPDF.setOnAction(e -> downloadPDF());
    	
    	btnLogOut = new Button("LogOut");
    	btnLogOut.setOnAction(e -> logOut());
    	
    	this.add(lblUsername, 0, 0);
    	this.add(txtUsername, 1, 0);
    	this.add(lblPassword, 0, 1);
    	this.add(txtPassword, 1, 1);
    	this.add(btnConnect, 0, 2);
    	
    	this.add(lblList, 0, 3);
    	this.add(btnList, 1, 3);
    	
    	this.add(lblGetPDF, 0, 4);
    	this.add(txtFileChoice, 1, 4);
    	this.add(btnGetPDF, 2, 4);
    	
    	this.add(btnLogOut, 0, 5);
    	
    	this.add(txtStatus, 3, 0, 1, 6);
    }

	public void connect(String INetAddress, int port)
    {
    	//Create client connection
    	try
    	{
			serverSocket = new Socket(INetAddress, port);
			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			out = new PrintWriter(serverSocket.getOutputStream());
			dataIn = new DataInputStream(serverSocket.getInputStream());
			dataOut = new DataOutputStream(serverSocket.getOutputStream());
			
			out.println("AUTH " + txtUsername.getText() + " " + txtPassword.getText());
			out.flush();
			
			String responseString = in.readLine();
			txtStatus.appendText(responseString + "\n");
			txtStatus.appendText("\n");
		} catch (IOException e)
    	{
			e.printStackTrace();
		}
    }
    
    public void getFileList()
    {
    	try
    	{
    		out.println("LIST");
    		out.flush();
    		
			String responseString = in.readLine();
			
			txtStatus.appendText("File List: \n");
			int count = 1;
			
			for (String string : responseString.split("\\s"))
			{
				fileList.add(string);
				txtStatus.appendText(count + " " + string + "\n");
				count ++;
			}
			txtStatus.appendText("\n");
		} catch (IOException e)
    	{
			e.printStackTrace();
		}
    }
    
    public void downloadPDF()
    {
		try 
		{
			String fileChoiceString = txtFileChoice.getText();
			
			out.println("PDFRET " + fileChoiceString);
			out.flush();
			
			String fileNameString = in.readLine();
			int fileIDString = Integer.parseInt(in.readLine());
			int fileSize = Integer.parseInt(in.readLine());
			
			txtStatus.appendText(fileNameString + " is being downloaded \n"
					+ "ID: " + fileIDString + " \n"
							+ "Size: " + fileSize + " \n");
			
			File file = new File("./data/client/" + fileNameString);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			
			byte[] buffer = new byte[2048];
			int n = 0;
			int total = 0;
			
			while(total != fileSize)
			{
				n = dataIn.read(buffer, 0, buffer.length);
				fileOutputStream.write(buffer, 0, n);
				fileOutputStream.flush();
				total += n;
			}
			
			txtStatus.appendText(fileNameString + " has been saved in " + file.getAbsolutePath() + " \n");
			txtStatus.appendText("\n");
			
			fileOutputStream.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
    
    public void logOut()
    {
		try
		{
			out.println("LOGOUT");
			out.flush();
			txtStatus.appendText(in.readLine());
			txtStatus.appendText("\n");
			
			in.close();
			out.close();
			dataIn.close();
			dataOut.close();
			serverSocket.close();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
