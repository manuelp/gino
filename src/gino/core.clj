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
(def tomcat-dir "/usr/share/tomcat6")
(def play "/home/manuel/bin/play")

;; Derivated (do not edit!)
(def separator (. File separator))
(def app-dir (str workspace-dir separator app-name))
(def dest (str dest-dir separator app-name))

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

;; TODO Read version from file
(defn create-war [version]
  (execute-cmd (build-cmd play ["war" app-dir "--%prod" "-o" (str dest "-" version) "--zip"])))

(defn remove-dir [directory]
  (. FileUtils deleteDirectory (File. directory)))

;; ----------

;; TODO implement the "recipe" in another ns
;; TODO move configuration in a dedicated ns

;; TODO Test with unprivileged user on Linux
(defn remove-tomcat-app []
  (let [webapps-dir (str tomcat-dir separator "webapps")]
    (do
     (remove-dir (str webapps-dir separator app-name))
     (. (new File (str webapps-dir separator app-name ".war")) delete))))
