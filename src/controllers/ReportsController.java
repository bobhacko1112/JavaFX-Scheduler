package controllers;

import popup.Popup;
import database.*;
import lang.Message;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import option.Option;

public class ReportsController {
	private Popup popup;
	private Message msg;
	private MySQL mysql;

	private static String table1_query = "SELECT COUNT(title) AS Count, title "
	                                     + "AS Type, DATE_FORMAT(start, '%Y-%m-%d') AS Date FROM appointment "
	                                     + "GROUP BY start ORDER BY start";

	private static String table2_query = "SELECT customer.customerName, "
	                                     + "appointment.description, appointment.start, appointment.end, "
	                                     + "appointment.contact FROM appointment JOIN customer ON "
	                                     + "customer.customerId=appointment.customerId ORDER BY appointment.contact";


	private static String table3_query = "SELECT COUNT(contact), "
	                                     + "contact FROM appointment GROUP BY contact";
	@FXML
	private TableView table1, table2, table3;

	public void set(Popup p, Message m, MySQL d) {
		this.popup = p;
		this.msg = m;
		this.mysql = d;
		this.mysql.query(table1_query, f -> {
			MySQL.setTable((ResultSet)f, table1, Option.None());
		});

		this.mysql.query(table2_query, f -> {
			MySQL.setTable((ResultSet)f, table2, Option.None());
		});

		this.mysql.query(table3_query, f -> {
			MySQL.setTable((ResultSet)f, table3, Option.None());
		});
	}





}