module com.cab302thursdaytbd {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cab302thursdaytbd to javafx.fxml;
    exports com.cab302thursdaytbd;
}
