package com.example.banknvd.Controllers.Client;

import com.example.banknvd.Models.Model;
import com.example.banknvd.Models.Transaction;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TransactionCellController implements Initializable {


    public ImageView getIn_icon;
    public ImageView getOut_icon;
    public Label dateTime;
    public Label sender_lbl;
    public Label receiver_lbl;
    public Label amount_lbl;
    public Label message_lbl;
    private final Transaction transaction;
    public TransactionCellController(Transaction transaction) {
        this.transaction = transaction;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String dateStr = transaction.dateProperty().get().toString();
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateTime.setText(date.format(formatter));
        sender_lbl.textProperty().bind(transaction.senderProperty());
        receiver_lbl.textProperty().bind(transaction.receiverProperty());
        message_lbl.textProperty().bind(transaction.messageProperty());
        amount_lbl.textProperty().bind(transaction.amountMoneyProperty().asString());
        onTransactionIcons();

    }



    private void onTransactionIcons(){
        String clientAddress = Model.getInstance().getClient().payeeAddressProperty().get();
        if (transaction.senderProperty().get().equals(clientAddress)) {
            // Nếu client là người gửi -> hiện icon chuyển đi
            getOut_icon.setVisible(true);
            getIn_icon.setVisible(false);
            amount_lbl.setTextFill(Color.RED); // màu đỏ cho chuyển tiền đi
        } else if (transaction.receiverProperty().get().equals(clientAddress)) {
            // Nếu client là người nhận -> hiện icon nhận tiền
            getIn_icon.setVisible(true);
            getOut_icon.setVisible(false);
            amount_lbl.setTextFill(Color.GREEN); // màu xanh cho nhận tiền
        } else {
            // Trường hợp khác, có thể ẩn cả 2 icon
            getIn_icon.setVisible(false);
            getOut_icon.setVisible(true);
            amount_lbl.setTextFill(Color.BLACK);
        }
    }
}