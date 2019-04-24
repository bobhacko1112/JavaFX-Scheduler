/*
*/

package controllers;

import popup.Popup;
import database.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import lang.Message;

import java.sql.*;
import java.util.logging.FileHandler;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.stage.Modality;
import javafx.fxml.Initializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;


public class LoginController implements Initializable {
	private Message msg;
	private Popup popup;
	private MySQL mysql;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private Label usernameLabel, passwordLabel;

	public LoginController() {
		this.msg = new Message();
		this.popup = new Popup();
		this.mysql = new MySQL();

	}

	private static void log(String s) {
		String out = "\n*" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "* " + s;

		try {
			FileWriter f = new FileWriter("log.txt", true);
			BufferedWriter buf = new BufferedWriter(f);
			buf.write(out);
			buf.close();
			f.close();
		}
		catch (Exception ex) {
			System.out.println("Logger broke" + ex);
		}
	}

	@FXML
	public void login(ActionEvent e) {

		String imminent_appointments_query =
		   "select appointment.start, customer.customerName from appointment "
		   + "JOIN customer ON appointment.customerId=customer.customerId "
		   + "WHERE start >= '" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).toString() + "' - INTERVAL 50 MINUTE AND start <= NOW() "
		   + "+ INTERVAL 50 MINUTE";
		System.out.println(imminent_appointments_query);

		String auth_list_query = "SELECT userName, password FROM user";
		/*
		The unwrap interface used in the MySQL class simplifies the usage of MySQL.
		I just want to be able to construct a string and pass it
		to the database. The sanitation mechanisms at entry should
		protect against user-level injection-type attacks. Still insecure if one
		of these plain text strings got changed in a hex editor \(0~0)/ A good
		start would be to hash the strings, but that's beyond the scope today.

		This callback method (MySQL.Query(String, Unwrap<ResultSet>)) also lets me have
		a guarantee of the lifetime of this.mysql so that it's the same instance
		referenced throughout the program. Memory safety first!
		*/
		this.mysql.query(auth_list_query, r -> {
			try {

				ResultSet rs = (ResultSet) r;
				Node  n = (Node)  e.getSource();
				Stage s  = (Stage) n.getScene().getWindow();

				// check if authorized.
				boolean isAuthorized = false;

				try {
					while (rs.next()) {
						if (rs.getString(1).matches(this.username.getText()) &&
						rs.getString(2).matches(this.password.getText())) {
							isAuthorized = true;
							break;
						}
					}

					try {
						if (isAuthorized) {
							//log successful login.
							log("User: '" + this.username.getText() + "' Has logged in successfully.");

							//check if there is an appointment within 15 minutes of this login.
							// no justification for the lambda use here really. It's
							// the interface I chose for the class so it's required here.
							this.mysql.query("SELECT start, end FROM appointment", resultset -> {
								try {
									ResultSet result = (ResultSet) resultset;

									while (result.next()) {
										/*
										The logic wasn't working properly here using just mysql queries.
										now everything is parsed into local time to compare against the start
										time of all appointments.

										*/
										LocalDateTime start = result.getTimestamp(1).toLocalDateTime();
										LocalDateTime end = result.getTimestamp(2).toLocalDateTime();
										LocalDateTime now = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();

										if (now.plusMinutes(15).isAfter(start) && now.isBefore(end)) {
											this.popup.alert(msg.get("appointment_notify"));
											break;
										}
									}

								}
								catch (Exception ex) {
									System.out.println(ex);
								}
							});
							FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/Main.fxml"));
							Parent parent = fxmlLoader.load();
							MainController mainController = fxmlLoader.<MainController>getController();
							mainController.set(this.popup, this.msg, this.mysql);
							Scene scene = new Scene(parent);
							Stage stage = new Stage();
							stage.initModality(Modality.APPLICATION_MODAL);
                                                        stage.setTitle("Scheduler - Current Timezone: " + ZoneId.systemDefault() +" UTC" + ZonedDateTime.now().getOffset().toString());
							stage.setScene(scene);
							stage.show();
							s.close();
						}
						else {
							//log unsuccessful login.
							log("User: '" + this.username.getText() + "' Has failed to log in.");
							popup.alert(msg.get("database_incorrect_cred"));
						}

					}
					catch (Exception ex) {
						popup.alert(msg.get("cannot_load_resource") + ": " + ex);
						s.close();
					}

				}
				catch (Exception ex) {
					System.out.println(msg.get("database_connection_bad"));
				}

			}
			catch (Exception ex) {
				popup.alert(msg.get("logging_error"));
			}
		});
	}




	public void initialize(URL location, ResourceBundle resources) {

		this.usernameLabel.setText(msg.get("username"));
		this.passwordLabel.setText(msg.get("password"));

	}
}
