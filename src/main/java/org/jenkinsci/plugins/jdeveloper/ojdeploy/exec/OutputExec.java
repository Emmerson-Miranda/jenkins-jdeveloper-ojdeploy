package org.jenkinsci.plugins.jdeveloper.ojdeploy.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * 
 * @author Emmerson Miranda
 *
 */
public class OutputExec extends Thread {

	InputStream is;
	String type;
	PrintStream logger;

	public OutputExec(PrintStream logger, InputStream is, String type) {
		super();
		this.is = is;
		this.type = type;
		this.logger = logger;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				logger.println(type + line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
