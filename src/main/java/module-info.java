module com.example.cats {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cats to javafx.fxml;
    exports com.example.cats;
}