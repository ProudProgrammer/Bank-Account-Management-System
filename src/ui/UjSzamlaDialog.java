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

public class UjSzamlaDialog extends JDialog implements ActionListener, DocumentListener, UgyfelValaszto {
	
	private static final long serialVersionUID = 1L;
	
	private MainWindow owner = null;
	
	private JPanel felsoPanel = null;
	private JPanel kozepsoPanel = null;
	private JPanel alsoPanel = null;

	private JButton kivalasztButton = new JButton("�gyf�l kiv�laszt�sa");
	
	private JLabel ugyfelszamLabel = new JLabel();
	private JLabel nevLabel = new JLabel();
	private JLabel lakcimLabel = new JLabel();
	private JLabel telszamLabel = new JLabel();
	private JLabel szigszamLabel = new JLabel();
	
	private OnlyNumberTextField kezdetiBefizetesTextField = new OnlyNumberTextField(10, 12);
	
	private JButton letrehozButton = new JButton("Sz�mla l�trehoz�sa");
	private JButton megsemButton = new JButton("M�gsem");
	
	{
		kezdetiBefizetesTextField.setToolTipText("Csak sz�m enged�lyezett ebben a mez�ben 12 karakter hossz�s�gban");
		
		kezdetiBefizetesTextField.setEditable(false);
		
		letrehozButton.setEnabled(false);
		
		kivalasztButton.setIcon(new ImageIcon("icons/Load.png"));
		letrehozButton.setIcon(new ImageIcon("icons/Save.png"));
		megsemButton.setIcon(new ImageIcon("icons/Exit.png"));
	}
	
	public UjSzamlaDialog(MainWindow owner) {
		this.owner = owner;
		setMinimumSize(new Dimension(580, 200));
		setModal(true);
		setTitle("�j sz�mla l�trehoz�sa");
		setIconImage(this.getToolkit().createImage("icons/Print.png"));
		setLayout(new BorderLayout());
		add(getFelsoPanel(), BorderLayout.NORTH);
		add(getKozepsoPanel(), BorderLayout.CENTER);
		add(getAlsoButtonPanel(), BorderLayout.SOUTH);
		
		kezdetiBefizetesTextField.getDocument().addDocumentListener(this);
		
		kezdetiBefizetesTextField.addActionListener(this);

		kivalasztButton.addActionListener(this);
		letrehozButton.addActionListener(this);
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
			kozepsoPanel.setLayout(new GridLayout(6, 2));
			kozepsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Kiv�lasztott �gyf�l param�terei"));
			
			kozepsoPanel.add(new JLabel("�gyf�lsz�m:"));
			kozepsoPanel.add(ugyfelszamLabel);
			
			kozepsoPanel.add(new JLabel("N�v:"));
			kozepsoPanel.add(nevLabel);
			
			kozepsoPanel.add(new JLabel("Lakc�m:"));
			kozepsoPanel.add(lakcimLabel);
			
			kozepsoPanel.add(new JLabel("Telefonsz�m:"));
			kozepsoPanel.add(telszamLabel);
			
			kozepsoPanel.add(new JLabel("Szem�lyigazolv�ny sz�m:"));
			kozepsoPanel.add(szigszamLabel);
			
			kozepsoPanel.add(new JLabel("Kezdeti befizet�s:"));
			JPanel kezdetiBefizetesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			kezdetiBefizetesPanel.add(kezdetiBefizetesTextField);
			kezdetiBefizetesPanel.add(new JLabel(" Ft"));
			kozepsoPanel.add(kezdetiBefizetesPanel);
		}
		return kozepsoPanel;
	}
	
	private JPanel getAlsoButtonPanel() {
		if(alsoPanel == null) {
			alsoPanel = new JPanel();
			alsoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			alsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			alsoPanel.add(letrehozButton);
			alsoPanel.add(megsemButton);
		}
		return alsoPanel;
	}
	
	@Override
	public void kiValasztottUgyfel(Ugyfel ugyfel) {
		ugyfelszamLabel.setText("" + ugyfel.getUgyfelszam());
		nevLabel.setText(ugyfel.getNev());
		lakcimLabel.setText(ugyfel.getLakcim());
		telszamLabel.setText(ugyfel.getTelszam());
		szigszamLabel.setText(ugyfel.getSzigszam());
		kezdetiBefizetesTextField.setEditable(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == kivalasztButton) {
			new UgyfelKivalasztasDialog(this, owner, this);
			return;
		}
		if(event.getSource() == megsemButton) {
			setVisible(false);
			return;
		}
		if(kezdetiBefizetesTextField.getText().equals(""))
			return;
		if((JOptionPane.showConfirmDialog(this, "Sz�mla l�trehoz�sa az " + ugyfelszamLabel.getText() + " �gyf�lsz�m� �gyf�lhez " + 
				kezdetiBefizetesTextField.getText() + " Ft kezdeti egyenleggel.", "Sz�mla l�trehoz�s meger�s�t�se",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
			int ugyfelszam = 0;
			long kezdetiBefizetes = 0;
			try {
				ugyfelszam = Integer.parseInt(ugyfelszamLabel.getText());
				kezdetiBefizetes = Long.parseLong(kezdetiBefizetesTextField.getText());
			}catch(NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Rendszerhiba!\n", "Renszerhiba", JOptionPane.ERROR_MESSAGE);
				return;
			}
			//min �s max k�z�tti v�letlen sz�m gener�l�sa = (int)(Math.random() * (max - min + 1) + min)
			long szamlaszam = (long)ugyfelszam * 10000 + (int)(Math.random() * 10000);
			boolean nemSikerult = true;
			int kiserlet = 0;
			while(nemSikerult & kiserlet <= 1000) {
				try {
					owner.getDBConnect().ujSzamla(szamlaszam, ugyfelszam, kezdetiBefizetes, "akt�v");
					nemSikerult = false;
				} catch (SQLException e) {
					kiserlet++;
				}
			}
			if(nemSikerult) {
				JOptionPane.showMessageDialog(this, "Nem siker�lt a sz�ml�t l�trehozni a " + ugyfelszam + " �gyf�lsz�m� �gyf�lhez mert elfogytak a szabad sz�mlasz�mok", "Nincs szabad sz�mlasz�m", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, "A " + nevLabel.getText() + " nev� �gyf�lhez a " + szamlaszam + " sz�m� sz�mla " + 
					kezdetiBefizetes + " Ft kezdeti egyenleggel l�trehozva", "Sikeres bevitel", JOptionPane.INFORMATION_MESSAGE);
		}
		return;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		letrehozButton.setEnabled(true);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		if(kezdetiBefizetesTextField.getText().equals(""))
			letrehozButton.setEnabled(false);
	}
}
