package GUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import suporte.Constantes;
import suporte.Funcoes;
import suporte.Variaveis;
import IO.Props;
import database.DataAccess;
import database.PostgreSQLJDBC;

public class Configuracoes {

	private JFrame frame;
	private JTextField editHost;
	private JTextField editPort;
	private JTextField editUser;
	private JTextField editBase;
	private JPasswordField editPassword;

	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public Configuracoes() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 217);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Host");
		lblNewLabel.setBounds(10, 11, 46, 14);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblPorta = new JLabel("Porta");
		lblPorta.setBounds(241, 11, 46, 14);
		frame.getContentPane().add(lblPorta);

		JLabel lblBase = new JLabel("Base");
		lblBase.setBounds(10, 103, 46, 14);
		frame.getContentPane().add(lblBase);

		editHost = new JTextField();
		editHost.setBounds(85, 11, 135, 20);
		frame.getContentPane().add(editHost);
		editHost.setColumns(10);
		editHost.setText(Variaveis.host);

		editPort = new JTextField();
		editPort.setColumns(10);
		editPort.setBounds(297, 11, 137, 20);
		editPort.setText(Variaveis.port);
		frame.getContentPane().add(editPort);

		editUser = new JTextField();
		editUser.setColumns(10);
		editUser.setBounds(85, 55, 135, 20);
		editUser.setText(Variaveis.user);
		frame.getContentPane().add(editUser);

		JButton btnSave = new JButton("Salvar");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					DataAccess.dataManager.open(editHost.getText(), editPort.getText(), editBase.getText(), editUser.getText(), new String(editPassword.getPassword()));

					Props props = new Props();

					Map<String, String> map = new HashMap<String, String>();
					map.put(Constantes.HOST, editHost.getText());
					map.put(Constantes.PORT, editPort.getText());
					map.put(Constantes.USER, editUser.getText());
					map.put(Constantes.PASSWORD, new String(editPassword.getPassword()));
					map.put(Constantes.DATABASE, editBase.getText());

					props.write(map);
					props.loadProperties();

					DataAccess.dataManager = new PostgreSQLJDBC();

					frame.setVisible(false);
					frame.dispose(); 
				} catch (Exception e) {
					if (!e.getMessage().contains("pkey")) {
						Funcoes.sendStackMail(e);
						JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro:\n" + e.getMessage());
					} else {
						frame.setVisible(false);
						frame.dispose(); 
					}
				}

				try {
					DataAccess.dataManager.close();
				} catch (SQLException e) {}
			}
		});
		btnSave.setBounds(66, 155, 89, 23);
		frame.getContentPane().add(btnSave);

		JButton btnCancel = new JButton("Cancelar");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				frame.setVisible(false);
				frame.dispose(); 
			}
		});
		btnCancel.setBounds(292, 155, 89, 23);
		frame.getContentPane().add(btnCancel);

		editBase = new JTextField();
		editBase.setText("");
		editBase.setColumns(10);
		editBase.setBounds(85, 100, 202, 20);
		editBase.setText(Variaveis.database);
		frame.getContentPane().add(editBase);

		JLabel lblUsurio = new JLabel("Usu\u00E1rio");
		lblUsurio.setBounds(10, 55, 65, 14);
		frame.getContentPane().add(lblUsurio);

		JLabel lblSenha = new JLabel("Senha");
		lblSenha.setBounds(241, 58, 46, 14);
		frame.getContentPane().add(lblSenha);

		editPassword = new JPasswordField();
		editPassword.setText("");
		editPassword.setColumns(10);
		editPassword.setBounds(299, 55, 135, 20);
		editPassword.setText(Variaveis.password);
		frame.getContentPane().add(editPassword);
	}

}
