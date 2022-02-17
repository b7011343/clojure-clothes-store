(ns clojure-clothes.routes.home
  (:require
   [clojure-clothes.layout :as layout]
   [clojure.java.io :as io]
   [clojure-clothes.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [clojure-clothes.db.core :as db]
  ))

(defn dashboard-page [request]
  (layout/render request "dashboard.html"))

(defn shop-page [request]
  (layout/render request "shop.html"))

(defn purchase-order [request]
  (layout/render request "shop.html"))

(defn get-products
  "Returns all products"
  [request]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (-> db/get-products)})

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/dashboard" {:get dashboard-page}]
   ["/shop" {:get shop-page
             :post purchase-order}]
   ["/api/products" {:get get-products}]
  ])
