(ns clojure-clothes.routes.home
  (:require
   [clojure-clothes.layout :as layout]
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [clojure-clothes.middleware :as middleware]
   [ring.util.response]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [clojure-clothes.util :as util]
   [clojure-clothes.db.core :as db]))

;; DB Interaction
(defn get-products-full []
  (vec (map util/parse-sku (db/get-products))))

(defn create-order [products]
  (map db/get-product))

;; Form functions
(defn purchase-order [request]
  (layout/render request "shop.html"))

;; Rest API
(defn get-products "Returns all products" [request]
  {:status 200
   :header {"Content-Type" "application/json"}
   :body (-> (get-products-full))})

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

;; Pages
(defn home-page [request]
  (layout/render request "home.html"))

(defn dashboard-page [{:keys [flash] :as request}]
  (layout/render
   request
   "dashboard.html"
   (merge
    {:products (get-products-full)}
    (select-keys flash [:SKU :name :quantity :price :size :color :quality :errors]))))

(defn shop-page [{:keys [flash] :as request}]
  (layout/render
   request
   "shop.html"
   (merge
    {:products (get-products-full)}
    (select-keys flash [:SKU :name :quantity :price :size :color :quality :errors]))))

(defn checkout-page [{:keys [flash] :as request}]
  (layout/render
   request
   "checkout.html"))

;; Routes
(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/dashboard" {:get dashboard-page}]
   ["/shop" {:get shop-page
             :post purchase-order}]
   ["/checkout" {:get checkout-page}]
   ["/api/products" {:get get-products}]
   ["/api/products/:id" {:get get-product}]
   ["/api/orders" {:get get-orders}]
   ["/api/order/:id" {:get get-order}]])
