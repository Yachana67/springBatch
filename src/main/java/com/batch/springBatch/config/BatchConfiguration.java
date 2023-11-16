package com.batch.springBatch.config;

import java.util.HashMap;
import java.util.Map;

import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import com.batch.springBatch.entity.Insurance;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

   @Bean
   @StepScope //@StepScope ensures that a new instance of the reader is created for each step and will closed when the step is finished. 
    public PoiItemReader<Insurance> excelReader() {
        PoiItemReader<Insurance> reader = new PoiItemReader<>();
        reader.setResource(new ClassPathResource("InsuranceData.xlsx"));
        reader.setLinesToSkip(1); // Skip header row
        reader.setRowMapper(excelDataRowMapper());
        return reader;
    }
    
    
    private RowMapper<Insurance> excelDataRowMapper() {
        return new ExcelDataRowMapper();
    }
    
    @Bean
    public ItemProcessor<Insurance, Insurance> yourItemProcessor() {
        return new LimitingItemProcessor(); 	
//        return insurance -> {
//         
//            return insurance;
//        };
    }
   
//    @Bean
//    public ItemWriter<Insurance> writer() {
//        return items -> {
//            for (Insurance insurance : items) {
//                // Write each item to the console
////                System.out.println(insurance);
////                System.out.println("Processing " );
//
//            }
//        };
//    }

    //bean for writing the excel files in database
    @Bean
    public JdbcBatchItemWriter<Insurance> writer()
    {
    	JdbcBatchItemWriter<Insurance> writer=new JdbcBatchItemWriter<>();
    	writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Insurance>());
    	writer.setSql("insert into example.insurance(Category,NAME,Email) values (:Category,:NAME,:Email)");
    	writer.setDataSource(this.dataSource);
    	return writer;
    }
    
    
    @Bean
    public Step myStep(ItemReader<Insurance> reader,ItemProcessor<Insurance, Insurance> processor, JdbcBatchItemWriter<Insurance> writer) {
        return stepBuilderFactory.get("myStep")
              .<Insurance,Insurance>chunk(20) 
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job myJob(Step myStep) {
        return jobBuilderFactory.get("myJob")
                .incrementer(new RunIdIncrementer())
                .start(myStep)
                .build();
    }
}
