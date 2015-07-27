(ns penkit.client 
  (:use-macros [purnam.core :only [obj arr ! def.n def*n def* do*n]]
			   [purnam.common :only [set-safe-aget]]
               [gyr.core :only [def.module def.filter
                                def.factory def.controller]]))

;https://purnam.github.io/purnam/
;https://github.com/purnam/gyr
;https://github.com/purnam/example.gyr/blob/master/src/example/gyr/todo.cljs
		   
(set-safe-aget false)

(enable-console-print!)

(def.module penkit [])

(def.controller penkit.PenkitController [$scope $interval]
	(! $scope.showDiff false)
	($interval (fn [] (! $scope.showDiff (not $scope.showDiff))) 1500)
	(! $scope.view "kg")
	(! $scope.teams (arr 
					{:name "OPH"
					 :totalScore {:kg 1234 :sinclair 435.44 :wilks 546.44}
					 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} } 
					{:name "LIHA"
					 :totalScore {:kg 1234.44 :sinclair 435 :wilks 546.44}
					 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} }
					{:name "VISIO"
					 :totalScore {:kg 1234.44 :sinclair 435.44 :wilks 546}
					 :difference {:kg 345.55 :sinclair 34.55 :wilks 564} }
					 ))
	(! $scope.round 
		(fn [x] (.toFixed (/ (Math/round (* 100 x)) 100) 2)))
	(! $scope.addLift
		(fn [form]
			(if form.$valid
				(println "jei")))))
					   
