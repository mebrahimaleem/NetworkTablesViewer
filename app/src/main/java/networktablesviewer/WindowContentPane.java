package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;

public class WindowContentPane {
	JFrame hWindow;
	JScrollPane scroll;
	JPanel content;

	public WindowContentPane(JFrame window){
		hWindow = window;

		content = new JPanel(null);
		content.setPreferredSize(new Dimension(5000, 5000));

		scroll = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scroll.setMinimumSize(new Dimension(10, 10));
		scroll.getHorizontalScrollBar().setUnitIncrement(10);
		scroll.getVerticalScrollBar().setUnitIncrement(10);
		scroll.getHorizontalScrollBar().setBlockIncrement(10);
		scroll.getVerticalScrollBar().setBlockIncrement(10);
	}

	public void add(TopicValue topic){
		content.add(new DashboardElement(topic));
		content.revalidate();
		content.repaint();
	}

	public JScrollPane getScrollPane() {return scroll;}
}
