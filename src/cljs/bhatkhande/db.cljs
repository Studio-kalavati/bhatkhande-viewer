(ns bhatkhande.db
  (:require
   [bhatkhande.hindi :as hindi]
   [sargam.spec :as us]))

(defn percentage-95
  [i]
  (let [ iw (js/parseInt i)]
    (- iw (* 0.05 iw))))

                                        ;(def selected-language #'bhatkhande.hindi)
(def dispinfo
  {:x 20 :y 30 :under 30
   :x-start 20
   :y-inc 80
   :x-end (int (percentage-95 (.-innerWidth js/window)))
   :y-end (int (percentage-95 (.-innerHeight js/window)))
   :over 30
   :write-part-label true
   :write-comp-label true
   :write-line-separator true
   :language :hindi
   :swaramap #'bhatkhande.hindi/swaramap

   :kan {:kan-raise 10
         :reduce-font-size 5
         :reduce-spacing 3
         :reduce-octave-size 5}
   :octave 15
   :part-coordinates []
   :part-header-font-size 30
   :comp-label-font-size 35
   :header-y-spacing 50
   :sam-khaali 35
   :debug {:disp-swara false}
   :font-size 20
   ;:font-size 25
   :spacing 10 :text-align :left})

(def iseq1 [[{:note [:madhyam :s]}]
            [{:note [:taar :r]}]
            [{:note [:mandra :-n]}]
            [{:note [:madhyam :r]
              :kan [:madhyam :-g]}]
            [
             {:note [:taar :g]}
             {:note [:taar :r]}
             {:note [:mandra :n]}
             {:note [:madhyam :s]}
             {:note [:madhyam :d]}
             {:note [:madhyam :m]}
             ]
            [{:note [:taar :r]}
             {:note [:mandra :n]}
             {:note [:madhyam :s]}
             {:note [:madhyam :s]}
             {:note [:madhyam :m]}
             ]
            [{:note [:taar :r]}
             {:note [:mandra :n]}
             {:note [:madhyam :s]}
             {:note [:madhyam :s]}
             ]
            [{:note [:madhyam :-g]}
             {:note [:madhyam :m]}]
            [{:note [:madhyam :-]}
             {:note [:madhyam :m]}]
            [{:note [:taar :m+]}]
            [{:note [:taar :r]
              :kan [:taar :-g]}]
            [{:note [:mandra :r]
              :kan [:mandra :-g]}]
            [{:note [:madhyam :s]}]
            [{:note [:taar :r]}]
            [{:note [:mandra :-n]}]
            [{:note [:madhyam :r]
              :kan [:madhyam :-g]}]
            [{:note [:taar :r]}
             {:note [:mandra :n]
              :kan [:madhyam :-g]}
             {:note [:madhyam :s]}]
            [{:note [:madhyam :-g]}
             {:note [:madhyam :m]}]
            [{:note [:madhyam :s]}]
            [{:note [:taar :r]}]
            [{:note [:madhyam :g]}]
            [{:note [:madhyam :g]}]
            [{:note [:madhyam :-]}]
            [{:note [:madhyam :a]}]
            [{:note [:madhyam :g]}]
            [{:note [:madhyam :r] :meend-start true}]
            [{:note [:madhyam :r] :meend-end true}]
            [{:note [:madhyam :-g]}
             {:note [:madhyam :m]}
             {:note [:madhyam :m]}]
            ])

(def antara1 [[{:note [:madhyam :s]}]
              [{:note [:madhyam :r]}]
              [{:note [:mandra :-n]}]
              [{:note [:madhyam :r]
                :kan [:madhyam :s] }]
              [{:note [:madhyam :s]}]
              [{:note [:madhyam :r]}]
              [{:note [:mandra :-n]}]
              [{:note [:madhyam :s]}]
              [{:note [:madhyam :r]}]
              [{:note [:mandra :-n]}]
              ])

(def test1
  {:m-noteseq iseq1
   :taal {:num-beats 10 :taal-name :jhaptaal
          :taal-label "झपताल"
          :sam-khaali {1 "x" 3 "2" 8 "4" 6 "o"}
          :bhaags [2 3 2 3]}
   :part-label "partname"})

(def comp1 {:parts [{:m-noteseq iseq1
                     :part-label "स्थाइ"}
                    {:m-noteseq antara1 :part-label "अन्तरा"}]
            :taal {:num-beats 10 :taal-name :jhaptaal
                   :taal-label "झपताल"
                   :sam-khaali {1 "x" 3 "2" 8 "4" 6 "o"}
                   :bhaags [2 3 2 3]}
            :comp-id "fadaccaa"
            :comp-label "नीर भरन कैसे जाउ"})

(def default-db
  {:init-state {:cursor-color 1}
   :dim (mapv dispinfo [:x-end :y-end])
   :saved-comp comp1
   :saved-part test1
   :dispinfo dispinfo})
