(ns clothes-store.routes.home
  (:require
   [clothes-store.layout :as layout]
   [clothes-store.db.core :as db]
   [clojure.java.io :as io]
   [clothes-store.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

;; Pages
(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn dashboard-page [request]
  (layout/render request "dashboard.html"))

(defn customer-page [request]
  (layout/render request "customer.html"))

;; Post requests
(defn buy-product [{:keys [params]}])

;; Routes
(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/dashboard" {:get dashboard-page}]
   ["/store" {:get customer-page
              :post buy-product}]])
