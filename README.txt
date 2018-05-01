---------------
Environnement de développement utilisé
---------------
MySQL 5.7.22
Tomcat 9.0
Java 8
MongoDB 3.2.19

---------------
Procédure de mise en place
---------------
Les fichiers sont situés dans le répertoire Install/
- 3I017_Twister.war → application tomcat
- importMongoDB.sh → script d'import de BDD MongoDB (lance la commande 'mongoimport' avec les paramètre adéquats)
- twister.sql → script SQL de création la BDD SQL (à importer dans MySQL)
- twister_mongo.message.json → fichier de sauvegarde de la BDD MongoDB

