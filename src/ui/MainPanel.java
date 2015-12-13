package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public final class MainPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTextArea jTextArea = null;
	
	public MainPanel() {
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(getJTextArea(), BorderLayout.CENTER);
	}

	public JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
			jTextArea.setBackground(SystemColor.control);
			jTextArea.setLineWrap(true);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setMargin(new Insets(10, 10, 10, 10));
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}
	
} 
