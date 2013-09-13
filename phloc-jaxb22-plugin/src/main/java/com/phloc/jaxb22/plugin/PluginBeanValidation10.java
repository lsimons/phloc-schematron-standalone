package com.phloc.jaxb22.plugin;


/**
 * big thanks to original author: cocorossello
 * 
 * @author Philip Helger
 */
public class PluginBeanValidation10 extends AbstractPluginBeanValidation
{
  private static final String OPT = "Xphloc-bean-validation10";

  public PluginBeanValidation10 ()
  {
    super (true);
  }

  @Override
  public String getOptionName ()
  {
    return OPT;
  }

  @Override
  public String getUsage ()
  {
    return "  -" + OPT + " locale   :  inject Bean validation 1.0 annotations (JSR 303)";
  }
}
