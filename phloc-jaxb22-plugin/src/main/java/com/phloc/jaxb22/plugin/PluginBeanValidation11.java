package com.phloc.jaxb22.plugin;

/**
 * big thanks to original author: cocorossello
 * 
 * @author Philip Helger
 */
public class PluginBeanValidation11 extends AbstractPluginBeanValidation
{
  private static final String OPT = "Xphloc-bean-validation11";

  public PluginBeanValidation11 ()
  {
    super (false);
  }

  @Override
  public String getOptionName ()
  {
    return OPT;
  }

  @Override
  public String getUsage ()
  {
    return "  -" + OPT + " locale   :  inject Bean validation 1.1 annotations (JSR 349)";
  }
}
