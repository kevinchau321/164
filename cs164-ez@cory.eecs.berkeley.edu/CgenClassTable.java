/*
Copyright (c) 2000 The Regents of the University of California.
All rights reserved.

Permission to use, copy, modify, and distribute this software for any
purpose, without fee, and without written agreement is hereby granted,
provided that the above copyright notice and the following two
paragraphs appear in all copies of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
*/

// This is a project skeleton file

import java.io.PrintStream;
import java.util.Vector;
import java.util.Enumeration;
import java.util.*;

/** This class is used for representing the inheritance tree during code
    generation. You will need to fill in some of its methods and
    potentially extend it in other useful ways. */
class CgenClassTable extends SymbolTable {
	private class ObjectLayout {
		int class_tag;
		int object_size;
		HashMap<Integer, String> dispatch_pointer = new HashMap<Integer, String>(); //change to Integer, Method
	}
	public HashMap<CgenNode, ObjectLayout> objectLayouts = new HashMap<CgenNode, ObjectLayout>();
    /** All classes in the program, represented as CgenNode */
    private Vector nds;

    /** This is the stream to which assembly instructions are output */
    private PrintStream str;

    private int objectclasstag;
    private int ioclasstag;
    private int stringclasstag;
    private int intclasstag;
    private int boolclasstag;

    public static IntSymbol EMPTY_INT_SLOT;
    public static StringSymbol EMPTY_STR_SLOT;


    // The following methods emit code for constants and global
    // declarations.

