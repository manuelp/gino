(ns gino.play
  (:use gino.core)
  (:require [clojure.string :as cs])
  (:import [java.io File]))

;; Requirements:
;; - dirs have to exist
;; - play should be in the PATH

;; Editable configuration
;; NB: paths have to be absolute!
(def app-name "timesheet")
(def workspace-dir "/home/manuel/workspace")
(def dest-dir "/home/manuel/deploy/timesheet")
(def tomcat-dir "/opt/apache-tomcat-5.5.23")
(def play "/home/manuel/software/Play/play")

;; Derivated (do not edit!)
(def separator (. File separator))
(def app-dir (str workspace-dir separator app-name))
(def dest (str dest-dir separator app-name))
(def webapps-dir (str tomcat-dir separator "webapps"))

;; TODO Documentation (@wiki)
;; TODO implement "recipe" concept
;; TODO Logging needs some love, too

(defn read-version []
  (cs/trim-newline (slurp (str app-dir separator "version.txt"))))

(defn create-war []
  (let [out-dir (str dest "-" (read-version))]
    (do
     (execute-cmd (build-cmd play ["war" app-dir "--%prod" "-o" out-dir "--zip"]))
     (remove-dir out-dir))))

(defn remove-tomcat-app []
  (do
    (remove-dir (str webapps-dir separator app-name))
    (. (new File (str webapps-dir separator app-name ".war")) delete)))

(defn deploy-local []
  (copy-to-file (str dest "-" (read-version) ".war") (str webapps-dir separator app-name ".war")))

;; ----------
;; Recipes:
(defn local-deploy []
  (do
    (create-war)
    (remove-tomcat-app)
    (deploy-local)))

(defn remote-deploy [host username password]
  (do
    (create-war)
    (remote-copy (str dest "-" (read-version) ".war") host username password "/root/TS")))