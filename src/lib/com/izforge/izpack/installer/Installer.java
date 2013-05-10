/*
 * IzPack - Copyright 2001-2008 Julien Ponge, All Rights Reserved.
 * 
 * http://izpack.org/
 * http://izpack.codehaus.org/
 * 
 * Copyright 2003 Jonathan Halliday
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.installer;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import com.izforge.izpack.util.Debug;
import com.izforge.izpack.util.StringTool;

/**
 * The program entry point. Selects between GUI and text install modes.
 *
 * @author Jonathan Halliday
 */
public class Installer {

	public static final int INSTALLER_GUI = 0, INSTALLER_AUTO = 1, INSTALLER_CONSOLE = 2, NEITHER = -1;
	public static final int CONSOLE_INSTALL = 0, CONSOLE_GEN_TEMPLATE = 1, CONSOLE_FROM_TEMPLATE = 2;

    /*
	 * The main method (program entry point).
	 * 
	 * @param args The arguments passed on the command-line.
	 */
	public static void main(String[] args) {
		Debug.log(" - Logger initialized at '" + new Date(System.currentTimeMillis()) + "'.");

		Debug.log(" - commandline args: " + StringTool.stringArrayToSpaceSeparatedString(args));

		// OS X tweakings
		if (System.getProperty("mrj.version") != null) {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "IzPack");
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			System.setProperty("com.apple.mrj.application.live-resize", "true");
		}

		try {
		    Iterator<String> args_it = Arrays.asList(args).iterator();
		    
		    int type = INSTALLER_GUI;
		    int consoleAction = CONSOLE_INSTALL;
		    String path = null, langcode = null;
		    
		    while (args_it.hasNext())
		    {
		        String arg = args_it.next().trim();
		        try {
    		        if ("-console".equalsIgnoreCase(arg))
    		        {
    		            type = INSTALLER_CONSOLE;
    		        }
    		        else if ("-options-template".equalsIgnoreCase(arg))
    		        {
    		            type = INSTALLER_CONSOLE;
    		            consoleAction = CONSOLE_GEN_TEMPLATE;
    		            path = args_it.next().trim();
    		        }
    		        else if ("-options".equalsIgnoreCase(arg))
                    {
                        type = INSTALLER_CONSOLE;
                        consoleAction = CONSOLE_FROM_TEMPLATE;
                        path = args_it.next().trim();
                    }
    		        else if ("-language".equalsIgnoreCase(arg))
    		        {
    		            type = INSTALLER_CONSOLE;
    		            langcode = args_it.next().trim();
    		        }
    		        else
    		        {
    		            if(arg.startsWith("-")){
                            Installer installer = new Installer();
                            
                            type = installer.bootstrap(args);
                        }else{
                            type = INSTALLER_AUTO;
                            path = arg;
                        }
    		        }
		        }
		        catch (NoSuchElementException e) {
		            System.err.println("- ERROR -");
		            System.err.println("Option \"" + arg + "\" requires an argument!");
		            System.exit(1);
		        }
		    }
		    
		    // if headless, just use the console mode
		    if (type == INSTALLER_GUI && GraphicsEnvironment.isHeadless()) 
		    {
		        type = INSTALLER_CONSOLE;
		    }
		    
		    switch (type)
		    {
		        case INSTALLER_GUI:
		            Class.forName("com.izforge.izpack.installer.GUIInstaller").newInstance();
		            break;
		        
		        case INSTALLER_AUTO:
		            AutomatedInstaller ai = new AutomatedInstaller(path);
		            ai.doInstall();
		            break;
		            
		        case INSTALLER_CONSOLE:
		            ConsoleInstaller consoleInstaller = new ConsoleInstaller(langcode);
		            consoleInstaller.run(consoleAction, path);
		            break;
		    }
		    
		} catch (Exception e) {
			System.err.println("- ERROR -");
			System.err.println(e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	int bootstrap(String[] args)
    {
        int status = NEITHER;
        
        Properties bootstrapSpec = new Properties();

        InputStream stream = getClass().getClassLoader().getResourceAsStream(
                "res/bootstrapSpec.properties");
        
        List<TemplateInstaller> objects = new ArrayList<TemplateInstaller>();
        
        try
        {                        
            if (stream != null)
            {
                bootstrapSpec.load(stream);
                Set<Entry<Object, Object>> entries = bootstrapSpec.entrySet();
                
                for(Entry<Object, Object> entry : entries){
                    Object objectClazz = Class.forName(entry.getValue().toString()).newInstance();
                    
                    if(objectClazz instanceof TemplateInstaller)
                        objects.add((TemplateInstaller) objectClazz);
                }
                
                TemplateInstaller clazz = null;
                
                for(TemplateInstaller object : objects){
                    object.setArgs(args);
                    if(object.validate()){
                        clazz = object;
                        break;
                    }
                }
             
                if(clazz != null)
                    clazz.run();
                else
                    status = INSTALLER_GUI;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        return status;
    }
}
