package com.izforge.izpack.installer;


public interface TemplateInstaller
{
    boolean setArgs(String[] args);
    boolean validate();
    void run();
}
