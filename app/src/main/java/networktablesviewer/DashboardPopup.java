package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import networktablesviewer.NetworkAbstraction.*;

public class DashboardPopup extends JPopupMenu {
	JMenuItem delItem = new JMenuItem("delete");

	public interface RemoveElement {
		public void remove();
	}

	public DashboardPopup(RemoveElement remover, JPanel content) {
		this.add(delItem);
		delItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				remover.remove();
				content.revalidate();
				content.repaint();
			}
		});
	}
}
