(ns anime-recommender.db
  (:require [anime-recommender.api :refer [get-anime-by-id]]))

;;Lista MAL ID-jeva koji su u bazi
(def mal-ids [52991 59978 40748 48561 51009 57658 46569 55825 56009 61128
              60692 61983 61663 60810 60223 61196 60509 61207 60071 61217])

;;Učitamo animee sa MAL-a po ID-ju
;;mapv prolazi kroz sve ID-jeve i za svaki poziva get-anime-by-id
(def anime-db
 (mapv get-anime-by-id mal-ids))