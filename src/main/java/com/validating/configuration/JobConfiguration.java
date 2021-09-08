package com.validating.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
//import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
//import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
//import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.validating.domain.Address;
import com.validating.domain.AddressClassifier;
import com.validating.domain.AddressRowMapper;
import com.validating.domain.Contacts;
import com.validating.domain.ContactsClassifier;
import com.validating.domain.ContactsFieldSetMapper;
import com.validating.domain.FilteringAddressProcessor;
import com.validating.domain.FilteringItemProcessor;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(JobConfiguration.class);
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
    DefaultBatchConfigurer batchConfigurer() {

        return new DefaultBatchConfigurer() {



            private JobRepository jobRepository;

            private JobExplorer jobExplorer;

            private JobLauncher jobLauncher;



            {

                MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean();

                try {

                    this.jobRepository = jobRepositoryFactory.getObject();

                    MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);

                    this.jobExplorer = jobExplorerFactory.getObject();

                    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();

                    jobLauncher.setJobRepository(jobRepository);

                    jobLauncher.afterPropertiesSet();

                    this.jobLauncher = jobLauncher;



                } catch (Exception e) {

                }

            }



            @Override

            public JobRepository getJobRepository() {

                return jobRepository;

            }



            @Override

            public JobExplorer getJobExplorer() {

                return jobExplorer;

            }



            @Override

            public JobLauncher getJobLauncher() {

                return jobLauncher;

            }

        };

    }
	
	@Value("${spring.csvfile}")
	private Resource csvResource;
	
	@Bean
	public FlatFileItemReader<Contacts> contactsItemReader() {
		logger.debug("FllatFileReader for contacts  ---> Reading CSV file");
		FlatFileItemReader<Contacts> reader = new FlatFileItemReader<>();

		reader.setLinesToSkip(1);
		reader.setResource(csvResource);

		DefaultLineMapper<Contacts> contactsLineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] { "Last_Name", "First_Name", "Phone", "Email", "Title", "Designation" });

		contactsLineMapper.setLineTokenizer(tokenizer);
		contactsLineMapper.setFieldSetMapper(new ContactsFieldSetMapper());
		contactsLineMapper.afterPropertiesSet();

		reader.setLineMapper(contactsLineMapper);

		return reader;
	}

	
	@Value("${spring.flatfile}")
	private Resource flatResource;
	
	@Bean
	public FlatFileItemReader<Address> addressItemReader() {
		logger.debug("FllatFileReader for Address  ---> Reading .DAT File");
		FlatFileItemReader<Address> reader1 = new FlatFileItemReader<>();
		reader1.setResource(flatResource);
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

		itemWriter.setDataSource(this.dataSource);
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
	    flatFileItemWriter.setResource(outputResource);
	    flatFileItemWriter.setLineAggregator(lineAggregator);

	    return flatFileItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<Address> addressItemWriter() {
		logger.debug("JDBC writer for address  ---> Writing into table");
		System.out.println("Inside Address  Writer");
		JdbcBatchItemWriter<Address> itemWriter1 = new JdbcBatchItemWriter<>();

		itemWriter1.setDataSource(this.dataSource);
		itemWriter1.setSql(
				"INSERT INTO ADDRESS VALUES (:customerPhone, :addressType, :addressLine1, :addressLine2, :city, :stateCode,:zipcode,:zipplus4,:addressType2,:addressLine12,:addressLine22,:city2,:stateCode2,:zipcode2,:zipplus42)");
		itemWriter1.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Address>());
		itemWriter1.afterPropertiesSet();

		return itemWriter1;
	}
	
	@Value("${spring.outfile}")
	private Resource outputResource;
	
//	@Bean
//	public FlatFileItemWriter<Contacts> contactsSkipWriter() {
//		logger.debug("JDBC writer for contacts ERRORS ---> Writing into csv file");
//		System.out.println("Inside Contacts Skip Writer");
//	    BeanWrapperFieldExtractor<Contacts> fieldExtractor = new BeanWrapperFieldExtractor<>();
//	    fieldExtractor.setNames(new String[] {"Error"});
//	    fieldExtractor.afterPropertiesSet();
//
//	    DelimitedLineAggregator<Contacts> lineAggregator = new DelimitedLineAggregator<>();
//	    lineAggregator.setDelimiter(",");
//	    lineAggregator.setFieldExtractor(fieldExtractor);
//
//	    FlatFileItemWriter<Contacts> flatFileItemWriter = new FlatFileItemWriter<>();
//	    flatFileItemWriter.setName("ContactsItemWriter");
//	    flatFileItemWriter.setResource(outputResource);
//	    flatFileItemWriter.setLineAggregator(lineAggregator);
//
//	    return flatFileItemWriter;
//	}
	private Resource outputResource2 = new FileSystemResource("Output/Skipped.csv");
	
	@Bean
    public FlatFileItemWriter<Contacts> contactsSkipWriter() 
    {
        //Create writer instance
        FlatFileItemWriter<Contacts> writer = new FlatFileItemWriter<>();
         
        //Set output file location
        writer.setResource(outputResource2);
         
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
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1").<Contacts, Contacts> chunk(1).reader(contactsItemReader())
				.processor(itemProcessor())
				.writer(classifierCustomerCompositeItemWriter2())
				.stream(contactsSkipWriter())
				.build();
	}

	@Bean
	public Step step2() throws Exception {
		return stepBuilderFactory.get("step2").<Address, Address> chunk(1).reader(addressItemReader())
				.processor(addressProcessor())
				.writer(classifierCustomerCompositeItemWriter())
				.stream(addressSkipWriter())
				.build();
	}

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("job").start(step1()).next(step2()).build();
	}
}
