module com.cab302thursdaytbd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.net.http;
    requires org.json;
    requires java.desktop;

    opens com.cab302thursdaytbd to javafx.fxml;
    exports com.cab302thursdaytbd;
}
