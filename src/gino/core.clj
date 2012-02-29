(ns gino.core
  (:import [org.apache.commons.io FileUtils]
           [java.io File]
           [org.apache.commons.exec CommandLine DefaultExecutor]))

;; Requirements:
;; - dirs have to exist
;; - play should be in the PATH

;; Editable configuration
(def app-name "timesheet")
(def workspace-dir "~/workspace")
(def dest-dir "~/deploy")
(def tomcat-dir "/usr/share/tomcat6")

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

(defn remove-dir [directory]
  (. FileUtils deleteDirectory (File. directory)))

;; TODO Read version from file
(defn create-war [version]
  (execute-cmd (build-cmd "play" ["war" app-name "--%prod" "-o" (str dest "-" version)])))
