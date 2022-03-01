(ns clojure-clothes.api
  (:require
   [clojure-clothes.db-interaction :as dbi]
   [clojure-clothes.db.core :as db]
   [clojure-clothes.util :as util]))

(defn get-products "Returns all products" [request]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (-> (dbi/get-products-full))})

(defn get-product "Returns a product given an id" [{:keys [path-params]}]
  (let [product (db/get-product (get path-params :id))
        sku-data (util/parse-sku (get product :SKU))]
    {:status 200
     :header {"Content-Type" "application/json"}
     :body (-> (merge product sku-data))}))

(defn get-orders "Returns all orders" [request]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (-> nil)})

(defn get-order "Returns an order given an id" [{:keys [path-params]}]
  (let [order nil]
    {:status 200
     :header {"Content-Type" "application/json"}
     :body (-> nil)}))