# Analiza softverskog sistema pomoću Moose platforme



Ovaj repozitorijum sadrži projekat analize softverskog sistema korišćenjem platforme Moose i FAMIX metamodela.

Kao studija slučaja odabran je open-source Java projekat za upravljanje bibliotekom (Library Management System).


Cilj je istraživanje strukture i zavisnosti softverskog sistema kroz različite nivoe apstrakcije i verzija projekta pomoću Moose platforme i FAMIX modela.

## Alati
- Moose Suite 12 osnova
- VerveineJ 4.2.6 za generisanje FAMIX modela iz izvornog koda
- Pharo/Smalltalk za izvršavanje upita nad FAMIX entitetima

## Učitavanje modela
Uz VerveineJ generisan je JSON model `models/bookverse-model.json`

![Entiteti FAMIX modela](screenshots/entities.png)

Filtriranjem klasa prema svojstvu isStub izdvojeno je 67 "ne-stub" klasa uključujući unutrašnje i anonimne klase.


## Zavisnosti arhitekturnih slojeva
Prvi korak - ispitivanje poziva metoda paketa controller -> service -> dao

Primer upita za izdvajanje jednog poziva, izvršeno nad grupom Invocations.

![Pozivi kontrolera ka servisnom sloju](screenshots/controller-service-invocations.png)

U ovom momentu analize rezultat je u skladu sa očekivanom organizacijom aplikacije (controller->service->dao, bez uočenih direktnih poziva kontrolera ka metodama, kao ni povratnih poziva među slojevima)


## Analiza na nivou klasa i metoda
Ispitano je koje controller klase imaju najviše poziva ka service sloju.

![Broj poziva ka servisima po kontroleru](screenshots/controller-service-call-count.png)

`BookDetailController` ostvaruje 22 poziva ka 18 različitih servisnih metoda komunicirajući sa četiri servisne klase.

`BookManagementController` ostvaruje 21 poziv ka servisnim metodama raspoređenim u dve servisne klase.

Dakle, ova dva kontrolera imaju gotovo jednak broj poziva, ali različitu širinu zavisnosti. Broj poziva i broj različitih servisnih klasa predstavljaju različite aspekte povezanosti.


## Ispitivanje veličine kontrolera

U FAMIX modelu identifikovanao je 17 glavnih klasa čija imena sadrže Controller. Za njih je pronađeno ukupno 258 metoda.

![Raspodela metoda po kontrolerima](screenshots/controller-methods.png)

Tri najveća kontrolera (BookDetailController, BookManagmentController i AccountManagementController) sadrže ukupno 121 od 258 metoda, odnosno približno 47% metoda svih posmatranih kontrolera. Naravno, još uvek nije dokaz preopterećenosti ili otežane održivosti.

## Međuklasni pozivi i ciklične zavisnosti

Nakon analize slojeva i pojedinačnih kontrolora, istraživanje je prošireno na međuklasne pozive celog sistema. Cilj - utvrditi da li postoje klase koje se međusobno pozivaju, odnosno da li gras poziva metoda sadrži cikluse.

Uz skripte 01 i 02, nad grupom Invocations izdvojeni su jedinstveni usmereni parovi klasa. Pronađeno je 55 klasa u grafu i 161 jedinstvena usmerena međuklasna veza (Jedna veza predstavlja postojanje najmanje jednog poziva metode između dve različite klase).

Skriptom 03 pretraživani su parovi klasa sa dvosmernim pozivima. Pronađena su tri direktna ciklusa:

![Entiteti FAMIX modela](screenshots/arrays.png)

Sva tri ciklusa uključuju klasu RightPanelController, što je ukazalo na njenu centralnu ulogu u ovoj cikličnoj strukturi.

Uz skriptu 04 identifikovano je devet jedinstvenih parova poziva metoda. 

"Snažno povezane komponente" predstavljaju grupu klasa u kojima postoji usmerena putana između svih klasa međusobno. (skripta 05). 

Od 55 pronađenih klasa u grafu međuklasnih poziva, četiri pripadaju pronađenoj cikličnoj komponenti, dok preostalih 51 nisu deo višeklasne ciklične komponente.

Nakon identifikacije, detaljnije je analizirana klasa RightPanelController. Ova klasa ima najveći broj ulaznih veza među četiri posmatrana kontrolera. 

* Da li bi dodavanje novih ekrana "preopteretilo" ovu klasu? Da li bi bilo praktičnije dodavanje interfejsa?

Nije dokazan funkcionalni problem, ali identifikuju konkretan arhitekturni obrazac relevantan za kasnije održavanje, testiranje i proširivanje.. 

## Poređenje sa starijom verzijom

Bookverse repozitorijum ima istoriju od približno 300 commita. Radi uporednog pregleda i analize, pristupljeno je jednoj starijoj verziji koda (ne i zvaničnoj verziji aplikacije) - `git log --oneline --all`

`models/bookverse-old-model.json`

`models/bookverse-model.json`

...

### Beleške za dalje
- Da li u Bookverse aplikaciji postoje ciklične zavisnosti (klase/paketi) i kako su različite verzije projekta uticale na njih
- Grafovi zavisnosti i vizualizacija
- Identifikacija "preopterećenih" delova koda i promene kroz verzije (kako su nove funkcionalnosti uticale na povećanje složenosti ili zavisnosti)
- ..

### Izvori
- https://fuhrmanator.github.io/posts/typescript-in-moose/index.html
- https://github.com/SAP2Moose/SAP2Moose
- https://fuhrmanator.github.io/posts/analyzing-java-with-moose/index.html
- https://modularmoose.org/blog/2022-08-08-moosecritics/
- https://modularmoose.org/users/moose-ide/moose-critics/
- https://arxiv.org/pdf/2509.11748
- 
