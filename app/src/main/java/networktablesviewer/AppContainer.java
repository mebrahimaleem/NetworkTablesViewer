package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;

import edu.wpi.first.networktables.*;

public class AppContainer{
	JFrame hWindow; //Window Handle
	RootTableSidebar sidebar;
	WindowContentPane contentPane;
	JSplitPane sidebarSplit;

	NetworkAbstraction netabs = new NetworkAbstraction();

	ArrayList<NetworkAbstraction.TopicValue> dtroot = new ArrayList<NetworkAbstraction.TopicValue>();

	public AppContainer(){
		//Create new window
		hWindow = new JFrame("Network Tables Viewer");
		hWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		hWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.out.println("Normal Termination");
				dispose();
			}
		});
		
		contentPane = new WindowContentPane(hWindow);
		sidebar = new RootTableSidebar(hWindow, contentPane);

		sidebarSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar.getScrollPane(), contentPane.getScrollPane());
		sidebarSplit.setDividerLocation(250);
		sidebarSplit.setPreferredSize(new Dimension(750, 750));
		
		hWindow.getContentPane().add(sidebarSplit);
		netabs.connect("127.0.0.1");
	}

	public void displayWindow(){
		hWindow.pack();
		hWindow.setVisible(true);
	}

	public void loopCycle(){
	ArrayList<TopicValue> diff;
	ArrayList<TopicValue> edit = new ArrayList<TopicValue>();
	int of;
		diff = netabs.getLatest();

		edit.clear();
		of = 0;
		for (int i = 0; i - of < diff.size(); i++){
			for (TopicValue j : dtroot){
				if (diff.get(i - of).name.equals(j.name)){
					edit.add(diff.get(i - of));
					diff.remove(i - of);
					of++;
					break;
				}
			}
		}
		
		sidebar.updateVal(edit);

		sidebar.createVal(diff);

		dtroot = NetworkAbstraction.squashLatest(dtroot, diff);
		dtroot = netabs.updateExists(dtroot);
	}

	public void dispose(){
		System.out.println("Closing sockets...");
		netabs.close();
		System.exit(0);
	}
}
