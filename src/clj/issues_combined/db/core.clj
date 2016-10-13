(ns issues-combined.db.core
  (:require [issues-combined.config :refer [env]]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all]
            [mount.core :refer [defstate]])
  (:import [com.mongodb MongoClientOptions ReadPreference ServerAddress WriteResult]))

(defstate db*
  :start (let [{:keys [host port user password]} (:db env)
               ^MongoClientOptions opts (mg/mongo-options (assoc (:db env)
                                                            :read-preference (ReadPreference/primaryPreferred)))
               ^ServerAddress sa (mg/server-address host port)
               conn (mg/connect sa opts)
               db (mg/get-db conn (:name (:db env)))]
           {:conn conn :db db})

  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn create-token [token]
  (let [^WriteResult result (mc/insert db "github" {:_id "token" :token token})]
    {:count (.getN result)}))

(defn create-user [user]
  (mc/insert db "issues" user))

(defn update-user [id first-name last-name email]
  (mc/update db "issues" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-token []
  (:token (mc/find-one-as-map db "github" {:_id "token"})))

