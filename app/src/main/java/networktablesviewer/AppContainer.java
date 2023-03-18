package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;
import java.util.concurrent.*;

import edu.wpi.first.networktables.*;

/**
 * App container for holding the underlying structure of the application
 */
public class AppContainer{
	JFrame hWindow; //Window Handle
	RootTableSidebar sidebar; //Sidebar
	WindowContentPane contentPane; //Dashboard
	JSplitPane sidebarSplit; //Split between sidebard and dashboard
	Semaphore netlock = new Semaphore(0, true); //Semaphore for stopping race condition on Topics
	Settings sets; //Application settings

	NetworkAbstraction netabs = new NetworkAbstraction(); //Network abstraction for updating local Topics

	ArrayList<NetworkAbstraction.TopicValue> dtroot = new ArrayList<NetworkAbstraction.TopicValue>(); //Container for local Topics

	/**
	 * Creates a JFrame and the underlying structure for the application
	 * @param settings Settings for the application
	 */
	public AppContainer(Settings settings){
		sets = settings;

		//Create new window
		hWindow = new JFrame("Network Tables Viewer"); //Create new JFrame
		hWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		hWindow.addWindowListener(new WindowAdapter() { //Handle exit to close NT
			public void windowClosing(WindowEvent e){
				System.out.println("Normal Termination");
				dispose();
			}
		});

		JMenuBar menuBar = new JMenuBar(); //Menu bar
		JMenu fileMenu = new JMenu("File");
		JMenuItem settingsMenuItem = new JMenuItem("Settings");

		settingsMenuItem.addActionListener(new ActionListener() { //Handle click on the settings menu
			@Override
			public void actionPerformed(ActionEvent e){
				SettingsDialog sd = new SettingsDialog(hWindow, settings, () -> changeNetworkServer());
			}
		});

		fileMenu.add(settingsMenuItem); //Add components
		menuBar.add(fileMenu);

		hWindow.setJMenuBar(menuBar);
		
		contentPane = new WindowContentPane(hWindow);
		sidebar = new RootTableSidebar(hWindow, contentPane);

		sidebarSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar.getScrollPane(), contentPane.getScrollPane()); //Split dashboard and sidebar
		sidebarSplit.setDividerLocation(250);
		sidebarSplit.setPreferredSize(new Dimension(750, 750));
		
		hWindow.getContentPane().add(sidebarSplit);
 
		//Get IP based on user preference
		if (settings.serverObtain == 0) netabs.connect(); //Use DS
		else if (settings.serverObtain == 1) netabs.connect(settings.ip); //Use static
		else netabs.connect("10." + //Use team number
			Integer.toString(((int)(settings.team / 100))) + "." + 
			Integer.toString(((int)(settings.team - ((int)(settings.team / 100)) * 100))) + 
			".1");

		netlock.release(); //Allow other threads to use netabs
	}

	/**
	 * Changes the NT server IP
	 * This function will wait on and acquire the semaphore
	 */
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

	/**
	 * Packs and displays the JFrame
	 */
	public void displayWindow(){
		hWindow.pack();
		hWindow.setVisible(true);
	}

	/**
	 * Updates local Topics
	 * This function should be called periodically
	 * Calling this function too often will lead to severe application slowdown
	 * This function will wait on and acquire the semaphore
	 */
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
			
			System.out.println(edit.size() + diff.size());
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
	
	/**
	 * Disposes the JFrame (and its children) and terminates teh JVM
	 */
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
