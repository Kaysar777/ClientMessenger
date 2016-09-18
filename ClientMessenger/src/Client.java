import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message;
	private String serverIP;
	private Socket connection;
	
	//constructor
	public Client(String host){
		super("Client mofo!   :D");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}
	
	//start running
	public void startRunning(){
		try{
			connectToServer();
			setUpStreams();
			whileChatting();
		}
		catch(EOFException eofException){
			showMessage("\nClient terminated the connection\n");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			closeCrap();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to "+connection.getInetAddress().getHostName());
	}
	
	//set up streams
	private void setUpStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nDude your streams are now good to go");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n"+message);
			}
			catch(ClassNotFoundException classNotFoundException){
				showMessage("\nI don't know that object type\n");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	//closing everything down
	private void closeCrap(){
		showMessage("\nClosing crap down...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send messages to the server
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - "+message);
			output.flush();
			showMessage("\nCLIENT - "+message);
		}
		catch(IOException ioException){
			chatWindow.append("\n something messed up sending the message hoss!");
		}
	}
	
	//show messages in chat window
	private void showMessage(final String m){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(m);
			}
		});
	}
	
	//gives user permission to type
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(tof);
			}
		});
	}
}
