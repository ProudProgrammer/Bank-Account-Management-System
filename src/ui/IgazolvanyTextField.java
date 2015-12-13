package ui;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class IgazolvanyTextField extends JTextField{
	
	private static final long serialVersionUID = 1L;
	
	private int engedelyezettColumns;

	public IgazolvanyTextField(int lathatoColumns, int engedelyezettColumns) {
		super(lathatoColumns);
		this.engedelyezettColumns = engedelyezettColumns;
		enableEvents(AWTEvent.KEY_EVENT_MASK);
	}

	public void processKeyEvent(KeyEvent e) {
		e.setKeyChar(Character.toUpperCase(e.getKeyChar()));
		char c = e.getKeyChar();
		if(c == KeyEvent.VK_ENTER || c == KeyEvent.VK_BACK_SPACE) {}
		else if(((c >= '0' & c <= '9') || (c >= 'A' & c <= 'Z')) & getText().length() < engedelyezettColumns) {}
		else
			e.consume();
		super.processKeyEvent(e);
	}
}
