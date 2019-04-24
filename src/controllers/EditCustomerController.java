/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;


import javafx.collections.FXCollections;

import java.sql.*;
import java.time.LocalDateTime;
import option.Option;
import java.time.format.DateTimeFormatter;
import popup.Popup;
import database.MySQL;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import lang.Message;
import javafx.fxml.FXML;
import java.util.Iterator;
import java.util.Set;
import javafx.scene.control.TableView;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ChoiceBox;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import sanitization.Sanitizer;
import javafx.util.Callback;
import javafx.scene.control.ToggleButton;


/**
 *
 * @author bob
 */
public class EditCustomerController {
	private MySQL mysql;
	private Message msg;
	private Popup popup;
	private enum Action {ADD, EDIT}


	@FXML
	private TableView customerListTable, addressTable;
	@FXML
	private TextField name, addressText, phoneText, zipText;
	@FXML
	private ToggleButton isActive;

	private static String repsQuery = "SELECT userId, userName FROM user ORDER BY userId";

	private static String customerListQuery = "SELECT * FROM customer";
	private static String addressQuery = "SELECT * FROM address";
        
        @FXML
        public void addAddress(){
            try {
            this.mysql.query("SELECT MAX(addressId) + 1 FROM address", r -> {
                ResultSet rs = (ResultSet)r;
                try {
                rs.next();
            String query = "INSERT INTO address (addressId, address, address2, "
                    + "cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES ('"+ rs.getString(1) +"', '"+ this.addressText.getText() +"', "
                    + "'n/a', '2', '"+ this.zipText.getText() +"', "
                    + "'"+ this.phoneText.getText() +"', NOW(), 'admin', NOW(), 'admin')";
              
            this.mysql.query(query, f -> {
                updateAddressList();
            
            
                });
              } catch (Exception ex) {
                    System.out.println(ex);
                }
            });
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        
	private void updateCustomerTable() {
		// populate customer table
		this.mysql.query(customerListQuery, f -> {
			String[] disabled = {"createDate", "createdBy", "lastUpdate", "lastUpdateBy", "customerId", "addressId"};
			MySQL.setTable((ResultSet) f, customerListTable, Option.Some(disabled));

		});
	}

	private void edit() {
		System.out.println("EDIT!");
		ObservableList l = (ObservableList)this.customerListTable.getSelectionModel().getSelectedItem();

		ObservableList a = (ObservableList)this.addressTable.getSelectionModel().getSelectedItem();

		String updateQuery = "UPDATE customer SET customerName='" + this.name.getText() + "', addressId='" + a.get(0) + "', active='" + (this.isActive.isSelected() ? "0" : "1") + "' WHERE customerId='" + l.get(0) + "'";


		mysql.query(updateQuery, r -> {
			System.out.println("RESULT OBJECT IS: " + r);
			boolean insertResult = (boolean)r;

			if (!insertResult) {
				popup.alert(msg.get("record_edit_success"));
				updateCustomerTable();
			}
		});


		System.out.println(updateQuery);

	}

	private void add() {
		// populate record after getting a unique new appointment ID,
		// No AUTO_INCREMENT on this? What a shame.
		mysql.query("SELECT MAX(customerId) + 1 FROM customer", g -> {
			ResultSet rs = (ResultSet)g;
			int newId = 0;

			try {
				rs.next();
				newId = rs.getInt(1);
			}
			catch (Exception ex) {
				System.out.println(ex);
			}

			ObservableList l = (ObservableList)customerListTable.getSelectionModel().getSelectedItem();
			ObservableList a = (ObservableList)addressTable.getSelectionModel().getSelectedItem();
			String insertQuery = "INSERT INTO customer (customerId, customerName, addressId, createDate, createdBy, lastUpdate, lastUpdateBy, active) VALUES ("
			+ "'" + newId + "', '" + name.getText() + "', '" + a.get(0) + "', NOW(), 'admin', NOW(), 'admin', '" + (this.isActive.isSelected() ? "0" : "1") + "')"; // TODO
			mysql.query(insertQuery, r -> {
				
				boolean insertResult = (boolean)r;

				if (!insertResult) {
					popup.alert(msg.get("record_add_success"));
					updateCustomerTable();
				}
			});


			
		});
	}

private void updateAddressList() {
    this.mysql.query(addressQuery, f -> {
			String[] disabled = {"address2", "createDate", "createdBy", "lastUpdate", "lastUpdateBy"};
			MySQL.setTable((ResultSet) f, addressTable, Option.Some(disabled));
		});
}

	public void set(Popup p, Message m, MySQL d) {
		this.mysql = d;
		this.msg = m;
		this.popup = p;

		//This hopfully will prevent most shinanagins.
		
		this.name.textProperty().addListener(Sanitizer.forceQuerySafe(name));
                this.phoneText.textProperty().addListener(Sanitizer.forceQuerySafe(phoneText));
                this.phoneText.textProperty().addListener(Sanitizer.forceLength(phoneText, 10));
                this.phoneText.textProperty().addListener(Sanitizer.forceIntegers(phoneText));
                this.zipText.textProperty().addListener(Sanitizer.forceIntegers(zipText));
                this.zipText.textProperty().addListener(Sanitizer.forceLength(zipText, 7));
                this.addressText.textProperty().addListener(Sanitizer.forceQuerySafe(addressText));

		//populate customer table
		updateCustomerTable();
		// set addresses table
		updateAddressList();
		this.isActive.setSelected(true);
		this.isActive.selectedProperty().addListener(f -> {
			if (this.isActive.isSelected())
				this.isActive.setText("Inactive");
			else
			{ this.isActive.setText("Active"); }
		});
		customerListTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				ObservableList row = (ObservableList)newSelection;
				this.name.setText((String)row.get(1));
				this.isActive.setSelected((String)row.get(3) == "1");
				ObservableList addresses = (ObservableList)addressTable.getItems();
				int index = 0;
				Iterator it = addresses.iterator();

				while (it.hasNext()) {
					ObservableList l = (ObservableList)it.next(); //column of addresses
					String s = (String)l.get(0);

					if (s.matches((String)row.get(2))) {
						break;
					}

					index++;
				}

				this.addressTable.scrollTo(index);
				this.addressTable.getSelectionModel().select(index);
			}

		});

	}

	@FXML
	public void deleteCustomer() {
		ObservableList row = (ObservableList)customerListTable.getSelectionModel().getSelectedItem();
		String customerId = (String)row.get(0);

		if (popup.confirm(msg.get("confirm_delete"))) {
			mysql.query("DELETE FROM customer WHERE customerId='" + customerId + "'", f -> {
				if (!(boolean)f) {
					popup.alert(msg.get("record_delete_success"));
					updateCustomerTable();
				}
			});
		}
	}

	@FXML
	public void editCustomer() {
		validateAndDo(Action.EDIT);
	}
	@FXML
	public void addCustomer() {
		validateAndDo(Action.ADD);
	}


	private void validateAndDo(Action A) {
		// check that all textFields are populated with something.
		if (name.getText().length() < 1) {
			popup.alert(msg.get("record_no_field_data"));
		}
		else {
			//make sure a address and customer are selected
			if ((customerListTable.getSelectionModel().getSelectedItem() == null && A != Action.EDIT) || addressTable.getSelectionModel().getSelectedItem() == null) {
				popup.alert(msg.get("record_no_rep_or_customer_selected"));
			}
			else {
				ObservableList a = (ObservableList)this.customerListTable.getSelectionModel().getSelectedItem();

				if ((a != null && A == Action.EDIT) || A == Action.ADD) {
					if (A.equals(Action.ADD)) {
						add();
					}
					else {
						edit();
					}

				}
				else {
					popup.alert(msg.get("record_not_selected"));
				}


			}
		}

	}

}