    /** Emits code to start the .data segment and to
     * declare the global names.
     * */
    private void codeGlobalData() {
	// The following global names must be defined first.

	str.print("\t.data\n" + CgenSupport.ALIGN);
	str.println(CgenSupport.GLOBAL + CgenSupport.CLASSNAMETAB);
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitProtObjRef(TreeConstants.Main, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitProtObjRef(TreeConstants.Int, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitProtObjRef(TreeConstants.Str, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	BoolConst.falsebool.codeRef(str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	BoolConst.truebool.codeRef(str);
	str.println("");
	str.println(CgenSupport.GLOBAL + CgenSupport.INTTAG);
	str.println(CgenSupport.GLOBAL + CgenSupport.BOOLTAG);
	str.println(CgenSupport.GLOBAL + CgenSupport.STRINGTAG);

	// We also need to know the tag of the Int, String, and Bool classes
	// during code generation.

	str.println(CgenSupport.INTTAG + CgenSupport.LABEL
		    + CgenSupport.WORD + intclasstag);
	str.println(CgenSupport.BOOLTAG + CgenSupport.LABEL
		    + CgenSupport.WORD + boolclasstag);
	str.println(CgenSupport.STRINGTAG + CgenSupport.LABEL
		    + CgenSupport.WORD + stringclasstag);

    }

    /** Emits code to start the .text segment and to
     * declare the global names.
     * */
    private void codeGlobalText() {
	str.println(CgenSupport.GLOBAL + CgenSupport.HEAP_START);
	str.print(CgenSupport.HEAP_START + CgenSupport.LABEL);
	str.println(CgenSupport.WORD + 0);
	str.println("\t.text");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitInitRef(TreeConstants.Main, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitInitRef(TreeConstants.Int, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitInitRef(TreeConstants.Str, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitInitRef(TreeConstants.Bool, str);
	str.println("");
	str.print(CgenSupport.GLOBAL);
	CgenSupport.emitMethodRef(TreeConstants.Main, TreeConstants.main_meth, str);
	str.println("");
    }

    /** Emits code definitions for boolean constants. */
    private void codeBools(int classtag) {
	BoolConst.falsebool.codeDef(classtag, str);
	BoolConst.truebool.codeDef(classtag, str);
    }

    /** Generates GC choice constants (pointers to GC functions) */
    private void codeSelectGc() {
	str.println(CgenSupport.GLOBAL + "_MemMgr_INITIALIZER");
	str.println("_MemMgr_INITIALIZER:");
	str.println(CgenSupport.WORD
		    + CgenSupport.gcInitNames[Flags.cgen_Memmgr]);

	str.println(CgenSupport.GLOBAL + "_MemMgr_COLLECTOR");
	str.println("_MemMgr_COLLECTOR:");
	str.println(CgenSupport.WORD
		    + CgenSupport.gcCollectNames[Flags.cgen_Memmgr]);

	str.println(CgenSupport.GLOBAL + "_MemMgr_TEST");
	str.println("_MemMgr_TEST:");
	str.println(CgenSupport.WORD
		    + ((Flags.cgen_Memmgr_Test == Flags.GC_TEST) ? "1" : "0"));
    }

    /** Emits code to reserve space for and initialize all of the
     * constants.  Class names should have been added to the string
     * table (in the supplied code, is is done during the construction
     * of the inheritance graph), and code for emitting string constants
     * as a side effect adds the string's length to the integer table.
     * The constants are emmitted by running through the stringtable and
     * inttable and producing code for each entry. */
    private void codeConstants() {
	// Add constants that are required by the code generator.
	AbstractTable.stringtable.addString("");
	AbstractTable.inttable.addString("0");

	AbstractTable.stringtable.codeStringTable(stringclasstag, str);
	AbstractTable.inttable.codeStringTable(intclasstag, str);
	codeBools(boolclasstag);
    }


    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     * */
    private void installBasicClasses() {
	AbstractSymbol filename
	    = AbstractTable.stringtable.addString("<basic class>");

	// A few special class names are installed in the lookup table
	// but not the class list.  Thus, these classes exist, but are
	// not part of the inheritance hierarchy.  No_class serves as
	// the parent of Object and the other special classes.
	// SELF_TYPE is the self class; it cannot be redefined or
	// inherited.  prim_slot is a class known to the code generator.

	addId(TreeConstants.No_class,
	      new CgenNode(new class_c(0,
				      TreeConstants.No_class,
				      TreeConstants.No_class,
				      new Features(0),
				      filename),
			   CgenNode.Basic, this));

	addId(TreeConstants.SELF_TYPE,
	      new CgenNode(new class_c(0,
				      TreeConstants.SELF_TYPE,
				      TreeConstants.No_class,
				      new Features(0),
				      filename),
			   CgenNode.Basic, this));

	addId(TreeConstants.prim_slot,
	      new CgenNode(new class_c(0,
				      TreeConstants.prim_slot,
				      TreeConstants.No_class,
				      new Features(0),
				      filename),
			   CgenNode.Basic, this));

	// The Object class has no parent class. Its methods are
	//        cool_abort() : Object    aborts the program
	//        type_name() : Str        returns a string representation
	//                                 of class name
	//        copy() : SELF_TYPE       returns a copy of the object

	class_c Object_class =
	    new class_c(0,
		       TreeConstants.Object_,
		       TreeConstants.No_class,
		       new Features(0)
			   .appendElement(new method(0,
					      TreeConstants.cool_abort,
					      new Formals(0),
					      TreeConstants.Object_,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.type_name,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.copy,
					      new Formals(0),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0))),
		       filename);

	installClass(new CgenNode(Object_class, CgenNode.Basic, this));

	// The IO class inherits from Object. Its methods are
	//        out_string(Str) : SELF_TYPE  writes a string to the output
	//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
	//        in_string() : Str            reads a string from the input
	//        in_int() : Int                "   an int     "  "     "

	class_c IO_class =
	    new class_c(0,
		       TreeConstants.IO,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new method(0,
					      TreeConstants.out_string,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Str)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.out_int,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_string,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_int,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0))),
		       filename);

	CgenNode IO_node = new CgenNode(IO_class, CgenNode.Basic, this);
	installClass(IO_node);

	// The Int class has no methods and only a single attribute, the
	// "val" for the integer.

	class_c Int_class =
	    new class_c(0,
		       TreeConstants.Int,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	installClass(new CgenNode(Int_class, CgenNode.Basic, this));

	// Bool also has only the "val" slot.
	class_c Bool_class =
	    new class_c(0,
		       TreeConstants.Bool,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	installClass(new CgenNode(Bool_class, CgenNode.Basic, this));

	// The class Str has a number of slots and operations:
	//       val                              the length of the string
	//       str_field                        the string itself
	//       length() : Int                   returns length of the string
	//       concat(arg: Str) : Str           performs string concatenation
	//       substr(arg: Int, arg2: Int): Str substring selection

	class_c Str_class =
	    new class_c(0,
		       TreeConstants.Str,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.Int,
					    new no_expr(0)))
			   .appendElement(new attr(0,
					    TreeConstants.str_field,
					    TreeConstants.prim_slot,
					    new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.length,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.concat,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Str)),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.substr,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int))
						  .appendElement(new formalc(0,
								     TreeConstants.arg2,
								     TreeConstants.Int)),
					      TreeConstants.Str,
					      new no_expr(0))),
		       filename);

