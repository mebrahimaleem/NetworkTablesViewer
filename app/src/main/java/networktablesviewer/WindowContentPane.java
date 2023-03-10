package networktablesviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class WindowContentPane {
	JFrame hWindow;
	JPanel panel;
	JScrollPane scroll;

	public WindowContentPane(JFrame window){
		hWindow = window;

		panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.black);
		panel.add(new Label("Dashboard"), BorderLayout.NORTH);

		scroll = new JScrollPane(panel);
		scroll.setMinimumSize(new Dimension(10, 10));
	}

	public JScrollPane getScrollPane() {return scroll;}
}
