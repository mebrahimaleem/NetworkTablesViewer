package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import networktablesviewer.NetworkAbstraction.*;

public class DashboardElement extends JPanel {
	TopicValue topic;

	public DashboardElement(TopicValue top){
		super (new BorderLayout());
		topic = top;
	}
}
