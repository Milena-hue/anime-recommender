(ns anime-recommender.recommender
  (:require [anime-recommender.db :refer [anime-db]]))

;;Broji koliko se žanrova poklapa između dva animea

(defn count-matching-genres
  "Prima dva animea, vraća broj zajedničkih žanrova"
  [anime1 anime2]
  (count (filter (fn [genre]
            (some #(= % genre) (:genres anime2)))
          (:genres anime1))))

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

;; Glavna recommendation funkcija

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

