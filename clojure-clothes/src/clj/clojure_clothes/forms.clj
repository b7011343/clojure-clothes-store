(ns clojure-clothes.forms
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.validation :as validate]
   [ring.util.http-response :as response]))

(defn checkout-form [{:keys [params]}]
  (if-let [errors (validate/validate-order params)]
    (-> (response/found "/checkout")
        (assoc :flash (assoc params :errors errors)))
    ())
  (log/info params)
  (response/found "/confirm-order"))
