(ns penkit.client
  (:require [clojure.string :as str])
  (:use-macros [purnam.core :only [obj arr ! def.n def*n def* do*n]]
               [gyr.core :only [def.module def.filter
                                def.factory def.controller]]))
(enable-console-print!)
(def.module penkit ["ui.bootstrap"])
(defn localStorage->user
	[u] (if u (.fromJson js/angular u) (obj :sex "FEMALE")))
(defn pow [x p] (Math/pow x p))
(defn log10 [x] (Math/log10 x))
(defn sinclairM [w l]
	(* l (pow 10 (* 0.794358141 (pow (log10 (/ w 174.393)) 2)))))
(defn sinclairF [w l]
	(* l (pow 10 (* 0.897260740 (pow (log10 (/ w 148.026)) 2)))))
(defn sinclair [w l s] (if (= s "MALE") (sinclairM w l) (sinclairF w l)))
(defn wilksM [w l]
	(* l (/ 500 (+ -216.0475144 (* 16.2606339 w) (* -0.002388645 (pow w 2))
	(* -0.00113732 (pow w 3)) (* 7.01863E-06 (pow w 4)) (* -1.291E-08 (pow w 5))))))
(defn wilksF [w l]
	(* l (/ 500 (+ 594.31747775582 (* -27.23842536447 w) (* 0.82112226871 (pow w 2))
	(* -9.30733913E-03 (pow w 3)) (* 4.731582E-05 (pow w 4)) (* -9.054E-08 (pow w 5))))))
(defn wilks [w l s] (if (= s "MALE") (wilksM w l) (wilksF w l)))

(def.controller penkit.PenkitController [$scope $interval $window]
	(! $scope.user (localStorage->user (.getItem $window.localStorage :PENKIT!)))
	(def socket (js/io))
	(. socket (on "NOSTOT" (fn [msg] (! $scope.teams msg))))
	($interval (fn [] (! $scope.showDiff (not $scope.showDiff))) 1500)
	(! $scope.round (fn [x] (.toFixed (/ (Math/round (* 100 x)) 100) 2)))
  (! $scope.calculateWilks
		  (fn [x] ($scope.round (wilks $scope.user.weight $scope.user.lift $scope.user.sex))))
	(! $scope.state "NOSTA!")
  (! $scope.times 0)

  (! $scope.addLift
		(fn [form]
			(if form.$valid
				(do (.cancel $interval $scope.sendPromise)
          (! $scope.times (+ 1 $scope.times))
          (! $scope.state (str/join ["x" $scope.times " = " (* $scope.times $scope.user.lift) "kg"]))
          (! $scope.sendPromise
             ($interval (fn []
                          (let [lift (* $scope.times $scope.user.lift)]
                            (! $scope.state "LÄHETETTY!")
                            (.emit socket "NOSTO!" (clj->js {
						                      :team $scope.user.team
						                      :kg lift
						                      :sc (sinclair $scope.user.weight lift $scope.user.sex)
						                      :wk (wilks $scope.user.weight lift $scope.user.sex)
						                }))
					                  (.setItem $window.localStorage :PENKIT! (.toJson js/angular $scope.user))
                            (! $scope.times 0)
                          )) 1500 1))))))
  )

