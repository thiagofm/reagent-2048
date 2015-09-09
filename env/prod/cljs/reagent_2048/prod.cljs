(ns reagent-2048.prod
  (:require [reagent-2048.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
