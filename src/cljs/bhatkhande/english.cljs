(ns bhatkhande.english
  (:require [sargam.ragas :as r]
            [sargam.spec :as us]
            [sargam.talas :as t]))

(def tala-labels (zipmap t/all-talas
                         ["Teentaal" "Jhaptaal"]))
(def raga-labels (zipmap r/all-ragas
                         ["Bhoop" "Hansadhwani" "Bilaval" "Todi" "Tilak Kamod"]))

(def swaramap (zipmap us/i-note-seq
                      ["s" "̱r" "r" "̱g" "g" "m" "̍m" "p" "̱d" "d" "̱n" "n" "-" "ऽ"]))
