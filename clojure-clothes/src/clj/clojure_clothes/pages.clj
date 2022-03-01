(ns clojure-clothes.pages
  (:require
   [clojure-clothes.layout :as layout]
   [clojure-clothes.db-interaction :as dbi]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn dashboard-page [{:keys [flash] :as request}]
  (layout/render
   request
   "dashboard.html"
   (merge
    {:products (dbi/get-products-full)}
    (select-keys flash [:SKU :name :quantity :price :size :color :quality :errors]))))

(defn shop-page [{:keys [flash] :as request}]
  (layout/render
   request
   "shop.html"
   (merge
    {:products (dbi/get-products-full)}
    (select-keys flash [:SKU :name :quantity :price :size :color :quality :errors]))))

(defn checkout-page [{:keys [flash] :as request}]
  (layout/render
   request
   "checkout.html"))

(defn confirm-order-page [request]
  (layout/render request "confirm-order.html"))
