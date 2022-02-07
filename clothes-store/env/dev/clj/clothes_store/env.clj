(ns clothes-store.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [clothes-store.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clothes-store started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clothes-store has shut down successfully]=-"))
   :middleware wrap-dev})
