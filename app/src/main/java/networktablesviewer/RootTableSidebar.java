package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import edu.wpi.first.networktables.*;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.*;

public class RootTableSidebar {
	JFrame hWindow;
	JPanel sidebar;
	JScrollPane scroll;
	SidebarList listModel = new SidebarList();
	JList list;
	ArrayList<String> filter = new ArrayList<String>();
	WindowContentPane content;

	public class SidebarList extends AbstractListModel{
		ArrayList<TopicValue> topics = new ArrayList<TopicValue>();

		public void addVal(TopicValue val){
			topics.add(val);
			fireIntervalAdded(this, getSize()-1, getSize()-1);
		}

		public void editVal(TopicValue val){
			for (int i = 0; i < topics.size(); i++){
				if (topics.get(i).name.equals(val.name)){
					topics.set(i, val);
					fireContentsChanged(this, i, i);
					return;
				}
			}
		}

		public ArrayList<TopicValue> getTopics(){
			return topics;
		}

		public int getSize() {
			return topics.size();
		}

		public String getElementAt(int i){
			if (i > getSize() - 1) return null;

			return topics.get(i).name + "  :  " + topics.get(i).getString();
		}
	}

	public RootTableSidebar(JFrame window, WindowContentPane con){
		hWindow = window;
		content = con;

		sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(new Color(82, 84, 112));

		list = new JList(listModel);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				JList ls = (JList)e.getSource();
				if (e.getClickCount() == 2) content.add(listModel.getTopics().get(ls.locationToIndex(e.getPoint())));

			}
		});

		sidebar.add(list, BorderLayout.NORTH);

		scroll = new JScrollPane(sidebar);
		scroll.setMinimumSize(new Dimension(2, 10));
		scroll.getHorizontalScrollBar().setUnitIncrement(10);
		scroll.getVerticalScrollBar().setUnitIncrement(10);
		scroll.getHorizontalScrollBar().setBlockIncrement(10);
		scroll.getVerticalScrollBar().setBlockIncrement(10);
	}

	public JScrollPane getScrollPane() {return scroll;}

	private boolean checkFilter(String i){
		for (String j : filter) if (i.equals(j)) return false;
		return true;
	}

	public void createVal(ArrayList<TopicValue> change){
		for (TopicValue i : change){
			if (checkFilter(i.name)){
				filter.add(i.name);
				listModel.addVal(i);
			}
		}
	}

	public void updateVal(ArrayList<TopicValue> change){
		for (TopicValue i : change) listModel.editVal(i);
		content.updateVal(change);
	}
}
