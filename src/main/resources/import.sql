-- CATEGORY
INSERT INTO CATEGORY (id, name) VALUES (1000, 'Romance');
INSERT INTO CATEGORY (id, name) VALUES (1001, 'Arts & Photography');
INSERT INTO CATEGORY (id, name) VALUES (1002, 'Biographies & Memoirs');
INSERT INTO CATEGORY (id, name) VALUES (1003, 'Business & Money');
INSERT INTO CATEGORY (id, name) VALUES (1004, 'Comics & Graphic Novels');
INSERT INTO CATEGORY (id, name) VALUES (1005, 'Computers & Technology');
INSERT INTO CATEGORY (id, name) VALUES (1006, 'Cookbooks, Food & Wine');
INSERT INTO CATEGORY (id, name) VALUES (1007, 'Engineering & Transportation');
INSERT INTO CATEGORY (id, name) VALUES (1008, 'Health, Fitness & Dieting');
INSERT INTO CATEGORY (id, name) VALUES (1009, 'Literature & Fiction');
INSERT INTO CATEGORY (id, name) VALUES (1010, 'Mystery, Thriller & Suspense');
INSERT INTO CATEGORY (id, name) VALUES (1011, 'Science & Math');
INSERT INTO CATEGORY (id, name) VALUES (1012, 'Science Fiction & Fantasy');

-- GENRE
INSERT INTO GENRE (id, name) VALUES (1000, 'Alternative Punk');
INSERT INTO GENRE (id, name) VALUES (1001, 'Alternative Rock');
INSERT INTO GENRE (id, name) VALUES (1002, 'Blues');
INSERT INTO GENRE (id, name) VALUES (1003, 'Classical Music');
INSERT INTO GENRE (id, name) VALUES (1004, 'Classic Rock');
INSERT INTO GENRE (id, name) VALUES (1005, 'Country');
INSERT INTO GENRE (id, name) VALUES (1006, 'Indie Music');
INSERT INTO GENRE (id, name) VALUES (1007, 'Jazz');
INSERT INTO GENRE (id, name) VALUES (1008, 'Latin Music');
INSERT INTO GENRE (id, name) VALUES (1009, 'Pop');
INSERT INTO GENRE (id, name) VALUES (1010, 'R&B');
INSERT INTO GENRE (id, name) VALUES (1011, 'Reggae');
INSERT INTO GENRE (id, name) VALUES (1012, 'Rock');
INSERT INTO GENRE (id, name) VALUES (1013, 'Psychedelic');
INSERT INTO GENRE (id, name) VALUES (1014, 'World Music');

-- LABEL
INSERT INTO LABEL (id, name) VALUES (1000, 'ABC Records');
INSERT INTO LABEL (id, name) VALUES (1001, 'Island');
INSERT INTO LABEL (id, name) VALUES (1002, 'EMI');
INSERT INTO LABEL (id, name) VALUES (1003, 'Sony');
INSERT INTO LABEL (id, name) VALUES (1004, 'BMG');
INSERT INTO LABEL (id, name) VALUES (1005, 'Universal Music Group');
INSERT INTO LABEL (id, name) VALUES (1006, 'PolyGram');
INSERT INTO LABEL (id, name) VALUES (1007, 'Apple');
INSERT INTO LABEL (id, name) VALUES (1008, 'Parlophone');

-- PUBLISHER
INSERT INTO PUBLISHER (id, name) VALUES (1000, 'Ablex Publishing');
INSERT INTO PUBLISHER (id, name) VALUES (1001, 'Pearson');
INSERT INTO PUBLISHER (id, name) VALUES (1002, 'Hachette Livre');
INSERT INTO PUBLISHER (id, name) VALUES (1003, 'Wiley');
INSERT INTO PUBLISHER (id, name) VALUES (1004, 'Oxford University Press');
INSERT INTO PUBLISHER (id, name) VALUES (1005, 'Harlequin');
INSERT INTO PUBLISHER (id, name) VALUES (1006, 'Flammarion');
INSERT INTO PUBLISHER (id, name) VALUES (1007, 'Groupe Gallimard');
INSERT INTO PUBLISHER (id, name) VALUES (1008, 'APress');
INSERT INTO PUBLISHER (id, name) VALUES (1009, 'O Reilly');
INSERT INTO PUBLISHER (id, name) VALUES (1010, 'Pottermore');

