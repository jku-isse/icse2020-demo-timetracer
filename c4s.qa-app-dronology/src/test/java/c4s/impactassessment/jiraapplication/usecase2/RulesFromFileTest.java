package c4s.impactassessment.jiraapplication.usecase2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import c4s.impactassessment.rulebase.OriginalKieSession;

public class RulesFromFileTest {

	Injector injector;
	
//	@Before
//	public void setUp() throws Exception {
//		
//	}

	@Test
	public void testLoadRulesFromFilesystem() {
		Properties props = new Properties();
		//props.setProperty("rulefolder", "dynamicrules");
		injector = Guice.createInjector(new RulesFromFileEvaluationSubsystem(props));
		KieSession kSession = injector.getInstance(Key.get(KieSession.class, OriginalKieSession.class));
		kSession.fireAllRules();
	
	}
	
	@Test
	public void testWalkPaths() {
		Path p = Paths.get("./");
		System.out.println(p.toAbsolutePath());
		
		try (Stream<Path> paths = Files.walk(Paths.get("./rules"))) {
			paths
//			.map(path -> {	System.out.println(path.getFileName()); 
//			return path; })
			.filter(Files::isRegularFile)
			.filter(path -> path.getFileName().toString().endsWith(".drl"))
			.forEach(path -> {	System.out.println(path); 
			});
		} catch (IOException e) {
			throw new RuntimeException("Error loading file paths \n"+e.getMessage());
		}
	}
	


}
