package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;

/**
 * Holds information for DashboardElement popups
 */
public class DashboardPopup extends JPopupMenu {
	/** Menu Item option to delete DashboardElement */
	JMenuItem delItem = new JMenuItem("delete"); //Delete menu option
	
	/**
	 * SAM type lambda for removing DashboardElement
	 */
	public interface RemoveElement {
		/**
		 * This method should be overrided to remove the DashboardElement the lambda is passed to
		 */
		public void remove();
	}

	/**
	 * Creates a popup for a DashbordElement
	 * @param remover lambda to remove the DashboardElement
	 * @param content parent container for the DashboardElement
	 */
	public DashboardPopup(RemoveElement remover, JPanel content) {
		super();
		this.add(delItem);
		delItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				remover.remove();
				content.revalidate();
				content.repaint();
			}
		});
	}
}
