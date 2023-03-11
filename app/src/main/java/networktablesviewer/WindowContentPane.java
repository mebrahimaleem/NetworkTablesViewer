package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class WindowContentPane {
	JFrame hWindow;
	JScrollPane scroll;
	DashboardData dashboardModel;
	JTable dashboardCurrent;


	public WindowContentPane(JFrame window){
		hWindow = window;

		dashboardModel = new DashboardData();

		dashboardCurrent = new JTable(dashboardModel);
		dashboardCurrent.setRowSelectionAllowed(false);
		dashboardCurrent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dashboardCurrent.setRowHeight(50);

		scroll = new JScrollPane(dashboardCurrent, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scroll.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e){
				dashboardModel.updateStructure();
				TableColumnModel cols = dashboardCurrent.getColumnModel();
				for (int i = 0; i < 100; i++){
					cols.getColumn(i).setPreferredWidth(50);
				}
			}
		});

		scroll.setMinimumSize(new Dimension(10, 10));
		scroll.getHorizontalScrollBar().setUnitIncrement(10);
		scroll.getVerticalScrollBar().setUnitIncrement(10);
		scroll.getHorizontalScrollBar().setBlockIncrement(10);
		scroll.getVerticalScrollBar().setBlockIncrement(10);
	}

	public JScrollPane getScrollPane() {return scroll;}
}
