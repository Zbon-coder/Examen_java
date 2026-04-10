package dao;

import database.DatabaseConnection;
import model.Etudiant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    // ── CREATE ──────────────────────────────────────────────────────────────────
    public boolean ajouterEtudiant(Etudiant etudiant) {
        String sql = "INSERT INTO etudiant (nom, prenom, email, filiere, niveau) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenom());
            stmt.setString(3, etudiant.getEmail());
            stmt.setString(4, etudiant.getFiliere());
            stmt.setString(5, etudiant.getNiveau());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
            return false;
        }
    }

    // ── READ – tous ──────────────────────────────────────────────────────────────
    public List<Etudiant> getTousLesEtudiants() {
        List<Etudiant> liste = new ArrayList<>();
        String sql = "SELECT * FROM etudiant ORDER BY nom, prenom";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture : " + e.getMessage());
        }
        return liste;
    }

    // ── READ – par id ────────────────────────────────────────────────────────────
    public Etudiant getEtudiantById(int id) {
        String sql = "SELECT * FROM etudiant WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par id : " + e.getMessage());
        }
        return null;
    }

    // ── READ – recherche par nom ou filiere ─────────────────────────────────────
    public List<Etudiant> rechercherEtudiants(String motCle) {
        List<Etudiant> liste = new ArrayList<>();
        String sql = "SELECT * FROM etudiant WHERE nom LIKE ? OR prenom LIKE ? OR filiere LIKE ? ORDER BY nom, prenom";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            String pattern = "%" + motCle + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) liste.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return liste;
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────────
    public boolean modifierEtudiant(Etudiant etudiant) {
        String sql = "UPDATE etudiant SET nom=?, prenom=?, email=?, filiere=?, niveau=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenom());
            stmt.setString(3, etudiant.getEmail());
            stmt.setString(4, etudiant.getFiliere());
            stmt.setString(5, etudiant.getNiveau());
            stmt.setInt(6, etudiant.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────────
    public boolean supprimerEtudiant(int id) {
        String sql = "DELETE FROM etudiant WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    // ── helper ───────────────────────────────────────────────────────────────────
    private Etudiant mapRow(ResultSet rs) throws SQLException {
        return new Etudiant(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("filiere"),
                rs.getString("niveau")
        );
    }
}
