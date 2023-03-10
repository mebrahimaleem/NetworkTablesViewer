
package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class RootTableSidebar {
	JFrame hWindow;
	JPanel sidebar;
	JScrollPane scroll;

	public RootTableSidebar(JFrame window){
		hWindow = window;

		sidebar = new JPanel(new BorderLayout());
		sidebar.setBackground(Color.gray);
		sidebar.add(new Label("Topics"), BorderLayout.NORTH);
		
		scroll = new JScrollPane(sidebar);
		scroll.setMinimumSize(new Dimension(10, 10));
	}

	public JScrollPane getScrollPane() {return scroll;}
}
