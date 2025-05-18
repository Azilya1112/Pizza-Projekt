<h1 align="center">
  <br>
  <a href="#"><img src="https://img.pikbest.com/origin/09/19/19/80spIkbEsT8H4.png!sw800" alt="Pizza Project" width="200"></a>
  <br>
  Pizza Projekt
  <br>
</h1>

<h4 align="center">PizzaProject — eine Anwendung für die Verwaltung eines Online-Pizza-Bestellservices, entwickelt mit 
Spring Boot, HTML und Bootstrap. Das Projekt umfasst grundlegende Funktionen für Bestellungen, Kunden und Lieferadressen mit 
der Möglichkeit zur späteren Erweiterung.</h4>

<p align="center">
  <a href="#teammitglieder">Teammitglieder</a> •
  <a href="#Wochenübersicht Arbeit">Wochenübersicht Arbeit</a> •
  <a href="#Probleme und offene Fragen"> Probleme und offene Fragen</a> •
  <a href="#Nächste Schritte">Nächste Schritte</a> 
</p>

## Teammitglieder

- **Akylbek uulu Jusup**
- **Munira Satanova**
- **Azilia Adylgazieva**


## Wochenübersicht Arbeit

| Woche                                 | Fortschritt                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | Beitrag der Teammitglieder                                                                                                                                                                                                                                                                                                                                                                                   |
|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Woche 4<br/>von 25.11.24 bis 3.12.24  | 1. Verbesserung der UX und Funktionalität des Warenkorbs:<br/> Optimierung der Warenkorbfunktionalität unter Berücksichtigung der Benutzeranmeldung.<br/> Überarbeitung des Designs und der Benutzeroberfläche für den Warenkorb und die Authentifizierungsseiten.<br/> Implementierung der dynamischen Aktualisierung von Daten und Verbesserung der Navigation.<br/>2.Funktionalität zum Hinzufügen von Pizza zum Warenkorb implementiert.<br/>3.Sicherheitskonfiguration für die Anzeige des Warenkorbinhalts eingerichtet. | 1. Akylber uulu Jusup: In Entwicklung, um die Kaufhistorie des Nutzers zu verbessern<br/>2. Munira Satanova: PizzaController, OrderService, SecurityConfig<br/>3 Azilia Adylgazieva: UX/templates, CartController,CustomAuthenticationSuccessHandler                                                                                                                                                         |
| Woche 3<br/>von 18.11.24 bis 24.11.24 | 1. Login-Mechanismus mit festen Zugangsdaten für Benutzer 'bnutz' und 'cnutz' integriert<br/>2. Tabellen durch Bootstrap Cards ersetzt<br/>3. Pizzatabelle erstellt, dynamische Anzeige der Preise konfiguriertPizza, PizzaController, LoginController, Item <br/>4. Shopping-Historie: Tabelle und Detailansicht in HTML und Controller erstellt <br/>5. OrderDTO: Datenabruf und Service-Funktionen implementiert<br/>6. Die SpringBoot-Konfiguration ist eingerichtet<br/>7. Registrierung und Autorisierung der Benutzer   | 1. Akylbek uulu Jusup: ShoppingHistoryController/HTML, OrderDTO/Service.<br/>2. Munira Satanova: PizzaController, LoginController, Item, Tabellen durch Bootstrap Cards ersetzt, Login-Mechanismus mit festen Zugangsdaten für Benutzer 'bnutz' und 'cnutz' integriert.<br/>3. Azilia Adylgazieva: User Entity, UserService, Cart, SecutityConfig, CurrentUser, CurrentUserProvider, RegistrationContrroller |
| Woche 2<br/>von 11.11.24 bis 17.11.24 | 1. Erstellen von relationalen Beziehungen<br>2. Erstellen einer Weboberfläche für die Hauptseite<br/>3. Hinzufügen einer Einkaufswagenseite und Verknüpfung der Übergänge im Controller<br/>4. Anmelde- und Registrierungsseite hinzugefügt und Übergänge im Controller verknüpft                                                                                                                                                                                                                                              | 1. Akylbek uulu Jusup: Erstellen von relationalen Beziehungen<br>2. Munira Satanova: Warenkorb screen, Loggen/Registrieren screen<br>3. Azilia Adylgazieva: Main screen                                                                                                                                                                                                                                      |
| Woche 1<br/>von 4.11.24 bis 10.11.24  | 1. Erstellen einer Projektstruktur<br>2. Erstellen einer Entity                                                                                                                                                                                                                                                                                                                                                                                                                                                                | 1. Azilia Adylgazieva: Erstellung der Projektstruktur<br>2. Munira Satanova: Erstellung Entity                                                                                                                                                                                                                                                                                                               |

## Probleme und offene Fragen

- **Probleme und offene Fragen**:

Woche 4

1. Das Problem war ein Konflikt zwischen SpringSecurity und der Dateninitialisierung 
2. Übertragung des Warenkorbs von der Sitzung zur Datenbank für einen nicht autorisierten Benutzer während der Registrierung
3. Konflikte in Github beim Übertragen von Änderungen

Woche 3

1. Es gab ein Problem bei der Organisation der früheren Bestellungen des Benutzers und des Warenkorbs. 
2. Es gab Probleme mit der falschen Anzeige von IDs in thymeleaf.
3. Es gab Probleme mit der SpringSecurity-Konfiguration

Woche 2

1. Repositories sind noch nicht erstellt
2. Die Seitenübergänge sind noch nicht verbunden
    

---

## Nächste Schritte

Woche 5

1. UX/UI verbessern
2. Die Schaltfläche „Entfernen“ im Warenkorb sollte funktionieren.
3. Ändern Sie die Menge oder Größe der Pizza, nachdem Sie sie in den Warenkorb gelegt haben.
4. Die Ordnerorganisation und die Codestruktur sollten neu gestaltet werden
5. Benutzer-Kaufhistorie
6. Validierung anpassen
7. Eine Seite für Admin hinzufügen
8. Funktionalität für Admin erstellen

Woche 3 und Woche 4

1. Überarbeitung der Web-Oberfläche
2. Arbeiten an der Projektfunktionalität
3. Kunden verwalten
4. Korrektur der Sicherheit
