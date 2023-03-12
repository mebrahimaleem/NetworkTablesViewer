package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import edu.wpi.first.networktables.*;
import networktablesviewer.NetworkAbstraction.*;

public class RootTableSidebar {
	JFrame hWindow;
	JPanel sidebar;
	JScrollPane scroll;
	JLabel title = new JLabel("Topics");
	SidebarList listModel = new SidebarList();
	JList list;
	ArrayList<String> filter = new ArrayList<String>();

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

			String val;
			switch (topics.get(i).value.getType()){
				case kBoolean:
					val = String.valueOf(topics.get(i).value.getBoolean());
					break;

				case kDouble:
					val = String.valueOf(topics.get(i).value.getDouble());
					break;

				case kFloat:
					val = String.valueOf(topics.get(i).value.getFloat());
					break;

				case kInteger:
					val = String.valueOf(topics.get(i).value.getInteger());
					break;

				case kString:
					val = String.valueOf(topics.get(i).value.getString());
					break;

				case kBooleanArray:
					val = Arrays.toString(topics.get(i).value.getBooleanArray());
					break;

				case kDoubleArray:
					val = Arrays.toString(topics.get(i).value.getDoubleArray());
					break;

				case kFloatArray:
					val = Arrays.toString(topics.get(i).value.getFloatArray());
					break;

				case kIntegerArray:
					val = Arrays.toString(topics.get(i).value.getIntegerArray());
					break;

				case kStringArray:
					val = Arrays.toString(topics.get(i).value.getStringArray());
					break;

				default:
					val = "<?>";
					break;
			}

			return topics.get(i).name + "  :  " + val;
		}
	}

	public RootTableSidebar(JFrame window){
		hWindow = window;

		sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(new Color(82, 84, 112));

		title.setForeground(Color.black);
		title.setFont(new Font("", Font.PLAIN , 20));
		sidebar.add(title, BorderLayout.NORTH);

		list = new JList(listModel);
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
			else System.out.println(i.name);
		}
	}

	public void updateVal(ArrayList<TopicValue> change){
		for (TopicValue i : change) listModel.editVal(i);
	}
}
