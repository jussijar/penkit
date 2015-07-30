(ns penkit.server
  (:require [cljs.nodejs :as node]))
(enable-console-print!)
(def express (node/require "express"))
(def app (express))
(def staticResources (. express (static "resources")))
(def compiledJs (. express (static "target/js")))
(def http (. (node/require "http") (Server app)))
(def socketio (node/require "socket.io"))
(def io (socketio http))
(def mongo (node/require "mongodb"))
(def nCollection (atom {}))
(def nostot (atom (array-map)))
(defn fetchResults [n f] (.aggregate n (clj->js [
		{ :$project {	:_id 0 
						:team 1
            :kg {:$cond [{:$gt [:$createdAt :null]} 0 :$kg]}
            :sc {:$cond [{:$gt [:$createdAt :null]} 0 :$sc]}
            :wk {:$cond [{:$gt [:$createdAt :null]} 0 :$wk]}
            :dkg {:$cond [{:$gt [:$createdAt :null]} :$kg 0]}
            :dsc {:$cond [{:$gt [:$createdAt :null]} :$sc 0]}
            :dwk {:$cond [{:$gt [:$createdAt :null]} :$wk 0]}
       }}
	   { :$group { 	:_id :$team
					:kg {:$sum :$kg} :sc {:$sum :$sc} :wk {:$sum :$wk}
					:dkg {:$sum :$dkg} :dsc {:$sum :$dsc} :dwk {:$sum :$dwk}
       }}]) f))
(def db (.connect mongo.MongoClient "mongodb://nostaja:password@localhost/penkit" (fn [err db] 
	(fetchResults (reset! nCollection (.collection db "n"))
		 (fn [err result] (do (print result) (reset! nostot (js->clj result)))))
)))

(defn -main [& args]
	(. io (on "connection" (fn [socket] 
		(. socket (on "NOSTO!" (fn [msgjson] 
			(let [msg (js->clj msgjson)]
			(do (print msg)
				(let [out { 
					:$set {:team (get msg "team")}
					:$inc {
					:kg (get msg "kg")
					:sc (get msg "sc")
					:wk (get msg "wk")
				}}]
				(.update @nCollection 
					(clj->js {:$and [
						{:createdAt {:$exists false}}
						{:team (get msg "team")}
					]})
					(clj->js out)
					(clj->js {:upsert true})
					(fn [err res] (print err)))
				(.insert @nCollection 
					(clj->js {
						:createdAt (new js/Date)
						:team (get msg "team")
						:kg (get msg "kg")
						:sc (get msg "sc")
						:wk (get msg "wk")
					})
					(fn [err res] (fetchResults @nCollection (fn [e result] 
						(.emit io "NOSTOT" (clj->js (reset! nostot (js->clj result))))
					)))
				)
				))))))
		(.emit socket "NOSTOT" (clj->js @nostot))
		(print "It's on!"))))
	(. app (use staticResources))
	(. app (use compiledJs))
	(. http (listen 3000 (fn [] (print "Upsy-daisy"))))
	)

(set! *main-cli-fn* -main)
