(ns clojure-clothes.api
  (:require
   [clojure-clothes.db.core :as db]
   [clojure-clothes.util :as util]))

(defn get-products "Returns all products" [request]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (-> (db/get-products-full))})

(defn get-product "Returns a product given an id" [{:keys [path-params]}]
  (let [product-id (get path-params :id)
        product (db/get-product product-id)]
    (if (nil? product)
      {:status 404
       :header {"Content-Type" "text/plain; charset=UTF-8"}
       :body (str "Product with ObjectId " product-id " not found")}
      (let [sku-data (util/parse-sku product)]
        {:status 200
         :header {"Content-Type" "application/json"}
         :body (-> (merge product sku-data))}))))

(defn get-orders "Returns all orders" [request]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (-> (db/get-orders))})

(defn get-order "Returns an order given an id" [{:keys [path-params]}]
  (let [order-id (get path-params :id)
        order (db/get-order order-id)]
    (if (nil? order)
      {:status 404
       :header {"Content-Type" "text/plain; charset=UTF-8"}
       :body (str "Order with ObjectId " order-id " not found")}
      {:status 200
       :header {"Content-Type" "application/json"}
       :body (-> order)})))
