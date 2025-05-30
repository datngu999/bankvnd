package com.example.banknvd.Controllers.Client;

import com.example.banknvd.Controllers.AlertBox;
import com.example.banknvd.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {


    public Label checkingNum_lbl;
    public Label checkingLimit_lbl;
    public Label checkingDataCreated_lbl;
    public Label checkingBalance_lbl;
    public Button checkingMove_btn;
    public TextField checkingAmount_fld;
    public Label savingNum_lbl;
    public Label savingLimit_lbl;
    public Label savingDateCreated_lbl;
    public Label savingBalance_lbl;
    public Button savingMove_btn;
    public TextField savingAmount_fld;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindData();
    }

    // Binding data --> get data to GUI.
    public void bindData() {
        // Checking Account
        checkingNum_lbl.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty());
        checkingLimit_lbl.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().transactionLimitProperty().asString()); // double parse to string.
        String dateStr = Model.getInstance().getClient().dateCreatedProperty().get().toString();
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        checkingDataCreated_lbl.setText(date.format(formatter));
        checkingBalance_lbl.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().asString());
        // Saving Account
        savingNum_lbl.textProperty().bind(Model.getInstance().getClient().savingAccountProperty().get().accountNumberProperty());
        savingLimit_lbl.textProperty().bind(Model.getInstance().getClient().savingAccountProperty().get().withdrawalLimitProperty().asString()); // double parse to string.
        savingDateCreated_lbl.setText(date.format(formatter));
        savingBalance_lbl.textProperty().bind(Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().asString());
        // Moving methods
        checkingMove_btn.setOnAction(e -> onMoveToSaving());
        savingMove_btn.setOnAction(e -> onMoveToChecking());
    }

    // Move money between checking account and saving account.
    public void onMoveToSaving() {
        try {
            double amount = Double.parseDouble(checkingAmount_fld.getText());
            double currentCheckingMoney = Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().get();
            double newSavingAmount = Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().get() + amount;

            if (currentCheckingMoney >= amount) {
                Model.getInstance().getDatabaseDriver().depositSaving(Model.getInstance().getClient().payeeAddressProperty().get(), newSavingAmount);
                Model.getInstance().getDatabaseDriver().depositChecking(Model.getInstance().getClient().payeeAddressProperty().get(), currentCheckingMoney - amount);
                Model.getInstance().getClient().checkingAccountProperty().get().setBalance(currentCheckingMoney - amount);
                Model.getInstance().getClient().savingAccountProperty().get().setBalance(newSavingAmount);
                AlertBox.display("Success !", "Deposited " + amount + " $ to Saving Account.");
            } else {
                AlertBox.display("Failed !", "Your checking account doesn't have enough money !");
            }
        } catch (NumberFormatException e) {
            AlertBox.display("Failed!", "Invalid input. Please enter a valid number.");
        }

        onEmpty();

    }

    public void onMoveToChecking() {
        try {
            double amount = Double.parseDouble(savingAmount_fld.getText());
            double currentSavingAmount = Model.getInstance().getClient().savingAccountProperty().get().balanceProperty().get();
            double newCheckingAmount = Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().get() + amount;
            if (currentSavingAmount >= amount) {
                Model.getInstance().getDatabaseDriver().depositChecking(Model.getInstance().getClient().payeeAddressProperty().get(), newCheckingAmount);
                Model.getInstance().getDatabaseDriver().depositSaving(Model.getInstance().getClient().payeeAddressProperty().get(), currentSavingAmount - amount);
                Model.getInstance().getClient().checkingAccountProperty().get().setBalance(newCheckingAmount);
                Model.getInstance().getClient().savingAccountProperty().get().setBalance(currentSavingAmount - amount);
                AlertBox.display("Success !", "Deposited " + amount + " $ to Checking Account.");
            } else {
                AlertBox.display("Failed !", "Your checking account doesn't have enough money !");
            }
        } catch (NumberFormatException e) {
            AlertBox.display("Failed!", "Invalid input. Please enter a valid number.");
        }

        onEmpty();

    }


    private void onEmpty() {
        checkingAmount_fld.setText("");
        savingAmount_fld.setText("");
    }
}
