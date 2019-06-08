(ns bhatkhande.hindi
  (:require [sargam.ragas :as r]
            [sargam.spec :as us]
            [sargam.talas :as t]))

(def tala-labels (zipmap t/all-talas
                         ["तीन्ताल" "झप्ताल"]))
(def raga-labels (zipmap r/all-ragas
                         ["भूप" "हम्सध्वनि" "बिलावल" "तोडि" "तिलक कामोद"]))
(def swaramap (zipmap us/i-note-seq
                      ["सा" "रे॒" "रे" "ग॒" "ग" "म" "म॑" "प" "ध॒" "ध" "नि॒" "नि" "-" "ऽ"]))
#_(def malalayam-taals (zipmap t/all-talas
                             ["തീന്താള്" "ജപ്താള്"]))
#_(def m-swaras ["സ" "രി" "ഗ" "മ" "പ" "ധ" "സ"])


