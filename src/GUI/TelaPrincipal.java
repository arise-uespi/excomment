package GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import search.BuscaPadroesComentarios;
import suporte.Constantes;
import suporte.Funcoes;
import suporte.Variaveis;
import IO.ManageFiles;
import IO.Props;
import database.DataAccess;
import database.PostgreSQLJDBC;
import database.ProjectDataSource;
import extracao.ExtractPatterns;
import extracao.ParseComments;
import extracao.Tagger;

public class TelaPrincipal {

	private static JFrame frmMineradorJava;
	private JTextField editName;
	private JTextField editPath;
	private static JEditorPane editExtracting;
	private Thread thread;

	private DefaultListModel modelPalavrasReservadas, modelCaracteres, modelLicencas, modelCoringa, modelStem;
	private ManageFiles manageFiles;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaPrincipal window = new TelaPrincipal();
					window.frmMineradorJava.setVisible(true);

					Props prop = new Props();
					if (!prop.loadProperties()) {
						JOptionPane.showMessageDialog(frmMineradorJava, "Informe corretamente as informações de configuração " +
								"antes de proseguir.", "Falha na conexão", JOptionPane.ERROR_MESSAGE);

						Configuracoes configuracoes = new Configuracoes();
						configuracoes.show();
					}

					DataAccess.dataManager = new PostgreSQLJDBC();
					
