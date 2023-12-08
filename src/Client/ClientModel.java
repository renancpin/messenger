package Client;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import Server.ServerInterface;

public class ClientModel implements ClientInterface {
	private static String SERVER_NAME = "MessengerServer";

	private Registry registry = null;
	private Remote remoteObject = null;
	private ServerInterface server = null;
	private String userName = null;
	private boolean isOnline = false;
	private BiConsumer<String, String> consumer = null;

	public ClientModel() {
		try {
			remoteObject = UnicastRemoteObject.exportObject(this, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMessageConsumer(BiConsumer<String, String> consumer) {
		this.consumer = consumer;
	}

	public void connect(String userName) {
		try {
			registry = LocateRegistry.getRegistry();
			registry.rebind(userName, remoteObject);
			server = (ServerInterface) registry.lookup(SERVER_NAME);
			server.connect(userName);

			this.userName = userName;
			this.isOnline = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			String userName = this.userName;

			this.isOnline = false;
			this.userName = null;

			registry.unbind(userName);
			server.disconnect(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getIsOnline() {
		return isOnline;
	}

	public void addContact(String contact) throws Exception {
		try {
			server.addContact(userName, contact);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Usuario nao pode ser adicionado");
		}
	}

	public void removeContact(String contact) throws Exception {
		try {
			server.removeContact(userName, contact);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Usuario nao pode ser removido");
		}
	}

	public List<String> getContacts() {
		try {
			return server.getContacts(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<String>();
	}

	public void sendMessage(String destination, String message) throws Exception {
		if (!isOnline || userName == null || userName.equals("") || destination == null || destination.equals("")
				|| message == null || message.equals("")) {
			return;
		}

		server.sendMessage(userName, destination, message);
	}

	@Override
	public void handleMessage(String sender, String message) {
		if (consumer == null) {
			return;
		}

		try {
			consumer.accept(sender, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
