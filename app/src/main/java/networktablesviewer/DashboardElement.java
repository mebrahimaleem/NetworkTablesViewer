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
	boolean dragging = false;
	Point dragOrigin = new Point();
	final int borderSz = 12;

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
		this.setBorder(BorderFactory.createLineBorder(Color.black, borderSz));

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e){
				dragOrigin = e.getPoint();
				if (dragOrigin.getX() > getWidth() - borderSz && dragOrigin.getY() > getHeight() - borderSz) {
					dragging = true;
					setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				}
			}

			@Override
			public void mouseReleased(MouseEvent e){
				dragging = false;
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				revalidate();
				repaint();
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e){
				if (!dragging) return;

				setSize((int)(getWidth() + (e.getPoint().getX() - dragOrigin.getX())), (int)(getHeight() + (e.getPoint().getY() - dragOrigin.getY())));
				dragOrigin = e.getPoint();
			}

		});
	}
}
