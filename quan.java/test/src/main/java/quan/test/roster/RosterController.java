package quan.test.roster;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by quanchangnai on 2020/5/11.
 */
public class RosterController implements Initializable {

    @FXML
    private TableView<Employee> tableView;

    @FXML
    private Pagination pagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.err.println("location:" + location);
        System.err.println("tableView:" + tableView);
        System.err.println("pagination:" + pagination);

        initializeTableView();

        int pageSize = 100;
        fillEmployeesData(0, pageSize);

        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> fillEmployeesData(newValue.intValue(), pageSize));

    }

    private void initializeTableView() {
        TableColumn<Employee, Integer> idColumn = (TableColumn<Employee, Integer>) tableView.getColumns().get(0);
        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setOnEditCommit(e -> e.getRowValue().setId(e.getNewValue()));

        TableColumn<Employee, String> nameColumn = (TableColumn<Employee, String>) tableView.getColumns().get(1);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setOnEditCommit(e -> e.getRowValue().setName(e.getNewValue()));

        TableColumn<Employee, String> phoneColumn = (TableColumn<Employee, String>) tableView.getColumns().get(2);
        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setOnEditCommit(e -> e.getRowValue().setPhone(e.getNewValue()));

        TableColumn<Employee, String> addressColumn = (TableColumn<Employee, String>) tableView.getColumns().get(3);
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressColumn.setOnEditCommit(e -> e.getRowValue().setAddress(e.getNewValue()));
    }

    private void fillEmployeesData(int pageIndex, int pageSize) {
        ObservableList<Employee> employees = tableView.getItems();
        if (employees == null) {
            employees = FXCollections.observableArrayList();
            tableView.setItems(employees);
        }
        employees.clear();

        for (int i = pageSize * pageIndex + 1; i <= pageSize * (pageIndex + 1); i++) {
            employees.add(new Employee(i, "张三" + i, "电话号码" + i, "地址" + i));
        }
    }

}
