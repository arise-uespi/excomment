package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import suporte.Constantes;
import suporte.Funcoes;
import suporte.Variaveis;
import IO.ManageFiles;
import adapters.QueryTableModel;
import java.awt.Component;
import javax.swing.ScrollPaneConstants;
import javax.swing.ListModel;

public class Consulta {

	private JFrame frame;
	private JTable tableSQL;
	private JTextField editPesquisa;
	private QueryTableModel qtmSQL;
	private QueryTableModel qtmPesquisa;
	private JTable tableComentario;
	private JLabel lblQtdeRowsPesquisa;

	private JCheckBox chckMetodo, chckClasse, chckProjeto, chckCaminho;

	private String selecionado = "comments";
	private String campo = "comment";
	private String termo = "any";

	private ArrayList<String> metodos = new ArrayList<String>();
	private ArrayList<String> classes = new ArrayList<String>();
	private ArrayList<String> caminhos = new ArrayList<String>();
	private ArrayList<String> projetos = new ArrayList<String>();

	private DefaultListModel modelMetodo, modelClasses, modelCaminhos, modelProjetos, modelPadroes, modelPadroesSelecionados;
	private JTextField edit_PesquisaPattern;

	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public Consulta() {
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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 794, 571);
		frame.getContentPane().add(tabbedPane);

		qtmPesquisa = new QueryTableModel();

		JPanel panelComments = new JPanel();
		tabbedPane.addTab("Comentários", null, panelComments, null);
		panelComments.setLayout(null);

