(ns user
  (:require [mount.core :as mount]
            [issues-combined.figwheel :refer [start-fw stop-fw cljs]]
            issues-combined.core))

(defn start []
  (mount/start-without #'issues-combined.core/repl-server))

(defn stop []
  (mount/stop-except #'issues-combined.core/repl-server))

(defn restart []
  (stop)
  (start))


