(ns issues-combined.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [clj-http.client :as client]
            [issues-combined.db.core :as db]))

(s/defschema Project
  {:corp s/Str
   :organiztion s/Str
   :repo-name s/Str})

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}
  
  (context "/api" []
    :tags ["issues"]

    (GET "/power" []
      :return      Long
      :header-params [x :- Long, y :- Long]
      :summary "x^y with header-parameters"
      (ok (long (Math/pow x y))))
    (POST "/register" []
      :summary "토큰 등록"
      :query-params [token :- String]
      (let [result (db/create-token! token)]
        (ok result)))
    (POST "/projects" []
      :summary "프로젝트 등록"
      :body [body Project]
      (let [result (db/create-projects! body)]
        result))
    (POST "/test" []
      :summary "x/y with form-parameters"
      (let [token (str "token " (db/get-token))
            result (client/get "https://github.daumkakao.com/api/v3/repos/MailProject/groot-api/issues" 
                     {:headers {"Authorization" token}
                      :as :json})
            body-firstpage (:body result)
            titles (map :title body-firstpage)]
        (db/create-user {:first_name "minsun" :last_name "Lee" :email "aaa"})
        (ok titles)))))

