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

	private JButton kivalasztButton = new JButton("Ügyfél kiválasztása");
	
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
	
	private JLabel modosithatoVagyNem = new JLabel("Nem módosítható");
	
	private JButton modositButton = new JButton("Ügyfél módosítása");
	private JButton torolButton = new JButton("Ügyfél törlése");
	private JButton megsemButton = new JButton("Mégsem");
	
	{
		nevTextField.setToolTipText("Csak betû és space engedélyezett ebben a mezõben 30 karakter hosszúságban");
		lakcimTextField.setToolTipText("Csak betû, szám és . , / írásjelek engedélyezettek ebben a mezõben 100 karakter hosszúságban");
		telszamTextField.setToolTipText("Csak szám engedélyezett ebben a mezõben 20 karakter hosszúságban");
		szigszamTextField.setToolTipText("Csak betû és szám engedélyezett ebben a mezõben 20 karakter hosszúságban");
		
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
		setTitle("Ügyfél módosítás/lekérdezés");
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
			kozepsoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Kiválasztott ügyfél paraméterei"));
			
			kozepsoPanel.add(new JLabel("Ügyfélszám:"));
			kozepsoPanel.add(ugyfelszam);
			kozepsoPanel.add(new JLabel("Nem módosítható"));
			
			kozepsoPanel.add(new JLabel("Név:"));
			kozepsoPanel.add(nev);
			kozepsoPanel.add(nevPanel);
			
			kozepsoPanel.add(new JLabel("Lakcím:"));
			kozepsoPanel.add(lakcim);
			kozepsoPanel.add(lakcimPanel);
			
			kozepsoPanel.add(new JLabel("Telefonszám:"));
			kozepsoPanel.add(telszam);
			kozepsoPanel.add(telszamPanel);
			
			kozepsoPanel.add(new JLabel("Személyigazolvány szám:"));
			kozepsoPanel.add(szigszam);
			kozepsoPanel.add(szigszamPanel);
			
			kozepsoPanel.add(new JLabel("Státusz:"));
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
		modosithatoVagyNem.setText("Módosítható");
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
			if(JOptionPane.showConfirmDialog(this, "Biztosan törlésre kerüljön a " + 
					ugyfelszam.getText() + " ügyfélszámú ügyfél?", "Törlés megerõsítése", 
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try {
					if(owner.getDBConnect().ugyfelTorlese(Integer.parseInt(ugyfelszam.getText()))) {
						disableBevitel();
						statusz.setText("törölt");
						modosithatoVagyNem.setText("Nem módosítható");
					}else {
						JOptionPane.showMessageDialog(this, "Nem sikerült törölni az ügyfelet mert vannak aktív számlái!",
								"Sikertelen törlés", JOptionPane.ERROR_MESSAGE);
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
