package controllers;

import popup.Popup;
import database.MySQL;
import lang.Message;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import option.Option;

public class CalenderController {
	private Popup popup;
	private Message msg;
	private MySQL mysql;
	/*
	 * As per feedback I have added a year and a week column, the table's string-sort works great
	 * with it. You can now "sort by week and year"
	 */
	private static String query = "SELECT appointmentId, customerId, title, "
	                              + "description, location, contact, url, start, end, createDate, "
	                              + "createdBy, CONCAT('Week# ',WEEK(start)) AS week, CONCAT(' Year: ', YEAR(start)) AS YEAR"
	                              + " FROM appointment ORDER BY start";

	@FXML
	private TableView table;

	public void set(Popup p, Message m, MySQL d) {
		this.popup = p;
		this.msg = m;
		this.mysql = d;

		mysql.query(query, f -> {
			ResultSet resultSet = (ResultSet) f;
			String[] disabled = {"appointmentId"};
			MySQL.setTable(resultSet, table, Option.Some(disabled));
		});
	}
}
