(ns anime-recommender.api
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [anime-recommender.config :refer [mal-client-id]]))

;; Svi potrebni podaci se uzimaju uz pomoć MAL ID-ja sa sajta My Anime List
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