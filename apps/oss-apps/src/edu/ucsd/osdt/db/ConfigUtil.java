package edu.ucsd.osdt.db;

/**
 * ConfigUtil: utility for reading and writing config file
 */

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;

import java.util.Iterator;
import java.util.List;
import java.io.FileWriter;
import java.io.File;
import java.util.LinkedList;
import java.util.HashMap;

import edu.ucsd.osdt.db.dt2dbMap;

public class ConfigUtil {


	LinkedList <String> chNames = new LinkedList<String> ();
	
	/**
	 * Write the given Config object to file; 
	 */
	public static void writeConfig( Config aConfig, String outputFileName ) {
		System.out.println( "writeConfig() called" );

		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement( "config" );

			// Output key-value params
			Element parElt = null;

			parElt = new DefaultElement( "param" );
			parElt.addAttribute( "name", "serverAddress" );
			parElt.addAttribute( "value", aConfig.getRbnbServerAddress() );
			root.add( parElt );

			parElt = new DefaultElement( "param" );
			parElt.addAttribute( "name", "serverPort" );
			parElt.addAttribute( "value", aConfig.getRbnbServerPort() );
			root.add( parElt );

			parElt = new DefaultElement( "param" );
			parElt.addAttribute( "name", "sampleIntParam" );
			parElt.addAttribute( "value", Integer.toString( aConfig.getSampleIntParam() ) );
			root.add( parElt );




			// Output table definitions
			List tables = aConfig.getTableConfigsAsList();

			for( Iterator tableIter = tables.iterator(); tableIter.hasNext(); ) {
				TableConfig aTable = (TableConfig) tableIter.next();

				Element tabElt = new DefaultElement( "table" );
				tabElt.addAttribute( "name", aTable.getName() );

				List columns = aTable.getTableConfigColumnsAsList();

				for( Iterator colIter = columns.iterator(); colIter.hasNext(); ) {
					TableConfigColumn aCol = (TableConfigColumn) colIter.next();

					Element colElt = new DefaultElement( "column" );
					colElt.addAttribute( "name", aCol.getName() );

					if( aCol.getChannelMapping() != null ) 
						colElt.addAttribute( "channelMapping", aCol.getChannelMapping() );

					
					// TODO  This needs iteration later.
					if( aCol.getDataValue() != null ) {
						colElt.addAttribute( "dataValue", aCol.getDataValue().getFirst() );
					}
					
					if( aCol.getType() != null ) {
						colElt.addAttribute( "type", aCol.getType().getFirst() );
					}
					
					tabElt.add( colElt );
				}

				root.add( tabElt );
			}

			// Write the xml to file 
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter( new FileWriter( outputFileName ), format );
			writer.write( document );
			writer.close();

			System.out.println( "Wrote the config to file: " + outputFileName );			

//			Pretty print the document to System.out
			writer = new XMLWriter( System.out, format );
			writer.write( document );

		}
		catch( Exception e ) {
			System.out.println( "Error: exception while writing config file!" );
			e.printStackTrace();
		}
	}

	/**
	 * Reads configuration from file into Config object;
	 * 	WARNING: comments in the file are not currently processed; 
	 * 		thus, if writing this config out to another file, comments will be lost.
	 */
	public static Config readConfig( String inputFileName ) {
		System.out.println( "readConfig() called" );

		Config aConfig = null;

		try {

			File inputFile = new File( inputFileName );	
			SAXReader reader = new SAXReader();
			Document doc = reader.read( inputFile );
			Element root = doc.getRootElement();

			aConfig = new Config();

			// Iterate through child elements of root with element name "param"
			for( Iterator parIter = root.elementIterator( "param" ); parIter.hasNext(); ) {
				Element aParElt = (Element) parIter.next();
				String parName = aParElt.attributeValue( "name" );
				String parValue = aParElt.attributeValue( "value" );

				System.out.println( "Found param: name=" + parName + ", value=" + parValue );

				// Set key value params
				if( parName.equals( "rbnbServerAddress" ) ) {
					aConfig.setRbnbServerAddress( parValue );
				}
				else if( parName.equals( "rbnbServerPort" ) ) {
					aConfig.setRbnbServerPort( parValue );
				}
				else if( parName.equals( "sampleIntParam" ) ) {
					aConfig.setSampleIntParam( Integer.parseInt( parValue ) );
				}
				
				// key values for db
				else if ( parName.equals("dbServerName") ) {
					aConfig.setDbServerName(parValue);
				}
				else if ( parName.equals("jdbcDriverName")) {
					aConfig.setJdbcDriverName(parValue);
				}
				else if ( parName.equals("dbName")) {
					aConfig.setDbName(parValue);
				}
				else if (parName.equals("dbUserName")) {
					aConfig.setDbUserName(parValue);
				}
				else if ( parName.equals("dbPassword")) {
					aConfig.setDbPassword(parValue);
				}
								
				else if (parName.equals("sysLoggerServerName")){
					aConfig.setSysLogServerAddress(parValue);
				}
				
				// key value for Sink Client Manger program
				else if ( parName.equals("startTimeFilePath")) {
					aConfig.setStartTimeFilePath(parValue);
				}
				else if (parName.equals("durationSeconds")) {
					aConfig.setDurationSeconds(Double.parseDouble(parValue));
				}
				else if (parName.equals("stopAtError")) {
					if (parValue.equals("NO") ){
						aConfig.setStopAtError(false);
					}
					else
						aConfig.setStopAtError(true);
				}
				else if (parName.equals("continueFlagFile")) {
					aConfig.setContinueFlagFile(parValue);
				}
				else if (parName.equals("emailContact")) {
					aConfig.setEmailContact(parValue);
				}
				else if (parName.equals("dataModel")) {
					aConfig.setDataModel(parValue);
				}
				else {
					System.out.println( "Error: unrecognized param (name=" + parName + ")!" );
				}

			}

			HashMap <String, dt2dbMap> mapper = new HashMap <String, dt2dbMap>();
			LinkedList <String> chNames = new LinkedList<String> ();
			// Iterate through child elements of root with element name "table"
			for( Iterator tableIter = root.elementIterator( "table" ); tableIter.hasNext(); ) {
				Element aTableElt = (Element) tableIter.next();
				String tableName = aTableElt.attributeValue( "name" );
				System.out.println( "Found table; name = " + tableName );

				TableConfig aTable = new TableConfig();
				aTable.setName( tableName );

				
				// Iterate through child elements of current table with element name "column"
				for( Iterator colIter = aTableElt.elementIterator( "column" ); colIter.hasNext(); ) {
					Element aColElt = (Element) colIter.next();
					String colName = aColElt.attributeValue( "name" );
					System.out.print( "Found column; name = " + colName );

					TableConfigColumn aCol = new TableConfigColumn();
					aCol.setName( colName );

					if( aColElt.attributeValue( "channelMapping" ) != null ) {
						aCol.setChannelMapping( aColElt.attributeValue( "channelMapping" ) );
						System.out.print( "... has channelMapping: " + aCol.getChannelMapping() );						
						chNames.add(aCol.getChannelMapping());
						dt2dbMap ddm =new dt2dbMap (aCol.getChannelMapping(), tableName, colName); 
						mapper.put(aCol.getChannelMapping(), ddm);
					}

					if( aColElt.attributeValue( "dataValue" ) != null ) {
						aCol.setDataValue( aColElt.attributeValue( "dataValue" ) );
						System.out.print( "... has dataValue: " + aCol.getDataValue() );						
					}

					if( aColElt.attributeValue( "type" ) != null ) {
						aCol.setType( aColElt.attributeValue( "type" ) );
						System.out.print( "... has type: " + aCol.getType() );						
					}

					System.out.println();


					aTable.putTableConfigColumn( aCol );

				}

				aConfig.putTableConfig( aTable );
			}

			

			
			for (Iterator mapItr = root.elementIterator("mapping"); mapItr.hasNext();) {
				Element aMapElt = (Element) mapItr.next();
				String tableName = aMapElt.attributeValue( "tableName" );
				System.out.println( "Found table; name = " + tableName );
				
				String colName = aMapElt.attributeValue( "colName" );
				System.out.println( "Found column; name = " + colName );
				
				String rbnbChannel = aMapElt.attributeValue( "rbnbChannel" );
				System.out.println( "Found table; name = " + rbnbChannel );
				
				chNames.add(rbnbChannel);
				dt2dbMap ddm = new dt2dbMap (rbnbChannel, tableName, colName);
				
				// Iterate through child elements of current table with element name "column"
				for( Iterator colIter = aMapElt.elementIterator( "value" ); colIter.hasNext(); ) {
					Element aColElt = (Element) colIter.next();
					String valueName = aColElt.attributeValue( "valueName" );
					System.out.print( "Found value; valueName = " + valueName );

					String value = aColElt.attributeValue( "value" );
					System.out.print( "Found value; valueName = " + value);

					String type = aColElt.attributeValue( "type" );
					System.out.print( "Found value; valueName = " + type);

					System.out.println();

					ddm.addValue(valueName, value, type);
					mapper.put(rbnbChannel, ddm);
				}
			}
			aConfig.setDt2dbMap(mapper);
			aConfig.setChNames(chNames);
		}
				

		catch( Exception e ) {
			System.out.println( "Error: caught exception while reading config file!" );
			e.printStackTrace();
		}

		return aConfig;
	}
	
	public LinkedList<String> getChNames() {
		return this.chNames;
	}

	public static void main( String[] args ) {
		String inputFileName =  "/Users/petershin/Downloads/config-parser/config.xml";
		//String inputFileName =  "./config2.xml";
		//String inputFileName =  "./config3.xml";
		String outputFileName =  "/Users/petershin/Downloads/config-parser/outputConfig.xml";
		
		Config aConfig = ConfigUtil.readConfig( inputFileName );
		
		System.out.println();

		ConfigUtil.writeConfig( aConfig, outputFileName );
	}
}
