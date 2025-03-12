package com.sisa;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;


public class LOG {
	 Logger logger;

	public LOG(String name) {
		// TODO Auto-generated constructor stub
		 logger = Logger.getLogger(LOG.class.getName());

	        // Create an instance of FileHandler that write log to a file called
	        // app.log. Each new message will be appended at the at of the log file.
	        FileHandler fileHandler;
			try {
				fileHandler = new FileHandler("C:\\eclipse_workspace\\jee-2022-06\\topo\\" + name + ".log", true);
				 logger.addHandler(fileHandler);

			        if (logger.isLoggable(Level.INFO)) {
			            logger.info("Information message");
			        }

			        if (logger.isLoggable(Level.WARNING)) {
			            logger.warning("Warning message");
			        }
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	}


	public void w(Level level, String msg) {
		 if (logger.isLoggable(Level.INFO)) {
	            logger.info(msg);
	        }

	        if (logger.isLoggable(Level.WARNING)) {
	            logger.warning(msg);
	        }
	}

}
