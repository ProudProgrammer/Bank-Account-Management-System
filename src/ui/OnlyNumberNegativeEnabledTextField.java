package ui;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public final class OnlyNumberNegativeEnabledTextField extends JTextField {
	
	private static final long serialVersionUID = 1L;
	
	private int columns;

	public OnlyNumberNegativeEnabledTextField(int columns) {
		super(columns);
		this.columns = columns;
		enableEvents(AWTEvent.KEY_EVENT_MASK);
	}

	public void processKeyEvent(KeyEvent e) {
		char c = e.getKeyChar();
		if(c == KeyEvent.VK_ENTER || c == KeyEvent.VK_BACK_SPACE) {}
		else if(getText().length() == 0 & c == '-') {}
		else if((getText().length() == 0 & c == '0') || (getText().equals("-") & c == '0'))
			e.consume();
		else if(c >= '0' & c <= '9' & getText().length() < columns) {}
		else
			e.consume();
		super.processKeyEvent(e);
	}
}
