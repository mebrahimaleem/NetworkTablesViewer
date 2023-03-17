package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.DashboardPopup.*;

public class DashboardElement extends JPanel {
	TopicValue topic;
	JLabel title;
	JLabel value;
	boolean dragging = false;
	boolean moving = false;
	int ymoveOf = 0;
	int xmoveOf = 0;
	Point moveTarget = new Point();
	Point dragOrigin = new Point();
	final int borderSz = 12;
	DashboardPopup popup;

	public DashboardElement(TopicValue top, JPanel content){
		super ();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		popup = new DashboardPopup(() -> content.remove(this), content);
		topic = top;
		
		title = new JLabel(topic.name.substring(topic.name.lastIndexOf('/')+1), JLabel.CENTER);
		value = new JLabel("<html>" + topic.getString() + "</html>", JLabel.CENTER);

		title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		value.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		title.setFont(new Font("Helvetica", Font.BOLD, 22));
		value.setFont(new Font("Serif", Font.PLAIN, 22));

		this.setBackground(new Color(118, 158, 222));

		this.add(title);
		this.add(value);

		this.setBounds(0, 0, 100, 100);
		this.setBorder(BorderFactory.createLineBorder(Color.black, borderSz));

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e){
				if (e.isPopupTrigger()){
					popup.show(e.getComponent(), e.getX(), e.getY());
					return;
				}

				dragOrigin = e.getPoint();
				if (dragOrigin.getX() > getWidth() - borderSz && dragOrigin.getY() > getHeight() - borderSz) {
					dragging = true;
					setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				}
				else if (dragOrigin.getY() < getY() + borderSz){
					moving = true;
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					ymoveOf = (int)dragOrigin.getY() - getY();
					xmoveOf = (int)dragOrigin.getX() - getX();
					moveTarget = getLocation();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e){
				if (e.isPopupTrigger()){
					popup.show(e.getComponent(), e.getX(), e.getY());
					return;
				}

				if (dragging) {
						dragging = false;
						title.setSize(getX(), (int)(getY()/3));
						value.setSize(getX(), (int)(2*getY()/2));
					}

				if (moving) setLocation(moveTarget);
				moving = false;
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				revalidate();
				repaint();
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e){
				if (dragging) {
					setSize((int)(getWidth() + (e.getPoint().getX() - dragOrigin.getX())), (int)(getHeight() + (e.getPoint().getY() - dragOrigin.getY())));
					if (getSize().width < 2 * borderSz || getSize().height < 2 * borderSz) 
						setSize(Math.max(2 * borderSz, getSize().width), Math.max(2 * borderSz, getSize().height));
					dragOrigin = e.getPoint();
				}

				else if (moving){
					moveTarget = new Point((int)e.getPoint().getX() - xmoveOf, (int)e.getPoint().getY() - ymoveOf);
				}
			}
		});
	}

	public String getName() {
		return topic.name;
	}

	public void setTopic(TopicValue top) {
		topic = top;
		value.setText("<html>" + topic.getString() + "</html>");
	}
}
