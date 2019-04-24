package controllers;

import popup.Popup;
import database.MySQL;
import lang.Message;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import option.Option;

public class CustomersController {
	private Popup popup;
	private Message msg;
	private MySQL mysql;

	private static String query = "SELECT * FROM customer";

	@FXML
	private TableView table;

	public void set(Popup p, Message m, MySQL d) {
		this.popup = p;
		this.msg = m;
		this.mysql = d;

		this.mysql.query(query, f -> {
			MySQL.setTable((ResultSet) f, table, Option.None());
		});
	}
}