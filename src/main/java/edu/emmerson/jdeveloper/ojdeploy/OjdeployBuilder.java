package edu.emmerson.jdeveloper.ojdeploy;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import edu.emmerson.jdeveloper.ojdeploy.exec.OjdeployExec;
import edu.emmerson.jdeveloper.ojdeploy.validator.ProfileValidation;

/**
 *
 *
 * @author Emmerson Miranda
 */
public class OjdeployBuilder extends Builder {

	private final String path_ojdeploy;
	private final String arg_workspace;
	private final String arg_buildfileschema;

	private final String opt_basedir;
	private final String opt_outputfile;
	private final String opt_nocompile;
	private final String opt_clean;
	private final String opt_nodatasources;
	private final String opt_forcerewrite;
	private final String opt_updatewebxmlejbrefs;
	private final String opt_define;
	private final String opt_statuslogfile;
	
	private final ArrayList<OjdeployRegistry> arg_projects;


	/**
	 * Fields in config.jelly must match the parameter names in the
	 * "DataBoundConstructor"
	 * 
	 * @param path_ojdeploy
	 * @param arg_workspace
	 * @param arg_buildfileschema
	 * @param opt_basedir
	 * @param opt_outputfile
	 * @param opt_nocompile
	 * @param opt_clean
	 * @param opt_nodatasources
	 * @param opt_forcerewrite
	 * @param opt_updatewebxmlejbrefs
	 * @param opt_define
	 * @param opt_statuslogfile
	 */
	@DataBoundConstructor
	public OjdeployBuilder(String path_ojdeploy,
			String arg_workspace, 
			String arg_buildfileschema, String opt_basedir,
			String opt_outputfile, String opt_nocompile, String opt_clean,
			String opt_nodatasources, String opt_forcerewrite,
			String opt_updatewebxmlejbrefs, String opt_define,
			String opt_statuslogfile, ArrayList<OjdeployRegistry> arg_projects) {
		super();
		this.path_ojdeploy = path_ojdeploy;
		this.arg_workspace = arg_workspace;
		this.arg_buildfileschema = arg_buildfileschema;
		this.arg_projects = arg_projects;

		this.opt_basedir = opt_basedir;
		this.opt_outputfile = opt_outputfile;
		this.opt_nocompile = opt_nocompile;
		this.opt_clean = opt_clean;
		this.opt_nodatasources = opt_nodatasources;
		this.opt_forcerewrite = opt_forcerewrite;
		this.opt_updatewebxmlejbrefs = opt_updatewebxmlejbrefs;
		this.opt_define = opt_define;
		this.opt_statuslogfile = opt_statuslogfile;	
	}
	

