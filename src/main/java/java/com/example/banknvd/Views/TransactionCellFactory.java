package com.example.banknvd.Views;

import com.example.banknvd.Controllers.Client.TransactionCellController;
import com.example.banknvd.Models.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class TransactionCellFactory extends ListCell<Transaction> {
    @Override
    protected void updateItem(Transaction transaction, boolean empty){{
        if(empty){
            setText(null);
            setGraphic(null);
        }else{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/TransactionCell.fxml"));
            TransactionCellController controller = new TransactionCellController(transaction);
            fxmlLoader.setController(controller);
            setText(null);
            try{
                setGraphic(fxmlLoader.load());
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    }
}