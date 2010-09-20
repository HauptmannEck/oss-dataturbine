/*
 * RDV
 * Real-time Data Viewer
 * http://rdv.googlecode.com/
 * 
 * Copyright (c) 2005-2007 University at Buffalo
 * Copyright (c) 2005-2007 NEES Cyberinfrastructure Center
 * Copyright (c) 2008 Palta Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * $URL: https://rdv.googlecode.com/svn/branches/RDV-1.9/src/org/rdv/rbnb/ChannelManager.java $
 * $Revision: 1149 $
 * $Date: 2008-07-03 10:23:04 -0400 (Thu, 03 Jul 2008) $
 * $Author: jason@paltasoftware.com $
 */

package org.rdv.rbnb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.rdv.datapanel.DigitalTabularDataPanel;

import com.rbnb.sapi.ChannelMap;

/**
 * @author Jason P. Hanley
 */
public class ChannelManager {
	
	static Log log = org.rdv.LogFactory.getLog(ChannelManager.class.getName());
	
	private Map<DataListener, List<String>> listenerChannelSubscriptions;
	private Map<String, Integer> channelSubscriptionCounts;
	private List<DataListener> playerChannelListeners;

	public ChannelManager() {
		listenerChannelSubscriptions = new HashMap<DataListener, List<String>>();
		channelSubscriptionCounts = new HashMap<String, Integer>();
		playerChannelListeners = new ArrayList<DataListener>();
	}

	public boolean subscribe(String channelName, DataListener listener) {
		//add channel to channel list for listener
		List<String> listenerChannelSubscription = listenerChannelSubscriptions.get(listener);
		if (listenerChannelSubscription == null) {
			listenerChannelSubscription = new ArrayList<String>();
			listenerChannelSubscriptions.put(listener, listenerChannelSubscription);
		}
		listenerChannelSubscription.add(channelName);
		
		//increment the count for this channel
		Integer count = channelSubscriptionCounts.get(channelName);
		if (count == null) {
			count = new Integer(1);
		} else {
			count = new Integer(count.intValue()+1);
		}
		channelSubscriptionCounts.put(channelName, count);

		//add the channel listener to the list
		if (!playerChannelListeners.contains(listener)) {
			playerChannelListeners.add(listener);
		}
		
		return true;
	}
	
	public boolean unsubscribe(String channelName, DataListener listener) {
		//remove channel from channel list for listener
		List<String> listenerChannelSubscription = listenerChannelSubscriptions.get(listener);
    
    //see if this listener and channel are actually subscribed
		if (listenerChannelSubscription == null || !listenerChannelSubscription.remove(channelName)) {
		  return false;
    }
    
		if (listenerChannelSubscription.size() == 0) {
			listenerChannelSubscriptions.remove(listener);
		}
		
		//decrement the count for this channel
		int count = channelSubscriptionCounts.get(channelName);
		if (count == 1) {
			channelSubscriptionCounts.remove(channelName);
		} else {
			channelSubscriptionCounts.put(channelName, --count);
		}
		
		//remove the channel listener from the list
		if (!isListenerSubscribedToAnyChannels(listener)) {
			playerChannelListeners.remove(listener);
		}

		return true;
	}
	
	private boolean isListenerSubscribedToAnyChannels(DataListener listener) {
		List<String> listenerChannelSubscription = listenerChannelSubscriptions.get(listener);
		return listenerChannelSubscription != null;
	}
		
	public boolean isChannelSubscribed(String channelName) {
		Integer count = channelSubscriptionCounts.get(channelName);
		return count != null;
	}

  /**
   * Returns true if there is at least one listener subscribed to a channel.
   * 
   * @return  true if there are channel listener, false if there are none
   */  
  public boolean hasSubscribedChannels() {
    return !playerChannelListeners.isEmpty();
  }
	
	public boolean isListenerSubscribedToChannel(String channelName, DataListener listener) {
		List<String> listenerChannelSubscription = listenerChannelSubscriptions.get(listener);
		if (listenerChannelSubscription != null) {
			return listenerChannelSubscription.contains(channelName);
		} else {
			return false;
		}
	}
  
  /**
   * Indicates whether the channel is only subscribed to listeners of type
   * <code>DigitalTabularDataPanel</code>.
   * 
   * @param channelName  the channel to check
   * @return             true if only tabular data panels are subscribed, false
   *                     if any other types listeners are subscribed to this
   *                     channel or no listeners are subscribed to this channel
   */
  public boolean isChannelTabularOnly(String channelName) {
    if (!isChannelSubscribed(channelName)) {
      return false;
    }
    
    for (DataListener listener : listenerChannelSubscriptions.keySet()) {
      if (listener instanceof DigitalTabularDataPanel) {
        continue;
      }      
      
      List<String> listenerChannelSubscription = listenerChannelSubscriptions.get(listener);
      if (listenerChannelSubscription == null) {
        continue;
      } else if (listenerChannelSubscription.contains(channelName)) {
        return false;
      }
    }

    return true;
  }
	
	public void postData(ChannelMap channelMap) {
		for (DataListener listener : playerChannelListeners) {
			try {
				listener.postData(channelMap);
			} catch (Exception e) {
				log.error("Failed to post time to " + listener + ".");
				e.printStackTrace();
			}
		}
	}
  
}