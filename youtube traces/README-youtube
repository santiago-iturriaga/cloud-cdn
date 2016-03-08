youtube_traces.tgz contains two different sets of traces:

- youtube.parsed.*.dat
  These trace files contain information about client requests for YouTube
  video clips. Each line in a file represents such a request and is of the 
  following format:
  
  #Timestamp	    #YouTube server IP #Client IP    #Request #Video ID   #Content server IP
                     (anonymized)       (anonymized)  
  1189828805.208862 63.22.65.73        140.8.48.66   GETVIDEO lML9dik8QNw 158.102.125.12 


- flows.*.dat 
  These trace files contain information about the tranport session for video clips 
  requested by clients from the UMass campus network:
  Each line in the trace files contains information about such a session in the 
  following format:
    
  % id  source ip     sport  dest_ip       dport _pro dir    start_time  finishtime   duration     datapkts size_in_bytes rate    flags
        (anonymized)         (anonymized)                                             (in seconds)
  # 5   64.15.112.107 80     148.85.44.11  2365   6    1     0.464492    74.901594    74.437101    3182     4644692       499.180 16


