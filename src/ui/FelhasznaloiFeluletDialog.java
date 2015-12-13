package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

public class FelhasznaloiFeluletDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private MainWindow owner = null;
	
	private JPanel opciokPanel = null;
	private JPanel buttonPanel = null;
	
	private File skinSettingsFile = new File("settings/skinsettings.dat");
	private PrintWriter skinMentese = null;
	private Scanner skinBetoltese = null;
	private static String aktualisSkin = "skin1";
	
	private JRadioButtonMenuItem skin1 = new JRadioButtonMenuItem("Metal");
	private JRadioButtonMenuItem skin2 = new JRadioButtonMenuItem("Motif");
	private JRadioButtonMenuItem skin3 = new JRadioButtonMenuItem("Nimbus");
	private JRadioButtonMenuItem skin4 = new JRadioButtonMenuItem("Windows");
	private JRadioButtonMenuItem skin5 = new JRadioButtonMenuItem("WindowsClassic");
	private ButtonGroup kinezetGroup = new ButtonGroup();
	
	private JButton alkalmazButton = new JButton("Alkalmaz");
	private JButton bezárButton = new JButton("Bezár");
	
	{
		kinezetGroup.add(skin1);
		kinezetGroup.add(skin2);
		kinezetGroup.add(skin3);
		kinezetGroup.add(skin4);
		kinezetGroup.add(skin5);
		
		alkalmazButton.setIcon(new ImageIcon("icons/Save.png"));
		bezárButton.setIcon(new ImageIcon("icons/Exit.png"));
	}
	
	public FelhasznaloiFeluletDialog(MainWindow owner) {
		this.owner = owner;
		setSize(300, 200);
		setMinimumSize(new Dimension(250, 100));
		setResizable(false);
		setModal(true);
		setTitle("Felhasználói felület beállítások");
		setIconImage(this.getToolkit().createImage("icons/Loading.png"));
		setLayout(new BorderLayout());
		add(getOpciokPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);
		
		try {
			skinBetoltese = new Scanner(skinSettingsFile);
			if(skinBetoltese.hasNext())
				aktualisSkin = skinBetoltese.nextLine();
		} catch (FileNotFoundException e) {
		}
		
		if(aktualisSkin.equals("skin1"))
			skin1.setSelected(true);
		if(aktualisSkin.equals("skin2"))
			skin2.setSelected(true);
		if(aktualisSkin.equals("skin3"))
			skin3.setSelected(true);
		if(aktualisSkin.equals("skin4"))
			skin4.setSelected(true);
		if(aktualisSkin.equals("skin5"))
			skin5.setSelected(true);
		
		alkalmazButton.addActionListener(this);
		bezárButton.addActionListener(this);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}
	
	private JPanel getOpciokPanel() {
		if(opciokPanel == null) {
			opciokPanel = new JPanel();
			opciokPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) , "Választható szkinek"));
			opciokPanel.setLayout(new GridLayout(5, 1));
			opciokPanel.add(skin1);
			opciokPanel.add(skin2);
			opciokPanel.add(skin3);
			opciokPanel.add(skin4);
			opciokPanel.add(skin5);
		}
		return opciokPanel;
	}
	
	private JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			buttonPanel.add(alkalmazButton);
			buttonPanel.add(bezárButton);
		}
		return buttonPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == bezárButton) {
			setVisible(false);
			return;
		}
		String lookAndFeel = null;
		if(skin1.isSelected()) {
			lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
			aktualisSkin = "skin1";
		}
		if(skin2.isSelected()) {
			lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			aktualisSkin = "skin2";
		}
		if(skin3.isSelected()) {
			lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
			aktualisSkin = "skin3";
		}
		if(skin4.isSelected()) {
			lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			aktualisSkin = "skin4";
		}
		if(skin5.isSelected()) {
			lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
			aktualisSkin = "skin5";
		}
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			SwingUtilities.updateComponentTreeUI(owner);
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(owner.getJelentesPanel());
			SwingUtilities.updateComponentTreeUI(owner.getMainPanel());
			SwingUtilities.updateComponentTreeUI(owner.getAtutalasPanel());
			SwingUtilities.updateComponentTreeUI(owner.getHelpPanel());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Nem sikerült a szkint beállítani!", "Felhasználói felület hiba", JOptionPane.ERROR_MESSAGE);
		}
		try {
			skinMentese = new PrintWriter(skinSettingsFile);
			skinMentese.println(aktualisSkin + "\n" + lookAndFeel);
		} catch (FileNotFoundException e1) {
		} finally {
			if(skinMentese != null)
				skinMentese.close();
		}
		return;
	}
}
