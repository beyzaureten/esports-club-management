-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: esports_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `games`
--

DROP TABLE IF EXISTS `games`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `games` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `mode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `games`
--

LOCK TABLES `games` WRITE;
/*!40000 ALTER TABLE `games` DISABLE KEYS */;
INSERT INTO `games` VALUES (1,'Valorant','FPS','5v5'),(2,'League of Legends','MOBA','5v5'),(3,'CS2','FPS','5v5'),(4,'Dota 2','MOBA','5v5'),(5,'Fortnite','Battle Royale','Solo'),(6,'Apex Legends','Battle Royale','3v3'),(7,'Rocket League','Sports','3v3'),(8,'Rainbow Six Siege','FPS','5v5'),(9,'PUBG','Battle Royale','4v4'),(10,'Overwatch 2','FPS','5v5'),(12,'FIFA 24','Sports','1v1'),(13,'Street Fighter 6','Fighting','1v1'),(14,'Tekken 8','Fighting','1v1'),(15,'Starcraft II','Strategy','Solo'),(16,'Age of Empires IV','Strategy','1v1'),(17,'Hearthstone','Strategy','Solo'),(18,'Minecraft','Simulation','Team'),(19,'Fall Guys','Battle Royale','Solo'),(20,'Halo Infinite','FPS','4v4'),(21,'Mortal Kombat 1','Fighting','1v1'),(22,'Warzone','Battle Royale','4v4'),(23,'Smite','MOBA','5v5'),(24,'Fortnite Chapter 5','Battle Royale','Solo'),(25,'Call of Duty MW3','FPS','6v6'),(26,'Teamfight Tactics','Strategy','Solo'),(27,'Clash Royale','Strategy','1v1'),(28,'Mobile Legends','MOBA','5v5');
/*!40000 ALTER TABLE `games` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tournament_id` int NOT NULL,
  `team1_id` int NOT NULL,
  `team2_id` int NOT NULL,
  `winner_id` int DEFAULT '0',
  `team1_score` int DEFAULT '0',
  `team2_score` int DEFAULT '0',
  `match_date` date NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tournament_id` (`tournament_id`),
  KEY `team1_id` (`team1_id`),
  KEY `team2_id` (`team2_id`),
  CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`),
  CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`team1_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `matches_ibfk_3` FOREIGN KEY (`team2_id`) REFERENCES `teams` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matches`
--

