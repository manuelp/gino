(defproject gino "0.1.1-dev"
  :description "Compile and deploy Play! projects on remove *nix hosts via SSH."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.apache.directory.studio/org.apache.commons.io "2.1"]
                 [org.apache.commons/commons-exec "1.1"]
                 [net.schmizz/sshj "0.7.0"]
                 [org.slf4j/slf4j-simple "1.6.4"]
                 [com.jcraft/jzlib "1.1.1"]])