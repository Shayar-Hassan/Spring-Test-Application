package com.ite5year.batch.configuration;

import com.ite5year.models.Car;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    public BatchConfiguration() {
    }

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }
    @Autowired
    public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }
    @Autowired
    public void setStepBuilderFactory(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public FlatFileItemReader<Car> reader() {
        return new FlatFileItemReaderBuilder<Car>()
                .name("carItemReader")
                .resource(new ClassPathResource("cars1.csv"))
                .delimited()
                .names("name", "price", "seatsNumber", "dateOfSale", "priceOfSale")
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Car>() {{
                    setTargetType(Car.class);
                }})
                .build();
    }


    @Bean
    public LineMapper<Car> lineMapper() {
        final DefaultLineMapper<Car> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("name", "price", "seatsNumber", "dateOfSale", "priceOfSale");
        final CarFieldSetMapper fieldSetMapper = new CarFieldSetMapper();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }


    @Bean
    public CarProcessor processor() {
        return new CarProcessor();
    }


    @Bean
    public JdbcBatchItemWriter<Car> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Car>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO car (name, price, seats_number, date_of_sale, price_of_sale) VALUES (:name, :price, :seatsNumber, :dateOfSale, :priceOfSale)")
                .dataSource(dataSource)
                .build();
    }


    @Bean
    public Job importToCarJob(NotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importToCarJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }


    @Bean
    public Step step1(JdbcBatchItemWriter<Car> writer) {
        return stepBuilderFactory.get("step1")

                .<Car, Car>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();

    }
}