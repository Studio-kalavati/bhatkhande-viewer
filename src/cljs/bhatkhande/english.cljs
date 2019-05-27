(ns bhatkhande.english
  (:require [bhatkhande.ragas :as r]
            [bhatkhande.spec :as us]
            [bhatkhande.talas :as t]))

(def tala-labels (zipmap t/all-talas
                         ["Teentaal" "Jhaptaal"]))
(def raga-labels (zipmap r/all-ragas
                         ["Bhoop" "Hansadhwani" "Bilaval" "Todi" "Tilak Kamod"]))

(def swaramap (zipmap us/i-note-seq
                      ["s" "̱r" "r" "̱g" "g" "m" "̍m" "p" "̱d" "d" "̱n" "n" "-" "ऽ"]))
