(ns issues-combined.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [issues-combined.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[issues-combined started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[issues-combined has shut down successfully]=-"))
   :middleware wrap-dev})
