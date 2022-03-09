(ns clojure-clothes.routes.home
  (:require
   [clojure-clothes.middleware :as middleware]
   [ring.util.response]
   [clojure-clothes.api :as api]
   [clojure-clothes.forms :as forms]
   [clojure-clothes.pages :as pages]))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get pages/home-page}]
   ["/dashboard" {:get pages/dashboard-page
                  :post forms/update-order-status}]
   ["/shop" {:get pages/shop-page}]
   ["/checkout" {:get pages/checkout-page
                 :post forms/checkout-form}]
   ["/order-tracker" {:get pages/order-tracker-page
                      :post forms/track-order}]
   ["/confirm-order" {:get pages/confirm-order-page}]
   ["/api/products" {:get api/get-products}]
   ["/api/products/:id" {:get api/get-product}]
   ["/api/orders" {:get api/get-orders}]
   ["/api/order/:id" {:get api/get-order}]])
