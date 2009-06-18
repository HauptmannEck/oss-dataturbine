import java.text.DateFormat;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class DTESPConfigObjCreator
{
	/**
	 * Load XML setting file
	 * 	 * @param fn - filename
	 * 
	 */
	public DTESPConfigObj CreateFromXml(String fn)
	{
		DTESPConfigObj co=new DTESPConfigObj();
		
		Document dom=null;
		

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(fn);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	
		Element docEle = dom.getDocumentElement();
		
		NodeList nl ;
		

		// Start parsing nodes
		nl = docEle.getElementsByTagName("Setting");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) SetEnvironment(co,(Element)nl.item(i));
		
		
		nl = docEle.getElementsByTagName("RequestTime");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) SetTime(co,(Element)nl.item(i));

		
		nl = docEle.getElementsByTagName("Source");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddSource(new SourceItem((Element)nl.item(i)));
		
		nl = docEle.getElementsByTagName("Sink");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddSink(new SinkItem((Element)nl.item(i)));
		
		nl = docEle.getElementsByTagName("Event");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddEvent(new EventItem((Element)nl.item(i)));

		nl = docEle.getElementsByTagName("SinkChannel");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddSinkChannel(new SinkChannelItem((Element)nl.item(i),co));

		nl = docEle.getElementsByTagName("SourceChannel");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddSourceChannel(new SourceChannelItem((Element)nl.item(i),co));

		nl = docEle.getElementsByTagName("Query");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddQuery(new QueryItem((Element)nl.item(i),co));

		nl = docEle.getElementsByTagName("SaveData");
		if(nl != null && nl.getLength() > 0) 
			for(int i = 0 ; i < nl.getLength();i++) co.AddSaveData(new SaveDataItem((Element)nl.item(i),co));
	
		
		return co;
	}
	
	
	
	/**
	 * <pre>
	 *  set environment from xml file 
	 *  tag <Setting>
	 *  
	 *  Attributes:
	 *   esper_time_granuality_minute: 	maximum time step esper can advance at once (in minutes)
	 *   esper_time_granuality_sec:     maximum time step esper can advance at once (in sec)
	 *   output_level:					Console output level
     * 1 - input from DT & output from ESPER
     * 2 - output from ESPER
     * 3 - no input & output
	 *   
	 *   subscribe:						use subscribe to receive data from DT
	 */
	protected void SetEnvironment(DTESPConfigObj co, Element e)
	{
		try
		{
			co.maximum_time_granuality=new Integer(e.getAttribute("esper_time_granuality_minute"))*60*1000;
		}		catch (Exception e_) {}
		
		try
		{
			co.maximum_time_granuality=new Integer(e.getAttribute("esper_time_granuality_sec"))*1000;
		}		catch (Exception e_) {}

		try
		{
			co.output_level=new Integer(e.getAttribute("output_level"));
		}		catch (Exception e_) {}
		
		try
		{
			co.bSubscribe=new Integer(e.getAttribute("subscribe"))==1;
		}		catch (Exception e_) {}

	}
	
	/**
	 * <pre>
	 *  Set time of data want to retrieve from xml file
	 *  Tag <Requesttime> 
	 *  
	 *  Example: 4/10/2009 AM 5:57:0
	 *  <RequestTime year="2009" month="4" date="10" hour="5" minute="57" second="0"></RequestTime>
	 *  
	 *  Attributes:
	 *  mode					:	if mode=="start", this will start fetching data from the start
	 *  request_time_window_min	: length of data window to be requested for one fetch instruction in minutes   
	 */
	protected void SetTime(DTESPConfigObj co, Element e)
	{
		try
		{
			String mode=e.getAttribute("mode");
			if (mode.compareTo("start")==0)
			{
				co.request_start=-1;
				return;
			}
		}		
		catch (Exception e_) {}

		
		Calendar c=new GregorianCalendar();
    	c.clear();
    	
		try
		{
			int t= 	new Integer(e.getAttribute("year"));
			c.set(Calendar.YEAR,t);
		}		catch (Exception e_) {}
		
		try
		{
			int t= 	new Integer(e.getAttribute("month"));
			c.set(Calendar.MONTH,t-1);
		}		catch (Exception e_) {}

		try
		{
			int t= 	new Integer(e.getAttribute("date"));
			c.set(Calendar.DATE,t);
		}		catch (Exception e_) {}
		
		try
		{
			int t= 	new Integer(e.getAttribute("hour"));
			c.set(Calendar.HOUR,t);
		}		catch (Exception e_) {}

		try
		{
			int t= 	new Integer(e.getAttribute("minute"));
			c.set(Calendar.MINUTE,t);
		}		catch (Exception e_) {}

		try
		{
			int t= 	new Integer(e.getAttribute("second"));
			c.set(Calendar.SECOND,t);
		}		catch (Exception e_) {}

		try
		{
			int t= 	new Integer(e.getAttribute("request_time_window_min"));
			co.request_duration=t*60;
		}		catch (Exception e_) {}
		
		
		co.request_start=c.getTimeInMillis()/1000;
		DateFormat df=DateFormat.getInstance();
		
    	System.out.println("Requested time "+df.format(c.getTime()));

	}
}