package org.springframework.batch.sample.item.processor;

import org.springframework.batch.core.domain.Job;
import org.springframework.batch.core.domain.JobExecution;
import org.springframework.batch.core.domain.JobInstance;
import org.springframework.batch.core.domain.JobParameters;
import org.springframework.batch.core.domain.StepExecution;
import org.springframework.batch.core.domain.StepInstance;
import org.springframework.batch.execution.scope.SimpleStepContext;
import org.springframework.batch.execution.scope.StepSynchronizationManager;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.util.ClassUtils;

public class StagingItemProcessorTests extends AbstractTransactionalDataSourceSpringContextTests {

	private StagingItemProcessor processor;

	public void setProcessor(StagingItemProcessor processor) {
		this.processor = processor;
	}

	protected String[] getConfigLocations() {
		return new String[] { ClassUtils.addResourcePathToPackagePath(StagingItemProcessor.class,
				"staging-test-context.xml") };
	}

	protected void prepareTestInstance() throws Exception {
		SimpleStepContext stepScopeContext = new SimpleStepContext(new StepExecution(new StepInstance(new Long(11)),
				new JobExecution(new JobInstance(new Long(12), new JobParameters(), new Job("job")))));
		StepSynchronizationManager.register(stepScopeContext);
		super.prepareTestInstance();
	}

	public void testProcessInsertsNewItem() throws Exception {
		int before = getJdbcTemplate().queryForInt("SELECT COUNT(*) from BATCH_STAGING");
		processor.process("FOO");
		int after = getJdbcTemplate().queryForInt("SELECT COUNT(*) from BATCH_STAGING");
		assertEquals(before + 1, after);
	}

}
