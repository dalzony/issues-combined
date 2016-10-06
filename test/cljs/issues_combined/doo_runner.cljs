(ns issues-combined.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [issues-combined.core-test]))

(doo-tests 'issues-combined.core-test)

