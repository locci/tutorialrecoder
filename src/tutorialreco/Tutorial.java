/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tutorialreco;


import java.lang.reflect.Type;
import java.util.List;
import recoder.CrossReferenceServiceConfiguration;
import recoder.ParserException;
import recoder.abstraction.ClassType;
import recoder.abstraction.Method;
import recoder.abstraction.TypeParameter;
import recoder.convenience.TreeWalker;
import recoder.io.PropertyNames;
import recoder.io.SourceFileRepository;
import recoder.java.CompilationUnit;
import recoder.java.ProgramElement;
import recoder.java.declaration.ClassDeclaration;
import recoder.java.declaration.MethodDeclaration;
import recoder.java.declaration.VariableSpecification;
import recoder.java.expression.operator.CopyAssignment;

/****
 * Based on "Get Started" tutorial from RECODER web site.
 * (http://sourceforge.net/apps/mediawiki/recoder/)
 */
public class Tutorial {
	
	public static List<CompilationUnit> serviceConfiguration (String srcPath) {
		//create a service configuration
		CrossReferenceServiceConfiguration crsc = new CrossReferenceServiceConfiguration();
		
		//set the path to source code ("src" folder). 
		//multiple source code paths, as well as paths to libraries, can be separated via ":" or ";".
		crsc.getProjectSettings().setProperty(PropertyNames.INPUT_PATH, srcPath);
		crsc.getProjectSettings().ensureSystemClassesAreInPath();
		
		//tell Recoder to parse all .java files it can find in the directory "src"
		SourceFileRepository sfr = crsc.getSourceFileRepository();
		List<CompilationUnit> cul = null;
		try {
			cul = sfr.getAllCompilationUnitsFromPath();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		crsc.getChangeHistory().updateModel();
		return cul;
	}

	public static void main(String[] args) {
		//setting src path
                int cont = 0;
		List<CompilationUnit> cul = serviceConfiguration("/home/alexandre/NetBeansProjects/TesteSimples/src/testesimples/"); 
		String arg = "";
		//traversing the abstract syntax trees of the parsed .java files
		for (CompilationUnit cunit : cul) {
			TreeWalker tw = new TreeWalker(cunit);
			while (tw.next()) {
				ProgramElement pe = tw.getProgramElement();
				//getting class information
                                
				if (pe instanceof ClassDeclaration) {
                                        System.out.println("\n------- Class -------");
					ClassDeclaration cls = (ClassDeclaration)pe;
					System.out.println(cls.getFullName());
                                        arg = cls.getFullName();
                                     	System.out.println("\n------- Methods -------");
					List<Method> methods = cls.getMethods();
                                        List<? extends VariableSpecification> var = cls.getVariablesInScope();
                                        
					for (Method method : methods) {
						System.out.println(method.getFullName());
                                                if(method.isPublic()){
                                                    System.out.println("Acesso: Public");
                                                }else{
                                                     System.out.println("Acesso: Default");
                                                }
                                                if(method.getReturnType() != null){
                                                    System.out.println("Tipo de retornado: " + method.getReturnType());
                                                }else{
                                                    System.out.println("Tipo de retorno: void");
                                                }
                                                List<recoder.abstraction.Type> paramis = method.getSignature();
                                                if(paramis.isEmpty()){
                                                    System.out.println("Método não recebe parametros");
                                                    
                                                }else{
                                                    System.out.println("número de parâmetros " + paramis.size());
                                                    cont = 0;
                                                    for (recoder.abstraction.Type parami : paramis) {
                                                        cont ++;
                                                        System.out.println(cont + " Parametro: "+ parami.getName());
                                                    }
                                                }
                                                if (!var.isEmpty()){
                                                    System.out.println("Variáveis declaradas no escopo");
                                                    for(VariableSpecification va : var){
                                                        System.out.println(va.getFullName());
                                                    }
                                                }
                                                List<ClassType> ex = method.getExceptions();
                                                if(!ex.isEmpty()){
                                                    System.out.println("EX");
                                                    for(ClassType e : ex){
                                                        System.out.println(e.getName());
                                                    }
                                                }
                                                System.out.println("\n------- ** -------");
					}
				}
				//getting method information
				if (pe instanceof MethodDeclaration) {
					TreeWalker tw2 = new TreeWalker(pe);
					while (tw2.next()) {
						ProgramElement pe2 = tw2.getProgramElement();
						if (pe2 instanceof CopyAssignment) {
							CopyAssignment ca = (CopyAssignment)pe2;
							String info = ca.getStartPosition().getLine()+ ": " + ca.toSource().trim();
							System.out.println("\n--> Definition of " + ca.getFirstElement() + ":\n" + info);
						}
					}
				}
			}		   
		}
	}
}