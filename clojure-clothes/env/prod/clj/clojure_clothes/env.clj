(ns clojure-clothes.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clojure-clothes started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clojure-clothes has shut down successfully]=-"))
   :middleware identity})
