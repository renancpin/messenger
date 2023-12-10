import Client.ClientModel;
import Client.ClientView;

public class App {
	public static void main(String args[]) {
		ClientModel client = new ClientModel();

		try {
			ClientView clientView = new ClientView(client);
			clientView.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
