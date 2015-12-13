package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import db.Ugyfel;

public class UgyfelModositasDialog extends JDialog implements ActionListener, DocumentListener, UgyfelValaszto {
	
	private static final long serialVersionUID = 1L;
	
	private MainWindow owner = null;
	
	private JPanel felsoPanel = null;
	private JPanel kozepsoPanel = null;
	private JPanel alsoPanel = null;

	private JButton kivalasztButton = new JButton("�gyf�l kiv�laszt�sa");
	
	private JLabel ugyfelszam = new JLabel();
	private JLabel nev = new JLabel();
	private JLabel lakcim = new JLabel();
	private JLabel telszam = new JLabel();
	private JLabel szigszam = new JLabel();
	private JLabel statusz = new JLabel();
	
	private NevTextField nevTextField = new NevTextField(15, 30);
	private LakcimTextField lakcimTextField = new LakcimTextField(15, 100);
	private OnlyNumberTextField telszamTextField = new OnlyNumberTextField(15, 20);
	private IgazolvanyTextField szigszamTextField = new IgazolvanyTextField(15, 20);
	
	private JLabel modosithatoVagyNem = new JLabel("Nem m�dos�that�");
	
	private JButton modositButton = new JButton("�gyf�l m�dos�t�sa");
	private JButton torolButton = new JButton("�gyf�l t�rl�se");
	private JButton megsemButton = new JButton("M�gsem");
	
	{
		nevTextField.setToolTipText("Csak bet� �s space enged�lyezett ebben a mez�ben 30 karakter hossz�s�gban");
		lakcimTextField.setToolTipText("Csak bet�, sz�m �s . , / �r�sjelek enged�lyezettek ebben a mez�ben 100 karakter hossz�s�gban");
		telszamTextField.setToolTipText("Csak sz�m enged�lyezett ebben a mez�ben 20 karakter hossz�s�gban");
		szigszamTextField.setToolTipText("Csak bet� �s sz�m enged�lyezett ebben a mez�ben 20 karakter hossz�s�gban");
		
		nevTextField.setEditable(false);
		lakcimTextField.setEditable(false);
		telszamTextField.setEditable(false);
		szigszamTextField.setEditable(false);
		
		modositButton.setEnabled(false);
		torolButton.setEnabled(false);
		
		kivalasztButton.setIcon(new ImageIcon("icons/Load.png"));
		modositButton.setIcon(new ImageIcon("icons/Save.png"));
		torolButton.setIcon(new ImageIcon("icons/Delete.png"));
		megsemButton.setIcon(new ImageIcon("icons/Exit.png"));
	}
	
