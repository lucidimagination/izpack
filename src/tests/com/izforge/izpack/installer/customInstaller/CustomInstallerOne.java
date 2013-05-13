package com.izforge.izpack.installer.customInstaller;

import com.izforge.izpack.installer.CustomInstaller;


public class CustomInstallerOne implements CustomInstaller{
    
    private String[] args;

    public void setArgs(String[] args)
    {
        this.args = args;
    }

    public boolean validate()
    {
        String arg = args[0];
        
        if(arg.startsWith("-") && "-one".equals(arg))
            return true;
        else
            return false;
    }

    public void run()
    {
        System.out.println("run "+getClass().getName());
    }
}
