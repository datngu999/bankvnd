package com.example.banknvd.Controllers.Client;

import com.example.banknvd.Controllers.AlertBox;
import com.example.banknvd.Models.Model;
import com.example.banknvd.Models.Transaction;
import com.example.banknvd.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<Transaction> transaction_listview;
    public TextField depo_address;
    public TextField depo_amount;
    public TextField depo_message;
    public Button depo_btn;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAllTransactionsList();
        transaction_listview.setItems(Model.getInstance().getAllTransactions());
        transaction_listview.setCellFactory(e -> new TransactionCellFactory());
        // onSendingDeposit
        depo_btn.setOnAction(e -> onSendingMoney());
    }

    public void initAllTransactionsList(){
        if(Model.getInstance().getAllTransactions().isEmpty()){
            Model.getInstance().setAllTransactions();
        }
    }


    public void onSendingMoney(){

        String Sender = Model.getInstance().getClient().payeeAddressProperty().get();
        String Receiver = depo_address.getText();
        if(Receiver.isEmpty() && depo_address.getText().isEmpty()){
            AlertBox.display("Failed", "Please enter in text fields !");
            return;

        }
        if(Receiver.isEmpty()){
            AlertBox.display("Failed", "Please enter a Receiver !");
            depo_address.setText("");
            return;
        }
        if(depo_amount.getText().isEmpty()){
            AlertBox.display("Failed", "Please enter an Amount !");
            depo_amount.setText("");
            return;
        }
        try{
            double Amount = Double.parseDouble(depo_amount.getText());
            if(Amount <= 0 ){
                AlertBox.display("Failed", "Please enter a Valid Amount !");
                depo_amount.setText("");
                return;
            }else{
                String Message = depo_message.getText();
                double currentSenderAmount = Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().get();
                // Checking current Amount of Sender.
                if(currentSenderAmount >=  Amount) {
                    // Check valid receiver
                    ResultSet rs = Model.getInstance().getDatabaseDriver().searchClient(Receiver);
                    try {
                        if (rs.next()) {
                            // Checking Valid Receiver
                            if (rs.getString("payeeAddress").equals(Receiver)) {
                                if(Sender.equals(Receiver)){
                                    AlertBox.display("Failed", "Please enter a Valid Receiver !");
                                }else{
                                    Model.getInstance().getDatabaseDriver().updateBalance(Receiver, Amount, "ADD");
                                    // Subtract form sender checking Account.
                                    Model.getInstance().getDatabaseDriver().updateBalance(Sender, Amount, "SUB");
                                    // Update Checking Account From Client Object.
                                    Model.getInstance().getClient().checkingAccountProperty().get().setBalance(Model.getInstance().getDatabaseDriver().getCheckingAccountBalance(Sender));
                                    // Record new Transaction
                                    Model.getInstance().getDatabaseDriver().newTransactions(Sender, Receiver, Amount, Message);
                                    // Update From Transaction ListView
                                    onUpdateTransaction();

                                    AlertBox.display("Successful!", "Deposited " + Amount + " $ to " + Receiver);
                                }

                            }
                        } else {
                            AlertBox.display("Failed", "Wrong Payee Address's Receiver. Please enter a valid Account !");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    AlertBox.display("Failed!", "Your Account doesn't enough money !");
                    depo_amount.setText("");
                }
            }
        }catch (NumberFormatException e){
            AlertBox.display("Failed", "Wrong Amount Format. Please enter a valid amount !");
            depo_amount.setText("");
            return;

        }
        // Clear Empty Fields
        depo_address.setText("");
        depo_amount.setText("");
        depo_message.setText("");
    }


    private void onUpdateTransaction() {
//        System.out.println(Model.getInstance().getDatabaseDriver().getTotalTransactions());
        ObservableList<Transaction> listTrans = FXCollections.observableArrayList();
        Model.getInstance().prepareTransactions(listTrans, -1);
        transaction_listview.setItems(listTrans);
        transaction_listview.setCellFactory(e -> new TransactionCellFactory());

    }
}