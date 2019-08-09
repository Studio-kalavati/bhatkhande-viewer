(ns bhatkhande.subs
  (:require
   [re-frame.core :as re-frame :refer [reg-sub]]
   [sargam.languages :refer [lang-labels]]))

(reg-sub
 ::saved-part
 (fn [db]
   (let [res (:saved-part db)]
     res)))

(reg-sub
 ::saved-comp
 (fn [db]
   (let [res (:saved-comp db)]
     res)))

(reg-sub
 ::div-dim
 (fn [db]
   (-> db :dim)))

(reg-sub
 ::dispinfo
 (fn [db]
   (-> db :dispinfo)))

(reg-sub
 ::init-state
 (fn [db]
   (-> db :init-state)))

(reg-sub
 ::language
 (fn [db]
   (-> db :language)))

(reg-sub
 ::swaramap
 (fn [db]
   (get-in lang-labels [(-> db :language) :swara-labels])))
