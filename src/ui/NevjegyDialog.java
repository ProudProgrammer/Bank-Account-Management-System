package ui;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public final class NevjegyDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel jContentPane = null;
	private JTextArea jTextArea = null;
	
	public NevjegyDialog(Frame owner) {
		initialize();
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void initialize() {
		setMinimumSize(new Dimension(350, 220));
		setResizable(false);
		setTitle("Névjegy");
		setIconImage(this.getToolkit().createImage("icons/Info.png"));
		setContentPane(getJContentPane());
		setModal(true);
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTextArea(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
			jTextArea.setLineWrap(true);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setEditable(false);
			jTextArea.setMargin(new Insets(10, 10, 10, 10));
			jTextArea.setText("Banki számlavezetõ rendszer\n\n" +
					"Verzió: 1.01\n\n" +
					"Készült: 2011. Miskolci Egyetem Alkalmazott Informatikai Tanszék megbízásából.\n\n" +
					"Gábor Balázs");
		}
		return jTextArea;
	}
} 
