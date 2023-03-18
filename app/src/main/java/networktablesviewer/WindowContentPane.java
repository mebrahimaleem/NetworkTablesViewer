package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.DashboardPopup.*;

/**
 * Holds underlying structure for the dashboard area
 */
public class WindowContentPane {
	JFrame hWindow; //Parent JFrame
	JScrollPane scroll; //Scroll
	JPanel content; //Dashboard panel
	ArrayList<DashboardElement> elems = new ArrayList<DashboardElement>(); //Store current DashboardElements

	/**
	 * Creates a JPanel for holding DashboardElements
	 * @param window Parent JFrame container
	 */
	public WindowContentPane(JFrame window){
		hWindow = window;

		content = new JPanel(null); //No Layout manager to allow drag and drop + resizing
		content.setPreferredSize(new Dimension(5000, 5000));

		scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //Allow scroll in all directions

		scroll.setMinimumSize(new Dimension(10, 10)); //Scroll settings
		scroll.getHorizontalScrollBar().setUnitIncrement(30);
		scroll.getVerticalScrollBar().setUnitIncrement(30);
		scroll.getHorizontalScrollBar().setBlockIncrement(30);
		scroll.getVerticalScrollBar().setBlockIncrement(30);
	}

	/**
	 * Adds a new TopicValue to the dashboard ands creates a new DashboardElement
	 * @param topic TopicValue to add
	 */
	public void add(TopicValue topic){
		elems.add(new DashboardElement(topic, content));
		content.add(elems.get(elems.size()-1));
		content.revalidate();
		content.repaint();
	}

	/**
	 * Updates the values of existing DashboardElements
	 * @param change TopicValues to update
	 */
	public void updateVal(ArrayList<TopicValue> change){
		for (TopicValue dif : change){
			for (DashboardElement elem : elems){
				if (elem.getName().equals(dif.name)) elem.setTopic(dif);
			}
		}
	}

	/**
	 * Gets the scroll pane for the dashboard
	 * @return the JScrollPane for the dashboard panel
	 */
	public JScrollPane getScrollPane() {return scroll;}
}
