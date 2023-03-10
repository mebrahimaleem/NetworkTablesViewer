package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import edu.wpi.first.networktables.*;

public class AppContainer{
	JFrame hWindow; //Window Handle
	NetworkAbstraction netabs = new NetworkAbstraction();

	ArrayList<NetworkAbstraction.TopicValue> dtroot = new ArrayList<NetworkAbstraction.TopicValue>();

	public AppContainer(){
		//Create new window
		hWindow = new JFrame("Network Tables Viewer");
		hWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hWindow.setSize(750,750);

		hWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.out.println("Normal Termination");
				dispose();
			}
		});

		netabs.connect("127.0.0.1");
	}

	public void displayWindow(){
		hWindow.setVisible(true);
	}

	public void blockingLoop(){
		while (true){
			dtroot = NetworkAbstraction.squashLatest(dtroot, netabs.getLatest());
			dtroot = netabs.updateExists(dtroot);
		}
	}

	public void dispose(){
		System.out.println("Closing sockets...");
		netabs.close();
	}
}
