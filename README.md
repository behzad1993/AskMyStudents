
#AskMyStudents

Behzad Karimi 

###Anforderung
Der Dozent moechte fuer seine Vorlesung einen Quiz und alle Studenten darin Teilnehmen lassen. Es soll fuer alle 
Teilnehmer die Moeglichkeit geben anonym teilzunehmen. Es sollte moeglich sein verschieden Arten von Fragen zu 
erstellen: Single-, Multiple-Choice (weitere Arten kann man leicht implementieren. Parent Class ist Question.java). Es soll eine Auswertung nach jeder Frage geben und eine gesamt 
Statistik gespeichert werden. Waehrend der Vorlesung soll den Teilnehmern die Moeglichkeit gegeben werden Feedback 
abzugeben. 
Eine Mail soll an den Dozenten abgeschickt werden um seinen Account zu veriﬁzieren.

###Features
- [x] **Groß:** Fragestellungen

- [x] **Mittel:** Feedbacks live abgeben

- [ ] **Klein:** Statistikerstellung

#####Email Verification
Der Email Server ist ein Heroku Add-On namens sendgrid. Es kann also mehrere Minuten dauern bis man nach der 
Registrierung eine Email erhaelt.



---
## Tutorials

### Application
[AskMyStudents](https://askmystudents.herokuapp.com)

User zum einloggen:  
**username::password** 

- user@user.com::user     
- teacher@teacher.com::admin

Es koennen beide user genutzt werden. Die Datenbank ist einfach wiederherzustellen, es kann also rumgespielt werden.
Registrieren mit eigener Mail Adresse ist implementiert.

#### How to deploy jar file to Heroku
1. Login to heroku in command line: `heroku login`
2. Create app using command: `heroku apps:create heroku open -a askmystudents`
3. Deploy war file using this command: `heroku deploy:jar classroom/target/classroom-0.1-SNAPSHOT.jar --app askmystudents`
4. Show logs `heroku logs --tail --app askmystudents`
5. Open app using command: `heroku open -a askmystudents`

Reference: [Heroku deploy Was/Jar](https://www.callicoder.com/deploy-host-spring-boot-apps-on-heroku/) 

---
### What to do 
- [x] Login
    - [ ] using csrf
- [x] [register with email and get email verification1](https://www.baeldung.com/registration-with-spring-mvc-and-spring-security) 
- [x] Getting all Entities from logged in user
    - [x] courses
    - [x] lessons
    - [x] quizzes
    - [x] questions
    - [x] answers
    - [x] live feedbacks
- [x] Editing all Entities from logged in user
    - [x] courses
    - [x] lessons
    - [x] quizzes
    - [x] questions
    - [x] answers
    - [x] live feedbacks
- [x] creating entity for logged in user
    - [x] courses
    - [x] lessons
    - [x] quizzes
    - [x] questions
    - [x] answers
    - [x] live feedbacks 


### TODO bzw. Fehler
- [x] list Ansichten: die Punkte links kommen nur einmal vor, sollten aber bei jedem Element kommen
- [ ] bisher nur eine Sprache, bzw. mal Englisch mal Deutsch. 
- [x] Delete funktion bisher nur fuer Answer erstellt.
- [x] Dropdowns in edit und create ausbauen, bisher sind es nur veranschaulichungen was damit gemacht werden koennte.
- [x] GROSSES FEATURE
- [x] MITTLERES FEATURE
- [ ] KLEINES FEATURE 
- [x] Allgemein Ansichten verbessern
- [x] Registrierung mit Email senden ermoeglichen
- [ ] Register controller aufraeumen
- [ ] Quizstart Ansicht
- [x] Studenten Ansicht
- [x] Studenten Login
- [x] Studenten live Feedback Abgabe
- [ ] Zwischenschicht an Model einbauen