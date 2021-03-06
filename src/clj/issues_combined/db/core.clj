(ns issues-combined.db.core
  (:require [issues-combined.config :refer [env]]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.operators :refer :all]
            [mount.core :refer [defstate]]
            [clj-http.client :as client])
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

(defn- make-url [{:keys [corp organization repo-name]}]
  (format "https://github.%s.com/api/v3/repos/%s/%s/issues" corp organization repo-name))


(defn create-token! [token]
  (let [^WriteResult result (mc/insert db "github" {:_id "token" :token token})]
    {:count (.getN result)}))

(defn create-user! [user]
  (mc/insert db "issues" user))

(defn update-user! [id first-name last-name email]
  (mc/update db "issues" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-token []
  (:token (mc/find-one-as-map db "github" {:_id "token"}))) 

(defn create-projects! [project]
  (let [^WriteResult result (mc/insert db "projects" project)]
    {:count (.getN result)}))

(defn get-all-projects []
  (mc/find-maps db "projects"))

(defn get-issues-titles [project]
  (let [token (str "token " (get-token))
        body-firstpage (:body (client/get (make-url project)
                                {:headers {"Authorization" token}
                                 :as :json}))
        titles (map :title body-firstpage)]
    titles))

(defn get-project-with-issues []
  (let [projects (get-all-projects)]
    (map #(hash-map
            :project-name (:repo-name %)
            :issues (get-issues-titles %))
      projects)))

(defn create-iterations! [iteration]
  (let [^WriteResult result (mc/insert db "iterations" iteration)]
    {:count (.getN result)}))

(defn get-all-iterations []
  (map #(dissoc % :_id) (mc/find-maps db "iterations")))
