(defproject studiokalavati/bhatkhande-viewer "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.516"]
                 [reagent "0.8.1"]
                 [studiokalavati/sargam-spec "0.1.0-SNAPSHOT"]
                 [reagent-utils "0.3.1"]
                 [cljs-bach "0.3.0"]
                 [re-frame "0.10.6"]
                 [quil "2.8.0"]
                 [cljs-ajax "0.5.5"]
                 [com.andrewmcveigh/cljs-time "0.5.0"]
                 [org.clojure/math.combinatorics "0.1.4"]
                 [re-com "2.5.0"]
                 [com.cognitect/transit-cljs "0.8.256"]
                 [compojure "1.6.1"]
                 [org.clojure/core.async "0.4.474"]
                 [pez/clerk "1.0.0"]
                 [bidi "2.1.4"]
                 [venantius/accountant "0.2.4"]
                 [org.clojars.nenadalm/uri "0.1.2-SNAPSHOT"]
                 [cljsjs/bootstrap "3.3.6-1"]
                 [chronoid "0.1.1"]
                 [funcool/promesa "2.0.1"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj" "src/cljs" "test/js"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]
  :figwheel {:css-dirs ["resources/public/css"]}

  :doo {:build "test"}
  :profiles
  {:dev
   {
   :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]} 
   :dependencies [[binaryage/devtools "0.9.10"]
                  [figwheel-sidecar "0.5.18" :exclusions [[org.clojure/tools.nrepl]]]
                   [cider/piggieback "0.3.10"]
                   [nrepl "0.6.0"]

                  ;;for re-frame-10x
                  [reagent "0.8.1"]
                  [day8.re-frame/re-frame-10x "0.4.0"]
                  [day8.re-frame/tracing "0.5.1"]


                   ]

    :plugins      [[lein-figwheel "0.5.18"]
                   [lein-doo "0.1.10"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "bhatkhande.core/mount-root"}
     :compiler     {:main                 bhatkhande.core
                    :output-to            "resources/public/js/compiled/bhatkhande.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "/js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :optimizations   :none
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}
     {:id           "prod"
     :source-paths ["src/cljs"]
     :compiler     {:main                 bhatkhande.core
                    :output-to            "resources/public/js/compiled/bhatkhande.js"
                    :language-out :ecmascript6
                    :optimizations   :advanced
                    }}
    
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:main          bhatkhande.runner
                    :output-to     "resources/public/js/compiled/kbdtest.js"
                    :output-dir    "resources/public/js/compiled/kbdtest/out"
                    :optimizations :none}}
    ]}
) 
