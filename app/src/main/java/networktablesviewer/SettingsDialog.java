package networktablesviewer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import javax.swing.text.*;
import javax.swing.event.*;
import networktablesviewer.NetworkAbstraction.*;
import networktablesviewer.DashboardPopup.*;

/**
 * This class contains the underlying structure for the settings dialog
 */
public class SettingsDialog extends JDialog {

	/**
	 * SAM type lambda for changing NT server IP
	 */
	public interface ChangeServer {
		/**
		 * This method should be overrided to change the NT server based on the current settings
		 */
		public void change();
	}
	
	/**
	 * Constructs a new JDialog with radio buttons and input to handle user settings
	 * @param frame The parent JFrame of the dialog
	 * @param settings A reference to the settings used by the application
	 * @param changer lambda for changing NT server based on settings
	 */
	public SettingsDialog(JFrame frame, Settings settings, ChangeServer changer) {
		super(frame, "Settings", Dialog.ModalityType.APPLICATION_MODAL);

		JPanel panel = new JPanel(); //Panel for storing UI

		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);

		JScrollPane scroll = new JScrollPane(panel); //Scroll settings
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.getVerticalScrollBar().setBlockIncrement(20);

		JRadioButton useDS = new JRadioButton("Use Driver Station", settings.serverObtain == 0); //Use DS
		JRadioButton useTeam = new JRadioButton("Use Team Number", settings.serverObtain == 2); //Use team #
		JRadioButton useStatic = new JRadioButton("Use Static IP", settings.serverObtain == 1); //Use static

		ButtonGroup ipObtainGroup = new ButtonGroup(); //Group radio buttons
		ipObtainGroup.add(useDS);
		ipObtainGroup.add(useTeam);
		ipObtainGroup.add(useStatic);

		useDS.addActionListener(new ActionListener() { //Listener for click on DS button
			@Override
			public void actionPerformed(ActionEvent e){ //Change settings
				settings.serverObtain = 0;
			}
		});

		useTeam.addActionListener(new ActionListener() { //Listener for click on team button
			@Override
			public void actionPerformed(ActionEvent e){ //Change settings
				settings.serverObtain = 2;
			}
		});

		useStatic.addActionListener(new ActionListener() { //Listener for click on static button
			@Override
			public void actionPerformed(ActionEvent e){ //Change settings
				settings.serverObtain = 1;
			}
		});

		JSpinner teamInput = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1)); //Team number input
		teamInput.setEditor(new JSpinner.NumberEditor(teamInput, "#"));
		teamInput.setValue(settings.team);
		((DefaultFormatter)((JFormattedTextField)(teamInput.getEditor().getComponent(0))).getFormatter()).setCommitsOnValidEdit(true); //Make all changes fire the event
		teamInput.addChangeListener(new ChangeListener() { //Listener for changes on input
			@Override
			public void stateChanged(ChangeEvent e){ //Change settings
				settings.team = (int)teamInput.getValue();
			}
		});

		JTextField staticInput = new JTextField(settings.ip); //Static IP input
		staticInput.getDocument().putProperty("filterNewlines", Boolean.TRUE); //One line only
		staticInput.getDocument().addDocumentListener(new DocumentListener() { //Listen for changes on input
			@Override
			public void changedUpdate(DocumentEvent e){ //Change settings
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e){ //Change settings
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e){ //Change settings
				update();
			}

			/**
			 * Updates the settings to use the static IP in the text field
			 */
			public void update(){
				settings.ip = staticInput.getText();
			}
		});

		JButton apply = new JButton("Save"); //Save button for changing server and writing changes to file
		apply.addActionListener(new ActionListener() { //Listen for button click
			@Override
			public void actionPerformed(ActionEvent e){ //Handle button click
				settings.save();
				changer.change();
				dispose();
			}
		});


		panel.add(useDS); //Add all components
		panel.add(useTeam);
		panel.add(teamInput);
		panel.add(useStatic);
		panel.add(staticInput);
		panel.add(apply);

		panel.setBorder(BorderFactory.createLineBorder(panel.getBackground(), 20)); //Add border to act as padding
		this.add(panel);
		this.pack();
		this.setVisible(true);
	}
}
