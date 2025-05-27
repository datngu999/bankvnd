package com.example.banknvd.Controllers.Client;

import com.example.banknvd.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {


    public BorderPane client_parent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observableValue,oldVal, newVal) -> {
            switch (newVal){
                case TRANSACTIONS -> client_parent.setCenter(Model.getInstance().getViewFactory().getTransactionView());
                case ACCOUNT -> client_parent.setCenter(Model.getInstance().getViewFactory().getAccountView());
                default -> client_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());
            }
        });
    }
}