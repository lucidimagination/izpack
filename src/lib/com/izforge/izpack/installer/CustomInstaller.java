package com.izforge.izpack.installer;


public interface CustomInstaller
{
    void setArgs(String[] args);
    boolean validate();
    void run();
}
