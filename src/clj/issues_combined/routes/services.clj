(ns issues-combined.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [issues-combined.db.core :as db]))

(s/defschema milestone s/Int)

(s/defschema Project
  {:corp s/Str
   :organization s/Str
   :repo-name s/Str})

(s/defschema Iteration
  {:date s/Int
   :headman s/Str
   :goal s/Str
   :milestones [milestone]})

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

    (GET "/projects" []
      :summary "프로젝트별 이슈 리스트 보기"
      (ok (db/get-project-with-issues))
      )
    
    (POST "/iterations" []
      :summary "새로운 이터레이션 등록"
      :body [body Iteration]
      (ok (db/create-iterations! body)))
    
    (GET "/iterations" []
      :summary "모든 이터레이션 보기"
      (ok (db/get-all-iterations)))

    (PUT "/iterations/:id" []
      :summary "iteration update"
      (ok))
    
    ))




