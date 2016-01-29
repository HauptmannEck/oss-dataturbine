# Introduction #

We need a network-based demo of live data for the NSF, these pages are to plan it out and explain the design.

## Rough plan ##

Start with a low-risk demo we **know** we can do, and plan cool additional bits as time/effort/resources permit.

### Base demo ###
Live data from SMER and marine images from Kenting, in a simplified RDV interface.

Details: SMER data has been reliably going to SDSU for months now, so the only issue is use of HWREN. A true RBNB mirror would solve that, too, but for now we have to punt and use a server shortcut.

RDV UI can be streamlined. We'll make our own branch of the RDV source code to accomplish this. Larry is on this.
Initial version here:
http://users.sdsc.edu/~ljmiller/side-tools/rdv/RDVsmer-sc.jnlp

Requires
  1. Demo data sources - SMER DAQ, SMER video, Kenting video, fake\_daq, whatever else we can muster
  1. niagara.sdsc.edu for RBNB host
  1. Custom RDV & JNLP for NSF

### In roughly increasing order of difficulty/risk ###
  1. Replace Axis 223m camera with iQeye unit. New housing, new Java codebase, but better integrated housing and nice test of new code. Diverse devices good. Requires
    1. Deployment of iQeye to SMER
  1. Campbell fuel moisture sensor to SMER for event detection. This requires
    1. Wiring the Campbell into the cRIO - perhaps a half day.
    1. New code for Campbell temperature sensor - very minimal
    1. New code for fuel moisture - non-trivial. The data comes off as a 600-1500Hz square wave, and the moisture percentage is proportional to the freqency of the wave. So we have to sample at 10-20kHz and run the data through a DSP routine. Larry and I have poked around a bit on the cRIO and figure this'll take a few days to write and debug.
  1. Monitoring page/portal. For this, we have Inca, Ebbe's code or perhaps the Tomcat. TBD.
  1. Additional data feeds - other cameras, data sources, etc. Mostly a question of time, configuration and "where to aim a camera that looks interesting"
  1. Simple event detection: Axis and iQeye have configurable motion detection. Use this plus webdav and glue code (perl?) to create an RBNB feed. Optionally generate events from the glue code. Can be tested locally, does require carefully configured FTP server and a bit of sysadmin + coding.
  1. MATLAB-based event detection: Much more interesting, we could run a video feed into RBNB and then look for changes/features using the matlab RBNB interface. This would require
    * Permission from HPWREN, even if we run MATLAB on the Mini at SMER
    * Deploying matlab at SMER, which would probably not work using the UCSD license server as HPWREN is on another subnet. Fixable by purchasing a statically-licensed copy of matlab.
    * Considerable code in MATLAB. I've written get/put image, but there's much to do on top of those!

## Other demo ideas ##
We could also try for a KML-based demo using google earth. Its planned anyway, the main difference is that adding geotagging to the various Java programs adds time and risk. Worth a try!