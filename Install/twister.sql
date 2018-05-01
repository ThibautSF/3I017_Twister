-- phpMyAdmin SQL Dump
-- version 4.5.4.1deb2ubuntu2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 01, 2018 at 06:21 PM
-- Server version: 5.7.22-0ubuntu0.16.04.1
-- PHP Version: 7.0.28-0ubuntu0.16.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `twister`
--
CREATE DATABASE IF NOT EXISTS `twister` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `twister`;

-- --------------------------------------------------------

--
-- Table structure for table `Friend`
--

DROP TABLE IF EXISTS `Friend`;
CREATE TABLE `Friend` (
  `id_user` int(10) UNSIGNED NOT NULL,
  `id_friend` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des amis';

--
-- Dumping data for table `Friend`
--

INSERT INTO `Friend` (`id_user`, `id_friend`) VALUES
(1, 2),
(1, 3),
(2, 1),
(2, 3),
(2, 5),
(3, 1),
(3, 2),
(5, 2),
(5, 4);

-- --------------------------------------------------------

--
-- Table structure for table `Session`
--

DROP TABLE IF EXISTS `Session`;
CREATE TABLE `Session` (
  `id` int(10) UNSIGNED NOT NULL,
  `skey` varchar(32) NOT NULL COMMENT 'Clé de connexion de la session',
  `id_user` int(10) UNSIGNED NOT NULL COMMENT 'utilisateur de la session',
  `sdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'timestamp de création de la session',
  `root` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Stockage des sessions';

--
-- Dumping data for table `Session`
--

INSERT INTO `Session` (`id`, `skey`, `id_user`, `sdate`, `root`) VALUES
(8, '62e0c192a89b433990a1e6bd88285bcf', 1, '2018-05-01 16:06:10', 0);

-- --------------------------------------------------------

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `id` int(10) UNSIGNED NOT NULL,
  `login` varchar(255) NOT NULL COMMENT 'Login de l''utilisateur',
  `password` blob NOT NULL COMMENT 'Mot de passe (fct PASSWORD() appliqué)',
  `root` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Utilisateur root (true) ou non (false)',
  `nom` varchar(255) DEFAULT NULL COMMENT 'Nom utilisateur',
  `prenom` varchar(255) DEFAULT NULL COMMENT 'Prénom utilisateur',
  `age` int(11) DEFAULT NULL COMMENT 'Age de l''utilisateur (NULL = Non renseigné)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Table des utilisateurs';

--
-- Dumping data for table `User`
--

INSERT INTO `User` (`id`, `login`, `password`, `root`, `nom`, `prenom`, `age`) VALUES
(1, 'thibaut', 0x2a30333731304343463735443943313437423036334533323239383535324241353332434445373144, 0, 'SIMON-FINE', 'Thibaut', 22),
(2, 'Noxi', 0x2a43463136313038424145324445423245453541433430363232383032364335304442434346433539, 0, NULL, NULL, NULL),
(3, 'panda', 0x2a35313843333643303843374230314242343639423930343542363737333134444430453533444535, 0, NULL, NULL, NULL),
(4, 'theGreatTrixie', 0x2a37454136413534423737414534303432373846433331304131444239373645393331433437334146, 0, NULL, NULL, NULL),
(5, 'sparkle', 0x2a34423044373243413044364444454430423444423143363231393635333842453345453235424542, 0, NULL, 'Twilight', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Friend`
--
ALTER TABLE `Friend`
  ADD PRIMARY KEY (`id_user`,`id_friend`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_friend` (`id_friend`);

--
-- Indexes for table `Session`
--
ALTER TABLE `Session`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_skey` (`skey`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `User`
--
ALTER TABLE `User`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_login` (`login`),
  ADD UNIQUE KEY `id` (`id`),
  ADD KEY `id_2` (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Session`
--
ALTER TABLE `Session`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `User`
--
ALTER TABLE `User`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `Friend`
--
ALTER TABLE `Friend`
  ADD CONSTRAINT `fk_friend_idfriend` FOREIGN KEY (`id_user`) REFERENCES `User` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_friend_iduser` FOREIGN KEY (`id_friend`) REFERENCES `User` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `Session`
--
ALTER TABLE `Session`
  ADD CONSTRAINT `fk_session_iduser` FOREIGN KEY (`id_user`) REFERENCES `User` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
