package controller;

import dao.EtudiantDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Etudiant;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EtudiantController implements Initializable {

    // ── TableView ────────────────────────────────────────────────────────────────
    @FXML private TableView<Etudiant>         tableEtudiants;
    @FXML private TableColumn<Etudiant, Integer> colId;
    @FXML private TableColumn<Etudiant, String>  colNom;
    @FXML private TableColumn<Etudiant, String>  colPrenom;
    @FXML private TableColumn<Etudiant, String>  colEmail;
    @FXML private TableColumn<Etudiant, String>  colFiliere;
    @FXML private TableColumn<Etudiant, String>  colNiveau;

    // ── Champs du formulaire ─────────────────────────────────────────────────────
    @FXML private TextField fieldNom;
    @FXML private TextField fieldPrenom;
    @FXML private TextField fieldEmail;
    @FXML private TextField fieldFiliere;
    @FXML private ComboBox<String> comboNiveau;

    // ── Barre de recherche ───────────────────────────────────────────────────────
    @FXML private TextField fieldRecherche;

    // ── Label de statut ──────────────────────────────────────────────────────────
    @FXML private Label labelStatut;

    // ── Etat interne ─────────────────────────────────────────────────────────────
    private final EtudiantDAO dao = new EtudiantDAO();
    private final ObservableList<Etudiant> listeEtudiants = FXCollections.observableArrayList();
    private Etudiant etudiantEnCours = null; // null = mode ajout, non-null = mode modification

    // ────────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurerColonnes();
        comboNiveau.setItems(FXCollections.observableArrayList("L1", "L2", "L3", "M1", "M2", "Doctorat"));
        comboNiveau.getSelectionModel().selectFirst();
        tableEtudiants.setItems(listeEtudiants);
        actualiserTable();

        // Remplir le formulaire au clic sur une ligne
        tableEtudiants.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancien, nouveau) -> { if (nouveau != null) remplirFormulaire(nouveau); }
        );
    }

    // ── Configuration des colonnes ───────────────────────────────────────────────
    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFiliere.setCellValueFactory(new PropertyValueFactory<>("filiere"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        // Largeurs relatives
        tableEtudiants.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ── Bouton Enregistrer (Ajouter ou Modifier) ─────────────────────────────────
    @FXML
    private void handleEnregistrer() {
        if (!validerFormulaire()) return;

        Etudiant e = collecterFormulaire();

        if (etudiantEnCours == null) {
            // Mode AJOUT
            if (dao.ajouterEtudiant(e)) {
                afficherStatut("Etudiant ajoute avec succes.", false);
                actualiserTable();
                viderFormulaire();
            } else {
                afficherStatut("Erreur lors de l'ajout.", true);
            }
        } else {
            // Mode MODIFICATION
            e.setId(etudiantEnCours.getId());
            if (dao.modifierEtudiant(e)) {
                afficherStatut("Etudiant modifie avec succes.", false);
                actualiserTable();
                viderFormulaire();
                etudiantEnCours = null;
            } else {
                afficherStatut("Erreur lors de la modification.", true);
            }
        }
    }

    // ── Bouton Modifier (charge la selection dans le formulaire) ─────────────────
    @FXML
    private void handleModifier() {
        Etudiant selectionne = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherStatut("Veuillez selectionner un etudiant a modifier.", true);
            return;
        }
        etudiantEnCours = selectionne;
        remplirFormulaire(selectionne);
        afficherStatut("Mode modification : modifiez les champs puis cliquez sur Enregistrer.", false);
    }

    // ── Bouton Supprimer ─────────────────────────────────────────────────────────
    @FXML
    private void handleSupprimer() {
        Etudiant selectionne = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherStatut("Veuillez selectionner un etudiant a supprimer.", true);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'etudiant");
        confirmation.setContentText("Voulez-vous vraiment supprimer " + selectionne.getPrenom()
                + " " + selectionne.getNom() + " ?");

        Optional<ButtonType> resultat = confirmation.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            if (dao.supprimerEtudiant(selectionne.getId())) {
                afficherStatut("Etudiant supprime avec succes.", false);
                actualiserTable();
                viderFormulaire();
            } else {
                afficherStatut("Erreur lors de la suppression.", true);
            }
        }
    }

    // ── Bouton Rechercher ────────────────────────────────────────────────────────
    @FXML
    private void handleRechercher() {
        String motCle = fieldRecherche.getText().trim();
        if (motCle.isEmpty()) {
            actualiserTable();
            return;
        }
        List<Etudiant> resultats = dao.rechercherEtudiants(motCle);
        listeEtudiants.setAll(resultats);
        afficherStatut(resultats.size() + " etudiant(s) trouve(s).", false);
    }

    // ── Bouton Actualiser ────────────────────────────────────────────────────────
    @FXML
    private void handleActualiser() {
        fieldRecherche.clear();
        actualiserTable();
        viderFormulaire();
        afficherStatut("Liste actualisee.", false);
    }

    // ── Bouton Annuler ───────────────────────────────────────────────────────────
    @FXML
    private void handleAnnuler() {
        viderFormulaire();
        etudiantEnCours = null;
        tableEtudiants.getSelectionModel().clearSelection();
        afficherStatut("", false);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────
    private void actualiserTable() {
        listeEtudiants.setAll(dao.getTousLesEtudiants());
    }

    private void remplirFormulaire(Etudiant e) {
        fieldNom.setText(e.getNom());
        fieldPrenom.setText(e.getPrenom());
        fieldEmail.setText(e.getEmail());
        fieldFiliere.setText(e.getFiliere());
        comboNiveau.setValue(e.getNiveau());
    }

    private void viderFormulaire() {
        fieldNom.clear();
        fieldPrenom.clear();
        fieldEmail.clear();
        fieldFiliere.clear();
        comboNiveau.getSelectionModel().selectFirst();
        etudiantEnCours = null;
    }

    private Etudiant collecterFormulaire() {
        return new Etudiant(
                fieldNom.getText().trim(),
                fieldPrenom.getText().trim(),
                fieldEmail.getText().trim(),
                fieldFiliere.getText().trim(),
                comboNiveau.getValue()
        );
    }

    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();
        if (fieldNom.getText().trim().isEmpty())     erreurs.append("- Le nom est obligatoire.\n");
        if (fieldPrenom.getText().trim().isEmpty())  erreurs.append("- Le prenom est obligatoire.\n");
        if (fieldFiliere.getText().trim().isEmpty()) erreurs.append("- La filiere est obligatoire.\n");
        if (comboNiveau.getValue() == null)          erreurs.append("- Le niveau est obligatoire.\n");

        String email = fieldEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            erreurs.append("- L'adresse email n'est pas valide.\n");
        }

        if (erreurs.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs invalides");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
            alert.setContentText(erreurs.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void afficherStatut(String message, boolean estErreur) {
        labelStatut.setText(message);
        labelStatut.setStyle(estErreur
                ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }
}

