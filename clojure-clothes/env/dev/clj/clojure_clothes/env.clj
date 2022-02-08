(ns clojure-clothes.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [clojure-clothes.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clojure-clothes started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clojure-clothes has shut down successfully]=-"))
   :middleware wrap-dev})
