package controllers;

import popup.Popup;
import database.MySQL;
import lang.Message;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import option.Option;


public class AppointmentsController {
	private Popup popup;
	private Message msg;
	private MySQL mysql;

	private static String query = "SELECT customer.customerName, appointment.title, "
	                              + "appointment.description, appointment.location, appointment.contact, appointment.start, appointment.end "
	                              + " FROM appointment JOIN customer ON appointment.customerId=customer.customerId ORDER BY appointmentId";

	@FXML
	private TableView table;

	public void set(Popup p, Message m, MySQL d) {
		this.popup = p;
		this.msg = m;
		this.mysql = d;

		mysql.query(query, result -> {
                    
			MySQL.setTable((ResultSet) result, table, Option.None());
		});
	}

}