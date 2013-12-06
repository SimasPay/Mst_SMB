package com.mfino.provision.tools.propertymanager;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogHandlerPM {

	LogManager lm = LogManager.getLogManager();
	static boolean append = true;
	static final DateFormat format = new SimpleDateFormat("h:mm:ss");
	static final String lineSep = System.getProperty("line.separator");
	static FileHandler fh;
	static Handler ch = new ConsoleHandler();

	Logger logger = Logger
			.getLogger("com.mfino.tools.Provisions.PropertyManager.logger");

	public LogHandlerPM(String logLocation, boolean debug)
			throws SecurityException, IOException {

		consoleLogFormatter formatterFull = new consoleLogFormatter();
		ch.setFormatter(formatterFull);

		String logFileLocation = logLocation;
		this.lm.addLogger(this.logger);
		fh = new FileHandler(logFileLocation, append);
		FileLogFormatter fileformatter = new FileLogFormatter();
		fh.setFormatter(fileformatter);
		// fh.setFormatter(new SimpleFormatter());
		this.logger.addHandler(fh);
		this.logger.addHandler(ch);

		this.logger.setLevel(Level.FINEST);
		ch.setLevel(Level.SEVERE);
		if (debug == false) {
			fh.setLevel(Level.INFO);
		} else {
			fh.setLevel(Level.ALL);
		}
		this.logger.setUseParentHandlers(false);
	}

	static class consoleLogFormatter extends Formatter {

		@Override
		public String format(LogRecord record) {

			String loggerName = record.getLoggerName();
			if (loggerName == null) {
				loggerName = "root";
			}
			StringBuilder output = new StringBuilder()
			// .append(loggerName)
			// .append("[")
			// .append(record.getLevel()).append('|')
			// .append(Thread.currentThread().getName()).append('|')
			// .append(format.format(new Date(record.getMillis())))
			// .append("]: \n")
					.append(record.getMessage()).append(' ').append(lineSep);
			return output.toString();
		}

	}

	static class FileLogFormatter extends Formatter {

		@Override
		public String format(LogRecord record) {

			String loggerName = record.getLoggerName();
			if (loggerName == null) {
				loggerName = "root";
			}
			StringBuilder output = new StringBuilder()
					// .append(loggerName)
					.append("[").append(record.getLevel())
					.append('|')
					// .append(Thread.currentThread().getName()).append('|')
					.append(format.format(new Date(record.getMillis())))
					.append("]: \n").append(record.getMessage()).append(' ')
					.append(lineSep);

			return output.toString();
		}

	}

}