	/**
	 * This is where you 'build' the project.
	 */
	@Override
	public boolean perform(@SuppressWarnings("rawtypes") AbstractBuild build, Launcher launcher, BuildListener listener) {
		// http://docs.oracle.com/cd/E26098_01/user.1112/e17455/deploying_apps.htm#OJDUG645
		int exitVal = 0;
		//se verifica que el fichero jws exista
		File f = new File(this.getOpt_basedir() + File.separator + this.getArg_workspace());
		if(!f.exists()){
			listener.getLogger().println("The workspace (" + this.getOpt_basedir() + File.separator + this.getArg_workspace() + ") not exist!");
			return false;
		}
		
		ArrayList<OjdeployRegistry> lstPrjs = this.getArg_projects();
		if(lstPrjs == null){
			lstPrjs = new ArrayList<OjdeployRegistry>();
		}
		if(lstPrjs.isEmpty()){
			lstPrjs.add(new OjdeployRegistry("", "*", false));
		}
		
		//se verifica que exista el nombre del profile indicado
		listener.getLogger().println("--------- verifying profiles ---------");
		for (OjdeployRegistry reg: lstPrjs) {
			if(reg.isDisabled()){
				listener.getLogger().println( reg.getValue() + " verification skipped (is disabled)!");
			}else{
				ProfileValidation check = new ProfileValidation(
												this.getArg_workspace(), 
												this.getOpt_basedir(), 
												reg.getName(), 
												reg.getValue());
				if(!check.projectExist()){
					listener.getLogger().println("The project (" + reg.getName() + ") not exist in workspace file!");
					return false;
				}
				if(!check.profileExist()){
					listener.getLogger().println("The deployment profile (" + reg.getValue() + ") not exist!");
					return false;
				};
				
				listener.getLogger().println( reg.getValue() + " exist!");
			}
		}
		
		//se ejecutan los proyectos
		listener.getLogger().println("--------- executing profiles ---------");
		
		for (OjdeployRegistry reg: lstPrjs) {
			ArrayList<String> al = new ArrayList<String>();
			al.add(this.getPath_ojdeploy());
	
			if (!StringUtils.isEmpty(this.getArg_workspace())) {
				al.add("-workspace");
				al.add(this.getOpt_basedir() + File.separator + this.getArg_workspace());
			}
	
			if (!StringUtils.isEmpty(this.getOpt_basedir())) {
				al.add("-basedir");
				al.add(this.getOpt_basedir());
			}
			
			if (!StringUtils.isEmpty(reg.getName())) {
				al.add("-project");
				al.add(reg.getName());
			}
			
			al.add("-profile");
			if (!StringUtils.isEmpty(reg.getValue())) {
				al.add(reg.getValue());
			}else{
				al.add("*");
			}
	
			//al.add("-clean");
			al.add("-forcerewrite");
	
			String args[] = al.toArray(new String[] {});
	
			OjdeployExec ojdeploy = new OjdeployExec();
			exitVal = ojdeploy.exec(listener.getLogger(), args, reg.isDisabled());		
			
			if(exitVal != 0){
				break;
			}
		}
		

		return exitVal == 0;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */

	public String getPath_ojdeploy() {
		return path_ojdeploy;
	}

	public String getArg_workspace() {
		return arg_workspace;
	}

	public String getArg_buildfileschema() {
		return arg_buildfileschema;
	}

	public String getOpt_basedir() {
		return opt_basedir;
	}

	public String getOpt_outputfile() {
		return opt_outputfile;
	}

	public String getOpt_nocompile() {
		return opt_nocompile;
	}

	public String getOpt_clean() {
		return opt_clean;
	}

	public String getOpt_nodatasources() {
		return opt_nodatasources;
	}

	public String getOpt_forcerewrite() {
		return opt_forcerewrite;
	}

	public String getOpt_updatewebxmlejbrefs() {
		return opt_updatewebxmlejbrefs;
	}

	public String getOpt_define() {
		return opt_define;
	}

	public String getOpt_statuslogfile() {
		return opt_statuslogfile;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	public ArrayList<OjdeployRegistry> getArg_projects() {
		return arg_projects;
	}

	/*		public List<OjdeployRegistry> getInstances() {
		return getPublisherDescriptor().getInstances();
	}

	public OjdeployRegistry getInstance(String name) {
		List<OjdeployRegistry> registros = this.getDescriptor().getOjdeploylist();
		for (OjdeployRegistry instance : registros) {
			if (instance.getName().equals(name)) {
				return instance;
			}
		}
		return null;
	}*/
	


	/**
	 * Descriptor for {@link OjdeployBuilder}. Used as a singleton. The class is
	 * marked as public so that it can be accessed from views.
	 *
	 * <p>
	 * See
	 * <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension
	// This indicates to Jenkins that this is an implementation of an extension
	// point.
	public static final class DescriptorImpl extends  BuildStepDescriptor<Builder> {
		
		private ArrayList<OjdeployRegistry> ojdeploylist;

		/**
		 * In order to load the persisted global configuration, you have to call
		 * load() in the constructor.
		 */
		public DescriptorImpl() {
			load();
		}

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 *
		 * @param value
		 *            This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the
		 *         browser.
		 */
		public FormValidation doCheckPath_ojdeploy(@QueryParameter String value)
				throws IOException, ServletException {
			value = value.trim();
			if (value.length() == 0) {
				return FormValidation.error("Please set a ojdeploy path");
			}
			File ojdeploy = new File(value);

			if (ojdeploy.exists()) {
				if (!ojdeploy.isFile()) {
					return FormValidation.error("The path(" + value + ") isn't a file!");
				}
			} else {
				return FormValidation.error("The path(" + value + ") not exist!");
			}
			if ((!value.endsWith("ojdeploy"))
					&& (!value.endsWith("ojdeploy.exe")))
				return FormValidation.error("The path(" + value + ") not end with '[ojdeploy|ojdeploy.exe]'!");

			return FormValidation.ok();
		}

		public FormValidation doCheckArg_workspace(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set a workspace file");
			
			if (!value.endsWith(".jws"))
				return FormValidation.error("The path not end with '.jws'!");
			if (value.length() < 5)
				return FormValidation .error("Please set a valid workspace name!");
			return FormValidation.ok();
		}

		public FormValidation doCheckOpt_basedir(@QueryParameter String value)
				throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set a basedir");

			File file = new File(value);
			if (!file.exists())
				return FormValidation.error("The basedir don't exist!");
			if (!file.isDirectory())
				return FormValidation.error("The basedir isn't a directory!");
			return FormValidation.ok();
		}

		public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project
			// types
			return true;
		}