	installClass(new CgenNode(Str_class, CgenNode.Basic, this));
    }

    // The following creates an inheritance graph from
    // a list of classes.  The graph is implemented as
    // a tree of `CgenNode', and class names are placed
    // in the base class symbol table.

    private void installClass(CgenNode nd) {
	AbstractSymbol name = nd.getName();
	if (probe(name) != null) return;
	nds.addElement(nd);
	addId(name, nd);
    }

    private void installClasses(Classes cs) {
        for (Enumeration e = cs.getElements(); e.hasMoreElements(); ) {
	    installClass(new CgenNode((Class_)e.nextElement(),
				       CgenNode.NotBasic, this));
        }
    }

    private void buildInheritanceTree() {
	for (Enumeration e = nds.elements(); e.hasMoreElements(); ) {
	    setRelations((CgenNode)e.nextElement());
	}
    }

    private void setRelations(CgenNode nd) {
	CgenNode parent = (CgenNode)probe(nd.getParent());
	nd.setParentNd(parent);
	parent.addChild(nd);
    }

    /** Constructs a new class table and invokes the code generator */
    public CgenClassTable(Classes cls, PrintStream str) {
	nds = new Vector();

	this.str = str;

	objectclasstag = 0;
	ioclasstag = 1;
	stringclasstag = 2 /* Change to your String class tag here */;
	intclasstag =    3 /* Change to your Int class tag here */;
	boolclasstag =   4 /* Change to your Bool class tag here */;

	enterScope();
	if (Flags.cgen_debug) System.out.println("Building CgenClassTable");

	installBasicClasses();
	installClasses(cls);
	buildInheritanceTree();

	code();

	exitScope();
    }

    /** This method is the meat of the code generator.  It is to be
        filled in programming assignment 5 */
    public void code() {
		if (Flags.cgen_debug) System.out.println("coding global data");
		codeGlobalData();

		if (Flags.cgen_debug) System.out.println("choosing gc");
		codeSelectGc();

		if (Flags.cgen_debug) System.out.println("coding constants");
		codeConstants();

		//                 Add your code to emit
		//                   - prototype objects
		//                   - class_nameTab
		//                   - dispatch tables
		codeClassNameTable();
		codeClassObjectTable();
		codeDispatchTables();
		codePrototypes();

		if (Flags.cgen_debug) System.out.println("coding global text");
		codeGlobalText();

		//                 Add your code to emit
		//                   - object initializer
		//                   - the class methods
		//                   - etc...
		codeObjectInitializer();
		codeClassMethods();
    }

    

	private void codeClassNameTable(){
		str.print(CgenSupport.CLASSNAMETAB + CgenSupport.LABEL);
		for (int i = 0; i < nds.size(); i++) {
			CgenNode n = (CgenNode) nds.get(i);
			str.print(CgenSupport.WORD);
			StringSymbol symbol = (StringSymbol)AbstractTable.stringtable.lookup(n.name.getString());
        	symbol.codeRef(str);
			str.println();
		}
		return;
	};



	private void codeClassObjectTable(){
		str.print(CgenSupport.CLASSOBJTAB + CgenSupport.LABEL);
        for (int i = 0; i < nds.size(); i++) {
            CgenNode n = (CgenNode) nds.get(i);
            str.print(CgenSupport.WORD);
            CgenSupport.emitProtObjRef(n.name, str);
            str.println();
            str.print(CgenSupport.WORD);
            CgenSupport.emitInitRef(n.name, str);
            str.println();
        }
		return;
	};



	private void codeDispatchTables(){
		//run a DFS over the inheritance table to fill out Dispatch Table
		Stack<CgenNode> stack = new Stack<>();
		CgenNode curr_class = (CgenNode) lookup(TreeConstants.Object_); // get the current curr_class
		stack.push(curr_class);
		while(!stack.empty()){
			curr_class = stack.pop();

			for (Enumeration e = curr_class.getChildren(); e.hasMoreElements(); ){
				stack.push((CgenNode)e.nextElement());
			}

			if(curr_class.getParent() != TreeConstants.No_class) {
				LinkedHashMap<AbstractSymbol, AbstractSymbol> parentBaseMethods = curr_class.getParentNd().inheritedMethods;
				LinkedHashMap<AbstractSymbol, AbstractSymbol> parentMethods = curr_class.getParentNd().overridenMethods;
				curr_class.inheritedMethods.putAll(parentBaseMethods);
				curr_class.inheritedMethods.putAll(parentMethods);
			}
		
			// get the curr_classes newly defined methods
			for(int j = 0 ; j < curr_class.getFeatures().getLength(); j++){
				method curr_feat = (method) curr_class.getFeatures().getNth(j);
				if(curr_feat instanceof method) {
					curr_class.overridenMethods.put(curr_feat.name, curr_class.name);  // f.name, curr_class?
					curr_class.nameToMethod.put(curr_feat.name, curr_feat);
				}
			}

			// emit dispatch table ref
			CgenSupport.emitDispTableRef(curr_class.name, str);
			str.print(CgenSupport.LABEL);

			int offset = 0;
			// print the inherited methods first
			for (AbstractSymbol methodName : curr_class.inheritedMethods.keySet()) {
		    	AbstractSymbol curr_className = curr_class.inheritedMethods.get(methodName);
		    	str.print(CgenSupport.WORD);
		    	str.print(curr_className.toString() + "." + methodName.toString());
		    	curr_class.methodOffsets.put(methodName, offset);
		    	offset++;
			}

			// print the enwly defined methods next
			for (AbstractSymbol methodName : curr_class.overridenMethods.keySet()) {
		    	AbstractSymbol curr_className = curr_class.overridenMethods.get(methodName);
		    	str.print(CgenSupport.WORD);
		    	str.print(curr_className.toString() + "." + methodName.toString());
		    	curr_class.methodOffsets.put(methodName, offset);
		    	offset++;
			}
		}
		return;
	};



	int currenttag = stringclasstag + 1; // first object starts at tag 5

    private void codePrototypes(){
    	Stack<CgenNode> stack = new Stack<>();
    	CgenNode curr_class = (CgenNode) lookup(TreeConstants.Object_);
    	stack.push(curr_class);

    	//set the curr_class tag in the cgen node
    	while(!stack.empty()){
    		curr_class = stack.pop();
    		if (curr_class.name == TreeConstants.Object_){
    			curr_class.tag = objectclasstag;
    		} else if (curr_class.name == TreeConstants.IO) {
    			curr_class.tag = ioclasstag;
    		} else if (curr_class.name == TreeConstants.Int){
    			curr_class.tag = intclasstag;
    		} else if (curr_class.name == TreeConstants.Bool) {
    			curr_class.tag = boolclasstag;
    		} else if (curr_class.name == TreeConstants.Str) {
    			curr_class.tag = stringclasstag;
    		} else {
    			curr_class.tag = currenttag++;
    		}

    		//push children onto stack
	    	for (Enumeration children = curr_class.getChildren(); children.hasMoreElements();){
	      		stack.push((CgenNode)children.nextElement());
	    	}

	    	int offset = 0;

	    	curr_class.objects.enterScope();

	    	if (curr_class.getParent() != TreeConstants.No_class) {
	    		LinkedHashMap<AbstractSymbol, AbstractSymbol> inheritedAttrs = curr_class.getParentNd().inheritedAttrs;
	    		curr_class.inheritedAttrs.putAll(inheritedAttrs);
	    		for (AbstractSymbol attrF : curr_class.inheritedAttrs.keySet()) {
	    			// add to the object table
	    			attr a = curr_class.nameToAttribute.get(attrF);
	    			curr_class.objects.addId(a.name, new ObjInfo(offset++, ObjInfo.OBJTYPE.ATTR, a.type_decl));
				}
	    	}

	  	  	for(int i = 0; i < curr_class.getFeatures().getLength(); i++) {
	            Feature f = (Feature)curr_class.getFeatures().getNth(i);
	            if(f instanceof attr) {
	                attr a = (attr) f;
	                curr_class.overridenAttrs.put(a.name, curr_class.name);
	                // add to object table
	                curr_class.objects.addId(a.name, new ObjInfo(offset++, ObjInfo.OBJTYPE.ATTR, a.type_decl));
	            }
	        }

	    	// create the object tables
	        str.println(CgenSupport.WORD + "-1");
	        CgenSupport.emitProtObjRef(curr_class.name, str);
	        str.print(CgenSupport.LABEL);
	        str.println(CgenSupport.WORD + curr_class.tag);
	        str.println(CgenSupport.WORD + (CgenSupport.DEFAULT_OBJFIELDS + curr_class.overridenAttrs.size() + curr_class.inheritedAttrs.size()));
	        str.print(CgenSupport.WORD);
	        CgenSupport.emitDispTableRef(curr_class.name, str);
	        str.println();
	        //emit attrs
	        for (AbstractSymbol attrF : curr_class.inheritedAttrs.keySet()){
	        	str.print(CgenSupport.WORD);
	        	CgenSupport.emitProtObjAttr(((ObjInfo)curr_class.objects.lookup(attrF)).type_decl,str);
	        	str.println();

	        }
	        for (AbstractSymbol attrF : curr_class.overridenAttrs.keySet()){
	        	str.print(CgenSupport.WORD);
	        	CgenSupport.emitProtObjAttr(((ObjInfo)curr_class.objects.lookup(attrF)).type_decl,str);
	        	str.println();
	        }
    	}

    	return;
    };



	private void codeObjectInitializer(){
		return;
	};



	private void codeClassMethods(){
		for (int i = 0; i < nds.size(); i++) { // iterate among all curr_classes
			CgenNode curr_class = (CgenNode) nds.get(i);
			// go through defined methods
			for (AbstractSymbol methodName: curr_class.inheritedMethods.keySet() ) {
				curr_class.objects.enterScope();
				method m = curr_class.nameToMethod.get(methodName);
				for (int j = 0; j < m.formals.getLength(); j++) {
					formalc f = (formalc) m.formals.getNth(j);
					// add to the object table
				}
				str.print(curr_class.name + CgenSupport.METHOD_SEP + methodName + CgenSupport.LABEL);
				// Object_init:
				// 	addiu	$sp $sp -12
				CgenSupport.emitAddiu(CgenSupport.SP, CgenSupport.SP, -3, str);
				// 	sw	$fp 12($sp)
				CgenSupport.emitStore(CgenSupport.FP, 3, CgenSupport.SP, str);
				// 	sw	$s0 8($sp)
				CgenSupport.emitStore(CgenSupport.SELF, 2, CgenSupport.SP, str);
				// 	sw	$ra 4($sp)
				CgenSupport.emitStore(CgenSupport.RA, 1, CgenSupport.SP, str);
				// 	addiu	$fp $sp 16
				CgenSupport.emitAddiu(CgenSupport.FP, CgenSupport.SP, 16,str);
				// 	move	$s0 $a0
				CgenSupport.emitMove(CgenSupport.SELF, CgenSupport.ACC,str);
				// 	move	$a0 $s0
				CgenSupport.emitMove(CgenSupport.ACC, CgenSupport.SELF,str);
				// 	lw	$fp 12($sp)
				CgenSupport.emitLoad(CgenSupport.FP, 3, CgenSupport.SP,str);
				// 	lw	$s0 8($sp)
				CgenSupport.emitLoad(CgenSupport.SELF, 2, CgenSupport.SP,str);
				// 	lw	$ra 4($sp)
				CgenSupport.emitLoad(CgenSupport.RA, 1, CgenSupport.SP,str);
				// 	addiu	$sp $sp 12
				CgenSupport.emitAddiu(CgenSupport.SP, CgenSupport.SP, 12,str);
				// 	jr	$ra
				CgenSupport.emitReturn(str);
			}


		}
		return;
	};

    /** Gets the root of the inheritance tree */
    public CgenNode root() {
		return (CgenNode)probe(TreeConstants.Object_);
    }

}


