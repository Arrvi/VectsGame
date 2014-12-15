package eu.arrvi.vects.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Chat panel that is part of client window. Consists of chat display and a single text field as an input.
 * Also it is main game information GUI part. Important game events are displayed here.
 * Outgoing chat message can be obtained by listening to `message` property with `PropertyChangeListener`. Incoming
 * ones are supposed to be displayed with `message` method.
 */
class ChatPane extends JPanel implements ActionListener {
	/**
	 * Component displaying all messages. Note that there is no history stored other than thid list of JLabels.
	 * There are no ways to manipulate displayed messages.
	 */
	private JList<JLabel> messageList;

	/**
	 * Input component. Message is sent on Action event, then contents of this element are cleared.
	 */
	private JTextField input;

	/**
	 * PCS for `message` property change. It overrides native JPanel PC events to simplify communication.
	 */
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Creates chat panel that is ready to be inserted into window.
	 */
	public ChatPane() {
		super();
		createGUI();
	}

	/**
	 * Creates and configures GUI elements
	 */
	private void createGUI() {
		// Component setup
		this.setLayout(new BorderLayout(0, 5));
		this.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder("Chat")));
		this.setPreferredSize(new Dimension(300,0));

		// Message list setup
		messageList = new JList<JLabel>();
		// Defalt model to allow simple adding elements to list
		messageList.setModel(new DefaultListModel<JLabel>());
		// That was necessary. Can't remember why :(
		messageList.setCellRenderer(new ListCellRenderer<JLabel>() {

			@Override
			public Component getListCellRendererComponent(
					JList list, JLabel value, int index,
					boolean isSelected, boolean cellHasFocus) {
				return value;
			}

		});
		JScrollPane chatPane = new JScrollPane(messageList);

		// Panel assembly
		this.add(new JLabel(new ImageIcon("res/VectsLogo.png")), BorderLayout.NORTH);
		this.add(chatPane, BorderLayout.CENTER);
		this.add(input = new JTextField(), BorderLayout.SOUTH);
		input.addActionListener(this);
	}

	/**
	 * Enables or disables chat input to prevent writing messages while socket is not connected.
	 * @param enabled true if chat should be enabled, false otherwise
	 */
	public void setChatEnabled(boolean enabled) {
		input.setEnabled(enabled);
	}

	/**
	 * Display message on chat. This is NOT a send message method.
	 * @param message String to be displayed. HTML allowed
	 */
	public void message(String message) {
		System.out.println("CHAT --- "+message);
		if (message.substring(0, 6).equals("<html>")) {
			message = message.substring(6);
		}
		message = "<html><body width=200>"+message+"</body></html>";
		((DefaultListModel<JLabel>) messageList.getModel()).addElement(new JLabel(message));
	}

	/**
	 * Display message from specific author (mainly system or other players). This is NOT send message method.
	 * @param author Message's author
	 * @param message Message to be displayed. HTML not allowed (not filtered neither).
	 */
	public void message(String author, String message) {
		message("<html><b>"+author+":</b> "+message+"</html>");
	}

	/**
	 * Send message via property change
	 * @param message Message to be sent
	 */
	private void sendMessage(String message) {
		pcs.firePropertyChange("message", null, message);
	}

	/**
	 * Input interaction support
	 * @param e event
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField tf = (JTextField)(e.getSource());
		if (tf != input) return;
		sendMessage(tf.getText());
		tf.setText("");
	}

	/**
	 * Add listener for message property change. This method overrides native JPanel PSC.
	 * @param listener listener to be added
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove listener from message property change. This method overrides native JPanel PSC.
	 * @param listener listener to be removed
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
