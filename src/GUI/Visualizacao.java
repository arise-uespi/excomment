package GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import adapters.QueryTableModel;
import suporte.Funcoes;

public class Visualizacao {

	private JFrame frame;
	private JTable table;
	private QueryTableModel qtm;

	public void show() {
		frame.setVisible(true);
	}
	
	/**
	 * Create the application.
	 */
	public Visualizacao() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("SQL Query");
		lblNewLabel.setBounds(10, 11, 110, 14);
		frame.getContentPane().add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 41, 735, 71);
		frame.getContentPane().add(scrollPane);
		
		final JLabel lbQtdeRows = new JLabel("");
		lbQtdeRows.setBounds(10, 546, 428, 14);
		frame.getContentPane().add(lbQtdeRows);

		final JEditorPane editQuery = new JEditorPane();
		scrollPane.setViewportView(editQuery);

		JButton btnRunQuery = new JButton("");
		btnRunQuery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int qtde;
				try {
					qtde = qtm.setQuery(editQuery.getText().trim());
					lbQtdeRows.setText(qtde + " registros retornados");
				} catch (Exception e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro:\n" + e.getMessage());
				}
			}
		});
		btnRunQuery.setIcon(new ImageIcon(Visualizacao.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		btnRunQuery.setBounds(750, 41, 34, 71);
		frame.getContentPane().add(btnRunQuery);

		qtm = new QueryTableModel();
		table = new JTable(qtm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		scrollPane_1.setViewportView(table);
		
		JScrollPane scrollPane_1 = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_1.setBounds(10, 155, 774, 378);
		frame.getContentPane().add(scrollPane_1);

		JLabel lblResultado = new JLabel("Resultado");
		lblResultado.setBounds(10, 130, 110, 14);
		frame.getContentPane().add(lblResultado);
	}
}
