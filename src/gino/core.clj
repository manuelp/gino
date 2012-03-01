(ns gino.core
  (:import [org.apache.commons.io FileUtils]
           [java.io File]
           [org.apache.commons.exec CommandLine DefaultExecutor]))

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

;; ---

(defn build-cmd [cmd args]
  (loop [cmdline (new CommandLine cmd)
         args args]
    (if (empty? args)
      cmdline
      (recur (. cmdline addArgument (first args))
             (rest args)))))

(defn execute-cmd [cmdline]
  (. (new DefaultExecutor) execute cmdline))

(defn remove-dir [directory]
  (. FileUtils deleteDirectory (File. directory)))

(defn copy-file [file to-dir]
  (. FileUtils copyFileToDirectory (File. file) (File. to-dir)))

(defn copy-file-to-file [file to-file]
  (. FileUtils copyFile (File. file) (File. to-file)))

;; ----------

;; TODO implement the "recipe" in another ns
;; TODO move configuration in a dedicated ns

;; TODO Read version from file
(defn create-war [version]
  (let [out-dir (str dest "-" version)]
    (do
     (execute-cmd (build-cmd play ["war" app-dir "--%prod" "-o" out-dir "--zip"]))
     (remove-dir out-dir))))

(defn remove-tomcat-app []
  (do
    (remove-dir (str webapps-dir separator app-name))
    (. (new File (str webapps-dir separator app-name ".war")) delete)))

(defn deploy-local [version]
  (copy-file-to-file (str dest "-" version ".war") (str webapps-dir separator app-name ".war")))