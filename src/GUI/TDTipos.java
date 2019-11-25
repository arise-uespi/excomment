package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import IO.ManageFiles;
import adapters.QueryTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

import suporte.Constantes;
import suporte.Funcoes;
import suporte.Variaveis;

public class TDTipos {
	private JFrame frame;
	private JPanel contentPane;
	private JTable tablePalavras;
	private QueryTableModel qtmPalavras;
	private JTextField textField;
	
	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public TDTipos() {
		initialize();
	}

	/**
	 * Create the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		tablePalavras = new JTable(qtmPalavras);
		tablePalavras.setCellSelectionEnabled(true);
		tablePalavras.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//		scrollPane_2.setViewportView(tableComentario);

		JScrollPane spTable = new JScrollPane(tablePalavras, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		spTable.setBounds(10, 148, 636, 337);
		panel.add(spTable);

		JButton btnPalavra = new JButton("+");
		btnPalavra.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		panel.setLayout(null);
		btnPalavra.setBounds(10, 524, 41, 23);
		panel.add(btnPalavra);

		JButton btnDelPalavra = new JButton("x");
		btnDelPalavra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnDelPalavra.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		btnDelPalavra.setBounds(116, 524, 39, 23);
		panel.add(btnDelPalavra);
		
		JLabel label = new JLabel("Filtro");
		label.setBounds(658, 116, 89, 14);
		panel.add(label);
		
		JCheckBox checkBox = new JCheckBox("Tipo");
		checkBox.setBounds(658, 145, 97, 23);
		panel.add(checkBox);
		
		final JList list = new JList((ListModel) null);
		list.setBounds(656, 179, 130, 306);
		panel.add(list);
		
		JButton button_1 = new JButton("x");
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (list.getSelectedIndex() > -1) {
					//TODO Excluir item aqui
					synchronized (list) {
						list.notify();
						try {
							
						} catch (Exception e) {
							Funcoes.sendStackMail(e);
							JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro: " + e.getMessage());
						}
					}
				}
			}
		});
		button_1.setBounds(748, 500, 39, 23);
		panel.add(button_1);
		
		JButton button_2 = new JButton("+");
		button_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frame, "Informe o nome do novo tipo.\nSepare multiplas palavras por um ponto e virgula ';'");

				String[] array = palavras.split(";");

				//TODO Adicionar tipo na base aqui
				try {
				} catch (Exception e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro: " + e.getMessage());
				}
			}
		});
		button_2.setBounds(656, 500, 41, 23);
		panel.add(button_2);
		
		JLabel label_1 = new JLabel("Pesquisa");
		label_1.setBounds(10, 15, 89, 14);
		panel.add(label_1);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(10, 44, 636, 20);
		panel.add(textField);
		
		JButton button = new JButton("");
		button.setBounds(91, 11, 22, 25);
		panel.add(button);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(381, 89, 239, 33);
		panel.add(panel_1);
		
		JRadioButton radioButton = new JRadioButton("Qualquer termo");
		radioButton.setSelected(true);
		panel_1.add(radioButton);
		
		JRadioButton radioButton_1 = new JRadioButton("Todos termos");
		panel_1.add(radioButton_1);
		
		JButton button_3 = new JButton("");
		button_3.setBounds(624, 94, 22, 25);
		panel.add(button_3);
	}
}
