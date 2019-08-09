(ns bhatkhande.events
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db]]
   [bhatkhande.db :as db]
   [clojure.zip :as z]))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 ::set-language
 (fn [db [_ lang]]
   (assoc db :language lang)))

(reg-event-db
 ::swap-language
 (fn [db _]
   (let [cur-lang (:language db)
         n-lang (if (= cur-lang :english) :hindi :english)]
     (assoc db :language n-lang))))
