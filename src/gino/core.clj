(ns gino.core
  (:import [org.apache.commons.io FileUtils]
           [java.io File]
           [org.apache.commons.exec CommandLine DefaultExecutor]))

;; Requirements:
;; - dirs have to exist
;; - play should be in the PATH

(def app-name "timesheet")
(def workspace-dir "~/workspace")
(def app-dir (str workspace-dir "/" app-name))
(def dest (str "~/deploy/" app-name))
(def tomcat-dir "/usr/share/tomcat6")

(defn create-war-cmd [workspace-dir app-name version dest]
  (str "play war " app-name " --%prod -o " dest "-" version))

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
