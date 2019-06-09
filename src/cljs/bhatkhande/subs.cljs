(ns bhatkhande.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::saved-comp
 (fn [db]
   (let [res (:saved-comp db)]
     res)))

(re-frame/reg-sub
 ::div-dim
 (fn [db]
   (-> db :dim)))

(re-frame/reg-sub
 ::dispinfo
 (fn [db]
   (-> db :dispinfo)))

(re-frame/reg-sub
 ::init-state
 (fn [db]
   (-> db :init-state)))