-- AUTHOR
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1000, 'Antoine', 'Sabot-Durand', 'FR', 'IT consultant since 1996 and Senior software Engineer at Red Hat since 2013, Antoine is CDI co-spec lead and in charge of CDI eco-system development and advocacy. He''s also tech lea on Agorava, a CDI framework helping social media consuming.');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1001, 'Nigel', 'Deakin', 'EN', 'Nigel Deakin is specification lead for JMS 2.0 (JSR 343) and a principal member of technical staff at Oracle.');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1002, 'Fred', 'Rowe', 'EN', 'Fred is the WebSphere Architect for a number of Application Server components including those responsible for JEE managed concurrency, JCA (Java Connector Architecture), and database connectivity and connection management. Fred has worked at IBM for 10...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1003, 'Kinman', 'Chung', 'EN', 'Spec lead for JSR 341, EL 3.0. Formerly a Tomcat committer. Currently a developer in Glassfish, working on web container, JSP and EL.');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language) VALUES (1004, 'Linda', 'Demichiel', 'EN');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1005, 'Arun', 'Gupta', 'EN', 'Arun Gupta is a Java evangelist working at Oracle. He works to create and foster the community around Java EE, GlassFish, and WebLogic. He has been with the Java EE team since its inception and contributed to all releases. Arun has extensive world wide...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1006, 'Lincoln', 'Baxter III', 'EN', 'Lincoln Baxter, III is a Research Engineer at Red Hat, working on JBoss open-source projects; most notably as project lead for JBoss Forge.');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1007, 'Ed', 'Burns', 'EN', 'Ed Burns is a Senior Staff Engineer for Oracle Corporation where he chairs the JSR 344 (JSF 2.2) Expert Group and contributes to the open source Mojarra project. He is the author of two books published by McGraw-Hill: Secrets of the Rock Star Programme...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1008, 'Roger', 'Kitain', 'EN', 'Roger Kitain is an Engineer at Oracle Corporation where he spent the last 15 years working with web technologies. He started working on JavaServer Faces in 2001 as a member of the implementation team and has co-led the specification for JSF since 1.1, ...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1009, 'Antonio', 'Goncalves', 'PT', 'Antonio is a senior developer specialized in Java/Java EE. As a consultant he advises customers and helps them in defining and developing their software architecture. This Java Champion is also the founder of the very successful Paris JUG, Devoxx Franc...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1010, 'Adam', 'Bien', 'GM', 'Consultant and author Adam Bien is an Expert Group member for the Java EE 6 / 7, EJB 3.X, JAX-RS and JPA 2.X JSRs. He has worked with Java technology since JDK 1.0 and with Servlets/EJB 1.0 and is now an architect and developer for Java SE and Java EE ...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1011, 'Emmanuel', 'Bernard', 'FR', 'Emmanuel Bernard is data platform architect at JBoss by Red Hat where he oversees data projects of the Red Hat''s middleware and is member of the Hibernate team. Emmanuel has spent a few years in the retail industry as developer and architect where he ...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1012, 'Jitendra', 'Kotamraju', 'EN', 'Jitendra Kotamraju is a software engineer at Oracle. He has been contributing to the many Java EE technologies and GlassFish projects for the last 8 years. He was the specification and implementation lead of JAX-WS 2.2. At present, he is the specificat...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1013, 'Mike', 'Keith', 'EN', 'Mike Keith has been an enterprise, distributed systems and persistence expert for over 20 years. He is a Java and Enterprise Architect at Oracle and contributes to Java EE and many of the subspecifications that make up the enterprise Java portfolio. He...');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1014, 'Anthony', 'Lai', 'EN', 'Anthony Lai is a principal member of technical staff at Oracle. He is the specification lead for JSR 236 - Concurrency Utilities for Java EE 1.0, and was previously a member of the J2EE Connector Architecture 1.5 specification (JSR 112) expert group.');
INSERT INTO AUTHOR (id, first_name, last_name, preferred_language, bio) VALUES (1015, 'J. K.', 'Rowling', 'EN', 'J K (Joanne Kathleen) Rowling was born in the summer of 1965 at Yate General Hospital in England and grew up in Chepstow, Gwent where she went to Wyedean Comprehensive. Jo left Chepstow for Exeter University, where she earned a French and Classics degree, and where her course included one year in Paris');
