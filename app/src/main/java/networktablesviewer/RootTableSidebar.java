package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import edu.wpi.first.networktables.*;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.*;

/**
 * Holds information about the sidebar
 */
public class RootTableSidebar {
	JFrame hWindow;
	JPanel sidebar;
	JScrollPane scroll;
	SidebarList listModel = new SidebarList();
	JList list;
	ArrayList<String> filter = new ArrayList<String>();
	WindowContentPane content;

	/**
	 * Holds information about the sidebar list
	 */
	public class SidebarList extends AbstractListModel{
		/** List of topics to display in the sidebar list */
		ArrayList<TopicValue> topics = new ArrayList<TopicValue>();

		/**
		 * Adds a new topic value
		 * @param val new topic value
		 */
		public void addVal(TopicValue val){
			topics.add(val);
			fireIntervalAdded(this, getSize()-1, getSize()-1);
		}

		/**
		 * Edits an existing topic value
		 * @param val latest update for a topic value
		 */
		public void editVal(TopicValue val){
			for (int i = 0; i < topics.size(); i++){
				if (topics.get(i).name.equals(val.name)){
					topics.set(i, val);
					fireContentsChanged(this, i, i);
					return;
				}
			}
		}

		/**
		 * Gets all stored topics
		 * @return All stored topics
		 */
		public ArrayList<TopicValue> getTopics(){
			return topics;
		}

		/**
		 * Gets the number of topics
		 * @return number of topics
		 */
		public int getSize() {
			return topics.size();
		}

		/**
		 * Gets the string value of the topic at the specified index
		 * @param i specified index to get topic value
		 */
		public String getElementAt(int i){
			if (i > getSize() - 1) return null;

			return topics.get(i).name + "  :  " + topics.get(i).getString();
		}
	}

	/**
	 * Constructs sidebar structure
	 * @param window JFrame container
	 * @param con Dashboard JPanel
	 */
	public RootTableSidebar(JFrame window, WindowContentPane con){
		hWindow = window;
		content = con;

		sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(new Color(82, 84, 112));

		list = new JList(listModel);
		list.addMouseListener(new MouseAdapter() { //Check for double clicks on the list to add ot the dashboard
			public void mouseClicked(MouseEvent e){
				JList ls = (JList)e.getSource();
				if (e.getClickCount() == 2) content.add(listModel.getTopics().get(ls.locationToIndex(e.getPoint())));

			}
		});

		sidebar.add(list, BorderLayout.NORTH);

		scroll = new JScrollPane(sidebar); //Set scroll speeds
		scroll.setMinimumSize(new Dimension(2, 10));
		scroll.getHorizontalScrollBar().setUnitIncrement(10);
		scroll.getVerticalScrollBar().setUnitIncrement(10);
		scroll.getHorizontalScrollBar().setBlockIncrement(10);
		scroll.getVerticalScrollBar().setBlockIncrement(10);
	}

	/**
	 * Gets the scroll pane
	 * @return The JScrollPane
	 */
	public JScrollPane getScrollPane() {return scroll;}

	/**
	 * Check if topic already exists in the list
	 * @param i name of the topic to check
	 * @return true if topic does not exist in list
	 */
	private boolean checkFilter(String i){
		for (String j : filter) if (i.equals(j)) return false;
		return true;
	}

	/**
	 * Creates new entries in the list
	 * @param change new values to put in list
	 */
	public void createVal(ArrayList<TopicValue> change){
		for (TopicValue i : change){
			if (checkFilter(i.name)){
				filter.add(i.name);
				listModel.addVal(i);
			}
		}
	}

	/**
	 * Updates old entries in the list
	 * @param change latest values to put in the list
	 */
	public void updateVal(ArrayList<TopicValue> change){
		for (TopicValue i : change) listModel.editVal(i);
		content.updateVal(change);
	}
}
