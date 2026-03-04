(ns anime-recommender.core
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response content-type]]
            [cheshire.core :as json])
  (:gen-class))
;; Baza anime podataka
;; Svaki anime ima id, title, genres, studio i rating
(def anime-db
  [{:id 1
    :title "Attack on Titan"
    :genres ["Action" "Drama" "Suspense"]
    :studio "Wit Studio"
    :rating 8.57}

   {:id 2
    :title "Death Note"
    :genres ["Supernatural" "Suspense"]
    :studio "Madhouse"
    :rating 8.62}

   {:id 3
    :title "Bleach"
    :genres ["Action" "Adventure" "Supernatural"]
    :studio "Studio Pierrot"
    :rating 7.99}

   {:id 4
    :title "Jack-of-All-Trades, Party of None"
    :genres ["Action" "Adventure" "Fantasy"]
    :studio "animation studio42"
    :rating 6.76}

   {:id 5
    :title "Jujutsu Kaisen 0"
    :genres ["Action" "School" "Shounen"]
    :studio "MAPPA"
    :rating 8.40}

   {:id 6
    :title "Black Clover"
    :genres ["Action" "Fantasy" "Shounen"]
    :studio "Studio Pierrot"
    :rating 8.14}

   {:id 7
    :title "Afro Samurai"
    :genres ["Action" "Adventure" "Samurai"]
    :studio "Gonzo"
    :rating 7.37}

   {:id 8
    :title "Champignon Witch"
    :genres ["Drama" "Adventure" "Fantasy"]
    :studio "Typhoon Graphics"
    :rating 7.31}

   {:id 9
    :title "Chainsaw Man"
    :genres ["Action" "Fantasy" "Shounen"]
    :studio "MAPPA"
    :rating 8.46}

   {:id 10
    :title "Yano-kun's Ordinary Days"
    :genres ["Comedy" "Romance" "School"]
    :studio "Aija-do"
    :rating 7.07}

   {:id 11
    :title "Clevatess"
    :genres ["Action" "Fantasy"]
    :studio "Lay-duce"
    :rating 7.89}

   {:id 12
    :title "Kingdom"
    :genres ["Action" "Seinen" "Military"]
    :studio "Studio Pierrot"
    :rating 7.89}

   {:id 13
    :title "Spriggan"
    :genres ["Action" "Adventure" "Sci-Fi"]
    :studio "David Production"
    :rating 6.85}

   {:id 14
    :title "Kaiju No. 8"
    :genres ["Action" "Shounen" "Sci-Fi"]
    :studio "Production I.G"
    :rating 8.28}

   {:id 15
    :title "Hell's Paradise"
    :genres ["Action" "Adventure" "Supernatural"]
    :studio "MAPPA"
    :rating 8.09}

   {:id 16
    :title "ERASED"
    :genres ["Psychological" "Seinen" "Suspense"]
    :studio "A-1 Pictures"
    :rating 8.30}

   {:id 17
    :title "Alice in Borderland"
    :genres ["Action" "Shounen" "Suspense"]
    :studio "SILVER LINK."
    :rating 7.25}

   {:id 18
    :title "Trigun Stargaze"
    :genres ["Action" "Adventure" "Sci-Fi"]
    :studio "Orange"
    :rating 7.35}

   {:id 19
    :title "Demon Slayer"
    :genres ["Action" "Shounen" "Supernatural"]
    :studio "ufotable"
    :rating 8.43}

   {:id 20
    :title "The Apothecary Diaries"
    :genres ["Drama" "Medical" "Historical"]
    :studio "OLM"
    :rating 8.82}])

;;Pretraga po žanru
;;Filter prolazi kroz sve anime i izdvaja samo one koji imaju traženi žanr

