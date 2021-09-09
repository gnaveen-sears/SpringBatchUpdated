package com.validating.configuration;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class BatchRepoConfig implements BatchConfigurer {

	private JobRepository jobRepository;

	private SimpleJobLauncher jobLauncher;

	private SimpleJobExplorer jobExplorer;

	@PostConstruct
	public void initialize() throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(repoTransactionManager());
		factory.afterPropertiesSet();
		this.jobRepository = factory.getObject();
		this.jobExplorer = new SimpleJobExplorer(factory.getJobInstanceDao(), factory.getJobExecutionDao(),
				factory.getStepExecutionDao(), factory.getExecutionContextDao());
		this.jobLauncher = new SimpleJobLauncher();
		this.jobLauncher.setJobRepository(this.jobRepository);
		this.jobLauncher.afterPropertiesSet();
	}

	@Override
	public JobRepository getJobRepository() throws Exception {
		return this.jobRepository;
	}

	@Override
	public ResourcelessTransactionManager getTransactionManager() throws Exception {
		return repoTransactionManager();
	}

	@Override
	public JobLauncher getJobLauncher() throws Exception {
		return this.jobLauncher;
	}

	@Override
	public JobExplorer getJobExplorer() throws Exception {
		return this.jobExplorer;
	}

	@Bean(name = "repoTransactionManager")
	public ResourcelessTransactionManager repoTransactionManager() {
		return new ResourcelessTransactionManager();
	}
}
