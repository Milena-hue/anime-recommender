# Anime Recommender

**Author:** Milena Nikolić  
**Course:** Tools and Methods of Artificial Intelligence and Software Engineering  
**Study Program:** Software Engineering and Artificial Intelligence  


## About the Project

Anime Recommender is a web application written in Clojure that allows users to search for anime series and receive recommendations based on genres, studios, or anime they have already watched.

The application is connected via Client ID to the MyAnimeList (MAL) API in order to automatically fetch all necessary anime information: Title, image, description, rating, genres, studio.


## Technologies

- Clojure - functional programming language running on the JVM
- Leiningen - build tool for Clojure projects
- Ring - HTTP server library
- Compojure - HTTP request routing library
- clj-http - HTTP client library
- Cheshire - JSON serialization library
- MyAnimeList API - data source for anime series
- HTML/CSS/JS - frontend


## Project Structure
```
src/anime-recommender/
---- core.clj - web server, routes, HTML frontend
---- db.clj - database (contains a few IDs from the MAL API, but the ID can also be entered manually on the web)
---- api.clj - MyAnimeList API communication
---- recommender.clj - search and recommendation functions
---- config.clj - configuration (client API key)
```


## Running the Application

### Prerequisites
- Java JDK 11 or newer
- Leiningen

### GIT
https://github.com/Milena-hue/anime-recommender


## Features

### 1. Search by Genre
The user enters a genre name (for example: Action) and the application displays all anime from the database that match that genre (I wanted to implement support for two genres at once, but it did not work).

### 2. Search by Studio
The user enters a studio name (for example: MAPPA) and the application displays all anime from the database produced by that studio.

### 3. Recommendation Based on Anime
The user enters the title of an anime they have watched and the application recommends the top 3 most similar anime.

### 4. Fetch Anime from MAL
The user enters a MAL ID and the application fetches all data directly from the MyAnimeList API in real time (the goal was to enable cross-comparison between anime from the database and anime fetched from the API).


## Recommendation Algorithm

The core of the application. The algorithm uses content-based filtering - comparing characteristics of anime series.

### How It Works

The algorithm consists of two functions:

#### `count-matching-genres`
```clojure
(defn count-matching-genres
  "Takes two anime, returns the number of shared genres"
  [anime1 anime2]
  (count (filter (fn [genre]
                   (some #(= % genre) (:genres anime2)))
                 (:genres anime1))))
```

This function takes two anime and counts how many genres they share.

- `(:genres anime1)` - extracts the genre list of the first anime (e.g. `["Action" "Fantasy"]`)
- `(filter ...)` - keeps only genres that also appear in the second anime
- `(some #(= % genre) (:genres anime2))` - checks if a genre exists in the second anime's list
- `(count ...)` - counts how many genres matched

#### `recommend`
```clojure
(defn recommend
  "Takes the title of a watched anime, returns top 3 similar ones"
  [title]
  (let [watched (first (filter #(= (:title %) title) anime-db))]
    (if (nil? watched)
      []
      (->> anime-db
           (filter #(not= (:title %) title))
           (sort-by #(count-matching-genres watched %) >)
           (take 3)))))
```

- `(filter #(= (:title %) title) anime-db)` - find the anime the user entered
- `(if (nil? watched) [])` - if the anime is not in the database, return an empty list
- `->>` - threading macro from the book Clojure for the Brave and True, Chapter 5. Passes the result through each step like a pipeline.
- `(filter #(not= (:title %) title))` - remove the watched anime from the list
- `(sort-by #(count-matching-genres watched %) >)` - sort by number of matching genres, descending
- `(take 3)` - take only the top 3 results

### Reference: Clojure for the Brave and True

- | `def`, `defn` | Chapter 3 | Defining the database and all functions |
- | Maps and vectors | Chapter 3 | Data structure for each anime |
- | Keywords (`:title`, `:genres`) | Chapter 3 | Keys in anime maps |
- | `filter`, `map`, `some` | Chapter 4 | Searching and filtering the database |
- | Anonymous functions `fn` and `#()` | Chapter 3 | Used in all filter/map calls |
- | `let` | Chapter 3 | Storing temporary values |
- | Threading macro `->>` | Chapter 5 | Recommendation pipeline |
- | `loop/recur` | Chapter 5 | CLI menu |


## MAL API Integration

The application uses MyAnimeList API v2 to fetch anime data.
```clojure
(defn get-anime-by-id
  "Takes a MAL anime ID, returns basic anime data"
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

- Request is sent with `X-MAL-CLIENT-ID` header for authentication
- `fields` parameter specifies which data we want
- `(mapv #(:name %) (:genres body))` - genres come as a list of maps, so we extract only the names
- `(get-in body [:main_picture :medium])` - extracts the image URL from a nested map