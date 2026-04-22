module com.cab302thursdaytbd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.cab302thursdaytbd to javafx.fxml;
    exports com.cab302thursdaytbd;
}