	public UgyfelModositasDialog(MainWindow owner) {
		this.owner = owner;
		setSize(780, 400);
		setMinimumSize(new Dimension(780, 200));
		setResizable(false);
		setModal(true);
		setTitle("�gyf�l m�dos�t�s/lek�rdez�s");
		setIconImage(this.getToolkit().createImage("icons/Profile.png"));
		setLayout(new BorderLayout());
		add(getFelsoPanel(), BorderLayout.NORTH);
		add(getKozepsoPanel(), BorderLayout.CENTER);
		add(getAlsoButtonPanel(), BorderLayout.SOUTH);
		
		nevTextField.getDocument().addDocumentListener(this);
		lakcimTextField.getDocument().addDocumentListener(this);
		telszamTextField.getDocument().addDocumentListener(this);
		szigszamTextField.getDocument().addDocumentListener(this);
		
		nevTextField.addActionListener(this);
		lakcimTextField.addActionListener(this);
		telszamTextField.addActionListener(this);
		szigszamTextField.addActionListener(this);
		
		kivalasztButton.addActionListener(this);
		modositButton.addActionListener(this);
		torolButton.addActionListener(this);
		megsemButton.addActionListener(this);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private JPanel getFelsoPanel() {
		if(felsoPanel == null) {
			felsoPanel = new JPanel();
			felsoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			felsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			felsoPanel.add(kivalasztButton);
		}
		return felsoPanel;
	}
	
	private JPanel getKozepsoPanel() {
		if(kozepsoPanel == null) {
			kozepsoPanel = new JPanel();
			kozepsoPanel.setLayout(new GridLayout(6, 3, 20, 0));
			JPanel nevPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			nevPanel.add(nevTextField);
			JPanel lakcimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			lakcimPanel.add(lakcimTextField);
			JPanel telszamPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			telszamPanel.add(telszamTextField);
			JPanel szigszamPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			szigszamPanel.add(szigszamTextField);
			kozepsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Kiv�lasztott �gyf�l param�terei"));
			
			kozepsoPanel.add(new JLabel("�gyf�lsz�m:"));
			kozepsoPanel.add(ugyfelszam);
			kozepsoPanel.add(new JLabel("Nem m�dos�that�"));
			
			kozepsoPanel.add(new JLabel("N�v:"));
			kozepsoPanel.add(nev);
			kozepsoPanel.add(nevPanel);
			
			kozepsoPanel.add(new JLabel("Lakc�m:"));
			kozepsoPanel.add(lakcim);
			kozepsoPanel.add(lakcimPanel);
			
			kozepsoPanel.add(new JLabel("Telefonsz�m:"));
			kozepsoPanel.add(telszam);
			kozepsoPanel.add(telszamPanel);
			
			kozepsoPanel.add(new JLabel("Szem�lyigazolv�ny sz�m:"));
			kozepsoPanel.add(szigszam);
			kozepsoPanel.add(szigszamPanel);
			
			kozepsoPanel.add(new JLabel("St�tusz:"));
			kozepsoPanel.add(statusz);
			kozepsoPanel.add(modosithatoVagyNem);
		}
		return kozepsoPanel;
	}
	
	private JPanel getAlsoButtonPanel() {
		if(alsoPanel == null) {
			alsoPanel = new JPanel();
			alsoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			alsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			alsoPanel.add(modositButton);
			alsoPanel.add(torolButton);
			alsoPanel.add(megsemButton);
		}
		return alsoPanel;
	}
	
	@Override
	public void kiValasztottUgyfel(Ugyfel ugyfel) {
		ugyfelszam.setText("" + ugyfel.getUgyfelszam());
		nev.setText(ugyfel.getNev());
		lakcim.setText(ugyfel.getLakcim());
		telszam.setText(ugyfel.getTelszam());
		szigszam.setText(ugyfel.getSzigszam());
		statusz.setText(ugyfel.getStatusz());
		modosithatoVagyNem.setText("M�dos�that�");
		enableBevitel(); 
	}
	
	private void disableBevitel() {
		nevTextField.setEditable(false);
		lakcimTextField.setEditable(false);
		telszamTextField.setEditable(false);
		szigszamTextField.setEditable(false);
		modositButton.setEnabled(false);
		torolButton.setEnabled(false);
	}
	
	private void enableBevitel() {
		nevTextField.setEditable(true);
		lakcimTextField.setEditable(true);
		telszamTextField.setEditable(true);
		szigszamTextField.setEditable(true);
		torolButton.setEnabled(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == kivalasztButton) {
			new UgyfelKivalasztasDialog(this, owner, this);
			return;
		}
		if(event.getSource() == torolButton) {
			if(JOptionPane.showConfirmDialog(this, "Biztosan t�rl�sre ker�lj�n a " + 
					ugyfelszam.getText() + " �gyf�lsz�m� �gyf�l?", "T�rl�s meger�s�t�se", 
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try {
					if(owner.getDBConnect().ugyfelTorlese(Integer.parseInt(ugyfelszam.getText()))) {
						disableBevitel();
						statusz.setText("t�r�lt");
						modosithatoVagyNem.setText("Nem m�dos�that�");
					}else {
						JOptionPane.showMessageDialog(this, "Nem siker�lt t�r�lni az �gyfelet mert vannak akt�v sz�ml�i!",
								"Sikertelen t�rl�s", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e) {
					owner.nincsKapcsolatMessage();
				}
			}
			return;
		}
		if(event.getSource() == megsemButton) {
			setVisible(false);
			return;
		}
		try {
			owner.getDBConnect().ugyfelModositasa(Integer.parseInt(ugyfelszam.getText()), nevTextField.getText(), lakcimTextField.getText(), telszamTextField.getText(), szigszamTextField.getText());
			if(!(nevTextField.getText().equals("")))
				nev.setText(nevTextField.getText());
			if(!(lakcimTextField.getText().equals("")))
				lakcim.setText(lakcimTextField.getText());
			if(!(telszamTextField.getText().equals("")))
				telszam.setText(telszamTextField.getText());
			if(!(szigszamTextField.getText().equals("")))
				szigszam.setText(szigszamTextField.getText());
			nevTextField.setText("");
			lakcimTextField.setText("");
			telszamTextField.setText("");
			szigszamTextField.setText("");
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			owner.nincsKapcsolatMessage();
		}
		return;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		JOptionPane.showMessageDialog(this, "changedUpdate", "", JOptionPane.DEFAULT_OPTION);
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		modositButton.setEnabled(true);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		if(nevTextField.getText().equals("") & lakcimTextField.getText().equals("") & telszamTextField.getText().equals("") & szigszamTextField.getText().equals(""))
			modositButton.setEnabled(false);
	}
}
