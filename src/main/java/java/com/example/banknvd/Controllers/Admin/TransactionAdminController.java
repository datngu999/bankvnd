package com.example.banknvd.Controllers.Admin;

import com.example.banknvd.Controllers.AlertBox;
import com.example.banknvd.Models.Client;
import com.example.banknvd.Models.Model;
import com.example.banknvd.Models.Transaction;
import com.example.banknvd.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TransactionAdminController implements Initializable {
    public TextField search_fld;
    public Button search_btn;
    public TextField chAmountMoney_fld;
    public Button chDeposit_btn;
    public Label dateTime_lbl;
    public ListView<Transaction> result_listView;
    public Button saDeposit_btn;
    public TextField saAmountMoney_fld;


    private Client client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // be first.

        // Search Client
        search_btn.setOnAction(e -> onSearchClient());
        chDeposit_btn.setOnAction(e -> onCheckingDeposit());
        saDeposit_btn.setOnAction(e -> onSavingDeposit());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateTime_lbl.setText(LocalDate.now().format(formatter));
        onShowClients();


    }
    public void onShowClients(){
        // Show all Clients first
        initClientsList();
        result_listView.setItems(Model.getInstance().getAllTransactionAdmin());
        result_listView.setCellFactory(e -> new TransactionCellFactory());
    }
    public void initClientsList(){
        if(Model.getInstance().getClients().isEmpty()){
            Model.getInstance().setClients();
        }
    }


    public void onSearchClient(){
        ResultSet rs = Model.getInstance().getDatabaseDriver().searchClient(search_fld.getText());
        try {
            if (rs != null && rs.next()) { // ✅ Fix chỗ này
                if (rs.getString("payeeAddress").equals(search_fld.getText())) {
                    ObservableList<Client> searchResults = Model.getInstance().searchClient(search_fld.getText());
                    client = searchResults.get(0);

                    ResultSet resultSet = Model.getInstance().getDatabaseDriver().getTransactions(search_fld.getText(), -1);
                    ObservableList<Transaction> Transactions = FXCollections.observableArrayList();
                    try {
                        while (resultSet.next()) {
                            String Sender = resultSet.getString("Sender");
                            String Receiver = resultSet.getString("Receiver");
                            double Amount = resultSet.getDouble("Amount");
                            String date = resultSet.getString("Date");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate dateTrans = LocalDate.parse(date, formatter);
                            String Message = resultSet.getString("Message");
                            Transactions.add(new Transaction(Sender, Receiver, Amount, dateTrans, Message));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    result_listView.setItems(Transactions);
                    result_listView.setCellFactory(e -> new TransactionCellFactory());
                }
            } else {
                AlertBox.display("Failed", "Wrong Payee Address's Client. Please Enter Valid Account!");
                search_fld.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onCheckingDeposit() {

        try {
            double amount = Double.parseDouble(chAmountMoney_fld.getText());
            try {
                if(amount > 0){
                    double newBalance = amount + client.checkingAccountProperty().get().balanceProperty().get();
                    //Update Checking Amount in GUI
                    client.checkingAccountProperty().get().setBalance(newBalance);
                    // Update Checking Amount in DB.
                    Model.getInstance().getDatabaseDriver().depositChecking(client.payeeAddressProperty().get(), newBalance);

                    AlertBox.display("Successful!", "Deposited " + amount + "$ to " + search_fld.getText() + " Successful!");
                }else{
                    AlertBox.display("Failed !", "Please Input Valid Amount.");
                }
            }catch (NullPointerException e){
                AlertBox.display("Failed!", "Please Enter Payee Address Client Before Deposit !");
            }
        } catch (NumberFormatException e) {
            AlertBox.display("Failed!", "Input is not a valid number!");
        }
        emptyField();
        onShowClients();
    }
    private  void onSavingDeposit(){
        try{
            double amount = Double.parseDouble(saAmountMoney_fld.getText());
            try {
                if(amount > 0){
                    double newBalance = amount + client.savingAccountProperty().get().balanceProperty().get();
                    Model.getInstance().getDatabaseDriver().depositSaving(client.payeeAddressProperty().get(), newBalance);
                    // Update in GUI Instance.
                    Model.getInstance().getClient().savingAccountProperty().get().setBalance(newBalance);

                    AlertBox.display("Successful!", "Deposited " + amount + " to " + search_fld.getText() + " Successful!");
                }else{
                    AlertBox.display("Failed !", "Please Input Valid Amount.");

                }

            }catch (NullPointerException e){
                AlertBox.display("Failed!", "Please Enter Payee Address Client Before Deposit !");
            }
        }catch(NumberFormatException e){
            AlertBox.display("Failed!", "Input is not a valid number!");
        }
        emptyField();
        onShowClients();

    }


    private void emptyField(){
        search_fld.setText("");
        chAmountMoney_fld.setText("");
        saAmountMoney_fld.setText("");
        result_listView.setItems(null);
    }
}
