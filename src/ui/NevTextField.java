package ui;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class NevTextField extends JTextField{
	
	private static final long serialVersionUID = 1L;
	
	private int engedelyezettColumns;

	public NevTextField(int lathatoColumns, int engedelyezettColumns) {
		super(lathatoColumns);
		this.engedelyezettColumns = engedelyezettColumns;
		enableEvents(AWTEvent.KEY_EVENT_MASK);
	}

	public void processKeyEvent(KeyEvent e) {
		if(e.isControlDown() || e.isAltDown() || e.isAltGraphDown())
			e.consume();
		char c = e.getKeyChar();
		if(c == KeyEvent.VK_ENTER || c == KeyEvent.VK_BACK_SPACE || (getText().length() != 0 & c == KeyEvent.VK_SPACE)) {}
		else if(Character.isLetter(c) & getText().length() < engedelyezettColumns) {}
		else
			e.consume();
		super.processKeyEvent(e);
	}
}
