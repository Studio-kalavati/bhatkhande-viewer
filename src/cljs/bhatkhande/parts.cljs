(ns bhatkhande.parts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [bhatkhande.spec :as us]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [quil.core :as q]))

(defn incr-ith
  [k]
  (let [i (pop k)
        i1 (inc (peek k))]
    (if (empty? i)
      [i1]
      (conj i i1))) )



(defn disp-octave
  [dispinfo inp]
  (if (or
       (= :taar inp)
       (= :mandra inp))
    (let [disptext "·"
          {:keys [x y font-size text-align spacing octave] :as di} dispinfo
          y1 (if (= :taar inp) (- y octave) (+ y octave))]
      (q/text disptext x y1)
      dispinfo)
    dispinfo))

(defn disp-kan
  [dispinfo inp]
  (let [[oct note] inp
        ;_ (println " disp-kan " note " - " inp )
        {:keys [x y font-size spacing text-align kan octave] :as di} dispinfo
        {:keys [kan-raise reduce-font-size reduce-spacing reduce-octave-size]} kan
        y1 (- y kan-raise)
        _ (q/text-size (- font-size reduce-font-size))
        _ (q/text-align text-align)
        _ (disp-octave (assoc dispinfo :y y1 :octave (- octave reduce-octave-size)) oct)
        swaramap (:swaramap dispinfo)
        disptext (swaramap note)
        dip (update-in dispinfo [:x] (fn[i] (+ i (/ spacing reduce-spacing) (q/text-width disptext))))
        ]
    (q/text disptext x y1)
    dip))

(defn disp-sam-khaali
  [dispinfo inp]
  (let [{:keys [x y sam-khaali font-size]} dispinfo
        {:keys [beat]} inp]
    (when beat
      (do
        (q/text-size (- font-size 5))
        (q/text beat x (+ y sam-khaali))))
    dispinfo))

(defn disp-bhaag
  [dispinfo inp]
  (let [{:keys [x y x-start x-end sam-khaali font-size spacing y-inc]} dispinfo
        {:keys [bhaag]} inp ]
    (if bhaag
      (let [ix x 
            [ix y1] (if (>= ix (* 0.9 x-end)) [x-start (+ y y-inc)]
                        [ix y])]
        (do
          (q/stroke-weight 2)
          (q/line [ix (- y1 sam-khaali)] [ix (+ y1 sam-khaali)])
          (assoc dispinfo :x (+ ix spacing (q/text-width bhaag)) :y y1)))
      dispinfo)))

(defn disp-meend
  [dispinfo ]
  (let [over-text "︵"
        {:keys [x y over]} dispinfo]
    (do
      (q/text over-text x (- y over)))
    dispinfo))

(defn disp-swara
  [dispinfo inp]
  (let [{:keys [:note :kan :khatka :meend-start :meend-end bhaag]} inp
        swaramap (:swaramap dispinfo)
        disptext (swaramap (note 1))
        dispinfo (-> dispinfo
                     (update-in [:part-coordinates] #(conj % {:x (:x dispinfo)
                                                              :y (:y dispinfo)
                                                              :text disptext
                                                              :ith (:ith dispinfo)})))
        dispinfo (if kan (disp-kan dispinfo kan) dispinfo)
        _ (if meend-start (disp-meend dispinfo))
        ;;save the starting x y to save the part-coordinates
        
        {:keys [x y font-size spacing text-align ith] :as di} dispinfo
        _ (disp-sam-khaali dispinfo inp)
        _ (q/text-size font-size)
        _ (q/text-align text-align)
        _ (disp-octave dispinfo (note 0))
        dip (-> dispinfo
                (update-in [:x] (fn[i] (+ i spacing (q/text-width disptext))))
                (update-in [:ith] incr-ith))]
    (if (-> dispinfo :debug :disp-swara)
      (println " disp-swara text " disptext " x " x " y " y
               " spacing "spacing " width " (q/text-width disptext)))
    (q/text disptext x y)
    dip))

(defn disp-note
  [dispinfo inp]
  (let [{:keys [:note bhaag]} inp]
    (if bhaag
      (disp-bhaag dispinfo inp)
      (disp-swara dispinfo inp))))

(defn disp-underbrace
  [dispinfo ]
  (let [ under-m "︶"
        {:keys [x y under]} dispinfo]
    (q/text under-m x (+ y under))))


