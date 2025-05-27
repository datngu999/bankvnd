package com.example.banknvd.Controllers.Admin;

import com.example.banknvd.Controllers.AlertBox;
import com.example.banknvd.Models.Client;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientCellController implements Initializable {
    public Label fullName_lbl;
    public Label payeeAddress_lbl;
    public Label ckAccount_lbl;
    public Label svAccount_lbl;
    public Label dateCreated_lbl;


    public Button info_btn;

    private final Client client;

    public ClientCellController(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fullName_lbl.textProperty().bind(Bindings.concat(client.lastNameProperty()).concat(" ").concat(client.firstNameProperty()));
        payeeAddress_lbl.textProperty().bind(client.payeeAddressProperty());
        ckAccount_lbl.textProperty().bind(client.checkingAccountProperty().asString());
        svAccount_lbl.textProperty().bind(client.savingAccountProperty().asString());
        String dateStr = client.dateCreatedProperty().get().toString();
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateCreated_lbl.setText(date.format(formatter));
        info_btn.setOnAction(e -> {
            String checkingBalance = String.valueOf(client.checkingAccountProperty().get().balanceProperty().get());
            String savingBalance = String.valueOf(client.savingAccountProperty().get().balanceProperty().get());
            AlertBox.display("Account Info!", "Checking Balance : " + checkingBalance + "$ | Saving Balance : " + savingBalance + "$");
        });
        onClientCelColor();
    }

    public void onClientCelColor() {
        dateCreated_lbl.setStyle("-fx-font-family: 'Cascadia Code'; -fx-text-fill: #CCFFFF");
        fullName_lbl.setStyle("-fx-font-family: 'Cascadia Code' ; -fx-text-fill: #5e3812;");
        payeeAddress_lbl.setStyle("-fx-font-family: 'Cascadia Code'; -fx-text-fill: #4d650c;");
        ckAccount_lbl.setStyle("-fx-font-family: 'Cascadia Code'; -fx-text-fill: #73736b ");
        svAccount_lbl.setStyle("-fx-font-family: 'Cascadia Code'; -fx-text-fill: #305d5d");
    }
}