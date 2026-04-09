-- ============================================================
--  Script SQL – Gestion des Étudiants
--  Base de données : gestion_etudiants
-- ============================================================

-- 1. Créer et sélectionner la base de données
CREATE DATABASE IF NOT EXISTS gestion_etudiants
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_etudiants;

-- 2. Créer la table etudiant
CREATE TABLE IF NOT EXISTS etudiant (
    id       INT          NOT NULL AUTO_INCREMENT,
    nom      VARCHAR(100) NOT NULL,
    prenom   VARCHAR(100) NOT NULL,
    email    VARCHAR(150),
    filiere  VARCHAR(100) NOT NULL,
    niveau   VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_etudiant PRIMARY KEY (id),
    CONSTRAINT uq_email    UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Données de test
INSERT INTO etudiant (nom, prenom, email, filiere, niveau) VALUES
    ('Dupont',   'Alice',   'alice.dupont@email.com',   'Informatique',    'L2'),
    ('Martin',   'Bob',     'bob.martin@email.com',     'Mathématiques',   'L3'),
    ('Leroy',    'Claire',  'claire.leroy@email.com',   'Informatique',    'M1'),
    ('Moreau',   'David',   'david.moreau@email.com',   'Physique',        'L1'),
    ('Bernard',  'Emma',    'emma.bernard@email.com',   'Informatique',    'M2'),
    ('Petit',    'François','francois.petit@email.com', 'Économie',        'L3');
