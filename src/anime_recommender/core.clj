(ns anime-recommender.core
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
    :rating 7.99}])

(defn -main
  "Anime Recommender - entry point"
  [& args]
  (println "Dobrodošli u Anime Recommeder!")
  (println "Broj animea u bazi:" (count anime-db)))
