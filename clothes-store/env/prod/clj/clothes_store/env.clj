(ns clothes-store.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clothes-store started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clothes-store has shut down successfully]=-"))
   :middleware identity})
