module com.example.banknvd {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.banknvd to javafx.fxml;
    exports com.example.banknvd;
    exports com.example.banknvd.Controllers;
    exports com.example.banknvd.Controllers.Admin;
    exports com.example.banknvd.Controllers.Client;
    exports com.example.banknvd.Models;
    exports com.example.banknvd.Views;
}