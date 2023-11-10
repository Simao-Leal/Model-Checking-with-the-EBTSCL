package claimlang;

import javax.management.RuntimeErrorException;
import javax.tools.*;

import java_cup.runtime.Symbol;
import claimlang.AST.FileNode;
import claimlang.AST.ToJavaVisitor;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;

public class ClaimLang {
	public static void main(String[] args) throws Exception {
		
		//TODO maybe implement some parameter options like -output directory
		
		//generating code
		TSScanner scanner = new TSScanner(new FileReader(args[0]));
        parser parser = new parser(scanner);
		Symbol result = parser.parse();
		ToJavaVisitor v = new ToJavaVisitor("Temp");
		((FileNode) result.value).accept(v);
		
        String code = v.file;
        //System.out.println(code); //FIXME delete
        
        //create temporary directory
        File tempDir = Files.createTempDirectory("ClaimLangTempDir").toFile();

        // Save string to .java file.
        File sourceFile = new File(tempDir, "Temp.java"); 
        Writer writer = new FileWriter(sourceFile);
        writer.write(code);
        writer.close();

        // Compile it using JavaCompiler.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler == null) throw new RuntimeException("You must have the JDK installed (javac command).");
        compiler.run(null, null, null, sourceFile.getPath());

        // Load and run.
        // Note: make sure the root path contains the compiled .class file.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { tempDir.toURI().toURL() });
        Class<?> cls = Class.forName("Temp", true, classLoader); 
        cls.getMethod("main", String[].class).invoke(null, (Object) new String[]{});  // casting to (Object) to prevent varargs issues
        
        // Delete temporary directory
        for(File file : tempDir.listFiles()) {
        	file.delete();
        }
        tempDir.delete();
    }
}