		/**
		 * This human readable name i s used in the configuration screen.
		 */
		public String getDisplayName() {
			return "JDeveloper ojdeploy";// "Say hello world";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			Logger log = LogManager.getLogManager().getLogger("hudson.WebAppMain");
/*			

			log.info("inicio - json crudo ----------------------------------------");
			log.info(json.toString());
			log.info("----------------------------------------");
			*/
			try {
				//GENERANDO EL LISTADO DE OJDEPLOY PARA QUE SE GUARDE EN LA CONFIGURACION GLOBAL
				JSONObject ojdeployConfigJson = json.getJSONObject("ojdeployconfig");
//				log.info(" ojdeployconfig ----------------------------------------");
//				log.info(ojdeployConfigJson.toString());
				
				ojdeploylist = new ArrayList<OjdeployRegistry>();
				
				try{
//					log.info("leyendo ojdeploylist como un array");
					JSONArray array = ojdeployConfigJson.getJSONArray("ojdeploylist");
//					log.info("       " + array.size() + " elementos");
					
					@SuppressWarnings("rawtypes")
					Iterator it = array.iterator();
					
					while(it.hasNext()){
						JSONObject obj = (JSONObject) it.next();
//						log.info("       " + obj.toString());
						registerElement(obj);
					}
				}catch(JSONException je){
//					log.info(je.getMessage());
					JSONObject obj = ojdeployConfigJson.getJSONObject("ojdeploylist");
//					log.info("       " + obj.toString());
					registerElement(obj);
				}
				
				/*for(OjdeployRegistry or : ojdeploylist){
					log.info("name:" + or.getName() + " - value:" + or.getValue());
				}*/
			} catch (Exception e) {
				log.info("OJDEPLOY CONFIGURE ERROR -->" + e.getMessage());
				e.printStackTrace();
			}
//			log.info("guardando ----------------------------------------");

			req.bindJSON(this, json);
		    save();
//		    log.info("fin ----------------------------------------");
			return true;
		}

		private void registerElement(JSONObject obj) {
			OjdeployRegistry or = new OjdeployRegistry();
			or.setName(obj.getString("name"));
			or.setValue(obj.getString("value"));
			or.setDisabled(obj.getBoolean("disabled"));
			ojdeploylist.add(or);
		}

		public ListBoxModel doFillPath_ojdeployItems() {
			ListBoxModel items = new ListBoxModel();

			List<OjdeployRegistry> lst = this.getOjdeploylist();
			items.add("...choose one...", "");
			for(OjdeployRegistry or : lst){
				if(!or.isDisabled()){
					items.add(or.getName(), or.getValue());
				}
			}
			
			return items;
		}

		public ArrayList<OjdeployRegistry> getOjdeploylist() {
			return ojdeploylist;
		}

		public void setOjdeploylist(ArrayList<OjdeployRegistry> ojdeploylist) {
			this.ojdeploylist = ojdeploylist;
		}

	}
}
