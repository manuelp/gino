(ns gino.core
  (:import [org.apache.commons.io FileUtils]
           [java.io File]
           [org.apache.commons.exec CommandLine DefaultExecutor]
           [net.schmizz.sshj SSHClient]
           [net.schmizz.sshj.xfer FileSystemFile]
           [net.schmizz.sshj.xfer.scp SCPFileTransfer]))

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

(defn copy-to-dir [file to-dir]
  (. FileUtils copyFileToDirectory (File. file) (File. to-dir)))

(defn copy-to-file [file to-file]
  (. FileUtils copyFile (File. file) (File. to-file)))


(defn remote-copy [file host username password dest]
  (let [ssh (new SSHClient)]
    (do (. ssh loadKnownHosts)
        (. ssh connect host)
        (. ssh authPassword username password)
        (. ssh useCompression)
        (. (. ssh newSCPFileTransfer) upload (new FileSystemFile file) dest))))
