package edu.rice.cs.plt.text;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Triple;
import edu.rice.cs.plt.tuple.Quad;

/**
 * A simple utility class for processing command-line argument arrays.  Argument strings
 * are assumed to be made up of <em>options</em>, <em>option arguments</em>, and 
 * <em>parameters</em>.  All argument string beginning with the character {@code '-'} are
 * interpreted as options.  Clients should specify the names and arities (and, optionally,
 * default values) of their supported options.  Aliases may also be specified, allowing
 * multiple names to be interpreted as the same option.  {@link #parse} may then be invoked on
 * an argument array, producing errors for unrecognized options or options with the wrong
 * number of arguments, and interpreting all remaining strings as parameters.
 */
public class ArgumentParser {
  
  private final Map<String, Integer> _supportedOptions;
  private final HashMap<String, Iterable<String>> _defaults; // HashMap implements Cloneable
  private final Map<String, String> _aliases;
  private boolean _strictOrder;
  private int _requiredParams;
  
  /** Create an argument parser with an initially-empty set of supported options. */
  public ArgumentParser() {
    _supportedOptions = new HashMap<String, Integer>();
    _defaults = new HashMap<String, Iterable<String>>();
    _aliases = new HashMap<String, String>();
    _strictOrder = false;
    _requiredParams = 0;
  }
  
  /** Require all options to precede any parameters (not required by default). */
  public void requireStrictOrder() { _strictOrder = true; }
  
  /** Require at least the given number of parameters (0 by default). */
  public void requireParams(int count) { _requiredParams = count; }
  
  /** Determine whether the given option is supported. */
  public boolean supportsOption(String name) {
    return _supportedOptions.containsKey(name) || _aliases.containsKey(name);
  }
  
  /**
   * Add the given option name to the list of supported options.
   * @param name  The option name, excluding the "-" prefix.
   * @param arguments  The number of arguments to follow the option.
   * @throws IllegalArgumentException  If the option is already supported.
   */
  public void supportOption(String name, int arity) {
    if (supportsOption(name)) { throw new IllegalArgumentException(name + " is already supported"); }
    _supportedOptions.put(name, arity);
  }
  
  /**
   * Add the given option name to the list of supported options and record the given default argument set.
   * @param name  The option name, excluding the "-" prefix.
   * @param arguments  The default arguments associated with the option.  Implicitly defines the arity
   *                   of the option as well.  If the length is 0, no default will be set (otherwise, the
   *                   nullary option would always appear to be present).
   * @throws IllegalArgumentException  If the option is already supported.
   */
  public void supportOption(String name, String... defaultArguments) {
    supportOption(name, defaultArguments.length);
    if (defaultArguments.length > 0) {
      _defaults.put(name, IterUtil.asIterable(defaultArguments));
    }
  }
  
  /**
   * Add the given option name to the list of supported options.  An arbitrary number
   * of arguments, terminated by another option or the end of the argument list, may follow the option.
   * @param name  The option name, excluding the "-" prefix.
   * @throws IllegalArgumentException  If the option is already supported.
   */
  public void supportVarargOption(String name) {
    if (supportsOption(name)) { throw new IllegalArgumentException(name + " is already supported"); }
    _supportedOptions.put(name, -1);
  }
  
  /**
   * Add the given option name to the list of supported options and record the given default argument set.  
   * An arbitrary number of arguments, terminated by another option or the end of the argument list, may 
   * follow the option.  (Use a 0-length array to set a 0-length default.  Otherwise,
   * {@link #supportVarargOption(String)} will be invoked instead.)
   * 
   * @param name  The option name, excluding the "-" prefix.
   * @param defaultArguments  The default arguments associated with the option.
   * @throws IllegalArgumentException  If the option is already supported.
   */
  public void supportVarargOption(String name, String... defaultArguments) {
    supportVarargOption(name);
    _defaults.put(name, IterUtil.asIterable(defaultArguments));
  }
  
