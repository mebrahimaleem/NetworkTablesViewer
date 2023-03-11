
package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RootTableSidebar {
	JFrame hWindow;
	JPanel sidebar;
	JScrollPane scroll;
	JLabel title = new JLabel("Topics");

	public RootTableSidebar(JFrame window){
		hWindow = window;

		sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(new Color(82, 84, 112));

		title.setForeground(Color.black);
		title.setFont(new Font("", Font.PLAIN , 20));
		sidebar.add(title, BorderLayout.NORTH);

		
		scroll = new JScrollPane(sidebar);
		scroll.setMinimumSize(new Dimension(2, 10));
	}

	public JScrollPane getScrollPane() {return scroll;}
}
