/*
 * A collection of dialog popups used in this project.
 */
package popup;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/**
 *
 * @author bob
 */
public class Popup {

	// 'intro' ... (the list in the .properties file)
	public void alert(String msg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Alert Message");
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.contentTextProperty().set(msg);
		alert.showAndWait();

	}
        public void message(String msg) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Message");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.contentTextProperty().set(msg);
            alert.showAndWait();
        }
	public boolean confirm(String msg) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setHeaderText("Confirm this action");
		confirm.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		confirm.contentTextProperty().set(msg);
		confirm.showAndWait();
		return confirm.getResult() == ButtonType.OK;
	}
}