LOCK TABLES `matches` WRITE;
/*!40000 ALTER TABLE `matches` DISABLE KEYS */;
INSERT INTO `matches` VALUES (8,1,1,2,1,13,8,'2026-03-05','FINISHED'),(9,1,3,4,3,16,11,'2026-03-06','FINISHED'),(10,1,1,4,1,14,9,'2026-03-10','FINISHED'),(11,2,3,5,3,16,14,'2026-03-12','FINISHED'),(12,2,5,6,6,11,16,'2026-03-13','FINISHED'),(13,2,3,6,3,16,10,'2026-03-15','FINISHED'),(16,4,7,8,7,16,12,'2026-04-20','FINISHED'),(17,4,7,8,8,13,16,'2026-04-25','FINISHED'),(28,1,1,2,1,16,10,'2026-04-01','FINISHED'),(34,2,1,3,1,16,13,'2026-03-20','FINISHED'),(35,1,1,2,2,13,16,'2026-04-05','FINISHED'),(36,1,3,4,3,16,9,'2026-04-05','FINISHED'),(37,1,5,6,5,16,11,'2026-04-06','FINISHED'),(38,1,7,8,8,9,16,'2026-04-06','FINISHED'),(39,1,1,3,1,16,14,'2026-04-12','FINISHED'),(40,1,5,7,7,14,16,'2026-04-12','FINISHED'),(41,1,1,5,1,16,10,'2026-04-20','FINISHED'),(42,4,1,2,2,14,16,'2026-04-16','FINISHED'),(43,4,3,4,3,16,12,'2026-04-16','FINISHED'),(44,4,5,6,6,11,16,'2026-04-17','FINISHED'),(45,4,7,8,4,16,8,'2026-04-17','FINISHED'),(46,4,2,3,3,13,16,'2026-04-25','FINISHED'),(47,4,6,7,6,16,11,'2026-04-25','FINISHED'),(48,4,3,7,3,16,14,'2026-05-10','FINISHED'),(49,28,1,2,2,13,16,'2026-05-02','FINISHED'),(50,28,3,4,3,16,9,'2026-05-02','FINISHED'),(51,28,5,6,6,11,16,'2026-05-03','FINISHED'),(52,28,7,8,8,16,12,'2026-05-03','FINISHED'),(53,28,1,4,1,16,11,'2026-05-08','FINISHED'),(54,28,5,8,8,14,16,'2026-05-08','FINISHED'),(55,28,1,5,1,16,13,'2026-05-14','FINISHED'),(56,29,2,3,2,16,14,'2026-05-11','FINISHED'),(57,29,4,5,5,9,16,'2026-05-11','FINISHED'),(58,29,6,7,6,16,11,'2026-05-12','FINISHED'),(59,29,1,8,8,13,16,'2026-05-12','FINISHED'),(60,29,3,4,3,16,14,'2026-05-16','FINISHED'),(61,29,1,7,1,16,10,'2026-05-16','FINISHED'),(62,29,1,4,4,14,16,'2026-05-19','FINISHED'),(63,30,1,3,1,16,11,'2026-05-06','FINISHED'),(64,30,2,4,4,9,16,'2026-05-06','FINISHED'),(65,30,5,7,5,16,13,'2026-05-07','FINISHED'),(66,30,6,8,8,11,16,'2026-05-07','FINISHED'),(67,30,3,2,3,16,12,'2026-05-12','FINISHED'),(68,30,7,6,6,14,16,'2026-05-12','FINISHED'),(69,30,3,6,3,16,9,'2026-05-17','FINISHED'),(70,31,1,4,1,16,11,'2026-06-02','FINISHED'),(71,31,2,3,3,13,16,'2026-06-02','FINISHED'),(72,31,5,8,8,11,16,'2026-06-03','FINISHED'),(73,31,6,7,6,16,14,'2026-06-03','FINISHED'),(74,31,1,2,0,0,0,'2026-06-10','PENDING'),(75,31,5,7,5,16,9,'2026-06-10','FINISHED'),(76,32,1,3,1,16,12,'2026-06-06','FINISHED'),(77,32,2,4,4,9,16,'2026-06-06','FINISHED'),(78,32,5,7,5,16,13,'2026-06-07','FINISHED'),(79,32,6,8,8,11,16,'2026-06-07','FINISHED'),(80,32,3,2,0,0,0,'2026-06-15','PENDING'),(81,32,5,6,0,0,0,'2026-06-15','PENDING'),(89,31,3,5,NULL,0,0,'2026-06-10','ONGOING'),(90,31,1,2,NULL,0,0,'2026-06-14','PENDING'),(91,31,3,6,NULL,0,0,'2026-06-18','PENDING'),(92,31,4,8,NULL,0,0,'2026-06-22','PENDING'),(93,32,3,5,NULL,0,0,'2026-06-10','ONGOING'),(94,32,2,6,NULL,0,0,'2026-06-15','PENDING'),(95,32,4,8,NULL,0,0,'2026-06-18','PENDING'),(96,32,1,3,NULL,0,0,'2026-06-22','PENDING'),(97,3,1,2,1,16,11,'2026-05-22','FINISHED'),(98,3,3,4,4,13,16,'2026-05-22','FINISHED'),(99,3,5,6,5,16,14,'2026-05-25','FINISHED'),(100,3,8,1,NULL,0,0,'2026-06-01','ONGOING'),(101,3,4,5,NULL,0,0,'2026-06-08','PENDING'),(102,3,2,8,NULL,0,0,'2026-06-15','PENDING'),(103,3,1,4,NULL,0,0,'2026-06-22','PENDING'),(104,33,1,2,NULL,0,0,'2026-07-02','PENDING'),(105,33,3,4,NULL,0,0,'2026-07-03','PENDING'),(106,33,5,6,NULL,0,0,'2026-07-05','PENDING'),(107,33,8,1,NULL,0,0,'2026-07-10','PENDING'),(108,34,2,5,NULL,0,0,'2026-07-11','PENDING'),(109,34,3,6,NULL,0,0,'2026-07-13','PENDING'),(110,34,1,4,NULL,0,0,'2026-07-18','PENDING');
/*!40000 ALTER TABLE `matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_members`
--

DROP TABLE IF EXISTS `team_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_members` (
  `id` int NOT NULL AUTO_INCREMENT,
  `team_id` int NOT NULL,
  `user_id` int NOT NULL,
  `join_date` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `team_id` (`team_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `team_members_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `team_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_members`
--

LOCK TABLES `team_members` WRITE;
/*!40000 ALTER TABLE `team_members` DISABLE KEYS */;
INSERT INTO `team_members` VALUES (1,1,2,'2026-03-01'),(2,1,3,'2026-03-01'),(3,1,4,'2026-03-02'),(4,2,5,'2026-03-03'),(5,2,6,'2026-03-03'),(6,3,7,'2026-03-04'),(7,3,8,'2026-03-04'),(8,4,9,'2026-03-05'),(9,4,10,'2026-03-05');
/*!40000 ALTER TABLE `team_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_requests`
--

DROP TABLE IF EXISTS `team_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_requests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `team_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `requested_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_at` timestamp NULL DEFAULT NULL,
  `reviewed_by` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_requests`
--

LOCK TABLES `team_requests` WRITE;
/*!40000 ALTER TABLE `team_requests` DISABLE KEYS */;
INSERT INTO `team_requests` VALUES (1,16,'Storm Breakers','REJECTED','2026-05-21 14:30:43','2026-05-21 14:32:11','coach1',NULL),(2,40,'Beta Wolves','REJECTED','2026-05-24 17:28:54','2026-05-24 17:32:06','admin',NULL),(3,40,'Shadow Corps','APPROVED','2026-05-24 17:32:32','2026-05-24 17:35:27','coach_shadow',NULL),(4,40,'Blaze United','PENDING','2026-05-24 18:09:16',NULL,NULL,NULL),(5,42,'Dragon Force','APPROVED','2026-05-25 16:49:59','2026-05-25 16:53:38','coach_dragon',NULL),(6,42,'Nova Esports','PENDING','2026-05-25 16:54:46',NULL,NULL,NULL),(7,44,'Alpha Squad','APPROVED','2026-05-26 15:54:20','2026-05-26 15:57:56','coach_alpha',NULL),(8,45,'Shadow Corps','REJECTED','2026-05-30 11:52:16','2026-05-30 14:26:10','admin',NULL),(9,45,'Shadow Corps','REJECTED','2026-05-30 14:26:32','2026-05-30 14:28:35','admin',NULL);
/*!40000 ALTER TABLE `team_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `game_id` int NOT NULL,
  `max_capacity` int NOT NULL DEFAULT '5',
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `game_id` (`game_id`),
  CONSTRAINT `teams_ibfk_1` FOREIGN KEY (`game_id`) REFERENCES `games` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` VALUES (1,'Phoenix Rising',1,5,'ACTIVE'),(2,'Nova Esports',1,5,'ACTIVE'),(3,'Dragon Force',2,5,'ACTIVE'),(4,'Alpha Squad',1,5,'ACTIVE'),(5,'Beta Wolves',1,5,'ACTIVE'),(6,'Omega Legion',2,5,'ACTIVE'),(7,'Shadow Corps',3,5,'INACTIVE'),(8,'Blaze United',8,5,'ACTIVE'),(9,'Storm Breakers',9,5,'INACTIVE'),(16,'new',18,3,'INACTIVE');
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tournament_teams`
--

DROP TABLE IF EXISTS `tournament_teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournament_teams` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tournament_id` int NOT NULL,
  `team_id` int NOT NULL,
  `registration_date` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tournament_id` (`tournament_id`),
  KEY `team_id` (`team_id`),
  CONSTRAINT `tournament_teams_ibfk_1` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`),
  CONSTRAINT `tournament_teams_ibfk_2` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tournament_teams`
--

LOCK TABLES `tournament_teams` WRITE;
/*!40000 ALTER TABLE `tournament_teams` DISABLE KEYS */;
INSERT INTO `tournament_teams` VALUES (7,1,1,'2026-03-01'),(8,1,2,'2026-03-02'),(9,1,3,'2026-03-03'),(10,1,4,'2026-03-04'),(11,2,3,'2026-02-20'),(12,2,5,'2026-02-21'),(13,2,6,'2026-02-22'),(14,3,1,'2026-04-15'),(15,3,2,'2026-04-16'),(16,4,7,'2026-04-01'),(17,4,8,'2026-04-02'),(18,5,1,'2026-03-25'),(19,5,2,'2026-03-26'),(20,5,3,'2026-03-27'),(21,5,4,'2026-03-28'),(22,4,2,'2026-04-07'),(23,7,1,'2026-04-13'),(24,8,1,'2026-04-13'),(25,8,2,'2026-04-13');
/*!40000 ALTER TABLE `tournament_teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tournaments`
--

DROP TABLE IF EXISTS `tournaments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tournaments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `game_id` int NOT NULL,
  `max_teams` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `game_id` (`game_id`),
  CONSTRAINT `tournaments_ibfk_1` FOREIGN KEY (`game_id`) REFERENCES `games` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tournaments`
--

LOCK TABLES `tournaments` WRITE;
/*!40000 ALTER TABLE `tournaments` DISABLE KEYS */;
INSERT INTO `tournaments` VALUES (1,'Valorant Spring Cup 2026',1,8,'2026-04-01','2026-04-30','FINISHED'),(2,'LoL Winter Championship 2026',2,8,'2026-03-01','2026-03-31','FINISHED'),(3,'Valorant Summer Cup 2026',1,4,'2026-05-20','2026-06-30','ONGOING'),(4,'CS2 Pro League Season 1',3,8,'2026-04-15','2026-05-15','FINISHED'),(5,'Valorant Masters Spring 2026',1,8,'2026-04-01','2026-04-30','UPCOMING'),(6,'Spring Cup',1,8,'2026-04-01','2026-04-30','UPCOMING'),(7,'Spring Test Cup',1,4,'2026-04-13','2026-04-20','FINISHED'),(8,'Spring Test Cup',1,4,'2026-04-13','2026-04-20','UPCOMING'),(9,'Spring Cup',1,8,'2026-04-01','2026-04-30','UPCOMING'),(10,'Spring Cup',1,8,'2026-04-01','2026-04-30','UPCOMING'),(23,'Test',1,8,'2023-04-14','2023-04-15','UPCOMING'),(24,'b',1,8,'2026-04-27','2026-04-28','UPCOMING'),(25,'y',1,8,'2026-04-27','2026-04-28','UPCOMING'),(26,'x',1,8,'2026-04-27','2026-05-28','UPCOMING'),(28,'Valorant May Clash 2026',1,8,'2026-05-01','2026-05-15','FINISHED'),(29,'CS2 Spring Invitational 2026',3,8,'2026-05-10','2026-05-20','FINISHED'),(30,'LoL May Cup 2026',2,8,'2026-05-05','2026-05-18','FINISHED'),(31,'Valorant June Series 2026',1,8,'2026-06-01','2026-06-30','ONGOING'),(32,'CS2 Summer Open 2026',3,8,'2026-06-05','2026-06-25','ONGOING'),(33,'LoL Summer Championship 2026',2,8,'2026-07-01','2026-07-20','UPCOMING'),(34,'Rocket League Cup 2026',7,4,'2026-07-10','2026-07-25','UPCOMING');
/*!40000 ALTER TABLE `tournaments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `temp_password` bit(1) DEFAULT NULL,
  `team_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','admin@esports.com','ADMIN','ACTIVE',NULL,NULL),(2,'ProGamer','cb1513ece93b4a593042a5c181ab2e123260f197a51a92b758c1697839067669','progamer@esports.com','MEMBER','ACTIVE',NULL,'Alpha Squad'),(3,'NightWolf','00adf4d902c3a37457f6dc47c67cb520c96a712b3ceb8e07ec5e43bdcb599d4e','nightwolf@esports.com','MEMBER','ACTIVE',NULL,'Nova Esports'),(4,'SnipeKing','f72e6b18ae714fbfbb92a127deb789a7896589d1c73aa5182d184a926f15f118','snipeking@esports.com','MEMBER','INACTIVE',NULL,'Nova Esports'),(5,'BladeRunner','265cba2d2e4efe668e66239bd71617c809852efb161e68df3fe045ab2085ab9e','bladerunner@esports.com','MEMBER','ACTIVE',NULL,'Dragon Force'),(6,'AceStrike','7b12399884cba97feed11e4275e0d170b9dc6538fc891d3bb008638ee0943a68','acestrike@esports.com','MEMBER','ACTIVE',NULL,NULL),(7,'IceBreaker','74a8ba93ea22c9141f56361b0b7288471c67ca09b83a5ad1ed700953906fe445','icebreaker@esports.com','MEMBER','INACTIVE',NULL,'Shadow Corps'),(8,'FireStorm','2258fc14a60d923b84a62ccf6e6e27133b16bad80149a940c5e9b222e5f44251','firestorm@esports.com','MEMBER','ACTIVE',NULL,'Blaze United'),(9,'ShadowStep','3b6babbd97efc128ed369be5af53d11fe16daecd5ba1475db59bab2e5c214d4d','shadowstep@esports.com','MEMBER','ACTIVE',NULL,'Blaze United'),(10,'GhostRider','87eb73b419778cc325ab99b54b894eaf993f222b46f9e3513de80c17913abc45','ghostrider@esports.com','MEMBER','INACTIVE',NULL,'Beta Wolves'),(11,'CyberWolf','44c9cb527f348080ae2646249b33b3a479feef4d285bd956cc2f55e2ef6fba86','cyberwolf@esports.com','MEMBER','ACTIVE',NULL,'Omega Legion'),(12,'beyza','2feeb74e63d6c9947c8144d94c13c4580521375d898e2721ec6423e5bdeda91f','beyza@esports.com','MEMBER','ACTIVE',NULL,NULL),(13,'meral','0bf68103388ed9027e51208f77d7b6b019e2b128d3f558e7ae3f079a27a487c0','meral@gmail.com','MEMBER','ACTIVE',NULL,NULL),(14,'coach1','e6b01cb48f28ac9774295380aa96454af033c53b167e148ab54a972f34af2148','coach1@esports.com','COACH','ACTIVE',_binary '\0','Phoenix Rising'),(15,'coach2','d4dffb181955d62f081e27912cc670bd856e918f8eee78b0e8e0f9ea20eab406','coach2@esports.com','COACH','ACTIVE',NULL,'Omega Legion'),(16,'testmember','6fec2a9601d5b3581c94f2150fc07fa3d6e45808079428354b868e412b76e6bb','testmember@esports.com','MEMBER','ACTIVE',_binary '\0',NULL),(17,'testuser','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','test@test.com','MEMBER','ACTIVE',_binary '\0',NULL),(28,'ProGamer99','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','progamer99@esports.com','MEMBER','ACTIVE',_binary '\0','Alpha Squad'),(29,'IceBreaker2','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','icebreaker2@esports.com','MEMBER','ACTIVE',_binary '\0','Shadow Corps'),(30,'ShadowStep2','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','shadowstep2@esports.com','MEMBER','ACTIVE',_binary '\0',NULL),(31,'GhostRider2','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','ghostrider2@esports.com','MEMBER','ACTIVE',_binary '\0','Beta Wolves'),(32,'NightHawk','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','nighthawk@esports.com','MEMBER','ACTIVE',_binary '\0','Phoenix Rising'),(33,'StarPlayer','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','starplayer@esports.com','MEMBER','ACTIVE',_binary '\0','Storm Breakers'),(34,'coach_alpha','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','coach.alpha@esports.com','COACH','ACTIVE',_binary '\0','Alpha Squad'),(35,'coach_nova','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','coach.nova@esports.com','COACH','ACTIVE',_binary '\0','Nova Esports'),(36,'coach_dragon','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','coach.dragon@esports.com','COACH','ACTIVE',_binary '\0','Dragon Force'),(37,'coach_shadow','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','coach.shadow@esports.com','COACH','ACTIVE',_binary '\0','Shadow Corps'),(38,'coach_blaze','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','coach.blaze@esports.com','COACH','ACTIVE',_binary '\0','Blaze United'),(40,'test1','ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae','test1@esports.com','MEMBER','ACTIVE',_binary '\0','Shadow Corps'),(42,'zeytin','6c55edba9ec0446e520dc4b5d8e7bdb94d2c8a805697d4d1c97ea495ece0095a','zeytin@hotmail.com','MEMBER','ACTIVE',_binary '','Alpha Squad'),(44,'deneme','8f2c7055352174207a44bf2fcaecec9adfae6c4dcb3ece70fbdb13b7af536d6f','deneme@esports.com','MEMBER','ACTIVE',_binary '\0',NULL),(45,'byz','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','byz@gmail.com','MEMBER','ACTIVE',_binary '\0',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-30 23:33:32
