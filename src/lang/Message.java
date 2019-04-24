/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lang;

/**
 * A quick object to interface with all the user-presented text in the program.
 */
import java.util.Locale;
import java.util.ResourceBundle;

public class Message {
	private final ResourceBundle strings;
	public Message() {
		String lang = Locale.getDefault().getLanguage();

		if (lang.matches("en") || lang.matches("es")) {
			this.strings = ResourceBundle.getBundle("lang/" + lang);
		}
		else {
			this.strings = ResourceBundle.getBundle("lang/en");
		}
	}
	public String get(String s) {
		return this.strings.getString(s);
	}
}
