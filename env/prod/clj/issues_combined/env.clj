(ns issues-combined.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[issues-combined started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[issues-combined has shut down successfully]=-"))
   :middleware identity})
