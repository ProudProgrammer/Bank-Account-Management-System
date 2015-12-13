package ui;

import javax.swing.JPanel;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.border.EtchedBorder;

import db.DBSettings;

public final class DBSettingsDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private static boolean reConnect = false;	//M�gse gomb hat�s�ra false, Ment�s gomb hat�s�ra true, Alapb�l false
	
	private JPanel jContentPane = null;
	private JPanel dBPanel = null;
	private JPanel userPanel = null;
	private JPanel passPanel = null;
	private JPanel buttonPanel = null;
	
	private JTextField dBTextField = new JTextField(20);
	private JTextField userTextField = new JTextField(10);
	private JTextField passTextField = new JTextField(10);			
	private JButton alkalmazButton = new JButton("Alkalmaz");
	private JButton megsemButton = new JButton("M�gsem");
	
	private String[] settings = DBSettings.getSettings();
	
	{
		dBTextField.setText(settings[0]);
		userTextField.setText(settings[1]);
		passTextField.setText(settings[2]);
		
		dBTextField.setToolTipText("Form�tum: \"//ip_c�m:port_sz�ma/adatb�zis_neve\"");
		
		alkalmazButton.setIcon(new ImageIcon("icons/Save.png"));
		megsemButton.setIcon(new ImageIcon("icons/Exit.png"));
	}

	public DBSettingsDialog(Frame owner) {
		initialize();
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void initialize() {
		setSize(400, 235);
		setResizable(false);
		setModal(true);
		setTitle("Adatb�zis kapcsolat be�ll�t�sok");
		setIconImage(this.getToolkit().createImage("icons/Modify.png"));
		setContentPane(getJContentPane());
		
		alkalmazButton.addActionListener(this);
		megsemButton.addActionListener(this);
		dBTextField.addActionListener(this);
		userTextField.addActionListener(this);
		passTextField.addActionListener(this);
	}

	public static boolean getReConnect() {
		return reConnect;
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridLayout(4,1));
			jContentPane.add(getDBPanel());
			jContentPane.add(getUserPanel());
			jContentPane.add(getPassPanel());
			jContentPane.add(getButtonPanel());

		}
		return jContentPane;
	}
	
	private JPanel getDBPanel() {
		if(dBPanel == null) {
			dBPanel = new JPanel();
			dBPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			dBPanel.add(new JLabel("Adatb�zis c�me:"));
			dBPanel.add(dBTextField);
		}
		return dBPanel;
	}
	
	private JPanel getUserPanel() {
		if(userPanel == null) {
			userPanel = new JPanel();
			userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			userPanel.add(new JLabel("Felhaszn�l�n�v:"));
			userPanel.add(userTextField);
		}
		return userPanel;
	}
	
	private JPanel getPassPanel() {
		if(passPanel == null) {
			passPanel = new JPanel();
			passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			passPanel.add(new JLabel("Jelsz�:"));
			passPanel.add(passTextField);
		}
		return passPanel;
	}
	
	private JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			buttonPanel.add(alkalmazButton);
			buttonPanel.add(megsemButton);
		}
		return buttonPanel;
	}

	/**
	 * Esem�nykezel�s
	 * Ha az alkalmaz�st olyan adathordoz�r�l haszn�ljuk ami �r�sv�dett akkor ment�sn�l
	 * a ment�s sikertelens�g�r�l dial�gus ad h�rt
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == megsemButton) {
			reConnect = false;
			setVisible(false);
		}
		else {
			try {
				DBSettings.setSettings(dBTextField.getText(), userTextField.getText(), passTextField.getText());
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "A lemezre nem lehet �rni!", "Hiba", JOptionPane.ERROR_MESSAGE);
			}
			reConnect = true;
			setVisible(false);
		}
	}

} 
