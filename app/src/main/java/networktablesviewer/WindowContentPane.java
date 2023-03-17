package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.DashboardPopup.*;

public class WindowContentPane {
	JFrame hWindow;
	JScrollPane scroll;
	JPanel content;
	ArrayList<DashboardElement> elems = new ArrayList<DashboardElement>();

	public WindowContentPane(JFrame window){
		hWindow = window;

		content = new JPanel(null);
		content.setPreferredSize(new Dimension(5000, 5000));

		scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scroll.setMinimumSize(new Dimension(10, 10));
		scroll.getHorizontalScrollBar().setUnitIncrement(30);
		scroll.getVerticalScrollBar().setUnitIncrement(30);
		scroll.getHorizontalScrollBar().setBlockIncrement(30);
		scroll.getVerticalScrollBar().setBlockIncrement(30);
	}

	public void add(TopicValue topic){
		elems.add(new DashboardElement(topic, content));
		content.add(elems.get(elems.size()-1));
		content.revalidate();
		content.repaint();
	}

	public void updateVal(ArrayList<TopicValue> change){
		for (TopicValue dif : change){
			for (DashboardElement elem : elems){
				if (elem.getName().equals(dif.name)) elem.setTopic(dif);
			}
		}
	}

	public JScrollPane getScrollPane() {return scroll;}
}
