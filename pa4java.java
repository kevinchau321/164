private void traverseAST(Classes classes) {
	/* Loop through each class */
	for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
        class_c currentClass = ((class_c)e.nextElement());
	    if (Flags.semant_debug) {
               System.out.println("First Pass Class " + currentClass.getName().toString());
        }
	    /* Inside each class, start new scope, traverse down the class AST */
	    objectSymTabMap.put(currentClass.getName().toString(), new MySymbolTable());
        MySymbolTable objectSymTab = objectSymTabMap.get(currentClass.getName().toString());
        objectSymTab.enterScope();
	    methodEnvironment.put(currentClass.getName().toString(), new HashMap<String,ArrayList<AbstractSymbol>>());
        HashMap<String, ArrayList<AbstractSymbol>> methodSymTab = methodEnvironment.get(currentClass.getName().toString());
	    Features features = currentClass.getFeatures();
	    for (Enumeration fe = features.getElements(); fe.hasMoreElements();) {
            Feature f = ((Feature)fe.nextElement());
	        if ( f instanceof attr ) {
		    //System.out.println("Attribute ");
 		    if (Flags.semant_debug) {
		    	System.out.println("Traversing Attribute : " + ((attr)f).getName().toString());
		    }
		    // Add attribute to object Symbol Table,if already present, scope error
		    if (objectSymTab.lookup(((attr)f).getName()) != null) {
			classTable.semantError(currentClass.getFilename(), f).println("Attribute " + ((attr)f).getName().toString() + " is multiply defined");
		    }
		    
  	 	    //add attribute to symbol table, overwrite if already there
		    objectSymTab.addId(((attr)f).getName(), ((attr)f).getType());
		    traverseExpression(currentClass, ((attr)f).getExpression(), objectSymTab, null);
        } else {
		    if (Flags.semant_debug) {
		    	System.out.println("Traversing Method : " + ((method)f).getName().toString());
		    }
		    // Add method to method Symbol Table,if already present, scope error
		    if (methodSymTab.containsKey(((method)f).getName().toString())) {
			classTable.semantError(currentClass.getFilename(), f).println("Method " + ((method)f).getName().toString() + " is multiply defined");
		    }
		    traverseMethod(currentClass, ((method)f), objectSymTab);
		}	
	    }
    }
}