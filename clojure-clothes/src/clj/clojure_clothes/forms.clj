(ns clojure-clothes.forms
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]))

(defn checkout-form [{:keys [params]}]
  (log/info params)
  (response/found "/confirm-order"))
