package org.jenkinsci.plugins.jdeveloper.ojdeploy.validator;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class ProfileValidation {
	
	private String workspace;
	private String pathBase;
	private String proyectName;
	private String profileName;
	
	private boolean hasProfile = false;
	private boolean hasProject = false;

	public ProfileValidation(String workspace, String pathBase, String proyectName, String profileName) {
		super();
		this.workspace = workspace;
		this.pathBase = pathBase;
		this.proyectName = proyectName;
		this.profileName = profileName;
		checkProfile();
	}
	
	private void checkProfile(){
		if(StringUtils.isEmpty(proyectName) && profileName.equals("*") ){
			hasProfile = true;
			hasProject = true;
			return;
		}
		
		try {
        	SAXReader reader = new SAXReader();
			Document document = null;
			if(StringUtils.isEmpty(proyectName)){
				document = reader.read(pathBase + File.separator + workspace);
			} else {
				document = reader.read(pathBase + File.separator + workspace);
				Node node = document.selectSingleNode( "//list[@n='listOfChildren']/hash/url[starts-with(@path,'" + proyectName + "/')]" );
				
				if(node == null){
					hasProject = false;
					return;
				}
				String projectPath = node.valueOf( "@path" );
				
				document = reader.read(pathBase + File.separator + projectPath);
			}
			this.hasProject = true;
			if(profileName.equals("*") ){
				hasProfile = true;
			}else{
				this.hasProfile = hasProfile(document, profileName);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			hasProfile = true;
			hasProject = true;
		}
	}
	
	private boolean hasProfile(Document document, String profileName){
		boolean res = false;
		Node node = document.selectSingleNode( "//list[@n=\"profileList\"]/string[@v=\"" + profileName + "\"]" );
		if(node != null) res = true;
		return res;
	}
	
	public boolean profileExist(){
		return hasProfile;
	}
	
	public boolean projectExist(){
		return hasProject;
	}

	public static void main(String[] args) {
		
		String workspace = "FadingFeedBack.jws";
		String pathBase = "C:\\JDeveloper\\11_1_1_7_0\\FadingFeedBack\\";
		String proyectName = "ViewController";
		//String profileName = "FadingFeedBack_ViewController_webapp1";
		String profileName = "*";
		
		ProfileValidation case1 = new ProfileValidation(workspace, pathBase, proyectName, profileName);
		System.out.println(case1.profileExist());
		System.out.println(case1.projectExist());
		System.out.println();
		
		//ProfileValidation case2 = new ProfileValidation(workspace, pathBase, null, "*");
		//System.out.println(case2.profileExist());
	}
	


}
