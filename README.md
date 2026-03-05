# Anime Recommender

Autor: Milena Nikolić
Predmet: Alati i metode veštačke inteligencije i softverskog inženjerstva
Studijski program: Softversko inženjerstvo i veštačka inteligencija 

## O projektu

Anime Recommender je web aplikacija koja je napisana u programskom jeziku Clojure koja korisnicima omogućava pretragu anime serija i dobijanja preporuka na osnovu žanrova, studija, ili animea koji su pogledali.

Aplikacija je povezana pomoću client id-ja sa My Anime List (MAL) API-jem kako bi se automatski preuzimale sve informacije potrebne za anime: Naziv, slika, opis, rating, žanrovi, studio.

## Tehnologije

- Clojure - funkcionalni programski jezik koji se izvršava na JVM-u
- Leiningen - build alat za Clojure projekte
- Ring - biblioteka za HTTP server
- Compojure - biblioteka za rutiranje HTTP zahteva
- clj-http - biblioteka za HTTP klijentske zahteve
- Cheshire - biblioteka za JSON serijalizaciju
- MyAnimeList API - izvor podataka o anime serijalima
- HTML/CSS/JS - frontend

## Struktura projekta

src/anime-recommender/
---- core.clj - web server, rute, HTML frontend
---- db.clj - baza podataka (unutar nje ima par ID-jeva sa MAL API-ja, ali sam ID se može i na web ručno uneti)
---- api.clj - komunikacija sa MyAnimeList API-jem
---- recommender.clj - funkcije za pretragu i preporuku
---- config.clj - konfiguracija (client api ključ)

## Pokretanje aplikacije

### Preduslovi
- Java JDK 11 ili noviji
- Leiningen

### GIT
https://github.com/Milena-hue/anime-recommender

## Funkcionalnosti

### 1. Pretraga po žanru
Korisnik unosi naziv žanra (na primer: Action) i aplikacija prikazuje sve anime iz baze koji se podudaraju sa tim žanrom (želela sam da uradim da može dva žanra, ali nije radilo).

### 2. Pretraga po studiju
Korisnik unosi naziv studija (na primer: MAPPA) i aplikacija prikazuje sve anime iz baze koji su rađeni od strane tog studija.

### 3. Preporuka na osnovu animea
Korisnik unosi naziv animea koji je gledao i aplikacija preporučuje top 3 najsličnija animea koja odgovaraju unetom.

### 4. Preuzimanje anime sa MAL-a
Korisnik unosi MAL ID animea i aplikacija preuzima direktno sve podatke sa MyAnimeList API-ja u realnom vremenu (cilj je bio da može da se radi unakrsno poređenje animea iz baze i animea sa API-ja)

## Recommendation algoritam

Centralni deo aplikacije. Algoritam koristi content-based filtering - poređenje karakteristika anime serijala.

### Kako radi

Algoritam se sastoji iz dve funckije:

#### `count-matching-genres`
```
(defn count-matching-genres
  "Prima dva animea, vraća broj zajedničkih žanrova"
  [anime1 anime2]
  (count (filter (fn [genre]
                   (some #(= % genre) (:genres anime2)))
                 (:genres anime1))))
```

Ova funkcija prima dva animea i broji koliko im se žanrova poklapa.

- `(:genres anime1)` - izvlači listu žanrova prvog animea (na primer `["Action" "Fantasy"]`)
- `(filter ...)` - zadržava samo žanrove koji se pojavljuju i u drugom animeu
- `(some #(= % genre) (:genres anime2))` - proverava da li žanr postoji u listi drugog animea
- `(count ...)` - broji koliko žanrova se poklopilo

#### `recommend`

```
(defn recommend
  "Prima naziv animea koji si gledao, vraća top 3 slična"
  [title]
  (let [watched (first (filter #(= (:title %) title) anime-db))]
    (if (nil? watched)
      []
      (->> anime-db
           (filter #(not= (:title %) title))
           (sort-by #(count-matching-genres watched %) >)
           (take 3)))))
```

- `(filter #(= (:title %) title) anime-db)` - pronađi anime koji je korisnik uneo
- `(if (nil? watched) [])` - ako anime nije u bazi, vrati praznu listu
- `->>` - threading macro iz knjige Clojure for the Brave and True, Poglavlje 5. Prosleđuje rezultat kroz svaki korak kao kroz cev.
- `(filter #(not= (:title %) title))` - ukloni gledani anime iz liste
- `(sort-by #(count-matching-genres watched %) >)` - sortiraj po broju poklapanja žanrova, od najvećeg
- `(take 3)` - uzmi samo prva 3 rezultata

### Referenta tačka: knjiga - Clojure for the Brave and True

- | `def`, `defn` | Poglavlje 3 | Definisanje baze i svih funkcija |
- | Mape i vektori | Poglavlje 3 | Struktura podataka za svaki anime |
- | Keywords (`:title`, `:genres`) | Poglavlje 3 | Ključevi u anime mapama |
- | `filter`, `map`, `some` | Poglavlje 4 | Pretraga i filtriranje baze |
- | Anonimne funkcije `fn` i `#()` | Poglavlje 3 | Korišćene u svim filter/map pozivima |
- | `let` | Poglavlje 3 | Čuvanje privremenih vrednosti |
- | Threading macro `->>`  | Poglavlje 5 | Recommendation pipeline |
- | `loop/recur` | Poglavlje 5 | CLI meni |


## MAL API Integracija

Aplikacija koristi MyAnimeList API v2 za preuzimanje podataka

```
(defn get-anime-by-id
  "Prima ID animea sa MAL-a, vraća osnovne podatke"
  [mal-id]
  (let [url (str "https://api.myanimelist.net/v2/anime/" mal-id)
        response (http/get url
                           {:headers {"X-MAL-CLIENT-ID" mal-client-id}
                            :query-params {"fields" "id,title,genres,studios,mean,synopsis,main_picture"}
                            :as :json})]
    (let [body (:body response)]
      {:id        (:id body)
       :title     (:title body)
       :genres    (mapv #(:name %) (:genres body))
       :studio    (:name (first (:studios body)))
       :rating    (:mean body)
       :image     (get-in body [:main_picture :medium])
       :description (:synopsis body)})))
```

- Zahtev se šalje sa `X-MAL-CLIENT-ID` headerom kao autentifikacija
- `fields` parametar specificira koje podatke želimo
- `(mapv #(:name %) (:genres body))` - genres dolaze kao lista mapa, pa izvlačimo samo imena
- `(get-in body [:main_picture :medium])` - izvlači URL slike iz ugnježdene mape