  /**
   * Create an alias: {@code aliasName} is an alias for {@code optionName}.  Parsed matches will appear
   * under the referenced option name in the result.
   * @throws IllegalArgumentException  If the alias name is already supported, or if the option name is 
   *                                   <em>not</em> supported.
   */
  public void supportAlias(String aliasName, String optionName) {
    if (supportsOption(aliasName)) { throw new IllegalArgumentException(aliasName + " is already supported"); }
    if (!supportsOption(optionName)) { throw new IllegalArgumentException(optionName + " is not supported"); }
    if (_aliases.containsKey(optionName)) { optionName = _aliases.get(optionName); }
    _aliases.put(aliasName, optionName);
  }
  
  
  /** Parse an array of arguments based on the previously-defined supported options. */
  public Result parse(String... args) throws IllegalArgumentException {
    @SuppressWarnings("unchecked")
    Map<String, Iterable<String>> options = (Map<String, Iterable<String>>) _defaults.clone();
    
    List<String> params = new LinkedList<String>();
    
    String currentOption = null;
    List<String> currentOptionArgs = null;
    int optionArgsCount = -1;
    boolean allowMoreOptions = true;
    
    for (String s : args) {
      if (currentOption != null && optionArgsCount == 0) {
        // convert to a SizedIterable to work around a Retroweaver bug (tests will fail)
        options.put(currentOption, IterUtil.asIterable(currentOptionArgs));
        currentOption = null;
      }
      
      if (s.startsWith("-")) {
        String opt = s.substring(1);
        if (_aliases.containsKey(opt)) { opt = _aliases.get(opt); }
        if (optionArgsCount > 0) {
          throw new IllegalArgumentException("Expected " + optionArgsCount + " more argument(s) for option " + 
                                             currentOption);
        }
        else if (!allowMoreOptions) {
          throw new IllegalArgumentException("Unexpected option: " + opt);
        }
        else if (!_supportedOptions.containsKey(opt)) {
          throw new IllegalArgumentException("Unrecognized option: " + opt);
        }
        else {
          if (currentOption != null) {
            // convert to a SizedIterable to work around a Retroweaver bug (tests will fail)
            options.put(currentOption, IterUtil.asIterable(currentOptionArgs));
          }
          currentOption = opt;
          currentOptionArgs = new LinkedList<String>();
          optionArgsCount = _supportedOptions.get(opt);
        }
      }
      
      else {
        if (currentOption == null) {
          if (_strictOrder) { allowMoreOptions = false; }
          params.add(s);
        }
        else {
          currentOptionArgs.add(s);
          if (optionArgsCount > 0) { optionArgsCount--; }
        }
      }
    }
    if (optionArgsCount > 0) {
      throw new IllegalArgumentException("Expected " + optionArgsCount + " more argument(s) for option " + 
                                         currentOption);
    }
    if (currentOption != null) {
      // convert to a SizedIterable to work around a Retroweaver bug (tests will fail)
      options.put(currentOption, IterUtil.asIterable(currentOptionArgs));
    }
    
    if (params.size() < _requiredParams) {
      throw new IllegalArgumentException("Expected at least " + _requiredParams + " parameter(s)");
    }
    return new Result(options, params);
  }
      

  /** A collection of the options and parameters parsed from an array of arguments. */
  public static class Result {
    private final Map<String, Iterable<String>> _options;
    private final Iterable<String> _params;
    
    public Result(Map<String, Iterable<String>> options, Iterable<String> params) {
      _options = options;
      _params = params;
    }
    
    /** Get the parameters (non-option strings associated with no option) that were parsed. */
    public Iterable<String> params() { return _params; }
    
    /** Test whether the given option was defined (or was given a default value). */
    public boolean hasOption(String opt) { return _options.containsKey(opt); }
    
    /**
     * Get the arguments associated with the given option, or {@code null} if it is undefined.
     * @param name  The option name, excluding the "-" prefix.
     */
    public Iterable<String> getOption(String opt) { return _options.get(opt); }
    
    /**
     * Test whether the given option was defined with 0 arguments.
     * @param name  The option name, excluding the "-" prefix.
     */
    public boolean hasNullaryOption(String opt) {
      return _options.containsKey(opt) && IterUtil.isEmpty(_options.get(opt));
    }
    
    /**
     * Get the single argument associated with the given option, or {@code null} if it is undefined
     * or has a different number of arguments.
     * @param name  The option name, excluding the "-" prefix.
     */
    public String getUnaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 1) { return null; }
      else { return IterUtil.first(result); }
    }
    
    /**
     * Get the 2 arguments associated with the given option, or {@code null} if it is undefined
     * or has a different number of arguments.
     * @param name  The option name, excluding the "-" prefix.
     */
    public Pair<String, String> getBinaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 2) { return null; }
      else { return IterUtil.asPair(result); }
    }
    
    public Triple<String, String, String> getTernaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 3) { return null; }
      else { return IterUtil.asTriple(result); }
    }
    
    public Quad<String, String, String, String> getQuaternaryOption(String opt) {
      Iterable<String> result = _options.get(opt);
      if (result == null || IterUtil.sizeOf(result) != 4) { return null; }
      else { return IterUtil.asQuad(result); }
    }
    
  }

}