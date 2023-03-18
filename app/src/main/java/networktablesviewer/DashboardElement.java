package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.DashboardPopup.*;

/**
 * This class holds information and acts as a Component for displaying topics on the Dashboard
 */
public class DashboardElement extends JPanel {
	/** TopicValue to display in DashboardElement */
	private TopicValue topic;

	/** Title for the DashboardElement */
	private JLabel title;

	/** Value for the DashboardElement */
	private JLabel value;

	/** true is actively resizing */
	private boolean dragging = false;

	/** true if actively moving */
	private boolean moving = false;

	/** y offset from click to top left of DashboardElement */
	private int ymoveOf = 0;

	/** x offset from click to top left of DashboardElement */
	private int xmoveOf = 0;

	/** desired move position */
	private Point moveTarget = new Point();

	/** desired resize bottom left location */
	private Point dragOrigin = new Point();

	/** border size of DashboardElement */
	private final int borderSz = 12;

	/** popup for DashboardElement */
	private DashboardPopup popup;

	/**
	 * Creates a JPanel and populates JLabels based on the topic
	 * @param top TopicValue to be displayed on this element
	 * @param content JPanel for the parent container
	 */
	public DashboardElement(TopicValue top, JPanel content){
		super ();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		popup = new DashboardPopup(() -> content.remove(this), content); //Create popup for removing this element
		topic = top;
		
		title = new JLabel(topic.name.substring(topic.name.lastIndexOf('/')+1), JLabel.CENTER); //Populate JLabels
		value = new JLabel("<html>" + topic.getString() + "</html>", JLabel.CENTER);

		title.setAlignmentX(JLabel.CENTER_ALIGNMENT); //Align text
		value.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		title.setFont(new Font("Helvetica", Font.BOLD, 22)); //Text font
		value.setFont(new Font("Serif", Font.PLAIN, 22));

		this.setBackground(new Color(118, 158, 222)); //panel color

		this.add(title);
		this.add(value);

		this.setBounds(0, 0, 100, 100);
		this.setBorder(BorderFactory.createLineBorder(Color.black, borderSz));

		this.addMouseListener(new MouseAdapter() { //Listener for mouse press and release
			@Override
			public void mousePressed(MouseEvent e){ //Handler for mouse press
				if (e.isPopupTrigger()){ //Check for right click (OS specific)
					popup.show(e.getComponent(), e.getX(), e.getY()); //show popup
					return;
				}

				dragOrigin = e.getPoint(); //Store drag origin and start dragging
				if (dragOrigin.getX() > getWidth() - borderSz && dragOrigin.getY() > getHeight() - borderSz) {
					dragging = true;
					setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
				} //Set click offsets and start moving
				else if (dragOrigin.getY() < getY() + borderSz){
					moving = true;
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					ymoveOf = (int)dragOrigin.getY() - getY();
					xmoveOf = (int)dragOrigin.getX() - getX();
					moveTarget = getLocation();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e){ //Handler for mouse release
				if (e.isPopupTrigger()){ //Check for right click (OS specific)
					popup.show(e.getComponent(), e.getX(), e.getY()); //show popup
					return;
				}

				if (dragging) { //Check if was resizing
						dragging = false;
						title.setSize(getX(), (int)(getY()/3)); //Resize JLabels
						value.setSize(getX(), (int)(2*getY()/2));
					}

				if (moving) setLocation(moveTarget); //Check if was moving
				moving = false; //Move panel
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				revalidate();
				repaint();
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() { //Listen for mouse move
			@Override
			public void mouseDragged(MouseEvent e){ //Handler for mouse drag
				if (dragging) { //Check if resizing and calculate new size
					setSize((int)(getWidth() + (e.getPoint().getX() - dragOrigin.getX())), (int)(getHeight() + (e.getPoint().getY() - dragOrigin.getY())));
					if (getSize().width < 2 * borderSz || getSize().height < 2 * borderSz)
						setSize(Math.max(2 * borderSz, getSize().width), Math.max(2 * borderSz, getSize().height));
					dragOrigin = e.getPoint();
				}

				else if (moving){ //Check if moving and calculate next location
					moveTarget = new Point((int)e.getPoint().getX() - xmoveOf, (int)e.getPoint().getY() - ymoveOf);
				}
			}
		});
	}

	/**
	 * Gets the name of the topic
	 * @return Name of the topic
	 */
	public String getName() {
		return topic.name;
	}

	/**
	 * Updates the topic to a new value
	 * @param top New topic value
	 */
	public void setTopic(TopicValue top) {
		topic = top;
		value.setText("<html>" + topic.getString() + "</html>");
	}
}
