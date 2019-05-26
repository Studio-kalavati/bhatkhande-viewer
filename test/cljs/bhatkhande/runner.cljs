(ns bhatkhande.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [bhatkhande.core-test]))

(doo-tests 'bhatkhande.core-test)
