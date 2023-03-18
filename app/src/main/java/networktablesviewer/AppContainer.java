package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;
import java.util.concurrent.*;

import edu.wpi.first.networktables.*;

public class AppContainer{
	JFrame hWindow; //Window Handle
	RootTableSidebar sidebar;
	WindowContentPane contentPane;
	JSplitPane sidebarSplit;
	Semaphore netlock = new Semaphore(0, true);
	Settings sets;

	NetworkAbstraction netabs = new NetworkAbstraction();

	ArrayList<NetworkAbstraction.TopicValue> dtroot = new ArrayList<NetworkAbstraction.TopicValue>();

	public AppContainer(Settings settings){
		netlock = new Semaphore(0, true);
		sets = settings;

		//Create new window
		hWindow = new JFrame("Network Tables Viewer");
		hWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		hWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.out.println("Normal Termination");
				dispose();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem settingsMenuItem = new JMenuItem("Settings");

		settingsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				SettingsDialog sd = new SettingsDialog(hWindow, settings, () -> changeNetworkServer());
			}
		});

		fileMenu.add(settingsMenuItem);
		menuBar.add(fileMenu);

		hWindow.setJMenuBar(menuBar);
		
		contentPane = new WindowContentPane(hWindow);
		sidebar = new RootTableSidebar(hWindow, contentPane);

		sidebarSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar.getScrollPane(), contentPane.getScrollPane());
		sidebarSplit.setDividerLocation(250);
		sidebarSplit.setPreferredSize(new Dimension(750, 750));
		
		hWindow.getContentPane().add(sidebarSplit);

		if (settings.serverObtain == 0) netabs.connect();
		else if (settings.serverObtain == 1) netabs.connect(settings.ip);
		else netabs.connect("10." + 
			Integer.toString(((int)(settings.team / 100))) + "." + 
			Integer.toString(((int)(settings.team - ((int)(settings.team / 100)) * 100))) + 
			".1");

		netlock.release();
	}

	private void changeNetworkServer() {
		Settings settings = sets;
		try {
			netlock.acquire();
			if (netabs.isOpen()) netabs.closeAndLock();
			netabs = new NetworkAbstraction();
			
			if (settings.serverObtain == 0) netabs.connect();
			else if (settings.serverObtain == 1) netabs.connect(settings.ip);
			else netabs.connect("10." + 
				Integer.toString(((int)(settings.team / 100))) + "." + 
				Integer.toString(((int)(settings.team - ((int)(settings.team / 100)) * 100))) + 
				".1");
		}

		catch (Exception e) {}

		finally {
			netlock.release();
		}
	}

	public void displayWindow(){
		hWindow.pack();
		hWindow.setVisible(true);
	}

	public void loopCycle(){
		try {
			netlock.acquire();
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

		catch (Exception e) {}

		finally {
			netlock.release();
		}
	}

	public void dispose(){
		try {
			netlock.acquire();
			netabs.closeAndLock();
		}
		
		catch (Exception e) {}

		finally {
			System.exit(0);
		}
	}
}
