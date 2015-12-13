package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class LeirasBevitelDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel bevitelPanel = null;
	private JPanel buttonPanel = null;
	
	private static String leiras = "";
	
	private LakcimTextField bevitelTextField = new LakcimTextField(20, 100);
	private JButton rendbenButton = new JButton("Rendben");
	
	private LeirasBevitelDialog(Component owner) {
		leiras = "";
		setMinimumSize(new Dimension(350, 100));
		setModal(true);
		setResizable(false);
		setTitle("Megadható egy tetszõleges leírás az utaláshoz");
		setLayout(new GridLayout(2, 1));
		add(getBevitelPanel());
		add(getButtonPanel());
		
		bevitelTextField.addActionListener(this);
		rendbenButton.addActionListener(this);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	
	public static String showOsszegBevitelDialog(Component owner) {
		new LeirasBevitelDialog(owner);
		return leiras;
	}
	
	private JPanel getBevitelPanel() {
		if(bevitelPanel == null) {
			bevitelPanel = new JPanel();
			bevitelPanel.add(new JLabel("Leírás: "));
			bevitelPanel.add(bevitelTextField);
		}
		return bevitelPanel;
	}
	
	private JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			buttonPanel.add(rendbenButton);
		}
		return buttonPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		leiras = bevitelTextField.getText();
		setVisible(false);
		return;
	}
}

