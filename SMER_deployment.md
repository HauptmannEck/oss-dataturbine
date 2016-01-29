## OVERVIEW ##
This document describes the proof of concept dataturbine architecture that was deployed at the Santa Margarita Ecological Reserve on 7/11/07.

## SYSTEM ARCHITECTURE ##
  * Data acquisition (DAQ) - National Instruments CompactRIO in an outdooor enclosure on HPWREN at 172.23.37.61 running software at: http://nladr-cvs.sdsc.edu/svn-public/CLEOS/labview/cleosRIO/
  * Point of presence (PoP) - Mac Mini at the SMER north station at 172.23.37.60 that:
    1. runs DaqToRbnb vs. DAQ at 172.23.37.61: http://nladr-cvs.sdsc.edu/svn-public/CLEOS/cleos-rbnb-apps/trunk/src/edu/sdsc/cleos/DaqToRbnb.java
    1. runs RBNB child of parent at 172.23.42.99
  * Uplink server (UL) - Debian Linux-x86 system at CSSD on HPWREN at 172.23.42.99; runs an RBNB server that aggregates the SMER RBNB data streams

Data acquisition (DAQ) -> Point of presence (PoP) -> Uplink server (UL)
172.23.37.60                      172.23.37.61                       172.23.42.99

## ADMIN TROUBLESHOOTING ##
Computer systems generally have a 'cleos' user from whose account RBNB and related extensions are run.
The CompactRIO DAQ system is currently configured for open access.

  * no data stream from DAQ -> PoP, RBNB infrastructure online
    1. make sure that the DAQ pings from the PoP
    1. stop the DaqToRbnb program on the PoP by doing:
    1. cleos/sw/bin/turbine-apps-rio.sh -k; kill -9 it, if necessary
    1. open the LabVIEW project: 'http://nladr-cvs.sdsc.edu/svn-public/CLEOS/labview/cleosRIO/cleosRio.lvproj' and run the programs 'daemon programs/server daemon' and '9205 RT' on the cRIO DAQ; verify data values being updated as the data streams
    1. start the DaqToRbnb program on the PoP by doing: cleos/sw/bin/turbine-apps-rio.sh -q; verify its connection indicated on the server daemon
    1. do "black box" verification of data streaming using some RBNB client (e.g. RDV or plot.jar)
  * no data stream from PoP -> UL, RBNB infrastructure offline
    1. make sure that the DAQ and PoP ping from UL
    1. stop the DaqToRbnb program on the PoP by doing: cleos/sw/bin/turbine-apps-rio.sh -k; kill -9 it, if necessary
    1. stop the RBNB server on the PoP by doing: ~cleos/sw/bin/rbnb stop
    1. stop the RBNB server on the UL by doing: sudo /etc/init.d/rbnb stop
    1. start back up in reverse order:
      1. 'sudo /etc/init.d/rbnb start' on UL
      1. '~cleos/sw/bin/rbnb start' on PoP
      1. '~cleos/sw/bin/turbine-apps-rio.sh -q' on PoP
    1. do "black box" verification of data streaming using some RBNB client (e.g. RDV or plot.jar)