(defn disp-s-note
  "show an s-note"
  [dispinfo inp]
  (if (= 1 (count inp))
    (disp-note dispinfo (first inp))
    (let [ispa (:spacing dispinfo)
          _ (disp-underbrace dispinfo)
          res (reduce disp-note (-> dispinfo
                                    (assoc :spacing 1)
                                    (update-in [:ith] #(conj % 0))) inp)]
      ;;remove spacing so that the swaras as close together
      (-> res
          (assoc :spacing ispa :x (+ ispa (:x res)))
          (update-in [:ith] (comp incr-ith pop))))))

(defn add-cursor-at-end
  [{:keys [part-coordinates spacing] :as dispinfo}]
  (let [slist (reverse (sort-by (comp first :ith) part-coordinates))
        {:keys [x y ith ]} (first slist)
        tw (reduce str (mapv :text (take-while (fn[k] (= (first ith) (first (:ith k)))) slist)))]
    (update-in dispinfo [:part-coordinates]
               #(conj % {:x (+ x (q/text-width tw) spacing)
                         :y y :ith [(inc (first ith))]}))))

(defn disp-m-note
  [dispinfo inp]
  (->> (reduce disp-s-note dispinfo inp)
       add-cursor-at-end))

(defn disp-part-label
  [dispinfo inp]
  (let [{:keys [part-header-font-size x y header-y-spacing write-part-label]} dispinfo]
    (if write-part-label 
      (do 
        (q/text-size part-header-font-size) 
        (q/text inp x y) 
        (assoc dispinfo :y (+ y header-y-spacing)))
      dispinfo)))

(defn split-into-bhaags
  [inp bhaags]
  (let [redfn (fn[{:keys [pre post] :as m} i]
                (let [[f1 f2] (split-at i post)]
                  {:pre (conj pre (vec f1)) :post f2}))
        res (reduce redfn {:pre [] :post inp}
                    bhaags)
        intr (repeat (count bhaags) [[{:bhaag "|"}]])]
    (reduce into (->> (->> (:pre res) (remove empty?) vec) 
                      (interleave intr)))))

(defn append-bhaags
  [{:keys [:m-noteseq :taal :part-label] :as m}]
  (let [{:keys [:num-beats :sam-khaali :bhaags]} taal]
    (update-in m [:m-noteseq]
               #(->> % 
                     (partition-all num-beats )
                     (mapv (fn[i] (split-into-bhaags i bhaags)))
                     (reduce into)))))

(defn add-sam-khali
  [{:keys [:m-noteseq :taal :part-label] :as m}]
  (let [sam-khaali (taal :sam-khaali)
        nb (taal :num-beats)]
    (assoc m :m-noteseq
           (mapv #(if-let [iv (sam-khaali %2)]
                    (update-in %1 [0] (fn[i](assoc i :beat iv)))
                    %1)
                 m-noteseq
                 (iterate #(if (> nb %) (inc %) 1) 1)))))

(defn line-separator
  [dispinfo]
  (let [{:keys [x y x-end header-y-spacing write-line-separator]} dispinfo
        x2 (/ x-end 2)
        x3 (/ x-end 4)]
    (when write-line-separator 
      (q/line [(- x2 x3 ) (+ y header-y-spacing)] 
              [(+ x3 x2) (+ y header-y-spacing)]))))

(defn disp-part
  [dispinfo inp]
  (let [{:keys [x y x-start header-y-spacing]} dispinfo
        {:keys [:m-noteseq :taal :part-label]}
        (-> inp add-sam-khali append-bhaags)
        d1 
        (-> dispinfo
            (assoc :ith [0])
            (disp-part-label part-label)
            (disp-m-note m-noteseq))]
    (line-separator d1)
    (-> d1 
        (assoc :x x-start :y (+ (:y d1) (* 2 header-y-spacing)))
        (update-in [:part-coordinates] (comp vec reverse)))))

(defn disp-comp-label
  [dispinfo inp]
  (let [{:keys [x y  comp-label-font-size header-y-spacing write-comp-label]} dispinfo]
    (if write-comp-label
      (do 
        (q/text-size comp-label-font-size) 
        (q/text inp x y) 
        (line-separator dispinfo) 
        (assoc dispinfo :y (+ y (* 2 header-y-spacing))))
      dispinfo)))

(defn disp-comp
  [dispinfo inp]
  (let [{:keys [:parts :taal :comp-label :comp-id]} inp
        d1 (-> dispinfo
               (disp-comp-label comp-label))
        inp2 (mapv #(if (% :taal) %
                        (assoc % :taal taal))
                   parts)]
    (reduce disp-part d1 inp2)))
