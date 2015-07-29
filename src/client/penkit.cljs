(ns penkit.client 
  (:use-macros [purnam.core :only [obj arr ! def.n def*n def* do*n]]
               [gyr.core :only [def.module def.filter
                                def.factory def.controller]]))
(enable-console-print!)
(def.module penkit [])
(defn localStorage->user 
	[u] (if u (.fromJson js/angular u) (obj :sex "FEMALE")))
(def.controller penkit.PenkitController [$scope $interval $window]
	(! $scope.user (localStorage->user (.getItem $window.localStorage :PENKIT!)))
	(def socket (js/io))
	(. socket (on "NOSTOT" (fn [msg] (! $scope.teams msg))))
	($interval (fn [] (! $scope.showDiff (not $scope.showDiff))) 1500)
	(! $scope.round 
		(fn [x] (.toFixed (/ (Math/round (* 100 x)) 100) 2)))
	(! $scope.addLift
		(fn [form]
			(if form.$valid
				(do (print $scope.user)
					(.emit socket "NOSTO!" $scope.user)
					(.setItem $window.localStorage :PENKIT! (.toJson js/angular $scope.user)))
					
				))))
					   