(defn find-by-genre
  "Prima žanr kao string, vraća sve anime koji imaju taj žanr"
  [genre]
  (filter (fn [anime]
            (some #(= % genre) (:genres anime)))
          anime-db))

;;Pretraga po studiu

(defn find-by-studio
  "Prima studio kao string, vraća sve anime koji imaju taj studio"
  [studio]
  (filter (fn [anime]
            (= (:studio anime) studio))
          anime-db))

;;Broji koliko se žanrova poklapa između dva animea

(defn count-matching-genres
  "Prima dva animea, vraća broj zajedničkih žanrova"
  [anime1 anime2]
  (count (filter (fn [genre]
            (some #(= % genre) (:genres anime2)))
          (:genres anime1))))

;; Glavna recommendation funkcija

(defn recommend
  "Prima naziv animea koji si gledao, vraća top 3 slična"
  [title]
  (let [watched (first (filter #(= (:title %) title) anime-db))]
    (if (nil? watched)
      (println "Anime nije pronađen u bazi!")
      (->> anime-db
           (filter #(not= (:title %) title))
           (sort-by #(count-matching-genres watched %) >)
           (take 3)))))
    
;;HTML stranica
(defn home-page []
  (content-type
   (response
    "<!DOCTYPE html>
<html>
<head>
   <meta charset='UTF-8'>
   <title>Anime Recommender</title>  
   <style> 
    body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; }
    h1 { color: #e50914; }
    input, select { padding: 8px; margin: 5px; width: 300px; }
    button { padding: 8px 20px; background: #e50914; color: white; border: none; cursor: pointer; }
    #results { margin-top: 20px; }
    .anime-card { border: 1px solid #ddd; padding: 10px; margin: 5px 0; border-radius: 5px; }
  </style> 
<head>
<body>
  <h1>Anime Recommender</h1>
  <select id='search-type'>
    <option value='recommend'>Preporuka po animeu</option>
    <option value='genre'>Pretraga po zanru</option>
    <option value='studio'>Pretraga po studiju</option>
  </select>
  <br>
  <input type='text' id='search-input' placeholder='Unesi naziv...'/>
  <button onclick='search()'>Trazi</button>
  <div id='results'></div>
  <script>
    function search() {
      var type = document.getElementById('search-type').value;
      var input = document.getElementById('search-input').value;
      fetch('/api/' + type + '?q=' + encodeURIComponent(input))
        .then(function(r) { return r.json(); })
        .then(function(data) {
          var html = '';
          if (data.length === 0) {
            html = '<p>Nema rezultata.</p>';
          } else {
            data.forEach(function(anime) {
              html += '<div class=\"anime-card\">';
              html += '<b>' + anime.title + '</b><br>';
              html += 'Studio: ' + anime.studio + '<br>';
              html += 'Rating: ' + anime.rating + '<br>';
              html += 'Zanrovi: ' + anime.genres.join(', ');
              html += '</div>';
            });
          }
          document.getElementById('results').innerHTML = html;
        });
    }
  </script>
</body>
</html>")
    "text/html"))

;;Rute

(defroutes app-routes
  (GET "/" [] (home-page))
  (GET "/api/recommend" [q] (content-type (response (json/encode (recommend q))) "aplication/json"))
  (GET "/api/genre" [q] (content-type (response (json/encode (find-by-genre q))) "aplication/json"))
  (GET "/api/studio" [q] (content-type (response (json/encode (find-by-studio q))) "aplication/json")))


(defn print-anime-list
  "Ispisuje listu animea sa rednim brojevima"
  [anime-list]
  (doseq [anime anime-list]
    (println "-" (:title anime) "| Žanr:" (:genres anime) "| Rating:" (:rating anime))))

(defn show-menu []
  (println "\n=== ANIME RECOMMENDER ===")
  (println "1. Pretraga po žanru")
  (println "2. Pretraga po studiju")
  (println "3. Preporuka na osnovu animea")
  (println "4. Izlaz")
  (println "Izaberi opciju: ")
  (flush))



(defn -main
  "Anime Recommender - entry point"
  [& args]
  (println "Pokrećemo Anime Recommender na http://localhost:3000")
  (run-jetty app-routes {:port 3000 :join? false}))
  