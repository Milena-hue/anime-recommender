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
    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #0d0d0d; color: #ffffff; min-height: 100vh; display: flex; flex-direction: column; align-items: center; padding: 40px 20px; }
    h1 { font-size: 2.8rem; color: #e50914; margin-bottom: 8px; letter-spacing: 3px; text-transform: uppercase; }
    p.subtitle { color: #888; margin-bottom: 35px; font-size: 1rem; letter-spacing: 1px; }
    .search-box { background: #1a1a1a; padding: 30px; border-radius: 14px; width: 100%; max-width: 600px; border: 1px solid #2a2a2a; }
    select, input { width: 100%; padding: 12px 16px; margin: 8px 0; background: #2a2a2a; color: #ffffff; border: 1px solid #444; border-radius: 8px; font-size: 1rem; }
    select:focus, input:focus { outline: none; border-color: #e50914; }
    button { width: 100%; padding: 13px; margin-top: 10px; background: #e50914; color: white; border: none; border-radius: 8px; font-size: 1rem; cursor: pointer; letter-spacing: 2px; text-transform: uppercase; font-weight: bold; }
    button:hover { background: #ff2020; }
    #results { margin-top: 35px; width: 100%; max-width: 600px; }
    .anime-card { background: #1a1a1a; border: 1px solid #2a2a2a; border-radius: 12px; margin: 14px 0; display: flex; gap: 16px; overflow: hidden; transition: border-color 0.2s, transform 0.2s; }
    .anime-card:hover { border-color: #e50914; transform: translateY(-2px); }
    .anime-card img { width: 110px; min-width: 110px; object-fit: cover; }
    .anime-info { padding: 16px 16px 16px 0; display: flex; flex-direction: column; gap: 6px; }
    .anime-title { font-size: 1.2rem; color: #e50914; font-weight: bold; }
    .anime-meta { font-size: 0.85rem; color: #aaa; }
    .anime-meta span { color: #fff; }
    .anime-desc { font-size: 0.88rem; color: #ccc; line-height: 1.5; margin-top: 4px; }
    .genre-tag { display: inline-block; background: #2a2a2a; border: 1px solid #444; border-radius: 20px; padding: 2px 10px; font-size: 0.75rem; color: #ccc; margin: 2px; }
    .no-results { color: #666; text-align: center; margin-top: 30px; font-size: 1rem; }
    .loading { color: #e50914; text-align: center; margin-top: 30px; font-size: 1rem; }
  </style>
</head>
<body>
  <h1>Anime Recommender</h1>
  <p class='subtitle'>Otkrij svoj sledeći omiljeni anime</p>
  <div class='search-box'>
    <select id='search-type'>
      <option value='recommend'>Preporuka po animeu</option>
      <option value='genre'>Pretraga po žanru</option>
      <option value='studio'>Pretraga po studiju</option>
      <option value='mal'>Preuzmi anime sa MAL-a (ID)</option>
    </select>
    <input type='text' id='search-input' placeholder='Unesi naziv ili MAL ID...'/>
    <button onclick='search()'>Pretrazi</button>
  </div>
  <div id='results'></div>
  <script>
    function renderCard(anime) {
      var html = '<div class=\"anime-card\">';
      if (anime.image) html += '<img src=\"' + anime.image + '\" alt=\"' + anime.title + '\" />';
      html += '<div class=\"anime-info\">';
      html += '<div class=\"anime-title\">' + anime.title + '</div>';
      if (anime.studio) html += '<div class=\"anime-meta\">Studio: <span>' + anime.studio + '</span></div>';
      if (anime.rating) html += '<div class=\"anime-meta\">Rating: <span>' + anime.rating + '</span></div>';
      if (anime.genres && anime.genres.length > 0) {
        html += '<div style=\"margin-top:4px\">';
        anime.genres.forEach(function(g) { html += '<span class=\"genre-tag\">' + g + '</span>'; });
        html += '</div>';
      }
      if (anime.description) html += '<div class=\"anime-desc\">' + anime.description + '</div>';
      html += '</div></div>';
      return html;
    }

    function search() {
      var type = document.getElementById('search-type').value;
      var input = document.getElementById('search-input').value;
      document.getElementById('results').innerHTML = '<p class=\"loading\">Ucitavanje...</p>';
      fetch('/api/' + type + '?q=' + encodeURIComponent(input))
        .then(function(r) { return r.json(); })
        .then(function(data) {
          var html = '';
          if (Array.isArray(data) && data.length === 0) {
            html = '<p class=\"no-results\">Nema rezultata.</p>';
          } else if (!Array.isArray(data)) {
            html = renderCard(data);
          } else {
            data.forEach(function(anime) { html += renderCard(anime); });
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
