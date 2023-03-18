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

public class SettingsDialog extends JDialog {
	public interface ChangeServer {
		public void change();
	}
	
	public SettingsDialog(JFrame frame, Settings settings, ChangeServer changer) {
		super(frame, "Settings", Dialog.ModalityType.APPLICATION_MODAL);

		JPanel panel = new JPanel();

		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);

		JScrollPane scroll = new JScrollPane(panel);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.getVerticalScrollBar().setBlockIncrement(20);

		JRadioButton useDS = new JRadioButton("Use Driver Station", settings.serverObtain == 0);
		JRadioButton useTeam = new JRadioButton("Use Team Number", settings.serverObtain == 2);
		JRadioButton useStatic = new JRadioButton("Use Static IP", settings.serverObtain == 1);

		ButtonGroup ipObtainGroup = new ButtonGroup();
		ipObtainGroup.add(useDS);
		ipObtainGroup.add(useTeam);
		ipObtainGroup.add(useStatic);

		useDS.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				settings.serverObtain = 0;
			}
		});

		useTeam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				settings.serverObtain = 2;
			}
		});

		useStatic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				settings.serverObtain = 1;
			}
		});

		JSpinner teamInput = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
		teamInput.setEditor(new JSpinner.NumberEditor(teamInput, "#"));
		teamInput.setValue(settings.team);
		((DefaultFormatter)((JFormattedTextField)(teamInput.getEditor().getComponent(0))).getFormatter()).setCommitsOnValidEdit(true);
		teamInput.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e){
				settings.team = (int)teamInput.getValue();
			}
		});

		JTextField staticInput = new JTextField(settings.ip);
		staticInput.getDocument().putProperty("filterNewlines", Boolean.TRUE);
		staticInput.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e){
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e){
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e){
				update();
			}

			public void update(){
				settings.ip = staticInput.getText();
			}
		});

		JButton apply = new JButton("Save");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				settings.save();
				changer.change();
				dispose();
			}
		});


		panel.add(useDS);
		panel.add(useTeam);
		panel.add(teamInput);
		panel.add(useStatic);
		panel.add(staticInput);
		panel.add(apply);

		panel.setBorder(BorderFactory.createLineBorder(panel.getBackground(), 20));
		this.add(panel);
		this.pack();
		this.setVisible(true);
	}
}
