;; This *recipe* implements the commands (and tasks if you want) to
;; build and deploy a [Play!](http://www.playframework.org/) v1.x
;; project in a remote Tomcat.
;;
;; Requirements:
;; - dirs have to exist
;; - `play` executable should be in the `PATH` environment variable
(ns gino.play
  (:use gino.core)
  (:require [clojure.string :as cs])
  (:import [java.io File]))

;; ## Editable configuration ##
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

;; ## Tasks ##
;; Here we define higher level operations (using the foundation laid
;; in the `core` namespace) for these specific recipes.

(defn read-version
  "Read the current version of the project in the working directory configured."
  []
  (cs/trim-newline (slurp (str app-dir separator "version.txt"))))

(defn create-war
  "Create WAR archive for the configured project."
  []
  (let [out-dir (str dest "-" (read-version))]
    (do
     (execute-cmd (build-cmd play ["war" app-dir "--%prod" "-o" out-dir "--zip"]))
     (remove-dir out-dir))))

(defn remove-tomcat-app
  "Removes the old WAR (both compressed and exploded) previously deployed in the local machine."
  []
  (do
    (remove-dir (str webapps-dir separator app-name))
    (. (new File (str webapps-dir separator app-name ".war")) delete)))

(defn deploy-local
  "Deploy the application"
  []
  (copy-to-file (str dest "-" (read-version) ".war") (str webapps-dir separator app-name ".war")))

;; ## Recipes ##
;; The actual recipes.

(defn local-deploy
  "Deploy the given application locally."
  []
  (do
    (create-war)
    (remove-tomcat-app)
    (deploy-local)))

(defn remote-deploy
  "Deploy the application to a remote host's Tomcat."
  [host username password]
  (do
    (create-war)
    (remote-copy (str dest "-" (read-version) ".war") host username password "/root/TS")))