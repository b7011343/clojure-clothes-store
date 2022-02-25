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
   [clojure-clothes.db.core :as db]))

;; Constants
(def STANDARD-PRICE 9.99)
(def SUPREME-PRICE 15.99)

;; Util functions
(defn parse-sku [product]
  (let [sku (get product :SKU)
        name (get product :name)
        quantity (get product :quantity)
        split-sku (str/split sku #"-")
        json-file (json/read-str (slurp "resources/sku-decoder.json") :key-fn keyword)
        quality (get (get json-file :quality) (keyword (get split-sku 2)))
        price (cond
                (= quality "Supreme") SUPREME-PRICE
                (= quality "Standard") STANDARD-PRICE)]
    {:SKU sku
     :name name
     :quantity quantity
     :color (get (get json-file :color) (keyword (get split-sku 0)))
     :size (get (get json-file :size) (keyword (get split-sku 1)))
     :quality quality
     :price price}))

(defn calculate-total-price [order]
  (apply + (map :price order)))

(defn check-stock [product-id quantity-to-buy]
  (let [quantity (get (db/get-product product-id) :quantity)]
    (> 0 (- quantity quantity-to-buy))))

(defn get-products-full []
  (vec (map parse-sku (db/get-products))))

;; ["", ""]
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
        sku-data (parse-sku (get product :SKU))]
    {:status 200
     :header {"Content-Type" "application/json"}
     :body (-> (merge product sku-data))}))

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

;; Routes
(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/dashboard" {:get dashboard-page}]
   ["/shop" {:get shop-page
             :post purchase-order}]
   ["/api/products" {:get get-products}]
   ["/api/products/:id" {:get get-product}]])
