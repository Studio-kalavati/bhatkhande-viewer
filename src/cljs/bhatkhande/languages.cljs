(ns bhatkhande.languages
  (:require
   [sargam.talas :as ta]
   [sargam.ragas :as ra]
   [sargam.spec :as us]))

(def lang-labels {:english
                  {
                   :tala-labels (zipmap (mapv :id ta/all-talas)
                                        ["Teentaal" "Jhaptaal"])
                   :raga-labels (zipmap (mapv :id ra/all-ragas)
                                        ["Bhoop" "Hansadhwani" "Bilaval" "Todi" "Tilak Kamod"])
                   :swara-labels (zipmap us/i-note-seq
                                         ["S"  "r" "R"  "g" "G" "M" "m" "P" "d" "D" "n" "N" "-" "ऽ"])
                   :raga "Raga"}
                  :hindi
                  {
                   :tala-labels (zipmap (mapv :id ta/all-talas) ["तीन्ताल" "झप्ताल"])
                   :raga-labels (zipmap (mapv :id ra/all-ragas) ["भूप" "हम्सध्वनि" "बिलावल" "तोडि" "तिलक कामोद"])
                   :swara-labels (zipmap us/i-note-seq
                                         ["सा" "रे॒" "रे" "ग॒" "ग" "म" "म॑" "प" "ध॒" "ध" "नि॒" "नि" "-" "ऽ"])
                   :raga "राग"}})

