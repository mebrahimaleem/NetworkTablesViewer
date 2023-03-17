package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import networktablesviewer.NetworkAbstraction.*;

public class DashboardElement extends JPanel {
	TopicValue topic;
	JLabel title;
	JLabel value;

	public DashboardElement(TopicValue top){
		super ();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		topic = top;
		
		title = new JLabel(topic.name, SwingConstants.RIGHT);
		value = new JLabel(topic.getString(), SwingConstants.RIGHT);

		this.setBackground(new Color(118, 158, 222));

		this.add(title);
		this.add(value);

		this.setBounds(0, 0, 100, 100);
		this.setBorder(BorderFactory.createLineBorder(Color.black, 5));
	}
}
