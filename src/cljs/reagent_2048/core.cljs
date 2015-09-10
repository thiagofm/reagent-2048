(ns reagent-2048.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; Definitions

(def empty-tiles
  [[0 0 0 0]
   [0 0 0 0]
   [0 0 0 0]
   [0 0 0 0]])
(def no-score-addition
  0)
(def keycodes
  {38 :up
   39 :right
   40 :down
   37 :left
   72 :left
   75 :up
   76 :right
   74 :down})

;; -------------------------
;; Atoms

(defonce score (atom 0))
(defonce score-addition (atom 0))
(defonce tiles (atom empty-tiles))
(defonce game-over (atom false))

;; -------------------------
;; Functions

(defn push-left [tiles]
  (vec (let [rest (filter pos? tiles)]
             (concat (repeat (- 4 (count rest)) 0) rest))))

(defn evaluate-pair [pair-of-tiles]
  (let [[tile-1 tile-2] pair-of-tiles]
    (cond
      (= tile-1 tile-2)
        (let [new-value (* tile-2 2)]
          (swap! score-addition #(+ % (* tile-2 2)))
          (swap! score #(+ % (* tile-2 2)))
          [0 new-value])
      (= tile-2 0) [0 tile-1]
      :else [tile-1 tile-2])))

(defn evaluate-tiles [tiles]
  (push-left (loop [tiles tiles current-index 0]
    (if (= current-index (dec (count tiles)))
      tiles
      (let [[tile-1 tile-2] (evaluate-pair [(get tiles current-index) (get tiles (inc current-index))])]
        (->
          tiles
          (assoc current-index tile-1)
          (assoc (inc current-index) tile-2)
          (recur (inc current-index))))))))

(defn transpose [coll]
  (into []
        (apply (partial map vector)
               coll)))

(defn reverse-matrix [coll]
  (vec (map #(vec (reverse %)) coll)))

(defn rotate-plus-90 [coll]
  (transpose (reverse-matrix coll)))

(defn evaluate-up->down [tiles]
  (transpose (vec (map evaluate-tiles (transpose tiles)))))

(defn evaluate-right->left [tiles]
  (reverse-matrix (vec (map evaluate-tiles (reverse-matrix tiles)))))

(defn evaluate-left->right [tiles]
  (vec (map evaluate-tiles tiles)))

(defn evaluate-down->up [tiles]
  (reverse-matrix (rotate-plus-90 (vec (map evaluate-tiles (reverse-matrix (rotate-plus-90 tiles)))))))

(defn generate-2-or-4 []
  (if (> (rand) .9) 4 2))

(defn random-tile-indexes []
  (vec (take 2 (shuffle (for [x [0 1 2 3] y [0 1 2 3]] [x y])))))

(defn reset-score-addition! []
  (reset! score-addition no-score-addition))

(defn start-game! []
  (reset-score-addition!)
  (reset! tiles empty-tiles)
  (let [rts (random-tile-indexes)
        rt1 (first rts)
        rt2 (last rts)]
    (swap! tiles #(assoc-in % rt1 (generate-2-or-4)))
    (swap! tiles #(assoc-in % rt2 (generate-2-or-4)))))

(defn add-random-tile! []
  (loop [tile-added false]
    (when-not tile-added
      (let [random-line (rand-nth (range 0 4))
            random-column (rand-nth (range 0 4))
            random-tile (get-in @tiles [random-line random-column])]
        (if (zero? random-tile)
          (let [random-value (generate-2-or-4)]
            (swap! tiles #(assoc-in % [random-line random-column] (generate-2-or-4)))
            (recur true))
          (recur false))))))

(defn board-full? []
  (= 0 (count (filter zero? (flatten @tiles)))))

(defn board-changed? [old-tiles]
  (not= @tiles old-tiles))

(defn game-over? [old-tiles]
  (let [right (evaluate-left->right @tiles)
        down  (evaluate-up->down @tiles)
        left  (evaluate-right->left @tiles)
        up    (evaluate-down->up @tiles)]
    (= right down left up old-tiles)))

(defn compute-move [side]
  (reset! score-addition 0)
  (let [old-tiles @tiles]
    (when (game-over? old-tiles)
      (reset! game-over true))
    (->>
      (case side
        :right (evaluate-left->right @tiles)
        :down  (evaluate-up->down @tiles)
        :left  (evaluate-right->left @tiles)
        :up    (evaluate-down->up @tiles))
      (reset! tiles))
      (when (board-changed? old-tiles)
        (add-random-tile!))))

;; -------------------------
;; Keypresses

(defn keypress-handler [e]
  (let [keycode (.-which e)]
    (when (contains? keycodes keycode)
      (compute-move (get keycodes keycode))
      (.preventDefault e))))

(defn hook-keypress-detection! []
  (.addEventListener js/window "keydown" keypress-handler))

;; -------------------------
;; Components

(defn heading-component []
  [:div {:class "heading"}
   [:h1 {:class "title"} "2048"]
   [:div {:class "score-container"}
    @score
    [:div {:class "score-addition"}
     (when-not (zero? @score-addition) (str "+" @score-addition))]]])

(defn grid-component []
  [:div {:class "grid-container"}  
     [:div {:class "grid-row"}  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}] ]  
     [:div {:class "grid-row"}  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}] ]  
     [:div {:class "grid-row"}  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}] ]  
     [:div {:class "grid-row"}  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]  
      [:div {:class "grid-cell"}]]])

(defn tile-component []
  [:div {:class "tile-container"}
  (remove nil? (map-indexed
   (fn[y row-of-tiles]
     (map-indexed
      (fn[x tile]
        (when (> tile 0)
           ^{:key (str x y)} [:div {:class (str "tile tile-" tile " tile-position-" (inc x) "-" (inc y))} tile]))
      row-of-tiles))
   @tiles))])

(defn game-explanation-component []
  [:p {:class "game-explanation"}
   [:strong {:class "important"} "How to play:"] " Use your " 
   [:strong "arrow keys"] " to move the tiles. When two tiles with the same number touch, they " 
   [:strong "merge into one!"]])

(defn footer-component [] 
  [:p "\n  Created by " 
   [:a {:href "http://gabrielecirulli.com", :target "_blank"} "Gabriele Cirulli."] " Based on " 
   [:a {:href "https://itunes.apple.com/us/app/1024!/id823499224", :target "_blank"} "1024 by Veewo Studio"] " and conceptually similar to " 
   [:a {:href "http://asherv.com/threes/", :target "_blank"} "Threes by Asher Vollmer."]])

(defn application-layout [rest]
  [:div {:class "container"}
   (heading-component)
   [:p {:class "game-intro"} "Join the numbers and get to the "
    [:strong "2048 tile!"]]
   [:div {:class "game-container"}
    (when @game-over
      [:div {:class "game-message game-over"}
       [:p "Game over!" ]
       [:div {:class "lower"}
        [:a {:class "retry-button" :href "/"} "Try again"]]])
    rest]
   (game-explanation-component)
   [:hr]
   (footer-component)])

;; -------------------------
;; Pages

(defn start-game-page []
  (application-layout
   [:div
    (grid-component)
    (tile-component)]))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")
(secretary/defroute "/" []
  (session/put! :current-page #'start-game-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (start-game!)
  (hook-browser-navigation!)
  (hook-keypress-detection!)
  (mount-root))
