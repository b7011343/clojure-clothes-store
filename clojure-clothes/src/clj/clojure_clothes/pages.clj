(ns clojure-clothes.pages
  (:require
   [clojure-clothes.layout :as layout]
   [clojure-clothes.db.core :as db]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn dashboard-page [{:keys [flash] :as request}]
  (layout/render
   request
   "dashboard.html"
   (merge
    {:products (db/get-products-full)
     :orders (db/get-orders)
     :awaiting-order-count (db/get-total-awaiting-orders)
     :processed-order-count (db/get-total-processed-orders)
     :profit (db/get-total-profit)}
    (select-keys flash [:SKU :name :quantity :price :size :color :quality :errors]))))

(defn shop-page [{:keys [flash] :as request}]
  (layout/render
   request
   "shop.html"
   (merge
    {:products (db/get-products-full)}
    (select-keys flash [:SKU :name :quantity :price :size :color :quality :errors]))))

(defn checkout-page [{:keys [flash] :as request}]
  (layout/render
   request
   "checkout.html"
   (select-keys flash [:errors])))

(defn confirm-order-page [{:keys [flash] :as request}]
  (layout/render
   request
   "confirm-order.html"
   (select-keys flash [:ok :oid])))

(defn order-tracker-page [{:keys [flash] :as request}]
  (layout/render
   request
   "order-tracker.html"
   (select-keys flash [:order :errors])))
