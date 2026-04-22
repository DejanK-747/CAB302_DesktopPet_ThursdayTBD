module com.cab302thursdaytbd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.cab302thursdaytbd to javafx.fxml;
    exports com.cab302thursdaytbd;
}
