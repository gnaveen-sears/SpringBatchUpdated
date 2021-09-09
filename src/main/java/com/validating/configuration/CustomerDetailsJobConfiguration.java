package com.validating.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.validating.domain.Address;
import com.validating.domain.AddressClassifier;
import com.validating.domain.AddressRowMapper;
import com.validating.domain.Contacts;
import com.validating.domain.ContactsClassifier;
import com.validating.domain.ContactsFieldSetMapper;
import com.validating.domain.FilteringAddressProcessor;
import com.validating.domain.FilteringItemProcessor;

@Configuration
public class CustomerDetailsJobConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(CustomerDetailsJobConfiguration.class);
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Value("${contacts.error.outfile}")
	private String contactsErrorsPath;
	
	@Value("${address.error.outfile}")
	private String addressErrorsPath;

	@Value("${contacts.csvfile}")
	private String csvResourcePath;
	
	@Value("${address.flatfile}")
	private String flatResourcePath;

	
	/*
	 * @Bean DefaultBatchConfigurer batchConfigurer() {
	 * 
	 * return new DefaultBatchConfigurer() {
	 * 
	 * 
	 * 
	 * private JobRepository jobRepository;
	 * 
	 * private JobExplorer jobExplorer;
	 * 
	 * private JobLauncher jobLauncher;
	 * 
	 * 
	 * 
	 * {
	 * 
	 * MapJobRepositoryFactoryBean jobRepositoryFactory = new
	 * MapJobRepositoryFactoryBean();
	 * 
	 * try {
	 * 
	 * this.jobRepository = jobRepositoryFactory.getObject();
	 * 
	 * MapJobExplorerFactoryBean jobExplorerFactory = new
	 * MapJobExplorerFactoryBean(jobRepositoryFactory);
	 * 
	 * this.jobExplorer = jobExplorerFactory.getObject();
	 * 
	 * SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
	 * 
	 * jobLauncher.setJobRepository(jobRepository);
	 * 
	 * jobLauncher.afterPropertiesSet();
	 * 
	 * this.jobLauncher = jobLauncher;
	 * 
	 * 
	 * 
	 * } catch (Exception e) {
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Override
	 * 
	 * public JobRepository getJobRepository() {
	 * 
	 * return jobRepository;
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Override
	 * 
	 * public JobExplorer getJobExplorer() {
	 * 
	 * return jobExplorer;
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Override
	 * 
	 * public JobLauncher getJobLauncher() {
	 * 
	 * return jobLauncher;
	 * 
	 * }
	 * 
	 * };
	 * 
	 * }
	 */
	
	@Bean(name = "aimsDataSource")
	@Primary
	public DataSource dataSource(@Value("${training.datasource.driverclass}") String driverClassName,
									 @Value("${training.datasource.url}") String url, @Value("${training.datasource.username}") String userName,
									 @Value("${training.datasource.password}") String password) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		return dataSource;
	}
	
	@Bean
	public FlatFileItemReader<Contacts> contactsItemReader() {
		logger.debug("FllatFileReader for contacts  ---> Reading CSV file");
		FlatFileItemReader<Contacts> reader = new FlatFileItemReader<>();

		reader.setLinesToSkip(1);
		reader.setResource(new ClassPathResource(csvResourcePath));

		DefaultLineMapper<Contacts> contactsLineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "Last_Name", "First_Name", "Phone", "Email", "Title", "Designation" });

		contactsLineMapper.setLineTokenizer(tokenizer);
		contactsLineMapper.setFieldSetMapper(new ContactsFieldSetMapper());
		contactsLineMapper.afterPropertiesSet();

		reader.setLineMapper(contactsLineMapper);

		return reader;
	}

	
	@Bean
	public FlatFileItemReader<Address> addressItemReader() {
		logger.debug("FllatFileReader for Address  ---> Reading .DAT File");
		FlatFileItemReader<Address> reader1 = new FlatFileItemReader<>();
		reader1.setResource(new ClassPathResource(flatResourcePath));
		DefaultLineMapper<Address> addressLineMapper = new DefaultLineMapper<>();
		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
		tokenizer.setNames(new String []{"customerPhone", "addressType", "addressLine1", "addressLine2", "city", "stateCode",
				"zipcode", "zipplus4", "addressType2", "addressLine12", "addressLine22", "city2",
				"stateCode2", "zipcode2", "zipplus42"});
		tokenizer.setColumns(new Range[] { new Range(1, 10), new Range(11, 11), new Range(12, 41),
				new Range(42, 71), new Range(72, 86), new Range(87, 88), new Range(89, 93),
				new Range(94, 97), new Range(98, 98), new Range(99, 128), new Range(129, 158),
				new Range(159, 173), new Range(174, 175), new Range(176, 180), new Range(181) });
		
		addressLineMapper.setLineTokenizer(tokenizer);
		addressLineMapper.setFieldSetMapper(new AddressRowMapper());
		addressLineMapper.afterPropertiesSet();

		reader1.setLineMapper(addressLineMapper);

		return reader1;
	}


	@Bean
	public JdbcBatchItemWriter<Contacts> contactsItemWriter() throws Exception {
		logger.debug("JDBC writer for contacts  ---> Writing into table");
		System.out.println("Inside Contacts  Writer");
		JdbcBatchItemWriter<Contacts> itemWriter = new JdbcBatchItemWriter<>();

		// Even if passing NULL here, Spring boot will replace these at runtime with values obtained by replacing placeholders like 
		// "${training.datasource.driverclass}" - MAGIC!!
		itemWriter.setDataSource(dataSource(null, null, null, null));  
		itemWriter
				.setSql("INSERT INTO CONTACTS VALUES (:Last_Name, :First_Name, :Phone, :Email, :Title, :Designation)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Contacts>());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

	@Bean
	public FlatFileItemWriter<Address> addressSkipWriter() {
		
		logger.debug("JDBC writer for address    ERRORS ---> Writing into csv file");
		System.out.println("Inside Address Skip Writer");
	    BeanWrapperFieldExtractor<Address> fieldExtractor = new BeanWrapperFieldExtractor<>();
	    fieldExtractor.setNames(new String[] {"Error"});
	    fieldExtractor.afterPropertiesSet();

	    DelimitedLineAggregator<Address> lineAggregator = new DelimitedLineAggregator<>();
	    lineAggregator.setDelimiter(",");
	    lineAggregator.setFieldExtractor(fieldExtractor);

	    FlatFileItemWriter<Address> flatFileItemWriter = new FlatFileItemWriter<>();
	    flatFileItemWriter.setName("AddressSkippedWriter");
	    flatFileItemWriter.setResource(new FileSystemResource(addressErrorsPath));
	    flatFileItemWriter.setLineAggregator(lineAggregator);

	    return flatFileItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<Address> addressItemWriter() {
		logger.debug("JDBC writer for address  ---> Writing into table");
		System.out.println("Inside Address  Writer");
		JdbcBatchItemWriter<Address> itemWriter1 = new JdbcBatchItemWriter<>();

		// Look for MAGIC!! to understand this
		itemWriter1.setDataSource(dataSource(null, null, null, null));
		itemWriter1.setSql(
				"INSERT INTO ADDRESS VALUES (:customerPhone, :addressType, :addressLine1, :addressLine2, :city, :stateCode,:zipcode,:zipplus4,:addressType2,:addressLine12,:addressLine22,:city2,:stateCode2,:zipcode2,:zipplus42)");
		itemWriter1.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Address>());
		itemWriter1.afterPropertiesSet();

		return itemWriter1;
	}
	
	

	@Bean
    public FlatFileItemWriter<Contacts> contactsSkipWriter() 
    {
        //Create writer instance
        FlatFileItemWriter<Contacts> writer = new FlatFileItemWriter<>();
         
        //Set output file location
        writer.setResource(new FileSystemResource(contactsErrorsPath));
         
        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);
         
        //Name field values sequence based on object properties 
        writer.setLineAggregator(new DelimitedLineAggregator<Contacts>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Contacts>() {
                    {
                        setNames(new String[] { "Last_Name", "First_Name", "Phone", "Email", "Title", "Designation", "Valid","Error" });
                    }
                });
            }
        });
        return writer;
    }
	
	 
	 @Bean
	    public ClassifierCompositeItemWriter<Contacts> classifierCustomerCompositeItemWriter2() throws Exception {
	        ClassifierCompositeItemWriter<Contacts> compositeItemWriter = new ClassifierCompositeItemWriter<>();
	        compositeItemWriter.setClassifier(new ContactsClassifier( contactsItemWriter(),contactsSkipWriter()));
	        return compositeItemWriter;
	    }
	 
	 
	@Bean
    public ClassifierCompositeItemWriter<Address> classifierCustomerCompositeItemWriter() throws Exception {
        ClassifierCompositeItemWriter<Address> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new AddressClassifier( addressItemWriter(),addressSkipWriter()));
        return compositeItemWriter;
    }
	
	

	@Bean
	public FilteringItemProcessor itemProcessor() {
		return new FilteringItemProcessor();
	}

	@Bean
	public FilteringAddressProcessor addressProcessor() {
		return new FilteringAddressProcessor();
	}

	@Bean
	public Step customerStep() throws Exception {
		return stepBuilderFactory.get("customerStep").<Contacts, Contacts> chunk(1).reader(contactsItemReader())
				.processor(itemProcessor())
				.writer(classifierCustomerCompositeItemWriter2())
				.stream(contactsSkipWriter())
				.build();
	}

	@Bean
	public Step addressStep() throws Exception {
		return stepBuilderFactory.get("addressStep").<Address, Address> chunk(1).reader(addressItemReader())
				.processor(addressProcessor())
				.writer(classifierCustomerCompositeItemWriter())
				.stream(addressSkipWriter())
				.build();
	}

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("CustomerDetailsJob").start(customerStep()).next(addressStep()).build();
	}
}
