


import java.util.*;








public class DTESPConfigObj {



	
	/**
	 * List of source configuration(SourceItem)
	 */
	HashMap<String, SourceItem> 		hmap_source_item			= new HashMap<String, SourceItem>();
	/**
	 * List of sink configuration(SinkItem)
	 */
	HashMap<String, SinkItem> 			hmap_sink_item				= new HashMap<String, SinkItem>();			
	/**
	 * List of source channel configuration(SourceChannelItem)
	 */
	HashMap<String, SourceChannelItem> 	hmap_source_channel_item	= new HashMap<String, SourceChannelItem>();
	/**
	 * List of sink channel configuration(SinkChannelItem)
	 */
	HashMap<String, SinkChannelItem> 	hmap_sink_channel_item		= new HashMap<String, SinkChannelItem>();
	/**
	 * List of event configuration(EventItem)
	 */
	HashMap<String, EventItem>			hmap_event_item				= new HashMap<String, EventItem>();
	/**
	 * List of query configuration(QueryItem)
	 */
	List<QueryItem>						list_query_item				= new LinkedList<QueryItem>();

	/**
	 * List of SaveData (save data to DT before running)
	 */
	LinkedList<SaveDataItem>			list_save_data_item			= new LinkedList<SaveDataItem>();

	
	/**
	 * Add source configuration to list
	 */
	public SourceItem AddSource(SourceItem si)
	{
		hmap_source_item.put(si.name, si);
		return si;
	}
	/**
	 * Retrieve source by its name 
	 */
	public SourceItem GetSource(String name)
	{
		return hmap_source_item.get(name);
	}

	/**
	 * Add sink configuration to list
	 */
	public SinkItem AddSink(SinkItem si)
	{
		hmap_sink_item.put(si.name, si);
		return si;
	}
	/**
	 * Retrieve sink by its name 
	 */
	public SinkItem GetSink(String name)
	{
		return hmap_sink_item.get(name);
	}
	
	/**
	 * Add source channel configuration to list
	 */	
	public void AddSourceChannel(SourceChannelItem sci)
	{
		hmap_source_channel_item.put(sci.name, sci);
	}
	
	/**
	 * Add sink channel configuration to list
	 */	
	public void AddSinkChannel(SinkChannelItem sci)
	{
		hmap_sink_channel_item.put(sci.name, sci);
	}
	
	/**
	 * Retrieve source channel by its name 
	 */	
	public SourceChannelItem	GetSourceChannel(String name)
	{
		return hmap_source_channel_item.get(name);
	}
	/**
	 * Add event configuration to list
	 */	
	public void AddEvent(EventItem ei)
	{
		hmap_event_item.put(ei.name, ei);
	}
	/**
	 * Retrieve event channel by its name 
	 */	
	public EventItem GetEvent(String name)
	{
		return hmap_event_item.get(name);
	}
	/**
	 * Add query configuration to list
	 */	
	public void AddQuery(QueryItem qi)
	{
		list_query_item.add(qi);
	}
	
	public void AddSaveData(SaveDataItem sd)
	{
		list_save_data_item.add(sd);
	}
	
	
	
    /** 
     *  Use subscribe to receive data from data turbine
     */	
	public boolean bSubscribe=false;
	
    /** 
     *  Start time of the request
     */	
	double request_start=0;
    /** 
     *  Data duration for one fetch
     */	
	double request_duration=600; // 10 min
    /** 
     * Last time of esper we set 
     */	
	long last_saved_esper_time;
    /**
     * <pre> 
     * maximum of time advancement for esper
     * 
     *   Example:
     *   Current esper time is 0 sec. maximum_time_granuality is .5 sec. Next data came at 2 sec. 
     *   Then, esper time will be set at .5 sec, 1 sec, 1.5 sec, and 2 sec.
     */	
	long maximum_time_granuality=60000; // 1 min
	
    /** 
     * <pre>
     * Console output level
     *
     * 1 - input from DT & output from ESPER
     * 2 - output from ESPER
     * 3 - no input & output
     * 4 - none
     */	
	int	output_level=1;
	
}







