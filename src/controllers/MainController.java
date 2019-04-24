/*
*/

package controllers;

import popup.Popup;
import database.*;
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
import java.util.ResourceBundle;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Tab;


public class MainController implements Initializable {

	private Popup popup;
	private Message msg;
	private MySQL mysql;


	@FXML
	private Tab appointmentsTab, reportsTab, customersTab, calenderTab;
	@FXML
	private AppointmentsController appointmentsController;
	@FXML
	private EditAppointmentController editAppointmentController;
	@FXML
	private EditCustomerController editCustomerController;
	@FXML
	private ReportsController reportsController;
	@FXML
	private CustomersController customersController;
	@FXML
	private CalenderController  calenderController;



	public void set(Popup p, Message m, MySQL d) {
		this.popup = p;
		this.msg = m;
		this.mysql = d;
                
                

		this.appointmentsTab.setText(this.msg.get("appointments"));
		this.reportsTab.setText(this.msg.get("reports"));
		this.customersTab.setText(this.msg.get("customers"));
		this.calenderTab.setText(this.msg.get("calender"));

		this.appointmentsController.set(this.popup, this.msg, this.mysql);
		this.reportsController.set(this.popup, this.msg, this.mysql);

		this.calenderController.set(this.popup, this.msg, this.mysql);

		this.editAppointmentController.set(this.popup, this.msg, this.mysql);

		this.customersController.set(this.popup, this.msg, this.mysql);

		this.editCustomerController.set(this.popup, this.msg, this.mysql);
	}

	@FXML
	public void initialize(URL location, ResourceBundle resources) {

	}



}
