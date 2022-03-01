(ns clojure-clothes.forms
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.validation :as validate]
   [clojure.string :as str]
   [ring.util.http-response :as response]))

(defn checkout-form [{:keys [params]}]
  (if-let [errors (validate/validate-order params)]
    (-> (response/found "/checkout")
        (assoc :flash (assoc params :errors errors)))
    (if (validate/validate-order-in-stock params)
      (-> (response/found "/confirm-order")
          (assoc :flash (assoc params :ok true)))
      (let [out-of-stock (validate/get-not-in-stock params)
            out-of-stock-skus (str/join ", " (keys out-of-stock))]
        (-> (response/found "/checkout")
            (assoc :flash (assoc params :errors {:stock (concat "The following SKU(s) do not have the required stock: " out-of-stock-skus)})))))))
