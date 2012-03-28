(ns gino.core
  (:import [org.apache.commons.io FileUtils IOUtils]
           [java.io File BufferedReader InputStreamReader]
           [java.util.concurrent TimeUnit]
           [org.apache.commons.exec CommandLine DefaultExecutor]
           [net.schmizz.sshj SSHClient]
           [net.schmizz.sshj.xfer FileSystemFile]
           [net.schmizz.sshj.xfer.scp SCPFileTransfer]
           [net.schmizz.sshj.transport.verification PromiscuousVerifier]))

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

(defn read-cmd-output [is]
  (.readLine (new BufferedReader (new InputStreamReader is))))

;; PromiscuousVerifier is for: https://gist.github.com/1321719#gistcomment-62366
(defn remote-cmd [host username password cmd]
  (let [ssh (new SSHClient)]
    (do (.addHostKeyVerifier ssh (new PromiscuousVerifier))
        (.connect ssh host)
        (.authPassword ssh username password)
        (.useCompression ssh)
        (.addHostKeyVerifier ssh "08:19:b9:c1:0b:33:71:bc:6e:24:db:45:3d:f4:a6:7b")
        (read-cmd-output (.getInputStream (.exec (.startSession ssh) cmd))))))
