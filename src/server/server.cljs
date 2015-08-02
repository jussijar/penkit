(ns penkit.server
  (:require [cljs.nodejs :as node]))
(enable-console-print!)
(def environment (js->clj js/process.env))
(def mongouri (get environment "MONGOLAB_URI" "mongodb://nostaja:password@localhost/penkit"))
(def port (get environment "PORT" 3000))
(def express (node/require "express"))
(def app (express))
(def http (. (node/require "http") (Server app)))
(def io ((node/require "socket.io") http))
(def mongojs (node/require "mongojs"))
(def nostot (atom (array-map)))
(defn update-nosto [[val-to-match] kg sc wk]
  (fn [nosto]
    (if (= val-to-match (get nosto "_id"))
        (assoc nosto  "kg" (+ kg (get nosto "kg"))
                      "sc" (+ sc (get nosto "sc"))
                      "wk" (+ wk (get nosto "wk"))
                      "dkg" (+ kg (get nosto "dkg"))
                      "dsc" (+ sc (get nosto "dsc"))
                      "dwk" (+ wk (get nosto "dwk")))
        nosto)))
(defn upsert-nostot [n team kg sc wk]
  (if (nil? (some #(= team (get % "_id")) n))
    (into [{"_id" team "kg" kg "sc" sc "wk" wk "dkg" kg "dsc" sc "dwk" wk}] n)
    (map (update-nosto [team] kg sc wk) n)
    ))
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
       }}]) f) n)
(def db (mongojs mongouri))
(def nCollection
  (fetchResults
     (.collection db "n")
       (fn [err result] (reset! nostot (js->clj result)))))

(defn -main [& args]
	(. io (on "connection" (fn [socket]
		(. socket (on "NOSTO!" (fn [msgjson]
				(let [msg (js->clj msgjson)
              team (get msg "team")
              kg (get msg "kg")
              sc (get msg "sc")
              wk (get msg "wk")]

        (.execute (let [bulk (.initializeUnorderedBulkOp nCollection)]
            (.insert bulk (clj->js {
                :createdAt (new js/Date)
                :team team
                :kg kg
                :sc sc
                :wk wk }))
            (.update (.upsert (.find bulk (clj->js {
                :$and [
						      {:createdAt {:$exists false}}
                  {:team team}
				    ]}))) (clj->js {
                :$set {:team team}
                :$inc {:kg kg :sc sc :wk wk }
            }))
            bulk) (fn [e] (.emit io "NOSTOT" (clj->js (reset! nostot
                            (upsert-nostot @nostot team kg sc wk))))
                    ))))))

		(.emit socket "NOSTOT" (clj->js @nostot))
		(print "It's on!"))))
	(. app (use (. express (static "resources"))))
	(. app (use (. express (static "target/js"))))
	(. http (listen port (fn [] (print "Upsy-daisy")))))

(set! *main-cli-fn* -main)
