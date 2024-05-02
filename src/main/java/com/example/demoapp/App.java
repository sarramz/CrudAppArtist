package com.example.demoapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Connection connection = DBConnexion.getConnection();
        if (connection == null) {
            System.out.println("La connexion à la base de données a échoué. L'application se ferme.");
            return;
        }
        System.out.println("Connexion à la base de données établie avec succès.");
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Artists.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Gestion!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}