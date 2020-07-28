package c4s.impactassessment.app;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;


public class ImpactAssessmentAppSetup {

	public static void main(String args[]) {	
		AbstractModule cc;		
		cc = new ComponentConfig(args);

		Injector injector = Guice.createInjector(cc);
		IRuleEvaluationSubsystem resw = injector.getInstance(IRuleEvaluationSubsystem.class);
		resw.start();
	}
	
	
}
