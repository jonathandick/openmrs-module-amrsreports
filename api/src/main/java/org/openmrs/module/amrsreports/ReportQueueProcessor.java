package org.openmrs.module.amrsreports;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.service.QueuedReportService;

/**
 * Processor for queued reports
 */
public class ReportQueueProcessor {

	private final Log log = LogFactory.getLog(this.getClass());

	private static Boolean isRunning = false; // allow only one running

	private static Integer count = 0;

	/**
	 * Empty constructor (requires context to be set using <code>setContext(Context)</code> method before any other calls
	 * are made)
	 */
	public ReportQueueProcessor() {
	}

	/**
	 * Process a single queue entry
	 */
	public void processQueuedReport(QueuedReport queuedReport) {

		if (log.isDebugEnabled())
			log.debug("Processing queued report (id=" + queuedReport);

		try {
			Context.getService(QueuedReportService.class).processQueuedReport(queuedReport);
		} catch (Exception e) {
			log.error("Unable to process hl7 in queue", e);
		}

		if (++count > 25) {
			// clean up memory after processing each queue entry (otherwise, the
			// memory-intensive process may crash or eat up all our memory)
			try {
				Context.clearSession();
			} catch (Exception e) {
				log.error("Exception while clearing session in report queue processor", e);
			}
		}

	}

	/**
	 * Transform the next queue entry. If there are no pending items in the queue, this method simply returns quietly.
	 *
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public boolean processNextQueuedReport() {
		boolean entryProcessed = false;
		QueuedReport queuedReport = Context.getService(QueuedReportService.class).getNextQueuedReport();
		if (queuedReport != null) {
			processQueuedReport(queuedReport);
			entryProcessed = true;
		}
		return entryProcessed;
	}

	/**
	 * Starts up a thread to process all existing queue entries
	 */
	public void processQueuedReports() throws HL7Exception {
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("Report queue processor aborting (another processor already running)");
				return;
			}
			isRunning = true;
		}
		try {
			log.debug("Start processing queued reports");
			while (processNextQueuedReport()) {
				// loop until queue is empty
			}
			log.debug("Done processing queued reports");
		} finally {
			isRunning = false;
		}
	}
}