					//ExtractPatterns.extractPatterns();
					//ExtractPatterns.extractScores();
					//ExtractPatterns.extractThemes();
					//ExtractPatterns.extractTDtypes();
					//ExtractPatterns.extractRelPatterns();

				} catch (Exception e) {
					System.err.println(e.getMessage());
					if (!e.getMessage().contains("config_pkey")) {
						JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
						Funcoes.sendStackMail(e);
					}
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TelaPrincipal() throws IOException{
		runBefore();
		initialize();
	}

	private void runBefore(){
		manageFiles = new ManageFiles();

		try {
			File file = new File(Constantes.PALAVRASRESERVADAS);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.palavrasReservadas = manageFiles.readFileSeparateLines(Constantes.PALAVRASRESERVADAS);

			file = new File(Constantes.CARACTERESESPECIAIS);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.caracteres = manageFiles.readFileSeparateLines(Constantes.CARACTERESESPECIAIS);

			file = new File(Constantes.LICENCAS);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.licencas = manageFiles.readFileSeparateLines(Constantes.LICENCAS);

			file = new File(Constantes.CORINGAS);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.coringas = manageFiles.readFileSeparateLines(Constantes.CORINGAS);

			file = new File(Constantes.PALAVRASSTEM);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.palavrasStem = manageFiles.readFileSeparateLines(Constantes.PALAVRASSTEM);
			
			file = new File(Constantes.PADROES);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.padroes = manageFiles.readFileSeparateLines(Constantes.PADROES);
			
			file = new File(Constantes.PADROESSELECIONADOS);
			if(!file.exists()) 
				file.createNewFile();
			else 
				Variaveis.padroesSelecionados = manageFiles.readFileSeparateLines(Constantes.PADROESSELECIONADOS);
		} catch (IOException e) { }
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMineradorJava = new JFrame();
		frmMineradorJava.setTitle("Minerador Java");
		frmMineradorJava.setBounds(100, 100, 800, 600);
		frmMineradorJava.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMineradorJava.setLocationRelativeTo(null);
		frmMineradorJava.setResizable(false);
		frmMineradorJava.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Nome do Projeto");
		lblNewLabel.setBounds(10, 11, 146, 14);
		frmMineradorJava.getContentPane().add(lblNewLabel);

		editName = new JTextField();
		editName.setBounds(138, 8, 130, 20);
		frmMineradorJava.getContentPane().add(editName);
		editName.setColumns(10);

		JLabel lblLocalizao = new JLabel("Localiza\u00E7\u00E3o do projeto");
		lblLocalizao.setBounds(278, 11, 174, 14);
		frmMineradorJava.getContentPane().add(lblLocalizao);

		editPath = new JTextField();
		editPath.setColumns(10);
		editPath.setBounds(445, 8, 298, 20);
		frmMineradorJava.getContentPane().add(editPath);

		JButton btnBrowse = new JButton("");
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 

				if (editPath.getText().trim().length() > 0) {
					chooser.setCurrentDirectory(new File(editPath.getText()));
				}

				int returnVal = chooser.showOpenDialog(frmMineradorJava);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					editPath.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnBrowse.setIcon(new ImageIcon(TelaPrincipal.class.getResource("/javax/swing/plaf/metal/icons/ocean/directory.gif")));
		btnBrowse.setBounds(753, 8, 31, 23);
		frmMineradorJava.getContentPane().add(btnBrowse);

		JLabel lblExtraindo = new JLabel("Extraindo...");
		lblExtraindo.setBounds(10, 322, 89, 14);
		frmMineradorJava.getContentPane().add(lblExtraindo);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 347, 774, 179);
		frmMineradorJava.getContentPane().add(scrollPane_1);

		editExtracting = new JEditorPane();
		scrollPane_1.setViewportView(editExtracting);
		Variaveis.docEditExtracting = editExtracting.getDocument();

		JButton btnStart = new JButton("Iniciar");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (thread != null) {
					thread.stop();
					thread.interrupt();
					thread = null;
				}

				Props props = new Props();

				try {
					props.loadProperties();
					DataAccess.dataManager = new PostgreSQLJDBC();
					DataAccess.dataManager.open();

					DataAccess.dataManager.close();
				} catch (Exception e) {
					if (!e.getMessage().contains("pkey")) {
						JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro:\n" + e.getMessage());
					}
				}

				Variaveis.Projectname = editName.getText();
				Variaveis.path = editPath.getText();
				Variaveis.tagger = new Tagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger"); 
				
				if (Variaveis.Projectname.trim().length() > 0) {
					Variaveis.idProject = ProjectDataSource.insertProject(Variaveis.Projectname);
					createThread();

					if (Variaveis.path.trim().length() > 0){
						thread.start();
					} else {
						JOptionPane.showMessageDialog(frmMineradorJava, "Informe o caminho do projeto!");
					}
				} else {
					JOptionPane.showMessageDialog(frmMineradorJava, "Informe o nome do projeto!");
				}
			}
		});
		btnStart.setBounds(10, 537, 89, 23);
		frmMineradorJava.getContentPane().add(btnStart);

		JButton btnStop = new JButton("Parar");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				thread.stop();
				JOptionPane.showMessageDialog(frmMineradorJava, "Processamento parado!");
			}
		});
		btnStop.setBounds(109, 537, 89, 23);
		frmMineradorJava.getContentPane().add(btnStop);

		JButton btnConsultar = new JButton("Consultar");
		btnConsultar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Consulta consulta = new Consulta();
				consulta.show();
			}
		});
		btnConsultar.setBounds(208, 537, 108, 23);
		frmMineradorJava.getContentPane().add(btnConsultar);

		JLabel lblPalavrasReservadas = new JLabel("Palavras Reservadas");
		lblPalavrasReservadas.setBounds(10, 53, 146, 14);
		frmMineradorJava.getContentPane().add(lblPalavrasReservadas);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 85, 145, 192);
		frmMineradorJava.getContentPane().add(scrollPane);

		modelPalavrasReservadas = new DefaultListModel();
		fillModel(modelPalavrasReservadas, Variaveis.palavrasReservadas);

		final JList listReservadas = new JList(modelPalavrasReservadas);
		scrollPane.setViewportView(listReservadas);
		JButton btnAddPalavras = new JButton("+");
		btnAddPalavras.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frmMineradorJava, "Informe as palavras " +
						"reservadas a serem ignoradas na busca separando-as por ponto e virgula ';'");

				String[] array = palavras.split(";");
				Variaveis.palavrasReservadas.addAll(Arrays.asList(array));

				modelPalavrasReservadas.clear();
				fillModel(modelPalavrasReservadas, Variaveis.palavrasReservadas);

				ManageFiles manageFiles = new ManageFiles();
				try {
					manageFiles.writeFileLineByLine(Constantes.PALAVRASRESERVADAS, Variaveis.palavrasReservadas);
				} catch (IOException e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
				}
			}
		});

		btnAddPalavras.setBounds(10, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnAddPalavras);

		JButton btnDelPalavras = new JButton("x");
		btnDelPalavras.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listReservadas.getSelectedIndex() > -1) {
					Variaveis.palavrasReservadas.remove(listReservadas.getSelectedIndex());
					fillModel(modelPalavrasReservadas, Variaveis.palavrasReservadas);

					synchronized (listReservadas) {
						listReservadas.notify();
						try {
							manageFiles.writeFileLineByLine(Constantes.PALAVRASRESERVADAS, Variaveis.palavrasReservadas);
						} catch (IOException e) {
							Funcoes.sendStackMail(e);
							JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
						}
					}
				}
			}
		});
		
		btnDelPalavras.setBounds(115, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnDelPalavras);

		JLabel lblCaracteres = new JLabel("Caracteres Especiais");
		lblCaracteres.setBounds(166, 53, 146, 14);
		frmMineradorJava.getContentPane().add(lblCaracteres);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(167, 85, 145, 192);
		frmMineradorJava.getContentPane().add(scrollPane_2);

		modelCaracteres = new DefaultListModel();
		fillModel(modelCaracteres, Variaveis.caracteres);

		final JList listCaracteres = new JList(modelCaracteres);
		scrollPane_2.setViewportView(listCaracteres);

		JButton btnAddCaracteres = new JButton("+");
		btnAddCaracteres.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frmMineradorJava, "Informe os caracteres " +
						"a serem ignorados na busca separando-os por um espaço em branco ' '");

				String[] array = palavras.split(" ");
				Variaveis.caracteres.addAll(Arrays.asList(array));

				modelCaracteres.clear();
				fillModel(modelCaracteres, Variaveis.caracteres);

				ManageFiles manageFiles = new ManageFiles();
				try {
					manageFiles.writeFileLineByLine(Constantes.CARACTERESESPECIAIS, Variaveis.caracteres);
				} catch (IOException e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
				}
			}
		});
		btnAddCaracteres.setBounds(166, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnAddCaracteres);

		JButton btnDelCaracteres = new JButton("x");
		btnDelCaracteres.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listCaracteres.getSelectedIndex() > -1) {
					Variaveis.caracteres.remove(listCaracteres.getSelectedIndex());
					fillModel(modelCaracteres, Variaveis.caracteres);

					synchronized (listReservadas) {
						listReservadas.notify();
						try {
							manageFiles.writeFileLineByLine(Constantes.CARACTERESESPECIAIS, Variaveis.caracteres);
						} catch (IOException e) {
							Funcoes.sendStackMail(e);
							JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
						}
					}
				}
			}
		});
		btnDelCaracteres.setBounds(271, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnDelCaracteres);

		JLabel lblPartesDeLicena = new JLabel("Partes de Licen\u00E7as");
		lblPartesDeLicena.setBounds(323, 53, 146, 14);
		frmMineradorJava.getContentPane().add(lblPartesDeLicena);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(323, 85, 145, 192);
		frmMineradorJava.getContentPane().add(scrollPane_3);

		modelLicencas = new DefaultListModel();
		fillModel(modelLicencas, Variaveis.licencas);

		final JList listLicenca = new JList(modelLicencas);
		scrollPane_3.setViewportView(listLicenca);

		JButton btnAddLicenca = new JButton("+");
		btnAddLicenca.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frmMineradorJava, "Informe as palavras/frases " +
						"de licenca a serem \nprocuradas separando-as por um duplo ponto e virgula ';;'");

				String[] array = palavras.split(";;");

				Variaveis.licencas.addAll(Arrays.asList(array));

				modelLicencas.clear();
				fillModel(modelLicencas, Variaveis.licencas);

				ManageFiles manageFiles = new ManageFiles();
				try {
					manageFiles.writeFileLineByLine(Constantes.LICENCAS, Variaveis.licencas);
				} catch (IOException e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
				}
			}
		});
		btnAddLicenca.setBounds(322, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnAddLicenca);

		JButton btnDelLicenca = new JButton("x");
		btnDelLicenca.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listLicenca.getSelectedIndex() > -1) {
					Variaveis.licencas.remove(listLicenca.getSelectedIndex());
					fillModel(modelLicencas, Variaveis.licencas);

					synchronized (listReservadas) {
						listReservadas.notify();
						try {
							manageFiles.writeFileLineByLine(Constantes.LICENCAS, Variaveis.licencas);
						} catch (IOException e) {
							Funcoes.sendStackMail(e);
							JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
						}
					}
				}
			}
		});
		btnDelLicenca.setBounds(427, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnDelLicenca);

		JLabel lblC = new JLabel("Palavras Coringas");
		lblC.setBounds(479, 53, 146, 14);
		frmMineradorJava.getContentPane().add(lblC);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(479, 85, 145, 192);
		frmMineradorJava.getContentPane().add(scrollPane_4);

		modelCoringa = new DefaultListModel();
		fillModel(modelCoringa, Variaveis.coringas);

		final JList listCoringas = new JList(modelCoringa);
		scrollPane_4.setViewportView(listCoringas);

		JButton btnAddCoringa = new JButton("+");
		btnAddCoringa.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String palavras = JOptionPane.showInputDialog(frmMineradorJava, "Informe as coringas " +
						"(comentários com estas palavras serão armazenados sem segunda checagem)\n " +
						"a serem procuradas separando-as por ponto e virgula ';'");

				String[] array = palavras.split(";");
				Variaveis.coringas.addAll(Arrays.asList(array));

				modelCoringa.clear();
				fillModel(modelCoringa, Variaveis.coringas);

				ManageFiles manageFiles = new ManageFiles();
				try {
					manageFiles.writeFileLineByLine(Constantes.CORINGAS, Variaveis.coringas);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro:\n" + e.getMessage());
				}
			}
		});
		btnAddCoringa.setBounds(478, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnAddCoringa);

		JButton btnDelCoringa = new JButton("x");
		btnDelCoringa.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listCoringas.getSelectedIndex() > -1) {
					Variaveis.coringas.remove(listCoringas.getSelectedIndex());
					fillModel(modelCoringa, Variaveis.coringas);

					synchronized (listReservadas) {
						listReservadas.notify();
						try {
							manageFiles.writeFileLineByLine(Constantes.CORINGAS, Variaveis.coringas);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro:\n" + e.getMessage());
						}
					}
				}
			}
		});
		btnDelCoringa.setBounds(583, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnDelCoringa);

		JLabel lblStem = new JLabel("Stem");
		lblStem.setBounds(638, 53, 70, 14);
		frmMineradorJava.getContentPane().add(lblStem);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(638, 85, 146, 192);
		frmMineradorJava.getContentPane().add(scrollPane_5);

		modelStem = new DefaultListModel();
		fillModel(modelStem, Variaveis.palavrasStem);

		JList listStem = new JList(modelStem);
		scrollPane_5.setViewportView(listStem);

		JButton btnDelStem = new JButton("x");
		btnDelStem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		btnDelStem.setEnabled(false);
		btnDelStem.setBounds(743, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnDelStem);

		JButton btnAddStem = new JButton("+");
		btnAddStem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		btnAddStem.setEnabled(false);
		btnAddStem.setBounds(638, 288, 41, 23);
		frmMineradorJava.getContentPane().add(btnAddStem);
		
		JButton btnExtrairPadroes = new JButton("Ext.Padrões");
		btnExtrairPadroes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					//A carga dos padroes sera dividida em etapas:
					//1 - Primeiro carrega todos os padroes
					//2 - Carrega o arquivo dos padroes com os scores
					//3 - Carrega o arquivos com os padroes e os themes
					//4 - Carrega o arquivos dos Themes com os tipos de DT
					//5 - Faz o relacionamento entre os padroes
					System.out.println("==== Extraindo Padrões ====");
					ExtractPatterns.extractPatterns();
					System.out.println("==== Extraindo Scores ====");
					ExtractPatterns.extractScores();
					System.out.println("==== Extraindo Themes ====");
					ExtractPatterns.extractThemes();
					System.out.println("==== Extraindo TDTypes ====");
					ExtractPatterns.extractTDtypes();
					System.out.println("==== Extraindo Relacoes de Padrões ====");
					ExtractPatterns.extractRelPatterns();
					JOptionPane.showMessageDialog(frmMineradorJava, "PROCESSAMENTO DE PADROES FINALIZADO");

					

				} catch (ClassNotFoundException | IOException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btnExtrairPadroes.setBounds(325, 537, 108, 23);
		frmMineradorJava.getContentPane().add(btnExtrairPadroes);
		
		JButton btnBusca = new JButton("Busca");
		btnBusca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					BuscaPadroesComentarios.Busca();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btnBusca.setBounds(440, 537, 108, 23);
		frmMineradorJava.getContentPane().add(btnBusca);

		JButton btnConfigurar = new JButton("Configurar");
		btnConfigurar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Configuracoes configuracoes = new Configuracoes();
				configuracoes.show();
			}
		});
		btnConfigurar.setBounds(676, 537, 108, 23);
		frmMineradorJava.getContentPane().add(btnConfigurar);
		
		

		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frmMineradorJava, "Palavras reservadas: palavras que devem ser ignoradas na busca por comentários;\n\n" +
						"Caracteres especiais: caracteres que devem ser ignorados na busca por comentários;\n\n" +
						"Partes de licenças: exemplos de licenças de software que se presentes em um comentário fará com que\n" +
						"este seja ignorado;\n\n" +
						"Palavras coringas: palavras que se presentes em um comentario farão com que este seja salvo automaticamente;\n\n" +
						"Stem: dicionário de stem para os comentários encontrados.\n");
			}
		});
		button.setIcon(new ImageIcon(TelaPrincipal.class.getResource("/javax/swing/plaf/metal/icons/ocean/question.png")));
		button.setBounds(763, 49, 22, 25);
		frmMineradorJava.getContentPane().add(button);
		
		
	}

	public void createThread() {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ParseComments getComments = new ParseComments();
				boolean sucess = false;

				try {
//					sucess = getComments.searchComments(Variaveis.path, "java");
					sucess = getComments.getComments(Variaveis.path, "java", null, null, false);
				} catch (Exception e) {
					Funcoes.sendStackMail(e);
					JOptionPane.showMessageDialog(frmMineradorJava, "Ocorreu o seguinte erro: " + e.getMessage());
				}

				if (sucess) {
					JOptionPane.showMessageDialog(frmMineradorJava, "Total of Analyzed Comments: " + Variaveis.countTotalComments + ". Total of Valide Comments: " + Variaveis.countVaideComments + " Total of Discarted Comments: " + Variaveis.countTotalDiscartedComments);
					JOptionPane.showMessageDialog(frmMineradorJava, "Processamento finalizado com sucesso!");
				}
			}
		});
	}
	
	

	private void fillModel(DefaultListModel model, ArrayList<String> content) {
		model.clear();
		for (int i = 0; i < content.size(); i++) {
			model.addElement(content.get(i).trim());
		}
	}
}
