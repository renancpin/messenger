package Client;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.BiConsumer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClientView {
	private ClientModel client;
	private JFrame frame;
	private JTextField userNameField;
	private JTextField messageField;
	private JTextArea messagesArea;

	/**
	 * Create the application.
	 */
	public ClientView(ClientModel client) {
		this.client = client;
		initialize();
	}

	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							client.disconnect();
							super.windowClosing(e);
						}
					});
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1010, 595);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNomeUsuario = new JLabel("Nome de usuario");
		lblNomeUsuario.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNomeUsuario.setBounds(75, 20, 115, 19);
		panel.add(lblNomeUsuario);

		userNameField = new JTextField();
		userNameField.setBounds(75, 49, 221, 31);
		panel.add(userNameField);

		JLabel lblStatus = new JLabel("[Offline]");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStatus.setBounds(306, 53, 61, 19);
		panel.add(lblStatus);

		JButton btnConnect = new JButton("Conectar");
		btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnConnect.setBounds(400, 48, 101, 31);
		panel.add(btnConnect);

		BiConsumer<String, String> receiveMessage = (sender, message) -> {
			messagesArea.append("[" + sender + "]: " + message + '\n');
		};

		client.setMessageConsumer(receiveMessage);

		DefaultListModel<String> contacts = new DefaultListModel<>();

		ActionListener conectar = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					boolean isOnline = client.getIsOnline();

					if (!isOnline) {
						String userName = userNameField.getText();
						if (userName == null || userName.equals("")) {
							return;
						}
						client.connect(userName);
						contacts.addAll(client.getContacts());
					} else {
						client.disconnect();
						contacts.clear();
					}

					isOnline = client.getIsOnline();
					lblStatus.setText(isOnline ? "[Online]" : "[Offline]");
					btnConnect.setText(isOnline ? "Desconectar" : "Conectar");
					userNameField.setEditable(!isOnline);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};
		userNameField.addActionListener(conectar);
		btnConnect.addActionListener(conectar);

		JLabel lblTitle1 = new JLabel("Contatos");
		lblTitle1.setBounds(75, 115, 93, 42);
		panel.add(lblTitle1);
		lblTitle1.setFont(new Font("Tahoma", Font.PLAIN, 24));

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(75, 167, 221, 259);
		panel.add(scrollPane_1);

		JList<String> contactList = new JList<>(contacts);
		scrollPane_1.setViewportView(contactList);

		ActionListener addContact = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String contactName = JOptionPane.showInputDialog("Novo Contato");

					if (contacts.contains(contactName)) {
						return;
					}

					client.addContact(contactName);
					contacts.addElement(contactName);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};

		JButton btnAddContact = new JButton("Adicionar");
		btnAddContact.setBounds(75, 441, 93, 31);
		panel.add(btnAddContact);
		btnAddContact.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAddContact.addActionListener(addContact);

		ActionListener removeContact = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String contactName = contactList.getSelectedValue();
					if (contactName == null || contactName.equals("")) {
						return;
					}

					client.removeContact(contactName);
					contacts.removeElement(contactName);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};

		JButton btnRemoveContact = new JButton("Remover");
		btnRemoveContact.setEnabled(false);
		btnRemoveContact.setBounds(203, 441, 93, 31);
		panel.add(btnRemoveContact);
		btnRemoveContact.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemoveContact.addActionListener(removeContact);

		JLabel lblTitle2 = new JLabel("Mensagens");
		lblTitle2.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblTitle2.setBounds(345, 115, 118, 42);
		panel.add(lblTitle2);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(345, 167, 570, 259);
		panel.add(scrollPane);

		messagesArea = new JTextArea();
		messagesArea.setEditable(false);
		scrollPane.setViewportView(messagesArea);

		JLabel lblMessage = new JLabel("Mensagem");
		lblMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMessage.setBounds(345, 441, 85, 19);
		panel.add(lblMessage);

		messageField = new JTextField();
		messageField.setBounds(345, 474, 446, 31);
		panel.add(messageField);

		JButton btnSendMessage = new JButton("Enviar");
		btnSendMessage.setEnabled(false);
		btnSendMessage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSendMessage.setBounds(822, 473, 93, 31);
		panel.add(btnSendMessage);

		ActionListener sendMessage = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String message = messageField.getText();
					if (message == null || message.equals("")) {
						return;
					}

					String contactName = contactList.getSelectedValue();
					if (contactName == null || contactName.equals("")) {
						return;
					}

					client.sendMessage(contactName, message);
					messageField.setText("");
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};

		messageField.addActionListener(sendMessage);
		btnSendMessage.addActionListener(sendMessage);

		contactList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean canSendMessages = client.getIsOnline() && contactList.getSelectedValue() != null;

				btnRemoveContact.setEnabled(canSendMessages);
				btnSendMessage.setEnabled(canSendMessages);
			}
		});
	}
}
