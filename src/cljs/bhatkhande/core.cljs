(ns bhatkhande.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [bhatkhande.events :as events]
   [bhatkhande.views :as views]
   [bhatkhande.config :as config]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
