(ns clojure-clothes.forms
  (:require
   [clojure-clothes.validation :as validate]
   [clojure.string :as str]
   [clojure-clothes.db.core :as db]
   [ring.util.http-response :as response]))

(defn checkout-form
  "Validates that the order request is valid and that all the
   items are in stock, if they are then the order is confirmed,
   otherwise it returns with an error"
  [{:keys [params]}]
  (if-let [errors (validate/validate-order params)]
    (-> (response/found "/checkout")
        (assoc :flash (assoc params :errors errors)))
    (if (validate/validate-order-in-stock params)
      (let [order-id (get (db/generate-order params) :_id)]
        (-> (response/found "/confirm-order")
            (assoc :flash (assoc params :ok true :oid order-id))))
      (let [out-of-stock (validate/get-not-in-stock params)
            out-of-stock-skus (str/join ", " (keys out-of-stock))]
        (-> (response/found "/checkout")
            (assoc :flash (assoc params :errors {:stock (concat "The following SKU(s) do not have the required stock: " out-of-stock-skus)})))))))

(defn track-order
  "Takes an order ID and checks whether the order exists,
   if it does then the page is reloaded and passed the order
   data, otherwise it returns with an error"
  [{:keys [params]}]
  (let [order-id (get params :oid)]
    (if (validate/validate-order-exists params)
      (let [order (db/get-order order-id)]
        (-> (response/found "order-tracker")
            (assoc :flash (assoc params :order order))))
      (-> (response/found "order-tracker")
          (assoc :flash (assoc params :errors {:order "That order does not exist"}))))))

(defn update-order-status
  "Updates an order's status and returns to
   the /dashboard route"
  [{:keys [params]}]
  (let [order-id (get params :oid)]
    (db/process-order order-id)
    (response/found "/dashboard")))
