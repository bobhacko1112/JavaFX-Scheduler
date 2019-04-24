// entry point.
package driver;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Driver extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader l = new FXMLLoader(getClass().getResource("/layout/Login.fxml"));
		Parent parent = l.load();
		Scene scene = new Scene(parent);
		stage.setScene(scene);
                stage.setTitle("Scheduler - LogIn");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
