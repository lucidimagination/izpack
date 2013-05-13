package com.izforge.izpack.installer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.*;
import org.junit.Test;


public class CustomInstallTest{
    
    private final static String BASE_PATH = "." + File.separator + "test_files" + File.separator;
    
    @Test
    public void firstTest() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        Installer a = new Installer();
        Collection<Object> objects = a.loadProperty(BASE_PATH + "test_custom_install_one.properties");
        CustomInstaller installer = a.bootstrap(new String[]{"-one"}, objects);
        
        assertNotNull(installer);
    }
    
    @Test(expected = NullPointerException.class)
    public void secondTest() throws IOException{
        Installer a = new Installer();

        a.loadProperty("neither.properties");
    }
    
    @Test
    public void thirdTest() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        Installer a = new Installer();

        Collection<Object> objects = a.loadProperty(BASE_PATH + "test_custom_install_three.properties");
        CustomInstaller installer = a.bootstrap(new String[]{"-three"}, objects);
        
        assertNull(installer);
    }
    
    @Test(expected = ClassNotFoundException.class)
    public void fourthTest() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        Installer a = new Installer();

        Collection<Object> objects = a.loadProperty(BASE_PATH + "test_custom_install_four.properties");
        a.bootstrap(new String[]{"-four"}, objects);
    }
    
    @Test
    public void fiveTest() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
        Installer a = new Installer();

        Collection<Object> objects = a.loadProperty(BASE_PATH + "test_custom_install_five.properties");
        CustomInstaller installer = a.bootstrap(new String[]{"-other"}, objects);
        
        assertNull(installer);
    }
}
