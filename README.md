# Paskirtis
Šios aplikacijos tikslas yra sukurti paprastą demonstracinę bibliotekos valdymo sistemą.

# Funkcionalumas
Aplikacija vartotojui leidžia pridėti knygas ir žurnalus, juos peržiūrėti, kopijuoti ir trinti. Taip pat knygos gali būti skolinamos ir grąžinamos. Vartotojas gali peržiūrėti knygos skolinimo istoriją, datas ir sužinoti, ar knyga yra laisva. Aplikacija taip pat leidžia vartotojui peržiūrėti visų knygų ir žurnalų sąrašą.

# Paleidimas
Aplikacijai reikalingas JDK ir JavaFX SDK. JavaFX SDK galima laikyti bet kurioje
kompiuterio vietoje, tačiau kelias iki jo kiekviename kompiuteryje gali skirtis.

## JavaFX paruošimas

1. Įdiekite JDK.
2. Atsisiųskite JavaFX SDK 21 iš
   [openjfx.io](https://openjfx.io/).
3. Išskleiskite SDK ir į komandas įrašykite kelią iki jo `lib` katalogo.

Pavyzdžiui, jei JavaFX SDK yra `C:\Other\java\javafx-sdk-21.0.7`, programa
kompiliuojama ir paleidžiama taip:

```bash
javac --module-path "C:\Other\java\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -d bin src\App.java src\abstracts\*.java src\datatypes\*.java src\exceptions\*.java src\factories\*.java src\gui\*.java src\interfaces\*.java src\publications\*.java
java --module-path "C:\Other\java\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -cp bin App
```

Jei JavaFX SDK laikomas kitoje vietoje, pakeiskite kelią abiejose komandose.

# Pagrindinės klasės
- `abstracts/Publication`: Pagrindinė klasė, kuri apibrėžia bendrus atributus ir metodus publikacijoms.
- `publications/Book`: Knygos klasė.
- `publications/Journal`: Žurnalo klasė.
- `datatypes/BorrowRecord`: Klasė, kuri saugo leidinio skolinimo informaciją ir istoriją.

Taip pat ir kitos klasės, kurios šiuo metu nėra plačiai naudojamos.

## Projektavimo šablonai
- **Factory Method**: Naudojamas kuriant publikacijas (knygas ir žurnalus) per `PublicationFactory` klasę.

# Plėtimo galimybės
- Galima pridėti daugiau publikacijų tipų.
- Galima pridėti vartotojų valdymą, kad būtų galima sekti, kas skolina knygas.
- Galima pridėti paieškos funkcionalumą.
- Galima pridėti GUI publikacijos duomenų redagavimą.
- Galima pridėti automatinį duomenų išsaugojimą ir atkūrimą.
- Galima pridėti kitus GUI patobulinimus, kad būtų lengviau naudotis aplikacija.
- Galima pridėti masinį ar šabloninį publikacijų kūrimą naudojant `PublicationFactory`.

# Klasių diagramos

Peržiūrėkite [aprasymas.pdf](aprasymas.pdf) failą, kad pamatytumėte klases.