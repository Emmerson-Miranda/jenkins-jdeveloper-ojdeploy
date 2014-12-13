package org.jenkinsci.plugins.jdeveloper.ojdeploy.exec;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Emmerson Miranda
 *
 */
public class OjdeployExec {
	
	public int exec(PrintStream logger, String[] cmd, boolean disabled) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		long ini = System.currentTimeMillis();
		int exitVal = 0;
		try {
			Runtime rt = Runtime.getRuntime();
			logger.println(" ");
			logger.println("------------------------- START EXECUTING -------------------------");
			for(String p : cmd){
				logger.print(p + " ");
			}
			logger.println(" ");
			logger.println("Start at " + sdf.format(new Date()));
			logger.println(">>>>>");
			if(disabled){
				logger.println("This step is disabled.");
				exitVal = 0;
			}else{
				Process proc = rt.exec(cmd);
				OutputExec error = new OutputExec(logger, proc.getErrorStream(), "");
				OutputExec output = new OutputExec(logger, proc.getInputStream(), "");
				error.start();
				output.start();
				exitVal = proc.waitFor();
			}
		} catch (Throwable t) {
			t.printStackTrace(logger);
			exitVal = 1;
		}
		
		logger.println("<<<<<");
		logger.println("Finished at " + sdf.format(new Date()));
		logger.println("Time spend " + ((System.currentTimeMillis() - ini) / 1000) + "(seg)");
		logger.println("------------------------- END EXECUTING : " + (exitVal==0 ? "OK" : "ERROR") + " -------------------------" );
		
		return exitVal;
	}

}