		editPesquisa = new JTextField();
		editPesquisa.setBounds(10, 40, 636, 20);
		editPesquisa.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				consultar();
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});

		JLabel lblPesquisa = new JLabel("Pesquisa");
		lblPesquisa.setBounds(10, 11, 89, 14);
		panelComments.add(lblPesquisa);
		panelComments.add(editPesquisa);
		editPesquisa.setColumns(10);

		JPanel panelTable = new JPanel();
		panelTable.setBounds(10, 85, 312, 33);
		panelComments.add(panelTable);

		JRadioButton rdbtnComentrio = new JRadioButton("Coment\u00E1rio");
		rdbtnComentrio.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				selecionado = "comments";
				campo = "comment";
				consultar();
			}
		});
		rdbtnComentrio.setSelected(true);
		panelTable.add(rdbtnComentrio);

		JRadioButton rdbtnMtodo = new JRadioButton("M\u00E9todo");
		rdbtnMtodo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				selecionado = "methods";
				campo = "name";
				consultar();
			}
		});
		panelTable.add(rdbtnMtodo);

		JRadioButton rdbtnClasse = new JRadioButton("Classe");
		rdbtnClasse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				selecionado = "classes";
				campo = "name";
				consultar();
			}
		});
		panelTable.add(rdbtnClasse);

		JRadioButton radioProjeto = new JRadioButton("Projeto");
		radioProjeto.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				selecionado = "projects";
				campo = "name";
				consultar();
			}
		});
		panelTable.add(radioProjeto);

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdbtnComentrio);
		btnGroup.add(rdbtnMtodo);
		btnGroup.add(rdbtnClasse);
		btnGroup.add(radioProjeto);

		tableComentario = new JTable(qtmPesquisa);
		tableComentario.setCellSelectionEnabled(true);
		tableComentario.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//		scrollPane_2.setViewportView(tableComentario);

		JScrollPane spTable = new JScrollPane(tableComentario, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		spTable.setBounds(10, 148, 636, 337);
		panelComments.add(spTable);

		lblQtdeRowsPesquisa = new JLabel("");
		lblQtdeRowsPesquisa.setBounds(10, 518, 428, 14);
		panelComments.add(lblQtdeRowsPesquisa);

		JLabel lblFiltros = new JLabel("Filtros");
		lblFiltros.setBounds(655, 11, 89, 14);
		panelComments.add(lblFiltros);

		chckMetodo = new JCheckBox("M\u00E9todo");
		chckMetodo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				consultar();
			}
		});
		chckMetodo.setBounds(652, 37, 97, 23);
		panelComments.add(chckMetodo);

		JScrollPane spMetodo = new JScrollPane();
		spMetodo.setBounds(660, 69, 123, 50);
		panelComments.add(spMetodo);

		modelMetodo = new DefaultListModel();
		final JList listMetodos = new JList(modelMetodo);
		spMetodo.setViewportView(listMetodos);

		JButton btnAddMethod = new JButton("+");
		btnAddMethod.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frame, "Informe o nome " +
						"dos metodos separando-os por ponto e virgula ';'");

				String[] array = palavras.split(";");
				metodos.addAll(Arrays.asList(array));

				modelMetodo.clear();
				fillModel(modelMetodo, metodos);

				consultar();
			}
		});
		btnAddMethod.setBounds(660, 129, 41, 23);
		panelComments.add(btnAddMethod);

		JButton btnDelMetodo = new JButton("x");
		btnDelMetodo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listMetodos.getSelectedIndex() > -1) {
					metodos.remove(listMetodos.getSelectedIndex());
					fillModel(modelMetodo, metodos);

					synchronized (listMetodos) {
						listMetodos.notify();
						consultar();
					}
				}
			}
		});
		btnDelMetodo.setBounds(742, 129, 41, 23);
		panelComments.add(btnDelMetodo);

		chckClasse = new JCheckBox("Classe");
		chckClasse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				consultar();
			}
		});
		chckClasse.setBounds(656, 163, 97, 23);
		panelComments.add(chckClasse);

		JScrollPane spClasse = new JScrollPane();
		spClasse.setBounds(660, 193, 123, 50);
		panelComments.add(spClasse);

		modelClasses = new DefaultListModel();
		final JList listClasses = new JList(modelClasses);
		spClasse.setViewportView(listClasses);

		JButton btnAddClasse = new JButton("+");
		btnAddClasse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frame, "Informe o nome " +
						"das classes separando-os por ponto e virgula ';'");

				String[] array = palavras.split(";");
				classes.addAll(Arrays.asList(array));

				modelClasses.clear();
				fillModel(modelClasses, classes);

				consultar();
			}
		});
		btnAddClasse.setBounds(660, 254, 41, 23);
		panelComments.add(btnAddClasse);

		JButton btnDelClasse = new JButton("x");
		btnDelClasse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listClasses.getSelectedIndex() > -1) {
					classes.remove(listClasses.getSelectedIndex());
					fillModel(modelClasses, classes);

					synchronized (listClasses) {
						listClasses.notify();
						consultar();
					}
				}
			}
		});
		btnDelClasse.setBounds(742, 254, 41, 23);
		panelComments.add(btnDelClasse);

		chckProjeto = new JCheckBox("Projeto");
		chckProjeto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				consultar();
			}
		});
		chckProjeto.setBounds(656, 284, 97, 23);
		panelComments.add(chckProjeto);

		JButton btnAddProjeto = new JButton("+");
		btnAddProjeto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frame, "Informe o nome " +
						"dos projetos separando-os por ponto e virgula ';'");

				String[] array = palavras.split(";");
				projetos.addAll(Arrays.asList(array));

				modelProjetos.clear();
				fillModel(modelProjetos, projetos);

				consultar();
			}
		});

		JScrollPane spProjeto = new JScrollPane();
		spProjeto.setBounds(660, 314, 123, 50);
		panelComments.add(spProjeto);

		modelProjetos = new DefaultListModel();
		final JList listProjetos = new JList(modelProjetos);
		spProjeto.setViewportView(listProjetos);

		btnAddProjeto.setBounds(660, 375, 41, 23);
		panelComments.add(btnAddProjeto);

		JButton btnDelProjeto = new JButton("x");
		btnDelProjeto.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listProjetos.getSelectedIndex() > -1) {
					projetos.remove(listProjetos.getSelectedIndex());
					fillModel(modelProjetos, projetos);

					synchronized (listProjetos) {
						listProjetos.notify();
						consultar();
					}
				}
			}
		});
		btnDelProjeto.setBounds(742, 375, 41, 23);
		panelComments.add(btnDelProjeto);

		chckCaminho = new JCheckBox("Caminho");
		chckCaminho.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				consultar();
			}
		});
		chckCaminho.setBounds(656, 405, 97, 23);
		panelComments.add(chckCaminho);

		JScrollPane spCaminho = new JScrollPane();
		spCaminho.setBounds(660, 435, 123, 50);
		panelComments.add(spCaminho);

		modelCaminhos = new DefaultListModel();
		final JList listCaminhos = new JList(modelCaminhos);
		spCaminho.setViewportView(listCaminhos);

		JButton btnAddCaminho = new JButton("+");
		btnAddCaminho.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frame, "Informe o caminho " +
						"dos arquivos separando-os por ponto e virgula ';'");

				String[] array = palavras.split(";");
				caminhos.addAll(Arrays.asList(array));

				modelCaminhos.clear();
				fillModel(modelCaminhos, caminhos);

				consultar();
			}
		});
		btnAddCaminho.setBounds(660, 496, 41, 23);
		panelComments.add(btnAddCaminho);

		JButton btnDelCaminho = new JButton("x");
		btnDelCaminho.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listCaminhos.getSelectedIndex() > -1) {
					caminhos.remove(listCaminhos.getSelectedIndex());
					fillModel(modelCaminhos, caminhos);

					synchronized (listCaminhos) {
						listCaminhos.notify();
						consultar();
					}
				}
			}
		});
		btnDelCaminho.setBounds(742, 496, 41, 23);
		panelComments.add(btnDelCaminho);

		JPanel panelTermo = new JPanel();
		panelTermo.setBounds(381, 85, 239, 33);
		panelComments.add(panelTermo);

		JRadioButton rdbtnQualquerTermo = new JRadioButton("Qualquer termo");
		rdbtnQualquerTermo.setSelected(true);
		rdbtnQualquerTermo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				termo = "any";
				consultar();
			}
		});
		panelTermo.add(rdbtnQualquerTermo);

		JRadioButton rdbtnTodosTermos = new JRadioButton("Todos termos");
		rdbtnTodosTermos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				termo = "all";
				consultar();
			}
		});
		panelTermo.add(rdbtnTodosTermos);

		ButtonGroup groupTermo = new ButtonGroup();
		groupTermo.add(rdbtnTodosTermos);
		groupTermo.add(rdbtnQualquerTermo);

		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "A opção 'Qualquer termo' retornará registros que possuem ao menos\n" +
						"um dos termos informados separados por %. Já a opção 'Todos termos'\n" +
						"retorna registros onde todos os termos separados por % estejam presentes.\n" +
						"Por exemplo para procurar comentários que possuam as palavras Read OU Write\n" +
						"selecione 'Qualquer termo' e pesquise por read%write o mesmo vale para\n" +
						"'Todos termos', mas neste caso retornando registros que possuam read E write.");
			}
		});
		button.setIcon(new ImageIcon(Consulta.class.getResource("/javax/swing/plaf/metal/icons/ocean/question.png")));
		button.setBounds(624, 90, 22, 25);
		panelComments.add(button);

		JButton button_1 = new JButton("");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Filtros servem para limitar a pesquisa aos fatores desejados.\n" +
						"Por exemplo para procurar comentários que estão somente no método" +
						"getCidade é só clicar no botão de adicionar '+' e colocar o nome deste.\n" +
						"Observando que a pesquisa é case sensitive, ou seja, A # a.");
			}
		});
		button_1.setIcon(new ImageIcon(Consulta.class.getResource("/javax/swing/plaf/metal/icons/ocean/question.png")));
		button_1.setBounds(761, 7, 22, 25);
		panelComments.add(button_1);

		JButton button_2 = new JButton("");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Para procurar por mais de um termo separe-os utilizando\n" +
						"o simbolo de porcentagem (%). Por exemplo para encontrar\n" +
						"comentários que possuam as palavras 'Read' e/ou 'Write' no\n" +
						"mesmo comentário pesquise por read%write.");
			}
		});
		button_2.setIcon(new ImageIcon(Consulta.class.getResource("/javax/swing/plaf/metal/icons/ocean/question.png")));
		button_2.setBounds(91, 7, 22, 25);
		panelComments.add(button_2);

		JButton button_3 = new JButton("");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Selecione a informação que deseja procurar.");

			}
		});
		button_3.setIcon(new ImageIcon(Consulta.class.getResource("/javax/swing/plaf/metal/icons/ocean/question.png")));
		button_3.setBounds(332, 90, 22, 25);
		panelComments.add(button_3);

		JButton btnExportComments = new JButton("Exportar");
		btnExportComments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int option = fc.showSaveDialog(frame);
				if(option == JFileChooser.APPROVE_OPTION){
					String filename = fc.getSelectedFile().getName(); 
					String path = fc.getSelectedFile().getParentFile().getPath();

					int len = filename.length();
					String ext = "";
					String file = "";

					if(len > 4){
						ext = filename.substring(len-4, len);
					}

					if(ext.equals(".xls")){
						file = path + "\\" + filename; 
					}else{
						file = path + "\\" + filename + ".xls"; 
					}
					ManageFiles manageFiles = new ManageFiles();

					try {
						manageFiles.toExcel(tableComentario, new File(file));
						JOptionPane.showMessageDialog(frame, "Arquivo exportado com sucesso!");
					} catch (Exception e) {
						Funcoes.sendStackMail(e);
						JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro:\n" + e.getMessage());
					}
				}
			}
		});
		btnExportComments.setBounds(557, 496, 89, 23);
		panelComments.add(btnExportComments);

		JPanel panelSQL = new JPanel();
		tabbedPane.addTab("Consulta", null, panelSQL, null);
		panelSQL.setLayout(null);

		JLabel label = new JLabel("SQL Query");
		label.setBounds(10, 11, 110, 14);
		panelSQL.add(label);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 41, 714, 71);
		panelSQL.add(scrollPane);

		final JEditorPane editQuery = new JEditorPane();
		scrollPane.setViewportView(editQuery);

		final JLabel lbQtdeRowsSQL = new JLabel("");
		lbQtdeRowsSQL.setBounds(10, 508, 428, 14);
		panelSQL.add(lbQtdeRowsSQL);

		JButton btnRunQuery = new JButton("");
		btnRunQuery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int qtde;
				try {
					qtde = qtmSQL.setQuery(editQuery.getText().trim());
					lbQtdeRowsSQL.setText(qtde + " registros retornados");
				} catch (Exception e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro:\n" + e.getMessage());
				}
			}
		});
		btnRunQuery.setIcon(new ImageIcon(Consulta.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		btnRunQuery.setBounds(735, 41, 34, 71);
		panelSQL.add(btnRunQuery);

		qtmSQL = new QueryTableModel();
		tableSQL = new JTable(qtmSQL);
		tableSQL.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//		scrollPane_1.setViewportView(table);

		JScrollPane scrollPane_1 = new JScrollPane(tableSQL, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_1.setBounds(10, 155, 759, 335);
		panelSQL.add(scrollPane_1);

		JLabel lblResultadoSQL = new JLabel("Resultado");
		lblResultadoSQL.setBounds(10, 130, 110, 14);
		panelSQL.add(lblResultadoSQL);

		JButton btn_exportarSQL = new JButton("Exportar");
		btn_exportarSQL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int option = fc.showSaveDialog(frame);
				if(option == JFileChooser.APPROVE_OPTION){
					String filename = fc.getSelectedFile().getName(); 
					String path = fc.getSelectedFile().getParentFile().getPath();

					int len = filename.length();
					String ext = "";
					String file = "";

					if(len > 4){
						ext = filename.substring(len-4, len);
					}

					if(ext.equals(".xls")){
						file = path + "\\" + filename; 
					}else{
						file = path + "\\" + filename + ".xls"; 
					}
					ManageFiles manageFiles = new ManageFiles();

					try {
						manageFiles.toExcel(tableSQL, new File(file));
						JOptionPane.showMessageDialog(frame, "Arquivo exportado com sucesso!");
					} catch (Exception e) {
						Funcoes.sendStackMail(e);
						JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro:\n" + e.getMessage());
					}
				}
			}
		});
		btn_exportarSQL.setBounds(680, 509, 89, 23);
		panelSQL.add(btn_exportarSQL);

		JButton button_4 = new JButton("");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Informação das tableas:\n" +
						"projects 		 -> id, name\n" +
						"classes  		 -> id, name, path, idproject\n" +
						"methods  		 -> id, name, idclass\n" +
						"comments 		 -> id, comment, steamed_comment, idmethod, idclass\n" +
						"			 		hasflag, path, idproject\n" +
						"linked_comments -> id, idcomment, idmethod, idclass, path");
			}
		});
		button_4.setIcon(new ImageIcon(Consulta.class.getResource("/com/sun/java/swing/plaf/windows/icons/Question.gif")));
		button_4.setBounds(91, 7, 22, 25);
		panelSQL.add(button_4);

		//TODO TAB Padroes
		JPanel panelPadrao = new JPanel();
		panelPadrao.setLayout(null);
		tabbedPane.addTab("Padrões", null, panelPadrao, null);

		JLabel lblSelecionados = new JLabel("Selecionados");
		lblSelecionados.setBounds(10, 11, 110, 14);
		panelPadrao.add(lblSelecionados);

		JLabel label_2 = new JLabel("");
		label_2.setBounds(10, 508, 428, 14);
		panelPadrao.add(label_2);

		JScrollPane scrollPane_3 = new JScrollPane((Component) null, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_3.setBounds(10, 313, 768, 191);
		panelPadrao.add(scrollPane_3);

		JLabel label_3 = new JLabel("Resultado");
		label_3.setBounds(10, 288, 110, 14);
		panelPadrao.add(label_3);

		JButton button_6 = new JButton("Exportar");
		button_6.setBounds(689, 509, 89, 23);
		panelPadrao.add(button_6);

		JButton button_7 = new JButton("");
		button_7.setBounds(91, 7, 22, 25);
		panelPadrao.add(button_7);

		modelPadroesSelecionados = new DefaultListModel<>();
		final JList listSelecionados = new JList(modelPadroesSelecionados);
		listSelecionados.setBounds(10, 36, 370, 191);
		fillModel(modelPadroesSelecionados, Variaveis.padroesSelecionados);
		panelPadrao.add(listSelecionados);

		modelPadroes = new DefaultListModel();
		final JList listPadroes = new JList(modelPadroes);
		listPadroes.setBounds(441, 36, 337, 191);
		fillModel(modelPadroes, Variaveis.padroes);
		panelPadrao.add(listPadroes);

		JButton btn_add = new JButton("+");
		btn_add.setBounds(664, 238, 41, 23);
		btn_add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String padroes = JOptionPane.showInputDialog(frame, "Informe o padrão a ser inserido");
				Variaveis.padroes.add(padroes);

				modelPadroes.clear();
				fillModel(modelPadroes, Variaveis.padroes);

				ManageFiles manageFiles = new ManageFiles();
				try {
					manageFiles.writeFileLineByLine(Constantes.PADROES, Variaveis.padroes);
				} catch (IOException e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro: " + e.getMessage());
				}
			}
		});
		panelPadrao.add(btn_add);

		JButton btn_del = new JButton("x");
		btn_del.setBounds(738, 238, 41, 23);
		btn_del.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listPadroes.getSelectedIndex() > -1) {
					int n = JOptionPane.showConfirmDialog(  
							null,
							"Deseja realmente excluir o padrão selecionado?!" ,
							"",
							JOptionPane.YES_NO_OPTION);

					if(n == JOptionPane.YES_OPTION)
					{
						Variaveis.padroes.remove(listPadroes.getSelectedIndex());
						fillModel(modelPadroes, Variaveis.padroes);

						synchronized (listPadroes) {
							listPadroes.notify();
							try {
								new ManageFiles().writeFileLineByLine(Constantes.PADROES, Variaveis.padroes);
							} catch (IOException e) {
								Funcoes.sendStackMail(e);
								JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro: " + e.getMessage());
							}
						}
					}
				}
			}
		});
		panelPadrao.add(btn_del);

		JLabel lblPadroes = new JLabel("Padroes");
		lblPadroes.setBounds(441, 11, 89, 14);
		panelPadrao.add(lblPadroes);

		JButton btn_selecionar = new JButton("<");
		btn_selecionar.setBounds(390, 92, 41, 23);
		btn_selecionar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listPadroes.getSelectedIndex() > -1) {
					Variaveis.padroesSelecionados.add(Variaveis.padroes.get(listPadroes.getSelectedIndex()));

					modelPadroesSelecionados.clear();
					fillModel(modelPadroesSelecionados, Variaveis.padroesSelecionados);

					synchronized (listSelecionados) {
						listSelecionados.notify();
						try {
							new ManageFiles().writeFileLineByLine(Constantes.PADROESSELECIONADOS, Variaveis.padroesSelecionados);
						} catch (IOException e) {
							Funcoes.sendStackMail(e);
							JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro: " + e.getMessage());
						}
					}
				}
			}
		});
		panelPadrao.add(btn_selecionar);

		JButton btn_deselecionar = new JButton(">");
		btn_deselecionar.setBounds(390, 163, 41, 23);
		btn_deselecionar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listSelecionados.getSelectedIndex() > -1) {
					Variaveis.padroesSelecionados.remove(listSelecionados.getSelectedIndex());

					modelPadroesSelecionados.clear();
					fillModel(modelPadroesSelecionados, Variaveis.padroesSelecionados);

					synchronized (listSelecionados) {
						listSelecionados.notify();
						try {
							new ManageFiles().writeFileLineByLine(Constantes.PADROESSELECIONADOS, Variaveis.padroesSelecionados);
						} catch (IOException e) {
							Funcoes.sendStackMail(e);
							JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro: " + e.getMessage());
						}
					}
				}
			}
		});
		panelPadrao.add(btn_deselecionar);

		edit_PesquisaPattern = new JTextField();
		edit_PesquisaPattern.setColumns(10);
		edit_PesquisaPattern.setBounds(10, 257, 540, 20);
		edit_PesquisaPattern.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				consultarPattern();
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		panelPadrao.add(edit_PesquisaPattern);

		JLabel label_4 = new JLabel("Pesquisa");
		label_4.setBounds(10, 232, 89, 14);
		panelPadrao.add(label_4);
	}

	private void consultar() {
		String query;
		String where = "";
		if (editPesquisa.getText().trim().length() > 0) {

			if (selecionado.equals("classes")) {
				query = locClasses();
			} else {	
				if (selecionado.equals("methods")) {
					query = locMetodos();
				} else {
					if (selecionado.equals("comments")) {
						query = locComentarios();
					} else {
						query = "SELECT * FROM " + selecionado + " WHERE lower(" + campo + ") LIKE '%" + editPesquisa.getText() + "%';";
					}
				} 
			}
			System.err.println(query);
			int qtde;
			try {
				qtde = qtmPesquisa.setQuery(query);
				lblQtdeRowsPesquisa.setText(qtde + " registros retornados");
			} catch (Exception e) {
				Funcoes.sendStackMail(e);
				JOptionPane.showMessageDialog(frame, "Ocorreu o seguinte erro:\n" + e.getMessage());
			}

		} else {
			qtmPesquisa.clear();
			lblQtdeRowsPesquisa.setText("");
		}
	}

	//TODO
	private void consultarPattern() {
		String query;
		String where = "";
		if (edit_PesquisaPattern.getText().trim().length() > 0) {
			if (Variaveis.padroesSelecionados.size() > 0) {
			}
		}
	}

	private String locClasses(){
		String query, aux, where = "";
		String[] txt = editPesquisa.getText().toLowerCase().trim().split("%");
		if (txt.length > 0 && (txt[0].length() > 0)) {
			aux = termo + "(array[";
			for (int i = 0; i < txt.length - 1; i++) {
				if (txt[i].length() > 0) 
					aux = aux + "'%" + txt[i] + "%', ";					
			}
			aux = aux + "'%" + txt[txt.length - 1] + "%'" + "])";
		} else 
			aux = "'%" + editPesquisa.getText().toLowerCase() + "%'";

		if (chckCaminho.isSelected() && caminhos.size() > 0) {
			where = " AND c.path LIKE '%";
			for (int i = 0; i < caminhos.size() - 1; i++) {
				where = where + caminhos.get(i).replace("\\", "\\\\").trim() + "%' OR c.path LIKE '%";
			}
			where = where + caminhos.get(caminhos.size() - 1).replace("\\", "\\\\").trim() + "%'";
		}
		query = "SELECT C.id, C.name, P.name as project, C.path " +
				"FROM " + selecionado + " C LEFT OUTER JOIN projects P ON (c.idproject = P.id) " +
				" WHERE lower(c." + campo + ") LIKE " + aux + where + " ORDER BY C.ID;";


		return query;
	}

	private String locMetodos(){
		String query, aux, where = "";
		String[] txt = editPesquisa.getText().toLowerCase().trim().split("%");
		if (txt.length > 0 && (txt[0].length() > 0)) {
			aux = termo + "(array[";
			for (int i = 0; i < txt.length - 1; i++) {
				if (txt[i].length() > 0) 
					aux = aux + "'%" + txt[i] + "%', ";					
			}
			aux = aux + "'%" + txt[txt.length - 1] + "%'" + "])";
		} else 
			aux = "'%" + editPesquisa.getText().toLowerCase() + "%'";

		if (chckCaminho.isSelected() && caminhos.size() > 0) {
			where = where + " AND (c.path LIKE '%";
			for (int i = 0; i < caminhos.size() - 1; i++) {
				where = where + caminhos.get(i).replace("\\", "\\\\").trim() + "%' OR c.path LIKE '%";
			}
			where = where + caminhos.get(caminhos.size() - 1).replace("\\", "\\\\").trim() + "%')";
		}

		if (chckProjeto.isSelected() && projetos.size() > 0) {
			where = where + " AND (p.name LIKE '";
			for (int i = 0; i < projetos.size() - 1; i++) {
				where = where + projetos.get(i).trim() + "' OR p.name LIKE '";
			}
			where = where + projetos.get(projetos.size() - 1).trim() + "')";
		}

		if (chckClasse.isSelected() && classes.size() > 0) {
			where = where + " AND (c.name LIKE '";
			for (int i = 0; i < classes.size() - 1; i++) {
				where = where + classes.get(i).trim() + "' OR c.name LIKE '";
			}
			where = where + classes.get(classes.size() - 1).trim() + "')";
		}

		query = "SELECT M.id, M.name, C.name as classe, P.name as project, C.path " +
				"FROM " + selecionado + " M LEFT OUTER JOIN classes C ON (M.idclass = c.id) " +
				"LEFT OUTER JOIN projects P ON (c.idproject = P.id) " +
				" WHERE lower(M." + campo + ") LIKE " + aux + where + " ORDER BY M.ID;";
		return query;
	}

	private String locComentarios(){
		String query, aux, where = "";
		String[] txt = editPesquisa.getText().toLowerCase().trim().split("%");
		if (txt.length > 0 && (txt[0].length() > 0)) {
			aux = termo + "(array[";
			for (int i = 0; i < txt.length - 1; i++) {
				if (txt[i].length() > 0) 
					aux = aux + "'%" + txt[i] + "%', ";					
			}
			aux = aux + "'%" + txt[txt.length - 1] + "%'" + "])";
		} else 
			aux = "'%" + editPesquisa.getText().toLowerCase() + "%'";

		if (chckCaminho.isSelected() && caminhos.size() > 0) {
			where = where + " AND (c.path LIKE '%";
			for (int i = 0; i < caminhos.size() - 1; i++) {
				where = where + caminhos.get(i).replace("\\", "\\\\").trim() + "%' OR c.path LIKE '%";
			}
			where = where + caminhos.get(caminhos.size() - 1).replace("\\", "\\\\").trim() + "%')";
		}

		if (chckProjeto.isSelected() && projetos.size() > 0) {
			where = where + " AND (p.name LIKE '";
			for (int i = 0; i < projetos.size() - 1; i++) {
				where = where + projetos.get(i).trim() + "' OR p.name LIKE '";
			}
			where = where + projetos.get(projetos.size() - 1).trim() + "')";
		}

		if (chckClasse.isSelected() && classes.size() > 0) {
			where = where + " AND (CL.name LIKE '";
			for (int i = 0; i < classes.size() - 1; i++) {
				where = where + classes.get(i).trim() + "' OR CL.name LIKE '";
			}
			where = where + classes.get(classes.size() - 1).trim() + "')";
		}

		if (chckMetodo.isSelected() && metodos.size() > 0) {
			where = where + " AND (M.name LIKE '";
			for (int i = 0; i < metodos.size() - 1; i++) {
				where = where + metodos.get(i).trim() + "' OR M.name LIKE '";
			}
			where = where + metodos.get(metodos.size() - 1).trim() + "')";
		}

		query = "SELECT C.id, C.comment, M.name as method, CL.name as class, P.name as project, C.path " +
				"FROM " + selecionado + " C LEFT OUTER JOIN methods M ON (c.idmethod = m.id) " +
				"LEFT OUTER JOIN classes CL ON (c.idclass = CL.id) " +
				"LEFT OUTER JOIN projects P ON (CL.idproject = P.id) OR (c.idproject = P.id) " +
				" WHERE lower(" + campo + ") LIKE " + aux + where + " ORDER BY C.ID;";
		return query;
	}

	private void fillModel(DefaultListModel model, ArrayList<String> content) {
		model.clear();
		for (int i = 0; i < content.size(); i++) {
			model.addElement(content.get(i).trim());
		}
	}
}
