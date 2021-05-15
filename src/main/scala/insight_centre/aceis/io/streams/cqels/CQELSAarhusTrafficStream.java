package insight_centre.aceis.io.streams.cqels;

import com.csvreader.CsvReader;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import org.deri.cqels.engine.ExecContext;
import insight_centre.aceis.eventmodel.EventDeclaration;
import insight_centre.aceis.eventmodel.TrafficReportService;
import insight_centre.aceis.io.rdf.RDFFileManager;
import insight_centre.aceis.io.streams.DataWrapper;
import insight_centre.aceis.observations.AarhusTrafficObservation;
import insight_centre.aceis.observations.SensorObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.hp.hpl.jena.graph.Node;

public class CQELSAarhusTrafficStream extends CQELSSensorStream implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CQELSAarhusTrafficStream.class);
	static long time1;
	// private CsvWriter cw;
	EventDeclaration ed;
	// private FileWriter fw;
	private File logFile;
	private Date startDate = null, endDate = null;
	private long messageCnt, byteCnt;
	String p1Street, p1City, p1Lat, p1Lon, p2Street, p2City, p2Lat, p2Lon, p1Country, p2Country, distance, id;
	// long sleep = 1000; // default frequency is 1.0
	// boolean stop = false;
	CsvReader streamData, metaData;
	Map<String, Integer> distanceByReportId = new HashMap<>();
	String txtFile;
	private List<String> lines = new ArrayList<String>();
	private Map<String, String> lFOI = new HashMap<>();

	// private Map<String, Integer> messageCnt;
	//
	// public Map<String, Integer> getMessageCnt() {
	// return messageCnt;
	// }
	//
	// public void setMessageCnt(Map<String, Integer> messageCnt) {
	// this.messageCnt = messageCnt;
	// }

	public CQELSAarhusTrafficStream(ExecContext context, String uri, String txtFile, EventDeclaration ed)
			throws IOException {
		super(context, uri);
		String fileName = "";
		if (this.getURI().split("#").length > 1)
			fileName = this.getURI().split("#")[1];
		else
			fileName = this.getURI();
		// logFile = new File("resultlog/" + fileName + ".csv");
		// fw = new FileWriter(logFile);
		// cw = new CsvWriter(fw, ',');
		// cw.write("time");
		// cw.write("message_cnt");
		// cw.write("byte_cnt");
		// cw.endRecord();
		// cw.flush();
		messageCnt = 0;
		byteCnt = 0;
		this.txtFile = txtFile;
		this.ed = ed;
		// time1 = time.getTime();
		streamData = new CsvReader(String.valueOf(txtFile));
		streamData.setTrimWhitespace(false);
		streamData.setDelimiter(',');
		streamData.readHeaders();
		// streamData.skipRecord();
		metaData = new CsvReader("dataset/MetaData/trafficMetaData.csv");
		metaData.readHeaders();
		streamData.readRecord();
		while (metaData.readRecord()) {
			if (!distanceByReportId.containsKey(metaData.get("REPORT_ID"))) {
				distanceByReportId.put(metaData.get("REPORT_ID"), Integer.parseInt(metaData.get("DISTANCE_IN_METERS")));
			}
		}
		metaData.close();
		/*while (metaData.readRecord()) {
			if (streamData.get("REPORT_ID").equals(metaData.get("REPORT_ID"))) {
				// p1Street = metaData.get("POINT_1_STREET");
				// p1City = metaData.get("POINT_1_CITY");
				// p1Lat = metaData.get("POINT_1_LAT");
				// p1Lon = metaData.get("POINT_1_LNG");
				// p1Country = metaData.get("POINT_2_COUNTRY");
				// p2Street = metaData.get("POINT_2_STREET");
				// p2City = metaData.get("POINT_2_CITY");
				// p2Lat = metaData.get("POINT_2_LAT");
				// p2Lon = metaData.get("POINT_2_LNG");
				// p2Country = metaData.get("POINT_2_COUNTRY");
				distance = metaData.get("DISTANCE_IN_METERS");
				// timestamp = metaData.get("TIMESTAMP");
				// id = metaData.get("extID");
				// stream(n(RDFFileManager.defaultPrefix + streamData.get("REPORT_ID")),
				// n(RDFFileManager.ctPrefix + "hasETA"), n(data.getEstimatedTime() + ""));
				// System.out.println("metadata: " + metaData.toString());
				metaData.close();
				break;
			}
		}*/
	}

	public CQELSAarhusTrafficStream(ExecContext context, String uri, String txtFile, EventDeclaration ed, Date start,
                                    Date end) throws IOException {
		super(context, uri);
		this.startDate = start;
		this.endDate = end;
		messageCnt = 0;
		byteCnt = 0;
		this.txtFile = txtFile;
		this.ed = ed;
		streamData = new CsvReader(String.valueOf(txtFile));
		streamData.setTrimWhitespace(false);
		streamData.setDelimiter(',');
		streamData.readHeaders();
		metaData = new CsvReader("dataset/MetaData/trafficMetaData.csv");
		metaData.readHeaders();
		streamData.readRecord();
		while (metaData.readRecord()) {
			if (!distanceByReportId.containsKey(metaData.get("REPORT_ID"))) {
				distanceByReportId.put(metaData.get("REPORT_ID"), Integer.parseInt(metaData.get("DISTANCE_IN_METERS")));
			}
		}
		metaData.close();
		/*while (metaData.readRecord()) {
			if (streamData.get("REPORT_ID").equals(metaData.get("REPORT_ID"))) {
				// p1Street = metaData.get("POINT_1_STREET");
				// p1City = metaData.get("POINT_1_CITY");
				// p1Lat = metaData.get("POINT_1_LAT");
				// p1Lon = metaData.get("POINT_1_LNG");
				// p1Country = metaData.get("POINT_2_COUNTRY");
				// p2Street = metaData.get("POINT_2_STREET");
				// p2City = metaData.get("POINT_2_CITY");
				// p2Lat = metaData.get("POINT_2_LAT");
				// p2Lon = metaData.get("POINT_2_LNG");
				// p2Country = metaData.get("POINT_2_COUNTRY");
				distance = metaData.get("DISTANCE_IN_METERS");
				// timestamp = metaData.get("TIMESTAMP");
				// id = metaData.get("extID");
				metaData.close();
				break;
			}
		}*/
	}

	@Override
	protected SensorObservation createObservation(Object objData) {
		// SensorObservation so = DataWrapper.getAarhusTrafficObservation((CsvReader) objData, ed);
		// DataWrapper.waitForInterval(currentObservation, so, startDate, getRate());
		// this.currentObservation = so;
		// return so;
		// return so;
		try {
			// CsvReader streamData = (CsvReader) objData;
			AarhusTrafficObservation data;
			// if (!this.txtFile.contains("mean"))
			data = new AarhusTrafficObservation(Double.parseDouble(streamData.get("REPORT_ID")),
					Double.parseDouble(streamData.get("avgSpeed")), Double.parseDouble(streamData.get("vehicleCount")),
					Double.parseDouble(streamData.get("avgMeasuredTime")), 0, 0, null, null, !streamData.get("Point1_Lat").isEmpty() ? Double.parseDouble(streamData.get("Point1_Lat")) : 0.0, !streamData.get("Point1_Long").isEmpty() ? Double.parseDouble(streamData.get("Point1_Long")) : 0.0, null, null, !streamData.get("Point2_Lat").isEmpty() ? Double.parseDouble(streamData.get("Point2_Lat")) : 0.0,
					!streamData.get("Point2_Long").isEmpty() ? Double.parseDouble(streamData.get("Point2_Long")) : 0.0, null, null, streamData.get("TIMESTAMP"));
			String obId = "AarhusTrafficObservation-" + streamData.get("_id");
			Double distance = Double.parseDouble(((TrafficReportService) ed).getDistance() + "");
			if (data.getAverageSpeed() != 0)
				data.setEstimatedTime(distance / data.getAverageSpeed());
			else
				data.setEstimatedTime(-1.0);
			if (distance != 0)
				data.setCongestionLevel(data.getVehicle_count() / distance);
			else
				data.setCongestionLevel(-1.0);
			data.setObId(obId);
			DataWrapper.waitForInterval(this.currentObservation, data, this.startDate, getRate());
			this.currentObservation = data;
			return data;
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected List<Statement> getStatements(SensorObservation so) throws NumberFormatException, IOException {
		// return DataWrapper.getAarhusTrafficStatements((AarhusTrafficObservation) data, ed);
		AarhusTrafficObservation data = (AarhusTrafficObservation) so;

		String idReport = data.getReport_ID()+"";
		Model m = ModelFactory.createDefaultModel();

		if(!lFOI.containsKey(idReport)){
			String idReportResource = RDFFileManager.defaultPrefix + "FOI-" + UUID.randomUUID();
			lFOI.put(idReport, idReportResource);

			Resource observationFoi = m.createResource(idReportResource);
			observationFoi.addProperty(RDF.type, m.createProperty(RDFFileManager.ssnPrefix + "FeatureOfInterest"));
			observationFoi.addLiteral(m.createProperty(RDFFileManager.ctPrefix + "hasStartLatitude"), data.getLatitude1() );
			observationFoi.addLiteral(m.createProperty(RDFFileManager.ctPrefix + "hasStartLongitude"), data.getLongtitude1() );
			observationFoi.addLiteral(m.createProperty(RDFFileManager.ctPrefix + "hasEndLatitude"), data.getLatitude2() );
			observationFoi.addLiteral(m.createProperty(RDFFileManager.ctPrefix + "hasEndLongitude"), data.getLongtitude2() );
		}
		else {
			logger.debug("lFOI.containsKey(idReport) ==> {}", idReport);
		}
		if (ed != null)
			for (String pStr : ed.getPayloads()) {
				// if (s.contains("EstimatedTime")) {
				// Resource observedProperty = m.createResource(s);
				String obId = data.getObId();
				Resource observation = m.createResource(RDFFileManager.defaultPrefix + obId + UUID.randomUUID());
				//CityBench.obMap.put(observation.toString(), data);
				// data.setObId(observation.toString());
				// System.out.println("OB: " + observation.toString());
				observation.addProperty(RDF.type, m.createResource(RDFFileManager.ssnPrefix + "Observation"));

				Resource serviceID = m.createResource(ed.getServiceId());
				observation.addProperty(m.createProperty(RDFFileManager.ssnPrefix + "observedBy"), serviceID);

				Resource observationProperty = m.createResource(RDFFileManager.defaultPrefix + "property-" + obId + UUID.randomUUID());
				observationProperty.addProperty(m.createProperty(RDFFileManager.ssnPrefix + "isPropertyOf"), m.createResource(lFOI.get(idReport)));
				//observation.addProperty(m.createProperty(RDFFileManager.ssnPrefix + "observedProperty"), m.createResource(pStr.split("\\|")(2)))
				observation.addProperty(m.createProperty(RDFFileManager.ssnPrefix + "observedProperty"), observationProperty);


				Property hasValue = m.createProperty(RDFFileManager.saoPrefix + "hasValue");
				// System.out.println("Annotating: " + observedProperty.toString());
				if (pStr.contains("AvgSpeed"))
					observation.addLiteral(hasValue, data.getAverageSpeed());
				else if (pStr.contains("VehicleCount")) {
					double value = data.getVehicle_count();
					observation.addLiteral(hasValue, value);
				} else if (pStr.contains("MeasuredTime"))
					observation.addLiteral(hasValue, data.getAvgMeasuredTime());
				else if (pStr.contains("EstimatedTime"))
					observation.addLiteral(hasValue, data.getEstimatedTime());
				else if (pStr.contains("CongestionLevel"))
					observation.addLiteral(hasValue, data.getCongestionLevel());
				// break;
				// }
			}
		return m.listStatements().toList();
	}

	public void run() {
		logger.info("Starting sensor stream: " + this.getURI() + " " + this.startDate + ", " + this.endDate);
		try {
			// Reads csv document for traffic metadata
			boolean completed = false;
			int cnt = 0;
			while (streamData.readRecord() && !stop) {
				Date obTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(streamData.get("TIMESTAMP"));
				logger.debug("Reading data: " + streamData.toString());
				if (this.startDate != null && this.endDate != null) {
					if (obTime.before(this.startDate) || obTime.after(this.endDate)) {
						logger.debug(this.getURI() + ": Disgarded observation @" + obTime);
						continue;
					}
				}
				AarhusTrafficObservation data = (AarhusTrafficObservation) this.createObservation(streamData);
				List<Statement> stmts = this.getStatements(data);
				long messageByte = 0;
				cnt += 1;
				// uncomment for testing the completeness, i.e., restrict the observations produced
				// if (cnt >= 2)
				// completed = true;
				try {
					if (completed) {
						logger.info("My mission completed: " + this.getURI());
						Thread.sleep(sleep);
						continue;
					}

				} catch (InterruptedException e) {

					e.printStackTrace();

				}
				for (Statement st : stmts) {
					stream(st.getSubject().asNode(), st.getPredicate().asNode(), st.getObject().asNode());
					// logger.info(this.getURI() + " Streaming: " + st.toString());
					messageByte += st.toString().getBytes().length;
				}
				this.messageCnt += 1;
				this.byteCnt += messageByte;
				if (sleep > 0) {
					try {
						if (this.getRate() == 1.0)
							Thread.sleep(sleep);
					} catch (InterruptedException e) {

						e.printStackTrace();

					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			logger.info("Stream Terminated: " + this.getURI());
			this.stop();
			// Thread.s
			// try {
			// cw.flush();
			// cw.close();
			// } catch (IOException e) {

			// e.printStackTrace();
			// }

		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	// public void setRate(int rate) {
	// sleep = sleep / rate;// should be 1000
	// }

}