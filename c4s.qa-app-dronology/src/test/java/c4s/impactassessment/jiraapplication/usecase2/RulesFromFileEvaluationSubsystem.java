package c4s.impactassessment.jiraapplication.usecase2;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import c4s.impactassessment.app.IRuleEvaluationSubsystem;
import c4s.impactassessment.app.MinimalRuleEvaluationSubSystemWithoutJiraOrJama;
import c4s.impactassessment.rulebase.OriginalKieSession;
import c4s.impactassessment.rulebase.PersistingKieSession;
import c4s.impactassessment.utils.BaseMockOrRuntimeExceptionComponentConfig;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEventPublisher;

public class RulesFromFileEvaluationSubsystem extends BaseMockOrRuntimeExceptionComponentConfig{

	public RulesFromFileEvaluationSubsystem(Properties props) {
		super.props = props;
	}

	@Override
	public void configureRuleEvaluationSubsystemBinding() {
		bind(IRuleEvaluationSubsystem.class).to(MinimalRuleEvaluationSubSystemWithoutJiraOrJama.class).asEagerSingleton();
	}

	@Override
	public void configureTaskStateTransitionEventPublisherBinding() {
		//no op to avoid race condition on binding
	}

	public void configureTaskStateTransitionEventPublisherBinding2() {
		bind(TaskStateTransitionEventPublisher.class).to(MockTaskStateTransitionEventPublisher.class).asEagerSingleton();
	}
	
	protected KieSession kSession;
	@Override
	public void configureKSessionBinding() {
		if (kSession == null) {
			createKsessionFromFiles();
		}
		bind(KieSession.class).annotatedWith(OriginalKieSession.class).toInstance(kSession);
		bind(KieSession.class).annotatedWith(PersistingKieSession.class).toInstance(kSession);
	}
	
	private void createKsessionFromResources() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kSession = kContainer.newKieSession("ksession-rules");
	}
	
	protected String relPath = "";
	protected String relPathPrefix = "./src/main/resources/"; // Mandatory to use src/main/resources/ as root for drl files, 
	// see: line 79 of https://github.com/kiegroup/drools/blob/master/drools-compiler/src/main/java/org/drools/compiler/kie/builder/impl/KieBuilderImpl.java
	// http://lists.jboss.org/pipermail/rules-users/2013-December/034306.html
	
	private void createKsessionFromFiles() {
		String ruleFolder = props.getProperty("rulefolder", "rules");
		if (ruleFolder.startsWith(relPathPrefix)) {
			relPath = ruleFolder;
		} else {
			if (ruleFolder.startsWith("\\") || ruleFolder.startsWith("/"))
				ruleFolder = ruleFolder.substring(1);
			relPath = relPathPrefix + ruleFolder;
		}
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		try (Stream<Path> paths = Files.walk(Paths.get(relPath))) {
		    paths
		        .filter(Files::isRegularFile)
		        .filter(path -> path.getFileName().toString().endsWith(".drl"))
		        .forEach(path -> loadRuleFromPath(path, kfs, ks));
		} catch (IOException e) {
			throw new RuntimeException("Error loading file paths \n"+e.getMessage());
		}
		KieBuilder kb = ks.newKieBuilder(kfs);
		kb.buildAll(); 
		if (kb.getResults().hasMessages(Level.ERROR)) {
		    throw new RuntimeException("Rule Base Build Errors:\n" + kb.getResults().toString());
		}

		KieContainer kContainer = ks.newKieContainer(kb.getKieModule().getReleaseId());
		kSession = kContainer.newKieSession();
	}
	
	private void loadRuleFromPath(Path filePath, KieFileSystem kfs, KieServices ks) {
		
		//File file = filePath.toFile();
		try {
			String content = new String(Files.readAllBytes(filePath));
			String filename = relPath.substring(1)+"/"+filePath.getFileName(); //filePath.getFileName().toString()
			System.out.println("Loading rule file: "+filename);
			kfs.write(filename, ks.getResources().newReaderResource(new StringReader(content))
					.setResourceType(ResourceType.DRL));
		} catch (IOException e) {
			throw new RuntimeException("Rule File Loading error: \n"+e.getMessage());
		}
	}
	
	
	@Override
	protected void configure() {
		super.configure();
		configureKSessionBinding();
		configureTaskStateTransitionEventPublisherBinding2();
		configureRuleEvaluationSubsystemBinding();
	}

	

}
