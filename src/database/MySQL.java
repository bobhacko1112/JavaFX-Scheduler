package database;
// A builder for a mySQL connection.
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import unwrap.Unwrap;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import popup.Popup;
import lang.Message;
import option.Option;


public class MySQL {
	private String driver, db, url, user, pass;
	private Connection conn;


	public static ObservableList<TableColumn> setTable(ResultSet resultSet, TableView table, Option<String[]> disabledColumns) {
		ObservableList<ObservableList> data = FXCollections.observableArrayList();
		ObservableList<TableColumn> columns = FXCollections.observableArrayList();
		table.getItems().clear();
		table.getColumns().clear();

		try {
			for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {

				int j = i;
				String name = resultSet.getMetaData().getColumnName(i + 1);
				TableColumn col = new TableColumn(name);
				// Option has built in logic that's quick to wrap parameter data.
				disabledColumns.Unwrap(f -> {
					String[] disabled = (String[])f;

					for (int k = 0; k < disabled.length; k++) {
						if (disabled[k].matches(name)) {
							col.setVisible(false);
							break;
						}
					}
				});

				col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
					if (param.getValue().get(j) != null) {
						return new SimpleStringProperty(param.getValue().get(j).toString());
					}
					else {
						return null;
					}
				});
				columns.add(col);
				table.getColumns().addAll(col);

			}
		}
		catch (Exception ex) {
			System.out.println(ex);
		}

		try {
                   final DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy @ hh:mm:ss a");
                   final ZoneId stz = ZoneId.of("UTC"); // system time zone and local time zone
                   final ZoneId ltz = ZoneId.systemDefault();
                   System.out.println(ltz);
			while (resultSet.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();

				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					try {
                                            // Add String or special formatted date or date/time  
                                            Object d = resultSet.getObject(i);
                                            row.add(d instanceof Timestamp?
						        ZonedDateTime.of(resultSet.getTimestamp(i).toLocalDateTime(), ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).format(format)
						        : d instanceof Date ?
						        resultSet.getDate(i).toLocalDate().format(DateTimeFormatter.ISO_DATE)
						        : d.toString());
					}
					catch (Exception ex) {
						row.add(resultSet.getString(i));
					}

				}

				data.add(row);
			}
		}
		catch (Exception ex)  {
			System.out.println(ex);
		}

		table.setItems(data);
		table.refresh();
		return columns;

	}


	public void query(String query, Unwrap<ResultSet> f) {
		if (this.conn != null) {
			try {
				Statement s = this.conn.createStatement();

				// super unsafe and dirty hack
				if (query.contains("SELECT") || query.contains("select")) {
					ResultSet rs = s.executeQuery(query);
					f.Unwrap((ResultSet)rs);
				}
				else  {
					boolean result = s.execute(query);
					f.Unwrap(result);
				}
			}
			catch (Exception ex) {
				System.out.println(ex);
			}
		}
		else {
			new Popup().alert("Cannot continue, database is disconnected.");
		}
	}

	public MySQL() {
		ResourceBundle config = ResourceBundle.getBundle("database/server_config");
		this.conn = null;
		this.driver = config.getString("driver");
		this.db = config.getString("database");
		this.url = config.getString("url") + this.db;
		this.user = config.getString("username");
		this.pass = config.getString("password");
		Popup popup = new Popup();
		Message msg = new Message();

		try {
			conn = DriverManager.getConnection(url, user, pass);


			System.out.println("Connected to database : " + db);
		}
		catch (Exception ex) {
			popup.alert("Connection to database failed: " + ex.toString());

		}

	}

}
