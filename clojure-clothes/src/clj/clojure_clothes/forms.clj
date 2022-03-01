(ns clojure-clothes.forms
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.validation :as validate]
   [ring.util.http-response :as response]))

(defn checkout-form [{:keys [params]}]
  (if-let [errors (validate/validate-order params)]
    (-> (response/found "/checkout")
        (assoc :flash (assoc params :errors errors)))
    (if (validate/validate-order-in-stock params)
      (do 
        (log/info "Here")
        (response/found "/checkout"))
      (-> (response/found "/checkout") ;; This needs to be modified, as js deletes the cart
          (assoc :flash (assoc params :errors {:stock "An item does not have the amount of stock you ordered"}))))))
