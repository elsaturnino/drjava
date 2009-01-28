/*BEGIN_COPYRIGHT_BLOCK
 *
 * Copyright (c) 2001-2007, JavaPLT group at Rice University (javaplt@rice.edu)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the names of DrJava, the JavaPLT group, Rice University, nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software is Open Source Initiative approved Open Source Software.
 * Open Source Initative Approved is a trademark of the Open Source Initiative.
 * 
 * This file is part of DrJava.  Download the current version of this project
 * from http://www.drjava.org/ or http://sourceforge.net/projects/drjava/
 * 
 * END_COPYRIGHT_BLOCK*/

package edu.rice.cs.drjava.config;

import edu.rice.cs.drjava.model.MultiThreadedTestCase;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.plt.lambda.Lambda2;
import java.util.*;
import java.io.File;

/**
 * Tests for the variables and language constructs that can be used in external processes.
 * @author Mathias Ricken
 */
public class DrJavaPropertySetupTest extends MultiThreadedTestCase {
  public final String PS = File.pathSeparator; // path separator
  public void setUp() throws Exception {
    super.setUp();
    DrJavaPropertySetup.setup();
  }
  public void tearDown() throws Exception {
    super.tearDown();
  }
  public void testArithmetic() throws CloneNotSupportedException {
    PropertyMaps pm = PropertyMaps.TEMPLATE.clone();
    DrJavaProperty p;
    
    // add
    p = pm.getProperty("Misc","add");
    assertTrue(p.getCurrent(pm).startsWith("(add Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(add Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(add Error"));
    p.setAttribute("op1","1");
    p.setAttribute("op2","2");
    assertEquals("3",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","2");
    assertEquals("32",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","1.23");
    assertEquals("31.23",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(add Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(add Error"));
    
    // sub
    p = pm.getProperty("Misc","sub");
    assertTrue(p.getCurrent(pm).startsWith("(sub Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(sub Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(sub Error"));
    p.setAttribute("op1","1");
    p.setAttribute("op2","2");
    assertEquals("-1",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","2");
    assertEquals("28",p.getCurrent(pm));
    p.setAttribute("op1","30.123");
    p.setAttribute("op2","2.1");
    assertEquals("28.023",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(sub Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(sub Error"));
    
    // mul
    p = pm.getProperty("Misc","mul");
    assertTrue(p.getCurrent(pm).startsWith("(mul Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(mul Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(mul Error"));
    p.setAttribute("op1","3");
    p.setAttribute("op2","4");
    assertEquals("12",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","-2");
    assertEquals("-60",p.getCurrent(pm));
    p.setAttribute("op1","30.2");
    p.setAttribute("op2","3");
    assertEquals("90.6",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(mul Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(mul Error"));
    
    // div
    p = pm.getProperty("Misc","div");
    assertTrue(p.getCurrent(pm).startsWith("(div Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(div Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(div Error"));
    p.setAttribute("op1","12");
    p.setAttribute("op2","6");
    assertEquals("2",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","-2");
    assertEquals("-15",p.getCurrent(pm));
    p.setAttribute("op1","-90.6");
    p.setAttribute("op2","-3");
    assertEquals("30.2",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(div Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(div Error"));
    
    // not
    p = pm.getProperty("Misc","not");
    assertTrue(p.getCurrent(pm).startsWith("(not Error"));
    p.setAttribute("op","true");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op","false");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op","x"); // anything but "true" counts as false as per new Boolean("x")
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op","2");
    assertEquals("true",p.getCurrent(pm));

    // gt
    p = pm.getProperty("Misc","gt");
    assertTrue(p.getCurrent(pm).startsWith("(gt Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(gt Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(gt Error"));
    p.setAttribute("op1","-1.123");
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","-1.123");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","30");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(gt Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(gt Error"));

    // gte
    p = pm.getProperty("Misc","gte");
    assertTrue(p.getCurrent(pm).startsWith("(gte Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(gte Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(gte Error"));
    p.setAttribute("op1","-1.123");
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","-1.123");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","30");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(gte Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(gte Error"));

    // lt
    p = pm.getProperty("Misc","lt");
    assertTrue(p.getCurrent(pm).startsWith("(lt Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(lt Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(lt Error"));
    p.setAttribute("op1","-1.123");
    p.setAttribute("op2","2");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","-1.123");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","30");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(lt Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(lt Error"));

    // lte
    p = pm.getProperty("Misc","lte");
    assertTrue(p.getCurrent(pm).startsWith("(lte Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(lte Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(lte Error"));
    p.setAttribute("op1","-1.123");
    p.setAttribute("op2","2");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","-1.123");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","30");
    p.setAttribute("op2","30");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(lte Error"));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertTrue(p.getCurrent(pm).startsWith("(lte Error"));

    // eq
    p = pm.getProperty("Misc","eq");
    assertTrue(p.getCurrent(pm).startsWith("(eq Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(eq Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(eq Error"));
    p.setAttribute("op1","-1.123");
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","2");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","xyz");
    p.setAttribute("op2","xyz");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertEquals("false",p.getCurrent(pm));

    // neq
    p = pm.getProperty("Misc","neq");
    assertTrue(p.getCurrent(pm).startsWith("(neq Error"));
    p.setAttribute("op1","1");
    assertTrue(p.getCurrent(pm).startsWith("(neq Error"));
    p.resetAttributes();
    p.setAttribute("op2","2");
    assertTrue(p.getCurrent(pm).startsWith("(neq Error"));
    p.setAttribute("op1","-1.123");
    p.setAttribute("op2","2");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","xyz");
    p.setAttribute("op2","xyz");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","x");
    p.setAttribute("op2","2");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertEquals("true",p.getCurrent(pm));

    // and
    p = pm.getProperty("Misc","and");
    assertTrue(p.getCurrent(pm).startsWith("(and Error"));
    p.setAttribute("op1","true");
    assertTrue(p.getCurrent(pm).startsWith("(and Error"));
    p.resetAttributes();
    p.setAttribute("op2","true");
    assertTrue(p.getCurrent(pm).startsWith("(and Error"));
    p.setAttribute("op1","true");
    p.setAttribute("op2","true");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","true");
    p.setAttribute("op2","false");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","false");
    p.setAttribute("op2","true");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","false");
    p.setAttribute("op2","false");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","x"); // anything but "true" counts as false as per new Boolean("x")
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","true");
    p.setAttribute("op2","x");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","true");
    assertEquals("false",p.getCurrent(pm));

    // or
    p = pm.getProperty("Misc","or");
    assertTrue(p.getCurrent(pm).startsWith("(or Error"));
    p.setAttribute("op1","true");
    assertTrue(p.getCurrent(pm).startsWith("(or Error"));
    p.resetAttributes();
    p.setAttribute("op2","true");
    assertTrue(p.getCurrent(pm).startsWith("(or Error"));
    p.setAttribute("op1","true");
    p.setAttribute("op2","true");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","true");
    p.setAttribute("op2","false");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","false");
    p.setAttribute("op2","true");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","false");
    p.setAttribute("op2","false");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","x"); // anything but "true" counts as false as per new Boolean("x")
    p.setAttribute("op2","2");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","x");
    assertEquals("false",p.getCurrent(pm));
    p.setAttribute("op1","true");
    p.setAttribute("op2","x");
    assertEquals("true",p.getCurrent(pm));
    p.setAttribute("op1","2");
    p.setAttribute("op2","true");
    assertEquals("true",p.getCurrent(pm));
  }
  public void testString() throws CloneNotSupportedException {
    PropertyMaps pm = PropertyMaps.TEMPLATE.clone();
    DrJavaProperty p;
    
    // strlen
    p = pm.getProperty("Misc","strlen");
    assertTrue(p.getCurrent(pm).startsWith("(strlen Error"));
    p.setAttribute("op","abc");
    assertEquals("3",p.getCurrent(pm));
    p.setAttribute("op","");
    assertEquals("0",p.getCurrent(pm));
    p.setAttribute("op","1234567890");
    assertEquals("10",p.getCurrent(pm));
    
    // replace-string
  }
  public void testList() throws CloneNotSupportedException {
    PropertyMaps pm = PropertyMaps.TEMPLATE.clone();
    DrJavaProperty p;
    
    // count
    p = pm.getProperty("Misc","count");
    assertTrue(p.getCurrent(pm).startsWith("(count Error"));
    p.setAttribute("list","abc");
    assertEquals("1",p.getCurrent(pm));
    p.setAttribute("list","");
    assertEquals("0",p.getCurrent(pm));
    p.setAttribute("list","abc"+PS+"def");
    assertEquals("2",p.getCurrent(pm));
    p.setAttribute("list",PS+"abc"+PS+"def");
    assertEquals("3",p.getCurrent(pm));
    p.setAttribute("list",PS+"abc"+PS+"def"+PS);
    assertEquals("4",p.getCurrent(pm));
    p.setAttribute("list",PS+"abc"+PS+"def"+PS+PS);
    assertEquals("5",p.getCurrent(pm));
    p.setAttribute("list",PS+PS+"abc"+PS+"def"+PS+PS);
    assertEquals("6",p.getCurrent(pm));
    p.setAttribute("list",PS+PS+"abc"+PS+PS+"def"+PS+PS);
    assertEquals("7",p.getCurrent(pm));
    
    p.setAttribute("list","abc 123");
    assertEquals("1",p.getCurrent(pm));
    p.setAttribute("list","");
    assertEquals("0",p.getCurrent(pm));
    p.setAttribute("list","abc 123"+PS+"def 456");
    assertEquals("2",p.getCurrent(pm));
    p.setAttribute("list",PS+"abc 123"+PS+"def 456");
    assertEquals("3",p.getCurrent(pm));
    p.setAttribute("list",PS+"abc 123"+PS+"def 456"+PS);
    assertEquals("4",p.getCurrent(pm));
    p.setAttribute("list",PS+"abc 123"+PS+"def 456"+PS+PS);
    assertEquals("5",p.getCurrent(pm));
    p.setAttribute("list",PS+PS+"abc 123"+PS+"def 456"+PS+PS);
    assertEquals("6",p.getCurrent(pm));
    p.setAttribute("list",PS+PS+"abc 123"+PS+PS+"def 456"+PS+PS);
    assertEquals("7",p.getCurrent(pm));
    
    p.setAttribute("sep"," ");
    p.setAttribute("list","abc"+PS+"def");
    assertEquals("1",p.getCurrent(pm));
    p.setAttribute("list","");
    assertEquals("0",p.getCurrent(pm));
    p.setAttribute("list","abc def");
    assertEquals("2",p.getCurrent(pm));
    p.setAttribute("list"," abc def");
    assertEquals("3",p.getCurrent(pm));
    p.setAttribute("list"," abc def ");
    assertEquals("4",p.getCurrent(pm));
    p.setAttribute("list"," abc def  ");
    assertEquals("5",p.getCurrent(pm));
    p.setAttribute("list","  abc def  ");
    assertEquals("6",p.getCurrent(pm));
    p.setAttribute("list","  abc  def  ");
    assertEquals("7",p.getCurrent(pm));
    
    // sublist
    
    // change.sep
  }
}
