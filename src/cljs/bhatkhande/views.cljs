(ns bhatkhande.views
  (:require
   [re-frame.core :as re-frame :refer [dispatch]]
   [re-com.core :as re-com :refer-macros [handler-fn]]
   [bhatkhande.subs :as subs]
   [reagent.core :as reagent :refer [atom]]
   [quil.core :as q :include-macros true]
  ; [cljs.spec.alpha :as s]  
   [quil.middleware :as m]
   [bhatkhande.events :as e]
   [bhatkhande.parts :as p]
   [bhatkhande.spec :as us]
   [bhatkhande.db :as db]
   [bhatkhande.events :as ev]
   ))
;(s/def ::abc boolean?)
(defn setup []
  (q/no-loop))

(defn draw [state]
  (q/background 255)
  (q/fill 0)
  (p/disp-comp db/dispinfo db/comp1)
  )

(q/defsketch foo
  :setup setup
  :draw   draw
  :host "foo"
  :no-start true
  :middleware [m/fun-mode]
  :size (mapv #(db/dispinfo %) [:x-end :y-end]))

(defn quil-area []
  (reagent/create-class
   {:reagent-render (fn [] [:canvas#foo])
    :component-did-mount foo}))

(defn main-panel []
  [quil-area])
