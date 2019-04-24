/*
* A collection of ChangeListeners for the various TextFields in the project.
* They try to guide the user's input. They do not cover all edge cases.
 */
package sanitization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 *
 * @author bob
 */
public class Sanitizer {
	public static ChangeListener<String> forceQuerySafe(TextField t) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
			                    String oldValue, String newValue) {
				if (!newValue.matches("a-z A-Z,\\-\\d*")) {
					t.setText(newValue.replaceAll("[^a-z A-Z,\\-\\d]", ""));
				}
			}
		};
	}
	public static ChangeListener<String> forceIntegers(TextField t) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
			                    String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					t.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		};
	}

	public static ChangeListener<String> forceLength(TextField t, int length) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
			                    String newValue) {
				if (t.getText().length() > length) {
					t.setText(oldValue);
				}
			}
		};
	}

	public static ChangeListener<String> forceDouble(TextField t) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
			                    String oldValue, String newValue) {
				if (!newValue.matches("\\d.*")) {
					t.setText(newValue.replaceAll("[^\\d.]", ""));
				}
			}
		};
	}
}