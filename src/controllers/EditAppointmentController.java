/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;


import javafx.collections.FXCollections;

import java.sql.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import option.Option;
import java.time.format.DateTimeFormatter;
import popup.Popup;
import database.MySQL;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import lang.Message;
import javafx.fxml.FXML;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import javafx.scene.control.TableView;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ChoiceBox;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import sanitization.Sanitizer;
import javafx.util.Callback;


/**
 *
 * @author bob
 */
public class EditAppointmentController implements Initializable  {
	private MySQL mysql;
	private Message msg;
	private Popup popup;
	private enum Action {ADD, EDIT}

	@FXML
	private ChoiceBox start, end;
	@FXML
	private DatePicker date;
	@FXML
	private TableView appointmentsTable, customersTable, repsTable;
	@FXML
	private TextField title, location, description;

	private static String repsQuery = "SELECT userId, userName FROM user ORDER BY userId";

	private static String appointmentsQuery = "SELECT user.userId, o.customerId, o.appointmentId,"
	      + " o.contact, TIMESTAMP(o.start) AS Date, "
	      + "o.start AS start, o.end AS end,"
	      + " o.location, o.title AS Type, o.description "
	      + "FROM (SELECT appointment.*, customer.customerName FROM "
	      + "appointment JOIN customer ON customer.customerID=appointment.customerId) AS o "
	      + "JOIN user ON user.userName=o.contact";
	private static String customersQuery = "SELECT customerId, customerName FROM customer";

	private void updateAppointmentsTable() {
		// populate appointmentsTable
		this.mysql.query(appointmentsQuery, f -> {
			String[] disabled = {"appointmentId", "customerId", "userId", "description", "Date"};
			MySQL.setTable((ResultSet) f, appointmentsTable, Option.Some(disabled));

		});
	}

	private void edit(String startTime, String endTime) {
		ObservableList a = (ObservableList)appointmentsTable.getSelectionModel().getSelectedItem();
		ObservableList l = (ObservableList)customersTable.getSelectionModel().getSelectedItem();
		ObservableList u = (ObservableList)repsTable.getSelectionModel().getSelectedItem();
		String updateQuery = "UPDATE appointment SET lastUpdateBy='" + u.get(1) + "', start='" + startTime + "', end='" + endTime + "', customerId='" + l.get(0) + "', title='" + title.getText() + "', location='" + location.getText() + "', contact='" + u.get(1) + "', description='" + description.getText() + "' WHERE appointmentId='" + a.get(2) + "'";


		mysql.query(updateQuery, r -> {
			System.out.println("RESULT OBJECT IS: " + r);
			boolean insertResult = (boolean)r;

			if (!insertResult) {
				popup.alert(msg.get("record_edit_success"));
				updateAppointmentsTable();
			}
		});

	}

	private void add(String startTime, String endTime) {
		// populate record after getting a unique new appointment ID,
		// No AUTO_INCREMENT on this? What a shame.
		mysql.query("SELECT MAX(appointmentId) + 1 FROM appointment", g -> {
			ResultSet rs = (ResultSet)g;
			int newId = 0;

			try {
				rs.next();
				newId = rs.getInt(1);
			}
			catch (Exception ex) {
				System.out.println(ex);
			}

			ObservableList l = (ObservableList)customersTable.getSelectionModel().getSelectedItem();
			ObservableList u = (ObservableList)repsTable.getSelectionModel().getSelectedItem();
			String insertQuery = "INSERT INTO appointment (url, lastUpdateBy, start, end, appointmentId, customerId, "
			+ "title, location, contact, description, "
			+ "createDate, createdBy) VALUES(' ', '" + u.get(1) + "', TIMESTAMP('" + startTime + "'),'" + endTime + "', '" + newId + "','"
			+ l.get(0) + "',"
			+ "'" + title.getText() + "',"
			+ "'" + location.getText() + "',"
			+ "'" + u.get(1) + "','" + description.getText() + "', NOW(), '"
			+ u.get(1) + "')";
			mysql.query(insertQuery, r -> {
				System.out.println("RESULT OBJECT IS: " + r);
				boolean insertResult = (boolean)r;

				if (!insertResult) {
					popup.alert(msg.get("record_add_success"));
					updateAppointmentsTable();
				}
			});
		});
	}

