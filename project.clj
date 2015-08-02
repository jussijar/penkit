(defproject penkit "0.0.1"
	:url "https://github.com/jussijar/penkit"
	:dependencies [	[org.clojure/clojure "1.6.0"]
					[org.clojure/clojurescript "0.0-2202"]
					[im.chit/purnam.core "0.5.2"]
					[im.chit/gyr "0.3.1"]]
	:jvm-opts ^:replace ["-Xmx1g" "-server"]
	:min-lein-version "2.0.0"
	:node-dependencies [[source-map-support "0.3.2"]]
	:plugins [[lein-cljsbuild "1.0.3"]
			[lein-ancient "0.6.7"]
			[lein-npm "0.5.0"]]
	:hooks [leiningen.cljsbuild]
	:clean-targets ["target"]
	:target-path "target"
	:cljsbuild {:builds[{:source-paths ["src/server"]
							:id "server"
							:compiler {
								:target :nodejs
								:language-in :ecmascript5
								:output-to "target/server.js"
								:optimizations :simple}}
						{:source-paths ["src/client"]
							:id "client"
							:compiler {	
									:main 'penkit.core
									:output-to "target/js/client.js"
									:optimzations :advanced
									:pretty-print true}}]})
