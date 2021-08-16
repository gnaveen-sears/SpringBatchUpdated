package com.validating.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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

//import io.spring.batch.domain.FilteringItemProcessor;

//import com.validating.domain.Contacts;
//import com.validating.*;
@Configuration
@EnableBatchProcessing
public class JobConfiguration {
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	
	@Value("${spring.csvfile}")
	private Resource csvResource;
	
	@Bean
	public FlatFileItemReader<Contacts> contactsItemReader() {
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
		FlatFileItemReader<Address> reader1 = new FlatFileItemReader<>();
		reader1.setResource(flatResource);
		reader1.setLineMapper(new DefaultLineMapper<Address>() {
			{
				setLineTokenizer(new FixedLengthTokenizer() {
					{
						setNames(new String []{"customerPhone", "addressType", "addressLine1", "addressLine2", "city", "stateCode",
								"zipcode", "zipplus4", "addressType2", "addressLine12", "addressLine22", "city2",
								"stateCode2", "zipcode2", "zipplus42"});
						setColumns(new Range[] { new Range(1, 10), new Range(11, 11), new Range(12, 41),
								new Range(42, 71), new Range(72, 86), new Range(87, 88), new Range(89, 93),
								new Range(94, 97), new Range(98, 98), new Range(99, 128), new Range(129, 158),
								new Range(159, 173), new Range(174, 175), new Range(176, 180), new Range(181) });
					}
				});
				setFieldSetMapper(new AddressRowMapper());
			}
		});

		return reader1;
	}

	@Bean
	public JdbcBatchItemWriter<Contacts> contactsItemWriter() throws Exception {
		JdbcBatchItemWriter<Contacts> itemWriter = new JdbcBatchItemWriter<>();

		itemWriter.setDataSource(this.dataSource);
		itemWriter
				.setSql("INSERT INTO CONTACTS VALUES (:Last_Name, :First_Name, :Phone, :Email, :Title, :Designation)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Contacts>());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}
//	@Bean 
//    public FlatFileItemWriter<Contacts> writer3() 
//    {
//        FlatFileItemWriter<Contacts> writer = new FlatFileItemWriter<>();
//        writer.setResource(outputResource);
//        writer.setAppendAllowed(true);
//        writer.setLineAggregator(new DelimitedLineAggregator<Contacts>() {
//            {
//                setDelimiter(",");
//                setFieldExtractor(new BeanWrapperFieldExtractor<Contacts>() {
//                    {
////                    	String fname="Address.dat";
//                        setNames(new String[] { "error" } );
//                    }
//                });
//            }
//        });
//        return writer;
//    }

	@Bean
	public FlatFileItemWriter<Address> addressSkipWriter() {
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
	//private Resource outputResource = new FileSystemResource("Output/Skipped.csv");
//	 @Bean 
//	    public FlatFileItemWriter<Address> writer2() 
//	    {
//	        FlatFileItemWriter<Address> writer = new FlatFileItemWriter<>();
//	        writer.setResource(outputResource);
//	        writer.setAppendAllowed(true);
//	        writer.setLineAggregator(new DelimitedLineAggregator<Address>() {
//	            {
//	                setDelimiter(",");
//	                setFieldExtractor(new BeanWrapperFieldExtractor<Address>() {
//	                    {
////	                    	String fname="Address.dat";
//	                        setNames(new String[] { "error" } );
//	                    }
//	                });
//	            }
//	        });
//	        return writer;
//	    }
	@Bean
	public FlatFileItemWriter<Contacts> contactsSkipWriter() {
	  BeanWrapperFieldExtractor<Contacts> fieldExtractor = new BeanWrapperFieldExtractor<>();
	  fieldExtractor.setNames(new String[] {"Error"});
	  fieldExtractor.afterPropertiesSet();

	  DelimitedLineAggregator<Contacts> lineAggregator = new DelimitedLineAggregator<>();
	  lineAggregator.setDelimiter(",");
	  lineAggregator.setFieldExtractor(fieldExtractor);

	  FlatFileItemWriter<Contacts> flatFileItemWriter = new FlatFileItemWriter<>();
	  flatFileItemWriter.setName("ContactsItemWriter");
	  flatFileItemWriter.setResource(outputResource);
	  flatFileItemWriter.setLineAggregator(lineAggregator);

	  return flatFileItemWriter;
	}
	
	 
	 @Bean
	    public ClassifierCompositeItemWriter<Contacts> classifierCustomerCompositeItemWriter2() throws Exception {
	        ClassifierCompositeItemWriter<Contacts> compositeItemWriter = new ClassifierCompositeItemWriter<>();
	        compositeItemWriter.setClassifier(new ContactsClassifier( contactsSkipWriter(),contactsItemWriter()));
	        return compositeItemWriter;
	    }
	 
	 
	@Bean
    public ClassifierCompositeItemWriter<Address> classifierCustomerCompositeItemWriter() throws Exception {
        ClassifierCompositeItemWriter<Address> compositeItemWriter = new ClassifierCompositeItemWriter<>();
        compositeItemWriter.setClassifier(new AddressClassifier( addressSkipWriter(),addressItemWriter()));
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
