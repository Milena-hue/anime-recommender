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

(defn -main
  "Anime Recommender - entry point"
  [& args]
  (println "Dobrodošli u Anime Recommeder!")
  (println "Broj animea u bazi:" (count anime-db)))