	public void initialize(URL location, ResourceBundle resources) {



	}

	public void set(Popup p, Message m, MySQL d) {
		this.mysql = d;
		this.msg = m;
		this.popup = p;
                popup.message("Adding appointments is in UTC. Results are shown in your local time-zone. Check the title of the main window to see your timezone offset. You can only schedule appointments between 8AM and 5PM UTC as those are the business hours.");
		//This hopfully will prevent most shinanagins.
		this.title.textProperty().addListener(Sanitizer.forceQuerySafe(title));
		this.location.textProperty().addListener(Sanitizer.forceQuerySafe(this.location));
		this.description.textProperty().addListener(Sanitizer.forceQuerySafe(description));
                
		// populate time selections
		ObservableList times = FXCollections.observableArrayList();

		for (LocalDateTime t = LocalDateTime.parse("2007-12-03T08:00:00");
		      t.isBefore(LocalDateTime.parse("2007-12-03T17:30:00")); t = t.plusMinutes(30)) {

			times.add(t.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		}

		start.setItems(times);

		start.getSelectionModel().select(0);
		end.setItems(times);
		end.getSelectionModel().select(1);

		// populate reps table
		this.mysql.query(repsQuery, f -> {
			String[] disabled = {"userId"};
			MySQL.setTable((ResultSet) f, repsTable, Option.Some(disabled));

			repsTable.getSelectionModel().select(0);
			repsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					ObservableList row = (ObservableList)newSelection;
				}
			});
		});

		//populate customer table
		this.mysql.query(customersQuery, f -> {
			String[] disabled = {"customerId"};
			MySQL.setTable((ResultSet) f, customersTable, Option.Some(disabled));
		});

		// populate appointments table
		updateAppointmentsTable();  // used again unlike others.

		// describe reactive selections, a appointmentTable selection from
		//the user will update the other fields such that an entry can be easily
		//edited or viewed in more detail.
		appointmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				ObservableList row = (ObservableList)newSelection;
				this.description.setText((String)row.get(9));
				this.title.setText((String)row.get(8));
				this.location.setText((String)row.get(7));

				this.date.setValue(LocalDate.now());

				ObservableList users = (ObservableList)repsTable.getItems();
				ObservableList customers = (ObservableList)customersTable.getItems();
				int index = 0;
				Iterator it = users.iterator();

				while (it.hasNext()) {
					ObservableList l = (ObservableList)it.next();
					String s = (String)l.get(0);

					if (s.matches((String)row.get(0))) {
						break;
					}

					index++;
				}

				repsTable.scrollTo(index);
				repsTable.getSelectionModel().select(index);
				index = 0;

				it = customers.iterator();

				while (it.hasNext()) {

					ObservableList l = (ObservableList)it.next();
					String s = (String)l.get(0);

					if (s.matches((String)row.get(1))) {
						break;
					}

					index++;

				}

				customersTable.scrollTo(index);
				customersTable.getSelectionModel().select(index);
			}

		});
	}

	@FXML
	public void deleteAppointment() {
		ObservableList row = (ObservableList)appointmentsTable.getSelectionModel().getSelectedItem();
		String appointmentId = (String)row.get(2);

		if (popup.confirm(msg.get("confirm_delete"))) {
			mysql.query("DELETE FROM appointment WHERE appointmentId='" + appointmentId + "'", f -> {
				if (!(boolean)f) {
					popup.alert(msg.get("record_delete_success"));
					appointmentsTable.getItems().remove(row);
				}
			});
		}
	}

	@FXML
	public void editAppointment() {
		validateAndDo(Action.EDIT);
	}
	@FXML
	public void addAppointment() {
		validateAndDo(Action.ADD);
	}


	private void validateAndDo(Action A) {

		// make sure date is filled out with something valid
		if (date.getValue() == null || date.getValue().isBefore(LocalDate.now())) {
			popup.alert(msg.get("record_invalid_date"));
		}
		else {
			// check that all textFields are populated with something.
			if (location.getText().length() < 1 || title.getText().length() < 1 || description.getText().length() < 1) {
				popup.alert(msg.get("record_no_field_data"));
			}
			else {
				//make sure a user and customer are selected
				if (repsTable.getSelectionModel().getSelectedItem() == null || customersTable.getSelectionModel().getSelectedItem() == null) {
					popup.alert(msg.get("record_no_rep_or_customer_selected"));
				}
				else {
					// check if times dont make sense, and is at least 30 minutes.
					int startIndex = start.getSelectionModel().getSelectedIndex();
					int endIndex = end.getSelectionModel().getSelectedIndex();

					if (startIndex >= endIndex || endIndex <= startIndex) {
						popup.alert(msg.get("record_add_impossible_times"));

					}
					else {
						// parse and format the end and start times from user input.
						String selectedStart = start.getSelectionModel().getSelectedItem().toString();
						String selectedEnd = end.getSelectionModel().getSelectedItem().toString();
						int startH, startM, endH, endM;
						startH = Integer.parseInt(selectedStart.substring(0, 2));
						startM = Integer.parseInt(selectedStart.substring(3, 5));
						endH = Integer.parseInt(selectedEnd.substring(0, 2));
						endM = Integer.parseInt(selectedEnd.substring(3, 5));
						System.out.println("Start: " + startH + ":" + startM + " End: " + endH + ":" + endM);

						Timestamp endTime = Timestamp.valueOf(LocalDateTime.of(this.date.getValue(), LocalTime.of(endH, endM)));
						Timestamp startTime = Timestamp.valueOf(LocalDateTime.of(this.date.getValue(), LocalTime.of(startH, startM)));
						ZoneId localTimeZone = ZoneId.systemDefault();
                                                final ZonedDateTime ps = ZonedDateTime.of(startTime.toLocalDateTime(), ZoneId.systemDefault());
                                                        //.withZoneSameInstant(ZoneId.of("UTC"));

                                                final ZonedDateTime pe = ZonedDateTime.of(endTime.toLocalDateTime(), ZoneId.systemDefault()).withZoneSameLocal(localTimeZone);
                                                        //.withZoneSameInstant(ZoneId.of("UTC"));
						
                                                //final LocalDateTime ps = ZonedDateTime.ofInstant(startTime.toInstant(), localTimeZone).toLocalDateTime();
						
                                                //final LocalDateTime pe = ZonedDateTime.ofInstant(endTime.toInstant(), localTimeZone).toLocalDateTime();

						// get appointmentId to possibly edit.

						ObservableList a = (ObservableList)this.appointmentsTable.getSelectionModel().getSelectedItem();

						if (a != null || A == Action.ADD) {

							// check the database for conflicting appointments

                                                        ObservableList li = (ObservableList)this.appointmentsTable.getSelectionModel().getSelectedItem();
                                                        String s = li == null? "":(String)li.get(2);
							mysql.query("SELECT start ,end FROM appointment" + (A.equals(Action.EDIT)? " WHERE appointmentId !='" + s + "'"  : ""), f-> {
								ResultSet result = (ResultSet)f;

								try {
									boolean hasConflict = false;

									if (result != null) {

										while (result.next()) {
											ZonedDateTime rs = ZonedDateTime.ofInstant(result.getTimestamp(1).toInstant(), ZoneId.of("UTC"));
											ZonedDateTime re = ZonedDateTime.ofInstant(result.getTimestamp(2).toInstant(), ZoneId.of("UTC"));

											if ((ps.plusMinutes(1).isAfter(rs) && ps.isBefore(re)) // if the proposed start time plus 1 minutes is after the result's start and before the result's end
											|| (pe.minusMinutes(1).isBefore(re) && pe.isAfter(rs)) // if the proposed end minus 1 minute is before the result end, and the proposed end is after the result start.
											   
                                                                                                ) {
												System.out.println("FOUND A CONFLICT!");
												hasConflict = true;
												break;
											}

											System.out.println("[rs: " + rs + "\nre: " + re + "\nps: " + ps + "\npe: " + pe + "]");
										}

										if (hasConflict) {
											popup.alert(msg.get("record_add_conflict_times"));
										}
										else {
											if (A.equals(Action.ADD)) {
												add(startTime.toString(), endTime.toString());
											}
											else {
												edit(startTime.toString(), endTime.toString());
											}
										}
									}

								}
								catch (Exception ex) {
									System.out.println("LINE " + ex);
								}
							});

						}
						else {
							popup.alert(msg.get("record_not_selected"));
						}

					}
				}
			}
		}
	}
}
