(ns anime-recommender.core
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.params :refer [wrap-params]]
            [cheshire.core :as json]
            [anime-recommender.recommender :refer [find-by-genre find-by-studio recommend]]
            [anime-recommender.api :refer [get-anime-by-id]])
  (:gen-class))

    
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
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: Arial, sans-serif; background-color: #0d0d0d; color: #ffffff; min-height: 100vh; display: flex; flex-direction: column; align-items: center; padding: 40px 20px; }
    h1 { font-size: 2.5rem; color: #e50914; margin-bottom: 10px; letter-spacing: 2px; }
    p.subtitle { color: #aaaaaa; margin-bottom: 30px; font-size: 1rem; }
    .search-box { background: #1a1a1a; padding: 30px; border-radius: 12px; width: 100%; max-width: 550px; border: 1px solid #333; }
    select, input { width: 100%; padding: 12px; margin: 8px 0; background: #2a2a2a; color: #ffffff; border: 1px solid #444; border-radius: 8px; font-size: 1rem; }
    select:focus, input:focus { outline: none; border-color: #e50914; }
    button { width: 100%; padding: 12px; margin-top: 10px; background: #e50914; color: white; border: none; border-radius: 8px; font-size: 1rem; cursor: pointer; letter-spacing: 1px; }
    button:hover { background: #ff1a1a; }
    #results { margin-top: 30px; width: 100%; max-width: 550px; }
    .anime-card { background: #1a1a1a; border: 1px solid #333; padding: 16px; margin: 10px 0; border-radius: 10px; transition: border-color 0.2s; }
    .anime-card:hover { border-color: #e50914; }
    .anime-card b { font-size: 1.2rem; color: #e50914; }
    .anime-card p { margin-top: 6px; color: #cccccc; font-size: 0.95rem; }
    .no-results { color: #aaaaaa; text-align: center; margin-top: 20px; font-size: 1rem; }
  </style> 
<head>
<body>
  <h1>Anime Recommender</h1>
    <p class='subtitle'>Pronađi anime po žanru, studiju ili dobij preporuku</p>
  <div class='search-box'>
   <select id='search-type'>
      <option value='recommend'>Preporuka po animeu</option>
      <option value='genre'>Pretraga po žanru</option>
      <option value='studio'>Pretraga po studiju</option>
    </select>
  <input type='text' id='search-input' placeholder='Unesi naziv...'/>
    <button onclick='search()'>TRAŽI</button>
  </div>
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
            html = '<p class=\"no-results\">Nema rezultata.</p>';
          } else {
            data.forEach(function(anime) {
              html += '<div class=\"anime-card\">';
              html += '<b>' + anime.title + '</b><br>';
              html += 'Studio: ' + anime.studio + '<br>';
              html += 'Rating: ' + anime.rating + '<br>';
              html += 'Žanrovi: ' + anime.genres.join(', ');
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
  (GET "/api/recommend" [q] (content-type (response (json/encode (vec (recommend q)))) "aplication/json"))
  (GET "/api/genre" [q] (content-type (response (json/encode (vec (find-by-genre q)))) "aplication/json"))
  (GET "/api/studio" [q] (content-type (response (json/encode (vec (find-by-studio q)))) "aplication/json"))
  (GET "/api/mal" [q] (content-type (response (json/encode (get-anime-by-id (Integer/parseInt q)))) "aplication/json")))



(defn -main
  "Anime Recommender - entry point"
  [& args]
  (println "Pokrećemo Anime Recommender na http://localhost:3000")
  (run-jetty (wrap-params app-routes) {:port 3000 :join? false}))
  