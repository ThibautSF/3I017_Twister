# 3I017_Twister
Projet du module 3I017 Technologies Web (L3 UPMC)

## Cours Suivi
Cours par [Laure Soulier](http://www-connex.lip6.fr/~soulier/content/about.html).
Unité d'enseignement de L3, 3I017 Technologies Web à l'[Université Pierre et Marie Curie](https://www.sorbonne-universite.fr/) : http://www-connex.lip6.fr/~soulier/content/TechnoWeb/TechnoWeb.html



## Projet
Réalisation d'un réseau social de microblogage (type twitter).
L'application sera en architecture client-serveur, en modèle MVC


### Serveur
* Serveur Apache Tomcat 9
* Java 8
* SGBDR : MySQL 5.7 (pour stockage des utilisateurs, sessions et amis)
* SGBD NoSQL : MongoDB 3.2 (pour stockage des messages)

#### Fonctionalitées implémentées
* Gestion utilisateur (création, login, logout)
* Gestion messages (ajout, ajout commentaires, listage des messages (all/user/friend), supression (non implémenté côté client))
* Gestion amis (ajout, suppression)
* Recherche (recherche de messages et utilisateur par mots clés)

### Client
* HTML
* Javascript
* Jquery
* Ajax
* API Mustache


### Membres du projet
* SIMON-FINE Thibaut


# Legal

## Avant le 1er Juin 2018 00:00:00
Le projet étant commun, interdiction d'utilisation et de modification de tout code dont je suis l'auteur sans autorisation de ma part (hormis services.classes.ErrorPrint qui peut être réutilisé/modifié tant que je suis cité en tant qu'auteur original).

## A partir du 1er Juin 2018
Vous êtes libres d'utiliser et modifier mon code tant que je suis cité en tant qu'auteur original.
