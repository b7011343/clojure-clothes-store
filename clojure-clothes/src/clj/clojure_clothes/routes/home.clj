(ns clojure-clothes.routes.home

  (:require

   [clojure-clothes.layout :as layout]

   [clojure.java.io :as io]

   [clojure-clothes.middleware :as middleware]

   [ring.util.response]

   [ring.util.http-response :as response]))

(defn dashboard-page [request]

  (layout/render request "dashboard.html"))

(defn shop-page [request]

  (layout/render request "shop.html"))

(defn home-routes []

  [""

   {:middleware [middleware/wrap-csrf

                 middleware/wrap-formats]}

   ["/dashboard" {:get dashboard-page}]

   ["/shop" {:get shop-page}]])



