\# Analiza softverskog sistema pomoću Moose platforme



Ovaj repozitorijum sadrži projekat analize softverskog sistema korišćenjem platforme Moose i FAMIX metamodela.



Kao studija slučaja odabran je open-source Java projekat za upravljanje bibliotekom (Library Management System).



Cilj je da se na realnom primeru istraži proces ekstrakcije i analize strukture softverskog sistema pomoću Moose platforme.



\## Proces analize



Java izvorni kod → VerveineJ → FAMIX JSON model → Moose 12 → analiza



VerveineJ se koristi za ekstrakciju informacija iz Java izvornog koda i njihovo predstavljanje pomoću FAMIX-a. Generisani model se zatim uvozi u Moose, gde se mogu istraživati klase, metode, atributi veze između elemenata sistema.



\## Struktura repozitorijuma



\- models – generisani FAMIX modeli;

\- scripts – skripte korišćene za analizu u Moose-u;

\- screenshots – rezultati;

\- research – beleške o istraživanju i korišćenim pristupima;

\- example – lokalna kopija analiziranog open-source projekta





\## Nakon uvoza FAMIX modela, Moose je identifikovao ukupno 32 klase. 

Model, pored klasa implementiranih u analiziranom projektu, sadrži i 

spoljne Java klase koje sistem koristi.



Za izdvajanje klasa koje pripadaju samom analiziranom sistemu korišćeno je - 

self reject: \[ :each | each isStub ]



!\[UML prikaz analiziranog sistema](screenshots/library-system-uml.png)

