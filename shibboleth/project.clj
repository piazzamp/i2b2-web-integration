(defproject org.healthsciencessc.i2b2/shibboleth "0.1.0-SNAPSHOT"
  :description "Provides a shibboleth aware authentication layer to the webclient."
  
  :url "https://github.com/HSSC/i2b2-webclient"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :source-paths ["src/main/clojure"]
  :resource-paths ["src/main/resource"]
  :test-paths ["src/test/clojure"]
  
  ;; Keep java source and project definition out of the artifact
  :jar-exclusions [#"^\." #"^*\/\." #"\.java$" #"project\.clj"]
  
  :plugins [[lein-package "2.0.0"]]
  
  :hooks [leiningen.package.hooks.deploy 
          leiningen.package.hooks.install]
  
  :package {:autobuild true :reuse true}
  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [pliant/configure "0.1.1"]
                 [pliant/process "0.1.0"]])
