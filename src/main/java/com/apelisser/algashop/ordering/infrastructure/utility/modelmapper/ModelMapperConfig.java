package com.apelisser.algashop.ordering.infrastructure.utility.modelmapper;

import com.apelisser.algashop.ordering.application.customer.query.CustomerOutput;
import com.apelisser.algashop.ordering.application.utility.Mapper;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.customer.BirthDate;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ModelMapperConfig {

    private static final Converter<FullName, String> fullNameToFirstNameConverter;
    private static final Converter<FullName, String> fullNameToLastNameConverter;
    private static final Converter<BirthDate, LocalDate> birthDateToLocalDateConverter;

    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        this.configuration(modelMapper);
        return modelMapper::map;
    }

    private void configuration(ModelMapper modelMapper) {
        modelMapper.getConfiguration()
            .setSourceNamingConvention(NamingConventions.NONE)
            .setDestinationNamingConvention(NamingConventions.NONE)
            .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Customer.class, CustomerOutput.class)
            .addMappings(mapping -> mapping.using(fullNameToFirstNameConverter).map(Customer::fullName, CustomerOutput::setFirstName))
            .addMappings(mapping -> mapping.using(fullNameToLastNameConverter).map(Customer::fullName, CustomerOutput::setLastName))
            .addMappings(mapping -> mapping.using(birthDateToLocalDateConverter).map(Customer::birthDate, CustomerOutput::setBirthDate));
    }

    static {
        fullNameToFirstNameConverter = mappingContext -> {
            FullName fullName = mappingContext.getSource();
            return fullName != null ? fullName.firstName() : null;
        };

        fullNameToLastNameConverter = mappingContext -> {
            FullName fullName = mappingContext.getSource();
            return fullName != null ? fullName.lastName() : null;
        };

        birthDateToLocalDateConverter = mappingContext -> {
            BirthDate birthDate = mappingContext.getSource();
            return birthDate != null ? birthDate.value() : null;
        };
    }

}
