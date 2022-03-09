(ns clojure-clothes.forms
  (:require
   [clojure.tools.logging :as log]
   [clojure-clothes.validation :as validate]
   [clojure.string :as str]
   [clojure-clothes.util :as util]
   [clojure-clothes.db-interface :as dbi]
   [clojure-clothes.db.core :as db]
   [ring.util.http-response :as response]))

(defn checkout-form [{:keys [params]}]
  (if-let [errors (validate/validate-order params)]
    (-> (response/found "/checkout")
        (assoc :flash (assoc params :errors errors)))
    (if (validate/validate-order-in-stock params)
      (let [order-id (get (dbi/generate-order params) :_id)]
        (log/info order-id)
        (-> (response/found "/confirm-order")
            (assoc :flash (assoc params :ok true :oid order-id))))
      (let [out-of-stock (validate/get-not-in-stock params)
            out-of-stock-skus (str/join ", " (keys out-of-stock))]
        (-> (response/found "/checkout")
            (assoc :flash (assoc params :errors {:stock (concat "The following SKU(s) do not have the required stock: " out-of-stock-skus)})))))))

(defn track-order [order-id]
  (let [order (db/get-order order-id)]
    (log/info order)))

(defn update-order-status []
  )
