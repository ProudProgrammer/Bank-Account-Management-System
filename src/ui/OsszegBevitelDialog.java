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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class OsszegBevitelDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel bevitelPanel = null;
	private JPanel buttonPanel = null;
	
	private static long osszeg = 0;
	
	private OnlyNumberTextField bevitelTextField = new OnlyNumberTextField(6, 12);
	private JButton rendbenButton = new JButton("Rendben");
	private JButton megsemButton = new JButton("Mégsem");
	
	private OsszegBevitelDialog(Component owner) {
		osszeg = 0;
		setMinimumSize(new Dimension(250, 100));
		setModal(true);
		setResizable(false);
		setTitle("Add meg a kívánt összeget!");
		setLayout(new GridLayout(2, 1));
		add(getBevitelPanel());
		add(getButtonPanel());
		
		bevitelTextField.addActionListener(this);
		rendbenButton.addActionListener(this);
		megsemButton.addActionListener(this);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	
	public static long showOsszegBevitelDialog(Component owner) {
		new OsszegBevitelDialog(owner);
		return osszeg;
	}
	
	private JPanel getBevitelPanel() {
		if(bevitelPanel == null) {
			bevitelPanel = new JPanel();
			bevitelPanel.add(new JLabel("Összeg: "));
			bevitelPanel.add(bevitelTextField);
			bevitelPanel.add(new JLabel(" Ft"));
		}
		return bevitelPanel;
	}
	
	private JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			buttonPanel.add(rendbenButton);
			buttonPanel.add(megsemButton);
		}
		return buttonPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == megsemButton) {
			osszeg = 0;
			setVisible(false);
			return;
		}
		setVisible(false);
		if(bevitelTextField.getText().equals("")) {
			osszeg = 0;
			return;
		}
		try {
			osszeg = Long.parseLong(bevitelTextField.getText());
		}catch(NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
			return;
		}
		return;
	}
	
}
