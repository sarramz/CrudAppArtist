package com.example.demoapp;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;

public class ArtistController{
    @FXML
    private TextField nomTextField;

    @FXML
    private Button imageButton;



    @FXML
    private TextArea bioTextArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Label imagePathLabel;

    private Connection connection;
    @FXML
    private TableView<Artist> artistTableView;
    private ObservableList<Artist> artistList = FXCollections.observableArrayList();
    @FXML
    public Button boutonSupprimer;


    public void loadArtists() {
        try {
            connection = DBConnexion.getConnection();
            if (connection == null) {
                System.out.println("La connexion à la base de données n'est pas établie.");
                return;
            } else {
                System.out.println("La connexion à la base de données est établie dans Load Artists.");
                System.out.println(connection);
            }
            String query = "SELECT * FROM `artists`";
            try (
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("HOUNI" + resultSet);
                artistList.clear();

                while (resultSet.next()) {
                    // Create a new Artist object for each row in the ResultSet
                    String name = resultSet.getString("name");
                    String biography = resultSet.getString("biography");
                    String imageUrl = resultSet.getString("imageUrl");

                    System.out.println(name + " " + biography);
                    int id = resultSet.getInt("id");
                    Artist artist = new Artist(id,name, biography, imageUrl);
                    artistList.add(artist);

                }
                artistTableView.setItems(artistList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Méthode appelée lors du clic sur le bouton "Afficher"
    @FXML
    private void afficherArtists() {

        if (artistTableView.getColumns().isEmpty()) {
            TableColumn<Artist, String> nameColumn = new TableColumn<>("Nom");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Artist, String> bioColumn = new TableColumn<>("Biographie");
            bioColumn.setCellValueFactory(new PropertyValueFactory<>("biography"));

            TableColumn<Artist, String> imageUrlColumn = new TableColumn<>("Image URL");
            imageUrlColumn.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

            artistTableView.getColumns().addAll(nameColumn, bioColumn, imageUrlColumn);
        }
        loadArtists();
    }

    @FXML
    private void modifierAction() {
        // Code pour modifier l'artiste sélectionné dans la TableView
    }

    @FXML
    private void supprimerAction() {
        Artist artist = artistTableView.getSelectionModel().getSelectedItem();
        if (artist != null) {
            try {
                connection = DBConnexion.getConnection();
                if (connection == null) {
                    System.out.println("La connexion à la base de données n'est pas établie.");
                    return;
                }

                String deleteQuery = "DELETE FROM artists WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, artist.getId());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Artiste supprimé avec succès !");
                    artistList.remove(artist);


                    String imageUrl = artist.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        String imagePath = "src/main/resources/com/example/demoapp/" + imageUrl;
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            if (imageFile.delete()) {
                                System.out.println("Image supprimée avec succès !");
                            } else {
                                System.out.println("Impossible de supprimer l'image.");
                            }
                        } else {
                            System.out.println("L'image n'existe pas.");
                        }
                    }
                } else {
                    System.out.println("Aucun artiste supprimé.");
                }

                preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("Erreur SQL lors de la suppression de l'artiste : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Veuillez sélectionner un artiste à supprimer.");
        }
    }


    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            imagePathLabel.setText(imagePath);
        }
    }

    @FXML
    private void saveArtist() {
        connection = DBConnexion.getConnection();
        if (connection == null) {
            System.out.println("La connexion à la base de données n'est pas établie.");
            return;
        }

        String name = nomTextField.getText();
        String biography = bioTextArea.getText();

        if (name.isEmpty() || biography.isEmpty()) {
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        if (imagePathLabel.getText().isEmpty()) {
            System.out.println("Veuillez choisir une image.");
            return;
        }

        String imageUrl = saveImageToUploads();

        try {
            String insertQuery = "INSERT INTO artists (id,name, biography, imageUrl) VALUES (default,?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, biography);
            preparedStatement.setString(3, imageUrl);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artiste enregistré avec succès !");
                clearFields();

            } else {
                System.out.println("Erreur lors de l'enregistrement de l'artiste.");
            }

            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Méthode pour sauvegarder l'image dans le dossier d'uploads
    private String saveImageToUploads() {
        try {
            String sourceImagePath = imagePathLabel.getText().substring(5);
            File sourceFile = new File(sourceImagePath);
            String fileName = sourceFile.getName();
            String destinationDirectory = "src/main/resources/com/example/demoapp/uploads";
            File destinationFolder = new File(destinationDirectory);

            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            File destFile = new File(destinationFolder.getAbsolutePath() + "/" + fileName);

            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return "uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @FXML
    private void clearFields() {
        // Code pour effacer les champs de saisie
        nomTextField.clear();
        bioTextArea.clear();
        // Effacer également le chemin de l'image dans le label
        imagePathLabel.setText("");
    }


}
