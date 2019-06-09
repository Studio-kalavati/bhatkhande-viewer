(ns bhatkhande.views
  (:require
   [re-frame.core :as re-frame :refer [dispatch subscribe]]
   [re-com.core :as re-com :refer-macros [handler-fn]]
   [bhatkhande.subs :as subs]
   [reagent.core :as reagent :refer [atom]]
   [quil.core :as q :include-macros true]
  ; [cljs.spec.alpha :as s]  
   [quil.middleware :as m]
   [bhatkhande.events :as e]
   [bhatkhande.parts :as p]
   [sargam.spec :as us]
   [bhatkhande.db :as db]
   [bhatkhande.events :as ev]
   ))

(defn color-map
  [frame-rate]
  (zipmap (iterate inc 1) (into
                           (vec (repeat (/ frame-rate 2) '(0,0,0)))
                           (vec (repeat (/ frame-rate 2) '(255,255,255))))))
(def cmap (atom 0))

(defn setup
  "takes a function to run on startup"
  ([] (setup false nil))
  ([loop? init-fn]
   (fn []
     (let [;fr @(subscribe [::subs/frame-rate])
           ;_ (reset! cmap (color-map fr))
           _ (q/no-loop)
           {:keys [location-info disp-info] :as imap} (init-fn)
           ]
       @(subscribe [::subs/init-state])))))

(defn viewer-sketch
  "creates an canvas for viewing"
  [part div-id size-fn dinfo]
  (let [idraw (fn[](let [_ (q/background 255)
                         _ (q/fill 0)]
                     (p/disp-comp dinfo @part)))]
    (q/sketch
     :setup (setup false idraw)
     :update identity 
     :draw (fn [state]
             state)
     :host div-id
     :middleware [m/fun-mode]
     :size (size-fn))))


(defn disp-swara-canvas
  " a canvas for viewing swaras on event such as button press . Note that cur-part
  must be a subscription that does not reference local vars such as part-name or composition name."
  [cur-part div-id vfn]
  (fn [cur-part]
    (reagent/create-class
     {:reagent-render
      (fn [] [(keyword (str "canvas#" div-id))])
      :component-did-update vfn 
      :component-did-mount vfn })))

(defn main-panel
  []
  (fn []
    (let [sp (subscribe [::subs/saved-comp])]
      (let [imap {:width "100%"
                  :height "100%"
                  :position :absolute
                  :top 0
                  :left 0}
            div-id "viewer"]
        [re-com/v-box
         :gap "20px"
         :children
         [[re-com/title
           :label "Example showing a composition annotated with Bhatkhande notation"
           :level :level2]
          [:div {:style {:position :relative
                         :width "500px"
                         :height "250px"}}
           [:div {:style imap}
            [disp-swara-canvas sp div-id
             #(viewer-sketch sp div-id
                             (constantly @(subscribe [::subs/div-dim :editor]))
                             (assoc @(subscribe [::subs/dispinfo]) :y 30))]]]]]))))

#_(defn main-panel []
  [quil-area])
