package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class HelpPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private File helpSzoveg = new File("help/help.txt");  //  @jve:decl-index=0:

	private JTextArea jTextArea = null;
	private Scanner scanner = null;
	private String szoveg = "";
	
	/**
	 * 	A param�terben megadott f�jl tartalma ker�l megjelen�t�sre
	 * 	Plain text sz�ks�ges
	 * 	Ha a f�jl nem el�rhet� err�l dial�gus t�j�koztat
	 */
	public HelpPanel() throws FileNotFoundException {
		scanner = new Scanner(helpSzoveg);
		while(scanner.hasNext())
			szoveg += scanner.nextLine() + "\n\n";
		if(scanner != null)
			scanner.close();
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(getJTextArea(), BorderLayout.CENTER);
		add(new JScrollPane(getJTextArea()));
	}

	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
			jTextArea.setLineWrap(true);
			jTextArea.setWrapStyleWord(true);
			jTextArea.setMargin(new Insets(10, 10, 10, 10));
			jTextArea.setEditable(false);
			jTextArea.setText(szoveg);
		}
		return jTextArea;
	}
} 
